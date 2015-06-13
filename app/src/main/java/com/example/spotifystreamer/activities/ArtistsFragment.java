package com.example.spotifystreamer.activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.provider.SearchRecentSuggestions;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.example.spotifystreamer.R;
import com.example.spotifystreamer.model.Artist;
import com.example.spotifystreamer.model.QuerySuggestionProvider;
import com.example.spotifystreamer.model.Track;
import com.example.spotifystreamer.utils.Utils;
import com.example.spotifystreamer.view.ArtistsArrayAdapter;

import java.util.ArrayList;
import java.util.List;


/**
 * Implementation of the SearchView & saving RecentQuerySuggestions
 * https://developer.android.com/training/search/setup.html
 * http://antonioleiva.com/actionbarcompat-action-views/
 * http://developer.android.com/guide/topics/search/adding-recent-query-suggestions.html
 */

public class ArtistsFragment extends Fragment implements  SearchView.OnQueryTextListener{

    private static final String LOG_TAG = ArtistsFragment.class.getSimpleName();
    private final boolean L = true;

    private final String EXTRA_TRACK_RESULTS = "com.example.spotifystreamer.activities.tracks";
    private final String PREFS_RESULTS_RETURNED = "pref_key_result_returned";
    private final String PREF_COUNTRY_KEY = "pref_key_country_code";

    private ListView mListView;
    private ArtistsArrayAdapter mArtistsAdapter;
    private List<Artist> mArtists;
    private SearchView mSearchView;
    private static SearchRecentSuggestions sSearchRecentSuggestions;
    private MenuItem mSearchMenuItem;
    private ProgressBar mProgressBar;

    public ArtistsFragment() { }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mArtists = new ArrayList<>();
        setRetainInstance(true); // ensure the fragment outlives device rotation
        setHasOptionsMenu(true); // add the search menu item

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_content, container, false);

        // cache references to view elements of interest
        mListView = (ListView) view.findViewById(R.id.list_view_item_container);
        mProgressBar = (ProgressBar) view.findViewById(R.id.progress_bar);
        mProgressBar.setVisibility(View.GONE);


        // register item click listener - execute Top-Ten Track download
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

                // retrieve id & name of the particular artist & add as an extra to the intent
                Artist artist = mArtistsAdapter.getItem(position);
                String artistName = artist.getName();
                String artistId = artist.getId();

                // retrieve user preferences from SharedPreferences
                SharedPreferences prefs =
                        PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
                String country = prefs.getString(PREF_COUNTRY_KEY, getActivity().getString(R.string.pref_country_code_default));

                // execute track download
                new ArtistQueryTask(artistName, artistId, country).execute();

            }
        });



        if(savedInstanceState == null) {
            // instantiate the ArrayAdapter and bind it to the ListView
            // when the fragment is first instantiated
            mArtistsAdapter = new ArtistsArrayAdapter(getActivity(), mArtists);
            mListView.setAdapter(mArtistsAdapter);
        } else {
            // re-bind the adapter to the listview on device rotation
            mListView.setAdapter(mArtistsAdapter);
        }

        return view;
    }



    // handle search query's submitted via the SearchView
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.menu_search, menu);

        // get the SearchView and set the searchable configuration
        SearchManager mgr = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        mSearchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        mSearchView.setSearchableInfo(mgr.getSearchableInfo(getActivity().getComponentName()));
        mSearchView.setSubmitButtonEnabled(true);
        mSearchView.setOnQueryTextListener(this);

        // cache a reference to the SearchMenuItem
        mSearchMenuItem = menu.findItem(R.id.action_search);

    }


    // submit search to Spotify
    @Override
    public boolean onQueryTextSubmit(String query) {

        // hide softkeyboard upon submitting search
        Utils.hideKeyboard(getActivity(), mSearchView.getWindowToken());

        // close the search menu item
        mSearchMenuItem.collapseActionView();

        // save the search query to the RecentSuggestionsProvider
        sSearchRecentSuggestions = new SearchRecentSuggestions(getActivity(),
                QuerySuggestionProvider.AUTHORITY, QuerySuggestionProvider.MODE);
        sSearchRecentSuggestions.saveRecentQuery(query, null);

        // instantiate and invoke the AsyncTask to download the search results
        new SearchQueryTask().execute(query);

        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == R.id.action_delete) {

            if(sSearchRecentSuggestions != null) {

                DialogFragment dialog = new ConfirmationDialogFragment();
                dialog.show(getFragmentManager(), "Clear History");

            } else {
                Utils.showToast(getActivity(), "No history saved");
            }

        }
        return super.onOptionsItemSelected(item);
    }




    // Search the Spotify site for the submitted artist name
    private class SearchQueryTask extends AsyncTask<String, Void, List<Artist>> {


        // display the progress indicator while the search/download occurs
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            // clear the listview and display the progress spinner
            if(!mArtistsAdapter.isEmpty())
                mArtistsAdapter.clear();
            mProgressBar.setVisibility(View.VISIBLE);
        }

        // execute the search, download & parse the json results
        @Override
        protected List<Artist> doInBackground(String... params) {

           List<Artist> artists = null;

            if(params.length == 0) {
                return null;
            }

            // retrieve the number of results returned from SharedPreferences
            SharedPreferences prefs =
                PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
            String limit = prefs.getString(PREFS_RESULTS_RETURNED,
                                getActivity().getString(R.string.pref_results_returned_default));

            // execute the Artist query & download the results
            String jsonStringResult = Utils.downloadJSONSearchResults(params[0], limit);

            // parse the downloaded Json
            if (jsonStringResult != null)
                artists = Utils.parseJSONSearchResults(jsonStringResult);

            return artists;

        }


        @Override
        protected void onPostExecute(List<Artist> artists) {

            // hide the progressbar
            mProgressBar.setVisibility(View.GONE);

            if(artists != null) {

                // search returns an empty array
                if(artists.size() == 0) {
                    Utils.showToast(getActivity(), "No results found");

                } else {
                    // pass the results to the array adapter and update the view
                    // notifyDataSetChanged() called
                    mArtistsAdapter.updateView(artists);
                }

            } else {
                Utils.showToast(getActivity(), "Network error");
            }

        }
    }


    // download the artist's top-ten tracks
    private class ArtistQueryTask extends AsyncTask<Void , Void, List<Track>> {

        String artistName;
        String artistId;
        String countryCode;

        public ArtistQueryTask(String name, String id, String country) {
            artistName = name;
            artistId = id;
            countryCode = country;
        }


        @Override
        protected List<Track> doInBackground(Void... voids) {

            String jsonResults = Utils.downloadJSONArtistResults(artistId, countryCode);

            List<Track> trackList = null;
            if(jsonResults != null)
                trackList = Utils.parseJSONTrackResults(jsonResults, artistName, artistId);

            return trackList;
        }


        @Override
        protected void onPostExecute(List<Track> tracks) {

            if(tracks!= null) {

                if(tracks.size() == 0) {
                    Utils.showToast(getActivity(), "No results found");
                }
                else if(tracks.size() == 1 && tracks.get(0).getTrackTitle().equals("Unavailable country")) {
                    Utils.showToast(getActivity(), "Album unavailable in the selected country");
                }

                else {
                    Log.i(LOG_TAG, "Track number: " + tracks.size());

                    Intent intent = new Intent(getActivity(), TracksActivity.class);
                    intent.putParcelableArrayListExtra(EXTRA_TRACK_RESULTS,
                            (ArrayList<? extends Parcelable>) tracks);
                    startActivity(intent);

                }

            } else {
                Utils.showToast(getActivity(), "Network error");
            }

        }

    }



    public static class ConfirmationDialogFragment extends DialogFragment {

        public ConfirmationDialogFragment() {}

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage(R.string.confirmation_dialog_message)
                    .setPositiveButton(R.string.confirmation_dialog_positive_button,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    sSearchRecentSuggestions.clearHistory();
                                    sSearchRecentSuggestions = null;
                                    Utils.showToast(getActivity(), "Search history cleared");
                                }
                            })
                    .setNegativeButton(R.string.confirmation_dialog_negative_button,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Utils.showToast(getActivity(), "Action cancelled");
                                }
                            });

            return builder.create();
        }
    }


}
