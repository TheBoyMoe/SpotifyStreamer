package com.example.spotifystreamer.utils;


import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.spotifystreamer.R;
import com.example.spotifystreamer.model.Artist;

import java.util.ArrayList;
import java.util.List;

public class SearchQueryTask extends AsyncTask<String, Void, List<Artist>> {

    private static final String LOG_TAG = SearchQueryTask.class.getSimpleName();
    private final boolean L = true;
    private Context mContext;
    private ArrayAdapter<String> mAdapter;
    private ListView mListView;

    public SearchQueryTask(Context context) {
        mContext = context;
    }

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

        List<String> list = new ArrayList<>();

        if(artists != null) {
            for (Artist artist : artists) {
                Log.d(LOG_TAG, artist.toString() + " : " + artist.getUrl());
                list.add(artist.toString());
            }

            mAdapter = new ArrayAdapter<>(
                    mContext,
                    R.layout.list_view_item,
                    R.id.text_view_item,
                    list
            );

        } else {
            Utils.showToast(mContext, "Error executing search");
        }

    }
}
