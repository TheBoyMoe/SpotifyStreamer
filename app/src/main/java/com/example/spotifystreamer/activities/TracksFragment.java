package com.example.spotifystreamer.activities;


/**
 * Add the subtitle to the ToolBar - fix thanks to MrEngineer13
 * http://stackoverflow.com/questions/26998455/how-to-get-toolbar-from-fragment
 */

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
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
    private final String EXTRA_TRACK_RESULTS = "com.example.spotifystreamer.activities.tracks";
    private final String EXTRA_TRACK_SELECTION = "com.example.spotifystreamer.activities.selection";
    private final String PREF_COUNTRY_KEY = "pref_key_country_code";

    private TracksArrayAdapter mTracksAdapter;
    private ListView mListView;
    private List<Track> mTrackList;
    private ProgressBar mProgressBar;
    private String mCountry;

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

        // retrieve country listing from shared preferences
        SharedPreferences prefs =
                PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
        mCountry = prefs.getString(PREF_COUNTRY_KEY,
                getActivity().getString(R.string.pref_country_code_default));
        if(mCountry.equals(""))
            mCountry = getActivity().getString(R.string.pref_country_code_default);


        // retrieve the intent extra
        Intent intent = getActivity().getIntent();
        mTrackList = intent.getParcelableArrayListExtra(EXTRA_TRACK_RESULTS);
        if(mTrackList != null) {

            // add the Artist name as subtitle to the ToolBar
            ActionBar toolbar = ((AppCompatActivity) getActivity()).getSupportActionBar();
            if(toolbar != null)
                toolbar.setSubtitle(mTrackList.get(0).getArtistName());

            // instantiate the ArrayAdapter and bind it to the ListView
            mTracksAdapter = new TracksArrayAdapter(getActivity(), mTrackList);
            mListView.setAdapter(mTracksAdapter);

            Utils.showToast(getActivity(), "Track list for " + mCountry.toUpperCase());

        }


        // ListView setOnItemClickListener
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                //Track track = mTracksAdapter.getItem(position);
                Intent intent = new Intent(getActivity(), TrackPlayerActivity.class);
                intent.putParcelableArrayListExtra(EXTRA_TRACK_RESULTS,
                        (ArrayList<? extends Parcelable>) mTrackList);
                intent.putExtra(EXTRA_TRACK_SELECTION, position); // item clicked on
                //intent.putExtra(EXTRA_TRACK_RESULTS, track);
                startActivity(intent);

            }
        });

        return view;
    }


    @Override
    public void onStart() {
        super.onStart();


    }

}
