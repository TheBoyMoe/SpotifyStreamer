package com.example.spotifystreamer.utils;


import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.IBinder;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

/**
 * Network connectivity method:
 * http://developer.android.com/training/monitoring-device-state/connectivity-monitoring.html#DetermineConnection
 */

public class Utils {

    private static final String LOG_TAG = Utils.class.getSimpleName();
    private static final boolean L = true;

    // ensure the Utils class can not be instantiated
    private Utils() {
        throw new AssertionError();
    }


    // display a Toast message to the user
    public static void showToast(Context context, String string) {
        Toast.makeText(context, string, Toast.LENGTH_SHORT).show();
    }


    // hide the keyboard on executing search
    public static void hideKeyboard(Activity activity, IBinder windowToken) {
        InputMethodManager mgr =
                (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        mgr.hideSoftInputFromWindow(windowToken, 0);
    }


    // check network connectivity, in case of drop offs
    public static boolean isConnected(Context context) {

        ConnectivityManager cm =
                (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }



}
