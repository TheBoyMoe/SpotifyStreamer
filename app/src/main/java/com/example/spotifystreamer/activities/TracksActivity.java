package com.example.spotifystreamer.activities;

/**
 * NOT CURRENTLY BEING USED
 */


import android.os.Bundle;

import com.example.spotifystreamer.R;
import com.example.spotifystreamer.base.BaseActivity;


public class TracksActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, new TracksFragment())
                    .commit();
        }

        if(getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true); // add the up 'home' arrow

    }

}
