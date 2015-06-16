package com.example.spotifystreamer.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.spotifystreamer.R;
import com.example.spotifystreamer.model.Track;
import com.example.spotifystreamer.utils.AudioPlayer;
import com.squareup.picasso.Picasso;


/**
 * Play/Pause button - Thanks to various contributors on Stack Overflow
 * http://stackoverflow.com/questions/18120174/how-to-play-and-pause-in-only-one-button-android
 * http://stackoverflow.com/questions/3855151/how-to-resume-the-mediaplayer
 */

public class TrackPlayerFragment extends Fragment {

    private final String EXTRA_TRACK_RESULTS = "com.example.spotifystreamer.activities.tracks";
    private static final String LOG_TAG = TrackPlayerFragment.class.getSimpleName();

    private ImageButton mPlayPauseButton;
    private ImageButton mPrevButton;
    private ImageButton mNextButton;
    private TextView mArtistNameTextView;
    private TextView mAlbumTitleTextView;
    private TextView mTrackTitleTextView;
    private ImageView mAlbumCoverImageView;
    private AudioPlayer mMediaPlayer;
    private int mCurrentPosition;
    private int mDuration;

    public TrackPlayerFragment() {}


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mMediaPlayer = new AudioPlayer();

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_player, container, false);

        // create references to each of the layout elements of interest
        cacheLayoutViews(view);

        // grab the track object bundled with the intent
        Intent intent = getActivity().getIntent();
        final Track track = intent.getParcelableExtra(EXTRA_TRACK_RESULTS);

        // use it to initialize the text and image views
        initializeTextViews(track);
        initializeImageView(track);

        // wire-up the play/pause button
        mPlayPauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // check if the player is actually playing and act accordingly
                if (mMediaPlayer.isPlaying()) {
                    mCurrentPosition = mMediaPlayer.getCurrentPosition();
                    mMediaPlayer.pause();
                    mPlayPauseButton.setImageResource(R.drawable.ic_media_play);
                } else {
                    if (mCurrentPosition > 0) {
                        // carry on playing from last position
                        mMediaPlayer.seekTo(mCurrentPosition);
                        mMediaPlayer.start();
                    } else {
                        // otherwise start from the beginning
                        mMediaPlayer.play(track.getPreviewUrl());
                    }
                    mPlayPauseButton.setImageResource(R.drawable.ic_media_pause);
                }

            }
        });

        return view;
    }



    @Override
    public void onDestroy() {
        super.onDestroy();

        // ensure media player resources are released
        mMediaPlayer.stop();
    }




    ///////////////////////////////////////////////////////////////////////////////////////////
    // Helper methods                                                                       ///
    ///////////////////////////////////////////////////////////////////////////////////////////

    private void cacheLayoutViews(View view) {
        // cache references to all the elements of interest
        mPlayPauseButton = (ImageButton) view.findViewById(R.id.image_button_play_pause);
        mPrevButton = (ImageButton) view.findViewById(R.id.image_button_prev_track);
        mNextButton = (ImageButton) view.findViewById(R.id.image_button_next_track);
        mArtistNameTextView = (TextView) view.findViewById(R.id.text_view_artist_name);
        mAlbumTitleTextView = (TextView) view.findViewById(R.id.text_view_album_title);
        mTrackTitleTextView = (TextView) view.findViewById(R.id.text_view_track_title);
        mAlbumCoverImageView = (ImageView) view.findViewById(R.id.image_view_album_cover);
    }


    private void initializeTextViews(Track track) {
        mArtistNameTextView.setText(track.getArtistName());
        mAlbumTitleTextView.setText(track.getAlbumTitle());
        mTrackTitleTextView.setText(track.getTrackTitle());
    }


    private void initializeImageView(Track track) {
        Picasso.with(getActivity())
                .load(track.getImageUrl())
                .resize(260, 260)
                .centerCrop()
                .placeholder(R.drawable.error_placeholder)
                .error(R.drawable.error_placeholder)
                .into(mAlbumCoverImageView);
    }



}
