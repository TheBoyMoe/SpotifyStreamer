package com.example.spotifystreamer.activities;


import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.example.spotifystreamer.R;


public class PlayerActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.media_player_container);

        Configuration config = getResources().getConfiguration();

        // target devices < 600dp in width, lock the device in portrait mode
//        if(config.smallestScreenWidthDp < 600) {
//            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//        }

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


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.action_settings) {
            startActivity(new Intent(PlayerActivity.this, SettingsActivity.class));
            finish(); // terminate player
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


}
