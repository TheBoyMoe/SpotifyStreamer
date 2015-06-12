package com.example.spotifystreamer.activities;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import com.example.spotifystreamer.R;


public class SettingsFragment extends PreferenceFragment {

    public SettingsFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // load the preferences xml file
        addPreferencesFromResource(R.xml.preferences);

    }
}
