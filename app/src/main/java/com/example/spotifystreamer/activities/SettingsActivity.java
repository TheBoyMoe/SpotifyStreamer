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


    // enable the 'up' arrow to send the user back to the previous activity
    // not req'd when activity has been defined as the child of another,
    // up arrow works automatically - sends user to the 'home' activity
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        if(item.getItemId() == android.R.id.home)
//            finish();
//        return super.onOptionsItemSelected(item);
//    }


}
