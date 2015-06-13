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

    // private final String EXTRA_ARTIST_ID = "artist id";
    // private final String EXTRA_ARTIST_NAME = "artist_name";

    private final String EXTRA_TRACK_RESULTS = "com.example.spotifystreamer.activities.tracks";
    private final String PREFS_RESULTS_RETURNED = "pref_key_result_returned";
    private final String PREF_COUNTRY_KEY = "pref_key_country_code";
    //private final String BUNDLE_LISTVIEW_STATE = "saved list view state";

    private ListView mListView;
    // private EditText mEditText;
    // private ImageButton mButton;
    private ArtistsArrayAdapter mArtistsAdapter;
    private List<Artist> mArtists;
    private SearchView mSearchView;
    private static SearchRecentSuggestions sSearchRecentSuggestions;
    private MenuItem mSearchMenuItem;

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
        View view = inflater.inflate(R.layout.fragment_artists, container, false);

        // get references to view elements of interest
        // mEditText = (EditText) view.findViewById(R.id.edit_text_search_query);
        // mButton = (ImageButton) view.findViewById(R.id.button_launch_query);
        mListView = (ListView) view.findViewById(R.id.list_view_item__artist_container);


        // setOnClickListener on search button - retrieve the Artist query and execute the search
//        mButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                Utils.hideKeyboard(getActivity(), mEditText.getWindowToken());
//
//                String artistQuery = mEditText.getText().toString();
//
//                if(artistQuery.equals("")) {
//                    Log.d(LOG_TAG, "No query submitted");
//                    Utils.showToast(getActivity(), "Enter search term(s)");
//                } else {
//                    // instantiate and invoke the AsyncTask to download the search results
//                    new SearchQueryTask().execute(artistQuery);
//                }
//
//            }
//        });


        // register item click listener - execute Top-Ten Track download
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

                // retrieve id & name of the particular artist & add as an extra to the intent
                Artist artist = mArtistsAdapter.getItem(position);
                String artistName = artist.getName();
                String artistId = artist.getId();

//                Intent intent = new Intent(getActivity(), TracksActivity.class);
//                intent.putExtra(EXTRA_ARTIST_NAME, artistName);
//                intent.putExtra(EXTRA_ARTIST_ID, artistId);
//                startActivity(intent);

                // retrieve user preferences from SharedPreferences
                SharedPreferences prefs =
                        PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
                String country = prefs.getString(PREF_COUNTRY_KEY, getActivity().getString(R.string.pref_country_code_default));

                // execute track download
                new ArtistQueryTask(artistName, artistId, country).execute();

            }
        });


        // retrieve the saved data from the saved state
//        if(savedInstanceState != null) {
//            if(L) Log.i(LOG_TAG, "Reading back the bundle");
//            Artist[] values = (Artist[]) savedInstanceState.getParcelableArray(BUNDLE_LISTVIEW_STATE);
//            Log.i(LOG_TAG, "Values length: " + values.length);
//            mArtistsAdapter.clear(); // prevents duplication of results
//            for (int i = 0; i < values.length; i++) {
//                Log.i(LOG_TAG, "Artist: " + values[i].toString());
//                mArtistsAdapter.add(values[i]);
//            }
//
//        }


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



      // save the array adapter objects to the bundle
//    @Override
//    public void onSaveInstanceState(Bundle outState) {
//        super.onSaveInstanceState(outState);
//
//        if(L) Log.i(LOG_TAG, "onSavedInstanceState called");
//        // save the arraylist of artist objects to the bundle
//        Artist[] values = new Artist[mArtistsAdapter.getCount()];
//        //values = mArtists.toArray(values);
//        if(L) Log.i(LOG_TAG, "Values length: " + values.length);
//        for (int i = 0; i < values.length; i++) {
//            values[i] = mArtistsAdapter.getItem(i);
//            if(L) Log.i(LOG_TAG, "Artist: " + values[i].toString());
//        }
//        outState.putParcelableArray(BUNDLE_LISTVIEW_STATE, values);
//
//    }


    // handle search query's submitted via the SearchView
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.menu_search, menu);

        // get the SearchView and set the searchable configuration
        SearchManager mgr = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        mSearchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        mSearchView.setSearchableInfo(mgr.getSearchableInfo(getActivity().getComponentName()));
        //searchView.setIconifiedByDefault(false);
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

//        if(query.equals("")) {
//            Log.d(LOG_TAG, "No query submitted");
//            Utils.showToast(getActivity(), "Enter search term(s)");
//        } else {
//            // instantiate and invoke the AsyncTask to download the search results
//            new SearchQueryTask().execute(query);
//        }

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
//                ConfirmationDialogFragment dialog =
//                        ConfirmationDialogFragment.newInstance(sSearchRecentSuggestions);
//                dialog.show(getFragmentManager(), "Clear History");

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

            // mArtistsAdapter.clear();

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

                    //Bundle bundle = new Bundle();
                    //bundle.putParcelableArrayList(EXTRA_TRACK_RESULTS, (ArrayList<? extends Parcelable>) tracks);

                    Intent intent = new Intent(getActivity(), TracksActivity.class);
                    //intent.putExtras(bundle);
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
