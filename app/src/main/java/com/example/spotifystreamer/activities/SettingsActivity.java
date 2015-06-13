package com.example.spotifystreamer.activities;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

import com.example.spotifystreamer.R;

/**
 * Activity which hosts the SettingsFragment
 */

public class SettingsActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // instantiate the SettingsFragment
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();

        // add the 'up' arrow to the action bar
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null)
            actionBar.setDisplayHomeAsUpEnabled(true);

        // set the default values for the app's settings
        PreferenceManager.setDefaultValues(SettingsActivity.this, R.xml.preferences, false);

    }


}
