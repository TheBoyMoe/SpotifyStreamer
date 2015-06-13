package com.example.spotifystreamer.utils;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import com.example.spotifystreamer.R;

/**
 * Simple Alert Dialog
 * http://developer.android.com/guide/topics/ui/dialogs.html
 *
 * Passing parameters to fragments via arguments from the Udemy Developing Android Lollipop Apps
 * - Flickr Browser Project
 */

public class ConfirmationDialogFragment extends DialogFragment {

    //private static final String EXTRA_RECENT_SUGGESTION =
           // "com.example.spotifystreamer.utils.ConfirmationDialogFragment.RecentSuggestion";
    //private SearchRecentSuggestions mSearchRecentSuggestions;

    public ConfirmationDialogFragment() {}

    /**
     * PASSING A PARAMETER TO A FRAGMENT ON INSTANTIATION USING ARGUMENTS
     */

//    public static ConfirmationDialogFragment newInstance(SearchRecentSuggestions suggestions) {
//
//        Bundle bundle = new Bundle();
//        bundle.putSerializable(EXTRA_RECENT_SUGGESTION, (Serializable) suggestions);
//
//        ConfirmationDialogFragment fragment = new ConfirmationDialogFragment();
//        fragment.setArguments(bundle);
//
//        return fragment;
//    }
//
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//
//        mSearchRecentSuggestions =
//                (SearchRecentSuggestions) getArguments().getSerializable(EXTRA_RECENT_SUGGESTION);
//    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.confirmation_dialog_message)
                .setPositiveButton(R.string.confirmation_dialog_positive_button,
                                                    new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //mSearchRecentSuggestions.clearHistory();
                        Utils.showToast(getActivity(), "Search history cleared");
                    }
                })
                .setNegativeButton(R.string.confirmation_dialog_negative_button,
                                                    new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Utils.showToast(getActivity(), "Action cancelled");
                    }
                });

        return builder.create();
    }
}
