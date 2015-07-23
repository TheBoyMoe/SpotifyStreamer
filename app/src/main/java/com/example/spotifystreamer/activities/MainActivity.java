package com.example.spotifystreamer.activities;

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
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.example.spotifystreamer.R;
import com.example.spotifystreamer.base.BaseActivity;
import com.example.spotifystreamer.model.Artist;
import com.example.spotifystreamer.model.MyTrack;
import com.example.spotifystreamer.model.QuerySuggestionProvider;
import com.example.spotifystreamer.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.AlbumSimple;
import kaaes.spotify.webapi.android.models.ArtistsPager;
import kaaes.spotify.webapi.android.models.Image;
import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.Tracks;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Single/TwoPane layout based on:
 * http://developer.android.com/guide/components/fragments.html
 * http://developer.android.com/training/basics/fragments/index.html
 * http://android-developers.blogspot.co.uk/2011/07/new-tools-for-managing-screen-sizes.html
 */
public class MainActivity extends BaseActivity
        implements ArtistsFragment.OnArtistSelectedListener,
                   SearchView.OnQueryTextListener{

    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private final boolean L = false;

    private final String EXTRA_TWO_PANE = "two_pane";
    private final String PREF_COUNTRY_KEY = "pref_key_country_code";
    private final String PREFS_RESULTS_RETURNED = "pref_key_result_returned";

    private SearchView mSearchView;
    private static SearchRecentSuggestions sSearchRecentSuggestions;
    private MenuItem mSearchMenuItem;
    private SpotifyApi mApi;
    private SpotifyService mSpotifyService;
    private String mCountry;
    private Map<String, Object> mOptions;
    private List<MyTrack> mTracks;
    private List<Artist> mArtists;
    private boolean mTwoPane;
    private String mLimit;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // if the activity is being launched for the first time
        if(savedInstanceState == null) {

            // determine if this is a phone/tablet
            if(findViewById(R.id.tracks_fragment_container) != null) {
                // two pane layout - tablet
                Log.d(LOG_TAG, "On a tablet!");
                mTwoPane = true;
                // instantiate the tracks fragment and add it
//                getSupportFragmentManager().beginTransaction()
//                        .add(R.id.fragment_container, new ArtistsFragment())
//                        .add(R.id.tracks_fragment_container, new TracksFragment())
//                        .commit();

            } else {
                // must be a phone
                Log.d(LOG_TAG, "On the phone!");
                mTwoPane = false;

                // first time in, instantiate the fragment
//                getSupportFragmentManager().beginTransaction()
//                        .add(R.id.fragment_container, new ArtistsFragment())
//                        .commit();
            }
        }

        mArtists = new ArrayList<>();
        mTracks = new ArrayList<>();
        mOptions = new HashMap<>();

        // instantiate the Spotify Service
        mApi = new SpotifyApi();
        mSpotifyService = mApi.getService();

        // restore state saved on device rotation
        if(savedInstanceState != null)  {
            mTwoPane = savedInstanceState.getBoolean(EXTRA_TWO_PANE);
            if(L) Log.i(LOG_TAG, "Restore twoPane value: " + mTwoPane);
        }
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putBoolean(EXTRA_TWO_PANE, mTwoPane);
    }


    @Override
    protected void onResume() {
        super.onResume();

        // // retrieve user preferences from SharedPreferences
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        mCountry = prefs.getString(PREF_COUNTRY_KEY, getString(R.string.pref_country_code_default));
        if(mCountry.isEmpty())
            mCountry = getString(R.string.pref_country_code_default);
        mLimit = prefs.getString(PREFS_RESULTS_RETURNED,
                getString(R.string.pref_results_returned_default));

        // add the retrieved saved preferences to Retrofit's config HashMap object
        mOptions.put("country", mCountry);
        mOptions.put("limit", mLimit);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        // instantiate the SearchView and set the searchable configuration
        SearchManager mgr = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        mSearchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        mSearchView.setSearchableInfo(mgr.getSearchableInfo(getComponentName()));
        mSearchView.setSubmitButtonEnabled(true);
        mSearchView.setOnQueryTextListener(this);
        mSearchView.setQueryRefinementEnabled(true);

        // cache a reference to the SearchMenuItem
        mSearchMenuItem = menu.findItem(R.id.action_search);

        return true;
    }



    ///////////////////////////////////////////////////////////////////////////////////////
    // Business Logic - deals with artist search and track download                     ///
    ///////////////////////////////////////////////////////////////////////////////////////


    // submit artist search through the Spotify Web API Wrapper
    @Override
    public boolean onQueryTextSubmit(final String query) {

        if(L) Log.d(LOG_TAG, "Search query: "  + query);
        // hide softkeyboard upon submitting search
        Utils.hideKeyboard(MainActivity.this, mSearchView.getWindowToken());

        // close the search menu item
        mSearchMenuItem.collapseActionView();

        // clear the artist array list, so you're not continuously adding to it
        mArtists.clear();

        // save the search query to the RecentSuggestionsProvider
        sSearchRecentSuggestions = new SearchRecentSuggestions(MainActivity.this,
                QuerySuggestionProvider.AUTHORITY, QuerySuggestionProvider.MODE);
        sSearchRecentSuggestions.saveRecentQuery(query, null);

        // execute the search on a background thread
        mSpotifyService.searchArtists(query, mOptions, new Callback<ArtistsPager>() {

            @Override
            public void success(final ArtistsPager artistsPager, final Response response) {

                runOnUiThread(new Runnable() {
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
                                    String str = String.format("Artist : %s, id: %s", name, id);
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

                            // instantiate the ArtistsFragment
                            addArtistFragment();

                        } else {
                            // No results found, http status code returned 200
                            Utils.showToast(MainActivity.this, "No results found for " + query);
                        }
                    }
                });
            }

            @Override
            public void failure(final RetrofitError error) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (error.getResponse() != null) {
                            // server problems & 404 not found errors
                            Log.d(LOG_TAG, "Error executing artist search, status code : "
                                    + error.getResponse().getStatus() + ", error message: " + error.getMessage());
                            Utils.showToast(MainActivity.this, "No results found for " + query);
                        } else {
                            // failure due to network problem
                            Utils.showToast(MainActivity.this, "Network error, check connection");
                        }
                        // hide the progressbar
                        //mProgressBar.setVisibility(View.GONE);
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
                dialog.show(getSupportFragmentManager(), "Clear History");
            } else {
                Utils.showToast(MainActivity.this, "No history saved");
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



    // execute the Top Ten MyTrack download for the selected artist
    // Implements the OnArtistSelectedListener of the Artists Fragment
    @Override
    public void onArtistSelected(final String artistName, final String artistId) {

        // clear any results in case user presses the back button
        if(mTracks != null)
            mTracks.clear();

        // download the artists top ten tracks using SpotifyWebWrapper in a bkgd thread
        mSpotifyService.getArtistTopTrack(artistId, mOptions, new Callback<Tracks>() {

            @Override
            public void success(final Tracks tracks, Response response) {

                // use runOnUiThread() to display the artists list on the UI thread
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        List<Track> list = tracks.tracks;
                        if (L) Log.d(LOG_TAG, "Number of returned results: " + list.size());
                        for (int i = 0; i < list.size(); i++) {
                            kaaes.spotify.webapi.android.models.Track track = list.get(i);
                            String trackTitle = track.name;
                            String previewUrl = track.preview_url;
                            long trackDuration = track.duration_ms / 1000;
                            String imageUrl = null, thumbnailUrl = null;

                            // retrieve album title & album images from the SimpleAlbum object
                            AlbumSimple album = track.album;
                            String albumTitle = album.name;
                            List<Image> imageList = album.images;
                            for (int j = 0; j < imageList.size(); j++) {
                                Image img = imageList.get(j);
                                if (img.width >= 200 && img.width < 400) {
                                    thumbnailUrl = img.url;
                                    continue;
                                }
                                if (img.width >= 600) {
                                    imageUrl = img.url;
                                }
                            }
                            // instantiate the app's MyTrack object
                            MyTrack retrievedTrack =
                                    new MyTrack(artistId, artistName, trackTitle,
                                            albumTitle, imageUrl, thumbnailUrl, previewUrl, trackDuration);
                            if (L) Log.i(LOG_TAG, retrievedTrack.toString() + ", imageUrl: "
                                    + imageUrl + ", thumbnailUrl: " + thumbnailUrl
                                    + ", track duration: " + trackDuration);

                            mTracks.add(retrievedTrack);
                        }

                        // if one or more tracks were found, display the results in a new activity
                        if (mTracks.size() > 0) {
                            updateTracksFragment();
                        } else {
                            if(L) Log.i(LOG_TAG, "No tracks found, array size: " + mTracks.size());
                            Utils.showToast(MainActivity.this, "Track list not available");
                        }
                    }
                });
            }


            @Override
            public void failure(final RetrofitError error) {
                runOnUiThread(new Runnable() {
                    // Log an error and display a message to the user
                    @Override
                    public void run() {
                        Log.d(LOG_TAG, "Error message: " + error.getUrl());
                        Utils.showToast(MainActivity.this,
                                "Track list not available for selected country");
                    }
                });
            }

        });

    }


    // Either swap the Artists Fragment out or update the Tracks fragment
    // depending on whether we're on a phone or tablet
    private void updateTracksFragment() {

        // instantiate a new TracksFragment containing the tracks bundle
        TracksFragment newTracksFragment = TracksFragment.newInstance(mTracks);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

         // If we're on the tablet
        if(mTwoPane) {
            // update the tracks fragment, not using the backstack on the tablet
            ft.replace(R.id.tracks_fragment_container, newTracksFragment);

        } else {

            // you're on a phone, swap the artists fragment
            // with the tracks fragment, adding the fragment to the backstack
            ft.replace(R.id.fragment_container, newTracksFragment);
            ft.addToBackStack(null);
        }
        // add the fragment to the BackStack so it's not destroyed & commit the transaction
        //ft.addToBackStack(null);
        ft.commit();
    }


    // instantiate the Artist fragment
    private void addArtistFragment() {
        // instantiate a new ArtistsFragment passing in the artists list
        Log.d(LOG_TAG, "Artist list: " + mArtists);
        ArtistsFragment artistsFragment = ArtistsFragment.newInstance(mArtists);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_container, artistsFragment);
        ft.commit();
    }


}
