package com.example.spotifystreamer.utils;


import android.media.AudioManager;
import android.media.MediaPlayer;
import android.util.Log;

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
 */

public class AudioPlayer implements MediaPlayer.OnPreparedListener{

    private static final String LOG_TAG = AudioPlayer.class.getSimpleName();
    private MediaPlayer mMediaPlayer;

    public void play(String url) {

        // ensure that there is only one media player instantiated
        stop();

        //mMediaPlayer = MediaPlayer.create(context, R.raw.one_small_step);
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            mMediaPlayer.setDataSource(url);
            mMediaPlayer.setOnPreparedListener(this);
            // ensure fetching, decoding and buffering track occurs in a bkgd thread
            mMediaPlayer.prepareAsync();
        } catch (IOException e) {
            Log.d(LOG_TAG, "Audio file not found: " + e.getMessage());
        }

        // call stop when the track has finished ensuring the media player is released
        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                stop();
            }
        });

    }


    public void stop() {
        if(mMediaPlayer != null) {
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
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
        // when onPrepared() is called the track has been buffered and is ready to be played
        mediaPlayer.start();
    }


    // forward the following calls
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


}
