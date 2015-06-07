package com.example.spotifystreamer.activities;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

import com.example.spotifystreamer.R;
import com.example.spotifystreamer.model.Artist;
import com.example.spotifystreamer.utils.ArtistsArrayAdapter;
import com.example.spotifystreamer.utils.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    private static final String LOG_TAG = MainActivityFragment.class.getSimpleName();

    private List<String> mList;
    private ArrayAdapter<String> mAdapter; // basic adapter
    private ListView mListView;
    private EditText mEditText;
    private ImageButton mButton;


    public MainActivityFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        // get references to view elements of interest
        mEditText = (EditText) view.findViewById(R.id.edit_text_search_query);
        mButton = (ImageButton) view.findViewById(R.id.button_launch_query);
        mListView = (ListView) view.findViewById(R.id.list_view_item_container);


        // setOnClickListener, retrieve the Artist query and execute the search
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String artistQuery = mEditText.getText().toString();

                // instantiate and invoke the AsyncTask to download the search results
                SearchQueryTask queryTask = new SearchQueryTask();
                queryTask.execute(artistQuery);

            }
        });

        // fake data - test listview
        String[] dataArray = {
            "Sondre Lerche - Faces Down 2002",
            "Frank Sinatra - 20 Golden Greats 1999",
            "Prince - Diamonds and Pearls 1992",
            "Led Zeppelin - Houses of the Holy 1974",
            "Rolling Stones - 40 Licks 2006",
            "Neil Diamond -  Home Before Dark 2013"
        };

        // convert the array into an array list
        mList = new ArrayList<>(Arrays.asList(dataArray));

        // populate the listview & bind it to the ListView - using basic adapter
        mAdapter = new ArrayAdapter<>(
                getActivity(),
                R.layout.list_view_item,
                R.id.text_view_item,
                mList
        );

        mListView.setAdapter(mAdapter);

        return view;
    }



    private class SearchQueryTask extends AsyncTask<String, Void, List<Artist>> {

        // execute the search, download & parse the json results
        @Override
        protected List<Artist> doInBackground(String... params) {

            if(params.length == 0 || params[0].equals("")) {
                Log.d(LOG_TAG, "No query submitted");
                return null;
            }

            // execute the Artist query & download the results
            String jsonStringResult = Utils.downloadJSONSearchResults(params[0]);

            List<Artist> artists = null;

            // parse the downloaded Json
            if (jsonStringResult != null)
                artists = Utils.parseJSONSearchResults(jsonStringResult);

            return artists;
        }


        @Override
        protected void onPostExecute(List<Artist> artists) {

            if(artists != null) {

//                mAdapter.clear(); // clear the adapter of all the previous entries
//                for (Artist artist : artists) {
//                    Log.d(LOG_TAG, artist.toString() + " : " + artist.getUrl());
//
//                    // re-populate the adapter - automatically calls notifyDatasetChanged()
//                    mAdapter.add(artist.toString());
//                }

                ArtistsArrayAdapter artistAdapter = new ArtistsArrayAdapter(getActivity(), artists);
                mListView.setAdapter(artistAdapter);


            } else {
                Utils.showToast(getActivity(), "Error executing search");
            }


        }
    }





}
