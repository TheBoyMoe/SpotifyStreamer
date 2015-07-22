package com.example.spotifystreamer.base;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.app.Fragment;

/**
 * Fragment class used to record Fragment LifeCycle changes for the purposes of debugging,
 * and implement common fragment features
 */
public class BaseFragment extends Fragment {

    private static final String LOG_TAG = BaseFragment.class.getSimpleName();
    private final boolean L = false;


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if(L) Log.d(LOG_TAG, "Calling onAttach()");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(L) {
            Log.d(LOG_TAG, "Calling onCreate()");

            if(savedInstanceState == null)
                Log.d(LOG_TAG, "Creating fragment for the first time");
            else
                Log.d(LOG_TAG, "Restoring fragment saved state");
        }

        // ensure the fragment hangs around after activity is destroyed
        setRetainInstance(true);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if(L) Log.d(LOG_TAG, "Calling onCreateView()");
        return super.onCreateView(inflater, container, savedInstanceState);
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(L) Log.d(LOG_TAG, "Calling onActivityCreated()");
    }

    @Override
    public void onStart() {
        super.onStart();
        if(L) Log.d(LOG_TAG, "Calling onStart()");
    }

    @Override
    public void onResume() {
        super.onResume();
        if(L) Log.d(LOG_TAG, "Calling onResume()");
    }



    @Override
    public void onPause() {
        super.onPause();
        if(L) Log.d(LOG_TAG, "Calling onPause()");
    }

    @Override
    public void onStop() {
        super.onStop();
        if(L) Log.d(LOG_TAG, "Calling onStop()");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if(L) Log.d(LOG_TAG, "Calling onDestroyView()");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(L) Log.d(LOG_TAG, "Calling onDestroy()");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if(L) Log.d(LOG_TAG, "Calling onDetach()");
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if(L) Log.d(LOG_TAG, "Calling onSaveInstanceState()");
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if(L) Log.d(LOG_TAG, "Calling onConfigurationChange()");
    }


}
