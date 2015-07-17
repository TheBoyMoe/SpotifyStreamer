package com.example.spotifystreamer.activities;


import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.example.spotifystreamer.R;

/**
 * orientation fix
 * http://stackoverflow.com/questions/8180764/how-do-i-lock-screen-orientation-for-phone-but-not-for-tablet-android
 */

public class PlayerActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // use the fragment_container layout for all devices(override BaseActivity)
        setContentView(R.layout.fragment_container);

        Configuration config = getResources().getConfiguration();

        // target devices < 600dp in width
        // lock the device in portrait mode
        if(config.smallestScreenWidthDp < 600) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }


        if(savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, new PlayerFragment())
                    .commit();
        }

        // Instantiate & configure the ToolBar
        Toolbar toolbar = (Toolbar) findViewById(R.id.app_toolbar);

        if(toolbar != null) {
            setSupportActionBar(toolbar);
            // 'drop shadow' effect is only supported in APi 21+
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                toolbar.setElevation(10.0f);
            }
        }


        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true); // add the up 'home' arrow
            getSupportActionBar().setDisplayShowTitleEnabled(false); // disable toolbar title
        }


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.action_settings) {
            startActivity(new Intent(PlayerActivity.this, SettingsActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }




}
