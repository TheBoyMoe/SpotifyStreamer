package com.example.spotifystreamer.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
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


public class MainActivity extends BaseActivity
        implements ArtistsFragment.OnArtistSelectedListener{

    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private final boolean L = true;

    private final String EXTRA_TRACK_RESULTS = "com.example.spotifystreamer.activities.tracks";
    private final String PREF_COUNTRY_KEY = "pref_key_country_code";
    private SpotifyApi mApi;
    private SpotifyService mSpotifyService;
    private String mCountry;
    private Map<String, Object> mOptions;
    private List<MyTrack> mTracks;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // determine whether the phone is a phone or tablet
        if(findViewById(R.id.container) != null) {
            // must be a phone
            if(savedInstanceState != null) return; // previous state is being restored

            // first time in, instantiate the fragment
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new ArtistsFragment())
                    .commit();

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

    }


    // execute the Top Ten MyTrack download for the selected artist
    @Override
    public void onTrackSelected(final String artistName, final String artistId) {

        // clear any results in case user presses the back button
        mTracks.clear();

        mSpotifyService.getArtistTopTrack(artistId, mOptions, new Callback<Tracks>() {
            @Override
            public void success(final Tracks tracks, Response response) {
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

                            // pass the tracks array list to the TracksActivity so it can display the results
                            Intent intent = new Intent(MainActivity.this, TracksActivity.class);
                            intent.putParcelableArrayListExtra(EXTRA_TRACK_RESULTS,
                                    (ArrayList<? extends Parcelable>) mTracks);
                            startActivity(intent);

                        } else {
                            Log.d(LOG_TAG, "No tracks found, array size: " + mTracks.size());
                            Utils.showToast(MainActivity.this, "MyTrack list not available");
                        }

                    }
                });
            }


            // Log an error and display a message to the user
            @Override
            public void failure(final RetrofitError error) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.d(LOG_TAG, "Error message: " + error.getUrl());
                        Utils.showToast(MainActivity.this,
                                "MyTrack list not available or invalid country code");
                    }
                });
            }


        });


    }





}
