package com.example.spotifystreamer.activities;

import android.app.Activity;
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
import com.example.spotifystreamer.model.Artist;
import com.example.spotifystreamer.model.ArtistsArrayAdapter;

import java.util.ArrayList;
import java.util.List;


/**
 * Implementation of the SearchView & saving RecentQuerySuggestions
 * https://developer.android.com/training/search/setup.html
 * http://antonioleiva.com/actionbarcompat-action-views/
 * http://developer.android.com/guide/topics/search/adding-recent-query-suggestions.html
 *
 * Thanks to user henry_27571687391820 for Callbacks fix
 * http://discussions.udacity.com/t/asynctask-vs-callbacks/21223
 *
 * Highlighting selected list items:
 * http://stackoverflow.com/questions/16189651/android-listview-selected-item-stay-highlighted
 *
 */

public class ArtistsFragment extends BaseFragment{

    private static final String LOG_TAG = ArtistsFragment.class.getSimpleName();
    private final boolean L = false;
    private static final String EXTRA_ARTIST_RESULTS = "com.example.spotifystreamer.activities.artists";
    private static final String EXTRA_TWO_PANE = "two_pane";

    private ListView mListView;
    private ArtistsArrayAdapter mArtistsAdapter;
    private List<Artist> mArtists;
    private ProgressBar mProgressBar;
    private OnArtistSelectedListener mCallback;
    private boolean mTwoPane;

    public ArtistsFragment() { }


    // newInstance() method instantiates a fragment with an added args bundle
    public static ArtistsFragment newInstance(List<Artist> artists, boolean twoPane) {
        // create a bundle, add the photo object
        Bundle args = new Bundle();
        args.putParcelableArrayList(EXTRA_ARTIST_RESULTS,
                (ArrayList<? extends Parcelable>) artists);
        args.putBoolean(EXTRA_TWO_PANE, twoPane);

        // instantiate a new fragment and add the bundle
        ArtistsFragment fragment = new ArtistsFragment();
        fragment.setArguments(args);

        return fragment;
    }


    // Callback Interface allowing communication between Artist and Tracks
    // fragments via the hosting activity - MainActivity
    public interface OnArtistSelectedListener {
        void onArtistSelected(String artistName, String artistId);
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
                    " must implement the OnArtistSelectedListener");
        }

    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // instantiate the various collections used
        mArtists = new ArrayList<>();

        // retrieve and args passed to the fragment and populate the tracks array list
        Bundle args = getArguments();
        if(args != null) {
            mArtists = args.getParcelableArrayList(EXTRA_ARTIST_RESULTS);
            mTwoPane = args.getBoolean(EXTRA_TWO_PANE);
        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_content, container, false);

        // cache references to view elements of interest
        mListView = (ListView) view.findViewById(R.id.list_view_item_container);
        mProgressBar = (ProgressBar) view.findViewById(R.id.progress_bar);
        mProgressBar.setVisibility(View.GONE);


        ActionBar toolbar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if(toolbar != null) {
            if(!mTwoPane) {
                // reverse changes implemented by Tracks fragment
                toolbar.setDisplayHomeAsUpEnabled(false); // display home icon
            }
            toolbar.setTitle(R.string.app_name);
            toolbar.setSubtitle("");
        }


        // register item click listener - execute Top-Ten MyTrack download
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, final int position, long id) {

//                if(mTwoPane)
//                    view.setSelected(true);

                // retrieve id & name of the particular artist &
                // use the callback to pass up to the activity
                Artist artist = mArtistsAdapter.getItem(position);
                String artistName = artist.getName();
                String artistId = artist.getId();

                mCallback.onArtistSelected(artistName, artistId);
                if(L) Log.d(LOG_TAG, "Clicked on position " + position
                        + ", " + artistName + ", artistId " + artistId);
            }
        });


        if(savedInstanceState == null) {
            // instantiate the ArrayAdapter and bind it to the ListView
            // when the fragment is first instantiated
            if(mArtists != null) {
                mArtistsAdapter = new ArtistsArrayAdapter(getActivity(), mArtists);
                mListView.setAdapter(mArtistsAdapter);
            }
        } else {
            // re-bind the adapter to the listview on device rotation
            mListView.setAdapter(mArtistsAdapter);
        }

        return view;
    }


}
