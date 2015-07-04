package com.example.spotifystreamer.activities;

/**
 * Thanks to user henry_27571687391820 for Callbacks fix
 * http://discussions.udacity.com/t/asynctask-vs-callbacks/21223
 */

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
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
import com.example.spotifystreamer.model.MyTrack;
import com.example.spotifystreamer.utils.Utils;
import com.example.spotifystreamer.view.ArtistsArrayAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.ArtistsPager;
import kaaes.spotify.webapi.android.models.Image;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


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
    private List<MyTrack> mTracks;
    private SearchView mSearchView;
    private static SearchRecentSuggestions sSearchRecentSuggestions;
    private MenuItem mSearchMenuItem;
    private ProgressBar mProgressBar;
    private SpotifyApi mApi;
    private SpotifyService mSpotifyService;
    private String mCountry;
    private String mLimit;
    private Map<String, Object> mOptions;
    private OnArtistSelectedListener mCallback;


    public ArtistsFragment() { }


    // Callback Interface allowing communication between fragment and hosting activity
    public interface OnArtistSelectedListener {
        void onTrackSelected(String artistName, String artistId);
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // get a reference to the hosting activity, ensuring it implements the interface,
        // so that messages can be delivered to it.
        try {
            mCallback = (OnArtistSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() +
                    " must implement the OnTrackSelectedListener");
        }

    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // instantiate the various collections used
        mArtists = new ArrayList<>();
        mTracks = new ArrayList<>();
        mOptions = new HashMap<>();

        // instantiate the Spotify Service Wrapper
        mApi = new SpotifyApi();
        mSpotifyService = mApi.getService();

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


        // register item click listener - execute Top-Ten MyTrack download
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, final int position, long id) {

                // retrieve id & name of the particular artist &
                // use the callback to pass up to the activity
                Artist artist = mArtistsAdapter.getItem(position);
                String artistName = artist.getName();
                String artistId = artist.getId();

                mCallback.onTrackSelected(artistName, artistId);
                Log.d(LOG_TAG, "Clicked on position " + position
                        + ", artist name " + artistName + ", artistId " + artistId);


                // clear the ArrayList of previous results
                //mTracks.clear();

                // set the Search Options
//              mOptions.clear();
//                mOptions.put("country", mCountry);

//                // download the selected artist's top ten tracks
//                mSpotifyService.getArtistTopTrack(artistId, mOptions, new Callback<Tracks>() {
//                    @Override
//                    public void success(final Tracks tracks, Response response) {
//                        getActivity().runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                List<kaaes.spotify.webapi.android.models.MyTrack> list = tracks.tracks;
//                                if(L) Log.i(LOG_TAG, "Number of returned results: " + list.size());
//                                for (int i = 0; i < list.size(); i++) {
//                                    kaaes.spotify.webapi.android.models.MyTrack track = list.get(i);
//                                    String trackTitle = track.name;
//                                    String previewUrl = track.preview_url;
//                                    long trackDuration = track.duration_ms / 1000;
//                                    String imageUrl = null, thumbnailUrl = null;
//
//                                    // retrieve album title & album images from the SimpleAlbum object
//                                    AlbumSimple album = track.album;
//                                    String albumTitle = album.name;
//                                    List<Image> imageList = album.images;
//                                    for (int j = 0; j < imageList.size(); j++) {
//                                        Image img = imageList.get(j);
//                                        if (img.width >= 200 && img.width < 400) {
//                                            thumbnailUrl = img.url;
//                                            continue;
//                                        }
//                                        if (img.width >= 600) {
//                                            imageUrl = img.url;
//                                        }
//                                    }
//                                    // instantiate the app's MyTrack object
//                                    MyTrack retrievedTrack = new MyTrack(artistId, artistName, trackTitle,
//                                            albumTitle, imageUrl, thumbnailUrl, previewUrl, trackDuration);
//                                    if (L) Log.i(LOG_TAG, retrievedTrack.toString() + ", imageUrl: "
//                                            + imageUrl + ", thumbnailUrl: " + thumbnailUrl
//                                            + ", track duration: " + trackDuration);
//
//                                    mTracks.add(retrievedTrack);
//                                }
//
//                                // if one or more tracks were found, display the results in a new activity
//                                if(mTracks.size() > 0) {
//
//                                    // pass the tracks array list to the TracksActivity so it can display the results
//                                    Intent intent = new Intent(getActivity(), TracksActivity.class);
//                                    intent.putParcelableArrayListExtra(EXTRA_TRACK_RESULTS,
//                                            (ArrayList<? extends Parcelable>) mTracks);
//                                    startActivity(intent);
//
//                                    // set item checked to be highlighted
//                                    mListView.setItemChecked(position, true);
//
//                                } else {
//                                    Log.d(LOG_TAG, "No tracks found, array size: " + mTracks.size());
//                                    Utils.showToast(getActivity(), "MyTrack list not available");
//                                }
//
//                            }
//                        });
//                    }
//
//                    @Override
//                    public void failure(final RetrofitError error) {
//                        getActivity().runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                Log.d(LOG_TAG, "Error message: " + error.getUrl());
//                                Utils.showToast(getActivity(), "MyTrack list not available or invalid country code");
//                            }
//                        });
//                    }
//                });


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


    @Override
    public void onStart() {
        super.onStart();

        // retrieve user preferences from SharedPreferences
        SharedPreferences prefs =
                PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
        mCountry = prefs.getString(PREF_COUNTRY_KEY,
                getActivity().getString(R.string.pref_country_code_default));
        if(mCountry.isEmpty())
            mCountry = getActivity().getString(R.string.pref_country_code_default);
        mLimit = prefs.getString(PREFS_RESULTS_RETURNED,
                getActivity().getString(R.string.pref_results_returned_default));
    }


    // Add SearchView to the ToolBar
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.menu_search, menu);

        // instantiate the SearchView and set the searchable configuration
        SearchManager mgr = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        mSearchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        mSearchView.setSearchableInfo(mgr.getSearchableInfo(getActivity().getComponentName()));
        mSearchView.setSubmitButtonEnabled(true);
        mSearchView.setOnQueryTextListener(this);
        mSearchView.setQueryRefinementEnabled(true);

        // cache a reference to the SearchMenuItem
        mSearchMenuItem = menu.findItem(R.id.action_search);

    }


    // submit artist search through the Spotify Web API Wrapper
    @Override
    public boolean onQueryTextSubmit(final String query) {

        // hide softkeyboard upon submitting search
        Utils.hideKeyboard(getActivity(), mSearchView.getWindowToken());

        // close the search menu item
        mSearchMenuItem.collapseActionView();

        // save the search query to the RecentSuggestionsProvider
        sSearchRecentSuggestions = new SearchRecentSuggestions(getActivity(),
                QuerySuggestionProvider.AUTHORITY, QuerySuggestionProvider.MODE);
        sSearchRecentSuggestions.saveRecentQuery(query, null);

        // set Artist top-track options
        mOptions.clear();
        mOptions.put("limit", mLimit);
        mOptions.put("country", mCountry);

        // clear the listview and display the progress spinner
        if(!mArtistsAdapter.isEmpty())
            mArtistsAdapter.clear();
        mProgressBar.setVisibility(View.VISIBLE);

        // execute the search on a background thread
        mSpotifyService.searchArtists(query, mOptions, new Callback<ArtistsPager>() {

            @Override
            public void success(final ArtistsPager artistsPager, final Response response) {

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (L) Log.i(LOG_TAG, "Search for query: " + query
                                + ", returned records: " + artistsPager.artists.total);

                        if (artistsPager.artists.total > 0) {
                            // retrieve a list of artist objects
                            List<kaaes.spotify.webapi.android.models.Artist> artistList =
                                    artistsPager.artists.items;

                            for (int i = 0; i < artistList.size(); i++) {
                                kaaes.spotify.webapi.android.models.Artist artist = artistList.get(i);
                                String name = artist.name;
                                String id = artist.id;
                                if (L) {
                                    String str = String.format("Artist name: %s, id: %s", name, id);
                                    Log.i(LOG_TAG, str);
                                }

                                // retrieve an appropriately sized image for the artist thumbnail,
                                // between 200 and 400px in width, and cache it's url
                                String imageUrl = null;
                                List<Image> imageList = artist.images;
                                for (int j = 0; j < imageList.size(); j++) {
                                    Image img = imageList.get(j);
                                    if (img.width >= 200 && img.width < 400) {
                                        imageUrl = img.url;
                                    }
                                }

                                // instantiate app artist object and populate the Artist ArrayList
                                Artist retrievedArtist = new Artist(id, name, imageUrl);
                                mArtists.add(retrievedArtist);
                            }

                            //update ArtistAdapter and ListView
                            mArtistsAdapter.updateView(mArtists);

                        } else {
                            // No results found, http status code returned 200
                            Utils.showToast(getActivity(), "No results found for " + query);
                        }
                        // hide the progressbar
                        mProgressBar.setVisibility(View.GONE);
                    }
                });

            }

            @Override
            public void failure(final RetrofitError error) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (error.getResponse() != null) {
                            // server problems & 404 not found errors
                            Log.d(LOG_TAG, "Error executing artist search, status code : "
                                    + error.getResponse().getStatus() + ", error message: " + error.getMessage());
                            Utils.showToast(getActivity(), "No results found for " + query);
                        } else {
                            // failure due to network problem
                            Utils.showToast(getActivity(), "Network error, check connection");
                        }
                        // hide the progressbar
                        mProgressBar.setVisibility(View.GONE);
                    }
                });

            }

        });

        return true;
    }


    @Override
    public boolean onQueryTextChange(String newText) {
        // not used - req'd by SearchView implementation
        return false;
    }


    // Clear the search cache from the device
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


    // Search cache confirmation dialog
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
