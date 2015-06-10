package com.example.spotifystreamer.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.example.spotifystreamer.R;

public class ArtistsActivity extends AppCompatActivity {

    private static final String LOG_TAG = ArtistsActivity.class.getSimpleName();
    private final boolean L = true;
    // private final String FRAGMENT_TAG = "fragment tag";
    // private ArtistsFragment mArtistsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_container);


        if(savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new ArtistsFragment())
                    .commit();
        }

        // version 2 - works
//        if(savedInstanceState == null) {
//            if(L) Log.i(LOG_TAG, "Instantiating a new fragment");
//            getSupportFragmentManager().beginTransaction()
//                    .add(R.id.container, new ArtistsFragment(), FRAGMENT_TAG)
//                    .commit();
//        } else {
//            if(L) Log.i(LOG_TAG, "Retrieving the fragment");
//            getSupportFragmentManager().findFragmentByTag(FRAGMENT_TAG);
//        }


        // version 3 - works
        // fetch the fragment, otherwise instantiate it
//        if(savedInstanceState != null) {
//            mArtistsFragment = (ArtistsFragment) getSupportFragmentManager()
//                    .findFragmentByTag(FRAGMENT_TAG);
//        } else if(mArtistsFragment == null) {
//            mArtistsFragment = new ArtistsFragment();
//        }
//
//        // add the fragment to the layout if it isn't already
//        if(!mArtistsFragment.isInLayout()) {
//            getSupportFragmentManager().beginTransaction()
//                    .replace(R.id.container, mArtistsFragment, FRAGMENT_TAG)
//                    .commit();
//        }

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
            startActivity(new Intent(ArtistsActivity.this, SettingsActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
