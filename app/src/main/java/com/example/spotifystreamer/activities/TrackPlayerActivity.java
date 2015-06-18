package com.example.spotifystreamer.activities;


import android.os.Bundle;

import com.example.spotifystreamer.R;
import com.example.spotifystreamer.utils.BaseActivity;

public class TrackPlayerActivity  extends BaseActivity{


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new TrackPlayerFragment())
                    .commit();
        }

        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true); // add the up 'home' arrow
            getSupportActionBar().setDisplayShowTitleEnabled(false); // disable toolbar title
        }


    }


}
