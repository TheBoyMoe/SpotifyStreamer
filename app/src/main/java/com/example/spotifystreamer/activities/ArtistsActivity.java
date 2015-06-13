package com.example.spotifystreamer.activities;

import android.os.Bundle;

import com.example.spotifystreamer.R;
import com.example.spotifystreamer.utils.BaseActivity;


public class ArtistsActivity extends BaseActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new ArtistsFragment())
                    .commit();
        }

    }


}
