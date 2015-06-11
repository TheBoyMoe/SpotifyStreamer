package com.example.spotifystreamer.activities;

import android.os.Bundle;

import com.example.spotifystreamer.R;


public class TracksActivity extends BaseActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.fragment_container);

        if(savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new TracksFragment())
                    .commit();
        }

        // Instantiate & configure the ToolBar
        //Toolbar toolbar = (Toolbar) findViewById(R.id.app_toolbar);
        //setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // add the up 'home' arrow
        //getSupportActionBar().setElevation(10.0f); // add a drop shadow

    }


//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_main, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//
//        if (item.getItemId() == R.id.action_settings) {
//            startActivity(new Intent(TracksActivity.this, SettingsActivity.class));
//            return true;
//        }
//        return super.onOptionsItemSelected(item);
//    }



}
