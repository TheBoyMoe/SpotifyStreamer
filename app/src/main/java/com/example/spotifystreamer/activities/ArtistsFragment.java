package com.example.spotifystreamer.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

import com.example.spotifystreamer.R;
import com.example.spotifystreamer.model.Artist;
import com.example.spotifystreamer.utils.Utils;
import com.example.spotifystreamer.view.ArtistsArrayAdapter;

import java.util.ArrayList;
import java.util.List;


public class ArtistsFragment extends Fragment {

    private static final String LOG_TAG = ArtistsFragment.class.getSimpleName();
    private final boolean L = true;

    private final String EXTRA_ARTIST_ID = "artist id";
    private final String EXTRA_ARTIST_NAME = "artist_name";
    private final String PREFS_RESULTS_RETURNED = "pref_key_result_returned";
    //private final String BUNDLE_LISTVIEW_STATE = "saved list view state";

    private ListView mListView;
    private EditText mEditText;
    private ImageButton mButton;
    private ArtistsArrayAdapter mArtistsAdapter;
    private List<Artist> mArtists;


    public ArtistsFragment() { }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mArtists = new ArrayList<>();
        setRetainInstance(true); // ensure the fragment outlives device rotation
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_artists, container, false);

        // get references to view elements of interest
        mEditText = (EditText) view.findViewById(R.id.edit_text_search_query);
        mButton = (ImageButton) view.findViewById(R.id.button_launch_query);
        mListView = (ListView) view.findViewById(R.id.list_view_item__artist_container);


        // setOnClickListener on search button - retrieve the Artist query and execute the search
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String artistQuery = mEditText.getText().toString();

                if(artistQuery.equals("")) {
                    Log.d(LOG_TAG, "No query submitted");
                    Utils.showToast(getActivity(), "Enter search term(s)");
                } else {
                    // instantiate and invoke the AsyncTask to download the search results
                    new SearchQueryTask().execute(artistQuery);
                }

            }
        });


        // register item click listener
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

                // retrieve id & name of the particular artist & add as an extra to the intent
                Artist artist = mArtistsAdapter.getItem(position);
                String artistName = artist.getName();
                String artistId = artist.getId();

                Intent intent = new Intent(getActivity(), TracksActivity.class);
                intent.putExtra(EXTRA_ARTIST_NAME, artistName);
                intent.putExtra(EXTRA_ARTIST_ID, artistId);
                startActivity(intent);
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


}
