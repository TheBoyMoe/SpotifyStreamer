package com.example.spotifystreamer.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
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
import com.example.spotifystreamer.utils.ArtistsArrayAdapter;
import com.example.spotifystreamer.utils.Utils;

import java.util.ArrayList;
import java.util.List;


/**
 * A placeholder fragment containing a simple view.
 */
public class ArtistsFragment extends Fragment {

    private static final String LOG_TAG = ArtistsFragment.class.getSimpleName();
    private final boolean L = true;

    private final String EXTRA_ARTIST_ID = "artist id";
    private final String EXTRA_ARTIST_NAME = "artist_name";

    private ListView mListView;
    private EditText mEditText;
    private ImageButton mButton;
    private ArtistsArrayAdapter mArtistsAdapter;
    private List<Artist> mArtists;

    public ArtistsFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mArtists = new ArrayList<>();
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
                    SearchQueryTask queryTask = new SearchQueryTask();
                    queryTask.execute(artistQuery);
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


        // instantiate the ArrayAdapter and bind it to the ListView
        mArtistsAdapter = new ArtistsArrayAdapter(getActivity(), mArtists);
        mListView.setAdapter(mArtistsAdapter);

        return view;
    }



    private class SearchQueryTask extends AsyncTask<String, Void, List<Artist>> {

        // execute the search, download & parse the json results
        @Override
        protected List<Artist> doInBackground(String... params) {

           List<Artist> artists = null;

            if(params.length == 0) {
                return null;
            }

            // execute the Artist query & download the results
            String jsonStringResult = Utils.downloadJSONSearchResults(params[0]);

            // parse the downloaded Json
            if (jsonStringResult != null)
                artists = Utils.parseJSONSearchResults(jsonStringResult);

            return artists;

        }


        @Override
        protected void onPostExecute(List<Artist> artists) {

            mArtistsAdapter.clear();

            // search returns an empty array
            if(artists.size() == 0) {
                Utils.showToast(getActivity(), "No results found");
                return;
            }

            if(artists != null) {
                // pass the results to the array adapter and update the view
                // notifyDataSetChanged() called
                mArtistsAdapter.updateView(artists);
            } else {
                Utils.showToast(getActivity(), "Network error");
            }

        }
    }


}
