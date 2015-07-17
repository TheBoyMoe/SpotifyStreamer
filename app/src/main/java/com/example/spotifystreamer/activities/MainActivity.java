package com.example.spotifystreamer.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;

import com.example.spotifystreamer.R;
import com.example.spotifystreamer.model.MyTrack;
import com.example.spotifystreamer.utils.BaseActivity;
import com.example.spotifystreamer.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.AlbumSimple;
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
        TracksFragment.OnTrackSelectedListener{

    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private final boolean L = true;
    private boolean mTwoPane;

    private final String EXTRA_TRACK_RESULTS = "com.example.spotifystreamer.activities.tracks";
    private final String EXTRA_TRACK_SELECTION = "com.example.spotifystreamer.activities.selection";
    private final String EXTRA_TWO_PANE = "two_pane";
    private final String PREF_COUNTRY_KEY = "pref_key_country_code";
    private SpotifyApi mApi;
    private SpotifyService mSpotifyService;
    private String mCountry;
    private Map<String, Object> mOptions;
    private List<MyTrack> mTracks;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        if(savedInstanceState == null) {

            // determine if this is a phone/tablet
            if(findViewById(R.id.tracks_fragment_container) != null) {
                // two pane layout - tablet
                Log.d(LOG_TAG, "On a tablet!");
                mTwoPane = true;
                // instantiate the tracks fragment and add it
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.tracks_fragment_container, new TracksFragment())
                        .commit();

            } else {
                // must be a phone
                Log.d(LOG_TAG, "On the phone!");
                mTwoPane = false;

                // first time in, instantiate the fragment
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.fragment_container, new ArtistsFragment())
                        .commit();
            }
        }


        // retrieve the country value saved to SharedPreferences
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        mCountry = prefs.getString(PREF_COUNTRY_KEY, getString(R.string.pref_country_code_default));
        if(mCountry.isEmpty())
            mCountry = getString(R.string.pref_country_code_default);

        mTracks = new ArrayList<>();
        mOptions = new HashMap<>();
        mOptions.put("country", mCountry);

        // instantiate the Spotify Service
        mApi = new SpotifyApi();
        mSpotifyService = mApi.getService();


        // restore state saved on device rotation
        if(savedInstanceState != null)  {
            mTwoPane = savedInstanceState.getBoolean(EXTRA_TWO_PANE);
            Log.d(LOG_TAG, "Restore twoPane value: " + mTwoPane);
        }

    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putBoolean(EXTRA_TWO_PANE, mTwoPane);
    }

    // execute the Top Ten MyTrack download for the selected artist
    // Implements the OnArtistSelectedListener of the Artists Fragment
    @Override
    public void onArtistSelected(final String artistName, final String artistId) {

        // clear any results in case user presses the back button
        if(mTracks != null)
            mTracks.clear();

        Log.d(LOG_TAG, "Spotify Service: " + mSpotifyService);
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
                            Log.d(LOG_TAG, "No tracks found, array size: " + mTracks.size());
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
                                "Track list not available or invalid country code");
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
            // update the tracks fragment
            ft.replace(R.id.tracks_fragment_container, newTracksFragment);

        } else {

            // you're on a phone, swap the artists fragment
            // with the tracks fragment
            ft.replace(R.id.fragment_container, newTracksFragment);
        }
        // add the fragment to the BackStack so it's not destroyed & commit the transaction
        ft.addToBackStack(null);
        ft.commit();
    }


    // launch the PlayerActivity to play the selected track
    @Override
    public void onTrackSelected(ArrayList<MyTrack> tracks, int position) {

        if(mTwoPane) {
            // launch the player in a fragment and swap it for the current tracks fragment
            PlayerFragment newPlayerFragment = PlayerFragment.newInstance(tracks, position);
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.tracks_fragment_container, newPlayerFragment);
            ft.addToBackStack(null);
            ft.commit();

        } else {
            // launch the Player in a new activity on phones
            Intent intent = new Intent(MainActivity.this, PlayerActivity.class);
            intent.putParcelableArrayListExtra(EXTRA_TRACK_RESULTS, tracks);
            intent.putExtra(EXTRA_TRACK_SELECTION, position); // item clicked on
            startActivity(intent);
        }



    }






}
