package com.example.spotifystreamer.activities;

import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.spotifystreamer.R;
import com.example.spotifystreamer.model.Track;
import com.squareup.picasso.Picasso;

import java.io.IOException;


/**
 * MediaPlayer class that encapsulates the MediaPlayer's features, basic implementation from
 * Android Programming - The Big Nerd Ranch Guide (Phillips & Hardy)
 * Which I've expanded upon.
 *
 * Also incorporates
 * http://developer.android.com/reference/android/media/MediaPlayer.html
 * http://developer.android.com/guide/topics/media/mediaplayer.html
 *
 * Play/Pause button - Thanks to various contributors on Stack Overflow
 * http://stackoverflow.com/questions/18120174/how-to-play-and-pause-in-only-one-button-android
 * http://stackoverflow.com/questions/3855151/how-to-resume-the-mediaplayer
 */

public class TrackPlayerFragment extends Fragment implements MediaPlayer.OnPreparedListener{

    private final String EXTRA_TRACK_RESULTS = "com.example.spotifystreamer.activities.tracks";
    private static final String LOG_TAG = TrackPlayerFragment.class.getSimpleName();

    private ImageButton mPlayPauseButton;
    private ImageButton mPrevButton;
    private ImageButton mNextButton;
    private TextView mArtistNameTextView;
    private TextView mAlbumTitleTextView;
    private TextView mTrackTitleTextView;
    private ImageView mAlbumCoverImageView;
    private SeekBar mSeekBar;
    //private AudioPlayer mMediaPlayer;
    private int mCurrentPosition;
    private boolean mCompleted;
    //private int mDuration;
    private MediaPlayer mMediaPlayer;

    public TrackPlayerFragment() {}


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_player_two, container, false);

        // create references to each of the layout elements of interest
        cacheLayoutViews(view);

        // grab the track object bundled with the intent
        Intent intent = getActivity().getIntent();
        final Track track = intent.getParcelableExtra(EXTRA_TRACK_RESULTS);

        // use it to initialize the text and image views
        initializeTextViews(track);
        initializeImageView(track);

        //mCompleted = false;

        // wire-up the play/pause button
        mPlayPauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // check if the player is actually playing and act accordingly
                if (isPlaying()) {
                    mCurrentPosition = getCurrentPosition();
                    pause();
                    mPlayPauseButton.setImageResource(R.drawable.ic_media_play_white);
                }
//                else if(mCompleted) {
//                    // mediaplayer in the PlaybackCompleted state, start it again from the beginning
//                    mPlayPauseButton.setImageResource(R.drawable.ic_media_pause);
//                    mCompleted = false;
//                    start();
//                }
                else {
                    if (mCurrentPosition > 0) {
                        // carry on playing from last position
                        seekTo(mCurrentPosition);
                        Log.d(LOG_TAG, "Current Position: " + mCurrentPosition);
                        start();
                    } else {
                        // otherwise start from the beginning
                        play(track.getPreviewUrl());
                    }
                    mPlayPauseButton.setImageResource(R.drawable.ic_media_pause_white);
                }

            }
        });

        return view;
    }



    @Override
    public void onDestroy() {
        super.onDestroy();
        // ensure media player resources are released
        stop();
    }






    //////////////////////////////////////////////////////////////////////////////////////////
    // MediaPlayer Helper Methods                                                          ///
    //////////////////////////////////////////////////////////////////////////////////////////

    public void play(final String url) {

        // ensure the is only one MediaPlayer instance running
        //stop();

        // fetch, decode and stream the music file on a bkgd thread thanks to prepareSync()
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            mMediaPlayer.setDataSource(url);
            mMediaPlayer.setLooping(false);
            mMediaPlayer.setOnPreparedListener(this);
            mMediaPlayer.prepareAsync();
        } catch (IOException e) {
            Log.d(LOG_TAG, "Remote file not found: " + e.getMessage());
        }


        // reset the image when the track finishes and enable the PlaybackCompleted flag
        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                //mCompleted = true;
                stop();
                mCurrentPosition = 0;
                mPlayPauseButton.setImageResource(R.drawable.ic_media_play_white);
            }
        });

    }


    public void stop() {
        if(mMediaPlayer != null) {
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }


    public void pause() {
        if(mMediaPlayer != null)
            mMediaPlayer.pause();
    }


    public int getCurrentPosition() {
        int value = 0;

        if(mMediaPlayer != null)
            value = mMediaPlayer.getCurrentPosition();

        return value;
    }


    public void seekTo(int value) {
        if(mMediaPlayer != null && value > 0)
            mMediaPlayer.seekTo(value);
    }


    public void start() {
        if(mMediaPlayer != null)
            mMediaPlayer.start();
    }


    // check the media player's state
    public boolean isPlaying() {
        boolean isPlaying = false;

        if(mMediaPlayer != null && mMediaPlayer.isPlaying()) {
            isPlaying = true;
        }
        return  isPlaying;
    }


    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        // when onPrepared() is called, the track is ready to be played
        mediaPlayer.start();
    }





    ///////////////////////////////////////////////////////////////////////////////////////////
    // General Helper methods                                                                       ///
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
        mSeekBar = (SeekBar) view.findViewById(R.id.seek_bar);
    }


    private void initializeTextViews(Track track) {
        mArtistNameTextView.setText(track.getArtistName());
        mAlbumTitleTextView.setText(track.getAlbumTitle());
        mTrackTitleTextView.setText(track.getTrackTitle());
    }


    private void initializeImageView(Track track) {
        Picasso.with(getActivity())
                .load(track.getImageUrl())
                .resize(320, 320)
                .centerCrop()
                .placeholder(R.drawable.dark_placeholder)
                .error(R.drawable.dark_placeholder)
                .into(mAlbumCoverImageView);
    }



}
