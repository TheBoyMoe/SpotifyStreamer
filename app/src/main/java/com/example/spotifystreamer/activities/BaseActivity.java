package com.example.spotifystreamer.activities;


/**
 * Adding the ToolBar to the Activity - Thanks to CodePath
 * https://github.com/codepath/android_guides/wiki/Defining-The-ActionBar
 *
 * Adding Toolbar 'drop shadow' - thanks to Roberto
 * http://stackoverflow.com/questions/26575197/no-shadow-by-default-on-toolbar
 *
 * Preserving ListView on returning to Activity via up 'home' button - fix thanks to vikki_logs
 * http://stackoverflow.com/questions/22182888/actionbar-up-button-destroys-parent-activity-back-does-not
 *
 */


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.example.spotifystreamer.R;

/**
 * BaseActivity provides all the common functionality for the other activities of the app
 */

public class BaseActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_container);

        // Instantiate & configure the ToolBar
        Toolbar toolbar = (Toolbar) findViewById(R.id.app_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setElevation(10.0f); // add a drop shadow

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
            startActivity(new Intent(BaseActivity.this, SettingsActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


}
