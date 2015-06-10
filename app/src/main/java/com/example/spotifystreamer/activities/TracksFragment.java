package com.example.spotifystreamer.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.spotifystreamer.R;
import com.example.spotifystreamer.model.Track;
import com.example.spotifystreamer.view.TracksArrayAdapter;
import com.example.spotifystreamer.utils.Utils;

import java.util.ArrayList;
import java.util.List;


public class TracksFragment extends Fragment {

    private static final String LOG_TAG = TracksFragment.class.getSimpleName();
    private final boolean L = false;
    private final String EXTRA_ARTIST_ID = "artist id";
    private final String EXTRA_ARTIST_NAME = "artist_name";

    private TracksArrayAdapter mTracksAdapter;
    private String mCountry;
    private String mArtistName;
    private String mArtistId;
    private ListView mListView;
    private List<Track> mTrackList;


    public TracksFragment() { }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mTrackList = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tracks, container, false);

        mListView = (ListView) view.findViewById(R.id.list_view_item_track_container);

        // retrieve the intent extras
        Intent intent = getActivity().getIntent();
        mArtistId = intent.getStringExtra(EXTRA_ARTIST_ID);
        mArtistName = intent.getStringExtra(EXTRA_ARTIST_NAME);
        getActivity().setTitle(mArtistName);

        if(L) Log.i(LOG_TAG, "Artist name: " + mArtistName + ", artist id: " + mArtistId);

        mCountry = "GB";

        // execute Artist top-track download
        new ArtistQueryTask().execute();

        // instantiate the ArrayAdapter and bind it to the ListView
        mTracksAdapter = new TracksArrayAdapter(getActivity(), mTrackList);
        mListView.setAdapter(mTracksAdapter);

        return view;
    }


    private class ArtistQueryTask extends AsyncTask<Void , Void, List<Track>> {

        @Override
        protected List<Track> doInBackground(Void... voids) {

            String jsonResults = Utils.downloadJSONArtistResults(mArtistId, mCountry);

            List<Track> trackList = null;
            if(jsonResults != null)
                trackList = Utils.parseJSONTrackResults(jsonResults, mArtistName, mArtistId);

            return trackList;
        }


        @Override
        protected void onPostExecute(List<Track> tracks) {

            if(tracks!= null) {

                if(tracks.size() == 0)
                    Utils.showToast(getActivity(), "No results found");
                else
                    mTracksAdapter.updateView(tracks);

            } else {
                Utils.showToast(getActivity(), "Network error");
            }

        }

    }



}
