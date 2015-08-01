package com.example.spotifystreamer.activities;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.spotifystreamer.R;


public class PlayerActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.media_player_container);


        // check if this is the first time in
        if(savedInstanceState == null) {

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.media_player_container, new PlayerFragment())
                    .commit();
        }

        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false); // disable actionBar title
        }


    }



}
