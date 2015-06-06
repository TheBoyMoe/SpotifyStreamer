package com.example.spotifystreamer;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    private List<String> mList;
    private ArrayAdapter<String> mAdapter;
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

        // populate the listview & bind it to the ListView
        mAdapter = new ArrayAdapter<String>(
                getActivity(),
                R.layout.list_view_item,
                R.id.text_view_item,
                mList
        );

        mListView.setAdapter(mAdapter);

        return view;
    }


}
