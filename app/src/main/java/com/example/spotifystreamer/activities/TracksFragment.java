package com.example.spotifystreamer.activities;


/**
 * Add the subtitle to the ToolBar - fix thanks to MrEngineer13
 * http://stackoverflow.com/questions/26998455/how-to-get-toolbar-from-fragment
 */

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.example.spotifystreamer.R;
import com.example.spotifystreamer.base.BaseFragment;
import com.example.spotifystreamer.model.MyTrack;
import com.example.spotifystreamer.model.TracksArrayAdapter;

import java.util.ArrayList;
import java.util.List;


public class TracksFragment extends BaseFragment {

    private static final String LOG_TAG = TracksFragment.class.getSimpleName();
    private static final String EXTRA_TRACK_RESULTS = "com.example.spotifystreamer.activities.tracks";
    private static final String EXTRA_TWO_PANE = "two_pane";
    private final String EXTRA_TRACK_SELECTION = "com.example.spotifystreamer.activities.selection";

    private TracksArrayAdapter mTracksAdapter;
    private ListView mListView;
    private List<MyTrack> mTrackList;
    private ProgressBar mProgressBar;
    private boolean mTwoPane;


    public TracksFragment() { }


    // newInstance() method instantiates a fragment with an added args bundle
    public static TracksFragment newInstance(List<MyTrack> tracks, boolean twoPane) {
        // create a bundle, add the photo object
        Bundle args = new Bundle();
        args.putParcelableArrayList(EXTRA_TRACK_RESULTS,
                (ArrayList<? extends Parcelable>) tracks);
        args.putBoolean(EXTRA_TWO_PANE, twoPane);

        // instantiate a new fragment and add the bundle
        TracksFragment fragment = new TracksFragment();
        fragment.setArguments(args);

        return fragment;
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mTrackList = new ArrayList<>();

        // retrieve and args passed to the fragment and populate the tracks array list
        Bundle args = getArguments();
        if(args != null) {
            mTrackList = args.getParcelableArrayList(EXTRA_TRACK_RESULTS);
            mTwoPane = args.getBoolean(EXTRA_TWO_PANE);
        }

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_content, container, false);

        // cache references to elements of interest
        mListView = (ListView) view.findViewById(R.id.list_view_item_container);
        mProgressBar = (ProgressBar) view.findViewById(R.id.progress_bar);
        mProgressBar.setVisibility(View.GONE);

        if(mTwoPane && mTrackList.size() > 0)
            view.setBackgroundResource(R.color.applicationAccent);

        if(mTrackList != null) {
            Log.d(LOG_TAG, "Track list size " + mTrackList.size());
            // add the Artist name as subtitle to the ToolBar
            ActionBar toolbar = ((AppCompatActivity) getActivity()).getSupportActionBar();
            if(toolbar != null && !mTrackList.isEmpty()) {
                if(!mTwoPane) {
                    toolbar.setDisplayHomeAsUpEnabled(true); // display home icon
                }
                toolbar.setTitle(R.string.top_ten_tracks);
                toolbar.setSubtitle(mTrackList.get(0).getArtistName());
            }

            // instantiate the ArrayAdapter and bind it to the ListView
            mTracksAdapter = new TracksArrayAdapter(getActivity(), mTrackList);
            mListView.setAdapter(mTracksAdapter);

        }


        // ListView setOnItemClickListener
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

                // Launch the Player Activity passing in the tracklist and position
                Intent intent = new Intent(getActivity(), PlayerActivity.class);
                intent.putParcelableArrayListExtra(EXTRA_TRACK_RESULTS,
                        (ArrayList<? extends Parcelable>) mTrackList);
                intent.putExtra(EXTRA_TRACK_SELECTION, position); // item clicked on
                startActivity(intent);

            }
        });

        return view;
    }

}
