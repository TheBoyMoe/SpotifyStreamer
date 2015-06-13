package com.example.spotifystreamer.activities;


/**
 * Add the subtitle to the ToolBar - fix thanks to MrEngineer13
 * http://stackoverflow.com/questions/26998455/how-to-get-toolbar-from-fragment
 */

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.example.spotifystreamer.R;
import com.example.spotifystreamer.model.Track;
import com.example.spotifystreamer.utils.Utils;
import com.example.spotifystreamer.view.TracksArrayAdapter;

import java.util.ArrayList;
import java.util.List;


public class TracksFragment extends Fragment {

    private static final String LOG_TAG = TracksFragment.class.getSimpleName();
    // private final String EXTRA_ARTIST_ID = "artist id";
    // private final String EXTRA_ARTIST_NAME = "artist_name";
    private final String EXTRA_TRACK_RESULTS = "com.example.spotifystreamer.activities.tracks";
    // private final String PREF_COUNTRY_KEY = "pref_key_country_code";


    private TracksArrayAdapter mTracksAdapter;
    // private String mCountry;
    // private String mArtistName;
    // private String mArtistId;
    private ListView mListView;
    private List<Track> mTrackList;
    private ProgressBar mProgressBar;


    public TracksFragment() { }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mTrackList = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_content, container, false);

        // cache references to elements of interest
        mListView = (ListView) view.findViewById(R.id.list_view_item_container);
        mProgressBar = (ProgressBar) view.findViewById(R.id.progress_bar);
        mProgressBar.setVisibility(View.GONE);

        // retrieve the intent extra
        Intent intent = getActivity().getIntent();
        mTrackList = intent.getParcelableArrayListExtra(EXTRA_TRACK_RESULTS);
        if(mTrackList != null) {
            // add the Artist name as subtitle to the ToolBar
            ((AppCompatActivity)getActivity()).getSupportActionBar()
                    .setSubtitle(mTrackList.get(0).getArtistName());

            // instantiate the ArrayAdapter and bind it to the ListView
            mTracksAdapter = new TracksArrayAdapter(getActivity(), mTrackList);
            mListView.setAdapter(mTracksAdapter);

        }

//        // retrieve user preferences from SharedPreferences
//        SharedPreferences prefs =
//            PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
//        mCountry = prefs.getString(PREF_COUNTRY_KEY, getActivity().getString(R.string.pref_country_code_default));

        // execute Artist top-track download
        //new ArtistQueryTask().execute();


        // ListView setOnItemClickListener
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Track track = mTracksAdapter.getItem(position);
                String trackTitle = track.getTrackTitle();
                String albumTitle = track.getAlbumTitle();
                Utils.showToast(getActivity(), trackTitle + " from " + albumTitle);
            }
        });

        return view;
    }


//    private class ArtistQueryTask extends AsyncTask<Void , Void, List<Track>> {
//
//        @Override
//        protected List<Track> doInBackground(Void... voids) {
//
//            String jsonResults = Utils.downloadJSONArtistResults(mArtistId, mCountry);
//
//            List<Track> trackList = null;
//            if(jsonResults != null)
//                trackList = Utils.parseJSONTrackResults(jsonResults, mArtistName, mArtistId);
//
//            return trackList;
//        }
//
//
//        @Override
//        protected void onPostExecute(List<Track> tracks) {
//
//            if(tracks!= null) {
//
//                if(tracks.size() == 0) {
//                    Utils.showToast(getActivity(), "No results found");
//                }
//                else if(tracks.size() == 1 && tracks.get(0).getTrackTitle().equals("Unavailable country")) {
//                    Utils.showToast(getActivity(), "Album unavailable in the selected country");
//                    //getActivity().finish();
//                }
//
//                else
//                    mTracksAdapter.updateView(tracks);
//
//            } else {
//                Utils.showToast(getActivity(), "Network error");
//            }
//
//        }
//
//    }



}
