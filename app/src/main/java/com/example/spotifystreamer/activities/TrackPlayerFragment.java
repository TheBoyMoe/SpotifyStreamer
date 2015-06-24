package com.example.spotifystreamer.activities;

import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.spotifystreamer.R;
import com.example.spotifystreamer.model.Track;
import com.squareup.picasso.Picasso;

import java.io.IOException;


/**
 *
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
 *
 * SeekerBar - Thanks to various contributions to StackOverflow & blog posts
 * http://stackoverflow.com/questions/8956218/android-seekbar-setonseekbarchangelistener
 * http://www.mopri.de/2010/timertask-bad-do-it-the-android-way-use-a-handler/
 * http://www.androidhive.info/2012/03/android-building-audio-player-tutorial/
 * http://stackoverflow.com/questions/21864890/change-progress-music-when-clicked-on-seekbar-in-android
 * http://united-coders.com/nico-heid/an-android-seekbar-for-your-mediaplayer/
 *
 */

public class TrackPlayerFragment extends Fragment implements MediaPlayer.OnPreparedListener{

    private final String EXTRA_TRACK_RESULTS = "com.example.spotifystreamer.activities.tracks";
    private static final String LOG_TAG = TrackPlayerFragment.class.getSimpleName();
    private final boolean L = true;

    private ImageButton mPlayPauseButton;
    private ImageButton mPrevButton;
    private ImageButton mNextButton;
    private TextView mArtistName;
    private TextView mAlbumTitle;
    private TextView mTrackTitle;
    private ImageView mAlbumCover;
    private SeekBar mSeekBar;
    private TextView mTrackTimer;
    private TextView mTrackDuration;
    private int mCurrentPosition;
    private long mDuration;
    private MediaPlayer mMediaPlayer;
    private ProgressBar mProgressBar;
    private Handler mSeekHandler;


    public TrackPlayerFragment() {}


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // used to update the SeekBar at 1 sec intervals
        mSeekHandler = new Handler();

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
        mProgressBar.setVisibility(View.INVISIBLE);


        // wire-up the play/pause button
        mPlayPauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // check if the player is actually playing and act accordingly
                if (isPlaying()) {
                    mCurrentPosition = getCurrentPosition();
                    if(L) Log.i(LOG_TAG, "isPlaying called, position at: " + mCurrentPosition +
                            " Media Player: " + mMediaPlayer);
                    pause();
                    mSeekHandler.removeCallbacks(onProgressUpdater); // stop updater when paused
                    mPlayPauseButton.setImageResource(R.drawable.ic_media_play_white);
                }
                else {
                    // first time through, position not at zero
                    if(mCurrentPosition > 0 && mMediaPlayer == null) {
                        if(L) Log.i(LOG_TAG, "First time through, position > 0 position at: "
                                + mCurrentPosition + " Media Player: " + mMediaPlayer);
                        play(track.getPreviewUrl());
                    } else
                    if(mCurrentPosition > 0) {
                        // paused track being re-started
                        if(L) Log.i(LOG_TAG, "Paused, restarting, position at: " + mCurrentPosition
                               +  " Media Player: " + mMediaPlayer);
                        mSeekHandler.postDelayed(onProgressUpdater, 1000);
                        start();
                    }
                    else {
                        // first time through, position at zero
                        if(L) Log.i(LOG_TAG, "First time through, position should be zero, position: "
                                + mCurrentPosition + " Media Player: " + mMediaPlayer);
                        play(track.getPreviewUrl());
                    }

                    mPlayPauseButton.setImageResource(R.drawable.ic_media_pause_white);
                }

            }
        });


        // wire-up the SeekBar
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

                // set the max value the progressbar can have based on the track duration
                seekBar.setMax((int)mDuration);

                // get the current clicked position
                mCurrentPosition = seekBar.getProgress() * 1000;
                if(L) Log.i(LOG_TAG, "onStartTracking position at:" + mCurrentPosition);

                // stop handler callbacks
                mSeekHandler.removeCallbacks(onProgressUpdater);
            }


            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean moved) {
                // update the timer based on the progress of the SeekBar
                if(moved)
                    mTrackTimer.setText(timeFormatter(progress));
            }


            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // update the media player's position in the track based on SeekBar progress
                mCurrentPosition = seekBar.getProgress() * 1000; // req'd in milliseconds
                if(L) Log.i(LOG_TAG, "onStopTracking position at: " + mCurrentPosition +
                        " Media Player: " + mMediaPlayer);
                seekTo(mCurrentPosition);
                mSeekHandler.postDelayed(onProgressUpdater, 1000);
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

        // show the progressbar while loading track
        mProgressBar.setVisibility(View.VISIBLE);

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

                // stop the handler callbacks
                mSeekHandler.removeCallbacks(onProgressUpdater);

                // stop the media player and reset the SeekBar & Timer
                stop();
                mCurrentPosition = 0;
                if(L) Log.i(LOG_TAG, "Track finished, setting position to: " + mCurrentPosition +
                        " Media Player: " + mMediaPlayer);
                mSeekBar.setProgress(mCurrentPosition);
                mTrackTimer.setText(timeFormatter(mCurrentPosition));
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
        if(mMediaPlayer != null && value >= 0) {
            mMediaPlayer.seekTo(value);
            if(L) Log.i(LOG_TAG, "Setting media player to position: " + value);
        }

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
        mProgressBar.setVisibility(View.INVISIBLE);
        if(L) Log.i(LOG_TAG, "onPrepared called, position at: " + mCurrentPosition +
                " Media Player: " + mMediaPlayer);
        seekTo(mCurrentPosition);
        start();

        // start the handler that updates the SeekBar and Timer
        mSeekHandler.postDelayed(onProgressUpdater, 1000);
    }


    public Runnable onProgressUpdater = new Runnable() {

        @Override
        public void run() {

            if(mMediaPlayer != null) {
                // get current position and update the SeekBar & Timer
                mCurrentPosition = getCurrentPosition();
                if(L) Log.i(LOG_TAG, "onProgressUpdater called, position at: " + mCurrentPosition);
                mSeekBar.setMax((int)mDuration);
                mSeekBar.setProgress(mCurrentPosition/1000);
                mTrackTimer.setText(timeFormatter(mCurrentPosition/1000));

                // repeat every second
                mSeekHandler.postDelayed(onProgressUpdater, 1000);
            }

        }

    };




    ///////////////////////////////////////////////////////////////////////////////////////////
    // General Helper methods                                                               ///
    ///////////////////////////////////////////////////////////////////////////////////////////

    private void cacheLayoutViews(View view) {
        // cache references to all the elements of interest
        mPlayPauseButton = (ImageButton) view.findViewById(R.id.image_button_play_pause);
        mPrevButton = (ImageButton) view.findViewById(R.id.image_button_prev_track);
        mNextButton = (ImageButton) view.findViewById(R.id.image_button_next_track);
        mArtistName = (TextView) view.findViewById(R.id.text_view_artist_name);
        mAlbumTitle = (TextView) view.findViewById(R.id.text_view_album_title);
        mTrackTitle = (TextView) view.findViewById(R.id.text_view_track_title);
        mAlbumCover = (ImageView) view.findViewById(R.id.image_view_album_cover);
        mTrackTimer = (TextView) view.findViewById(R.id.text_view_timer);
        mTrackDuration = (TextView) view.findViewById(R.id.text_view_duration);
        mSeekBar = (SeekBar) view.findViewById(R.id.seek_bar);
        mProgressBar = (ProgressBar) view.findViewById(R.id.progress_bar);
    }


    private void initializeTextViews(Track track) {
        mArtistName.setText(track.getArtistName());
        mAlbumTitle.setText(track.getAlbumTitle());
        mTrackTitle.setText(track.getTrackTitle());
        mTrackTimer.setText(R.string.track_timer_initial_value);

        // format the track duration to suitable output
        //mDuration = track.getTrackDuration();
        mDuration = 30;  // since the media player currently only downloads 30 sec samples
        String result = timeFormatter(mDuration);
        mTrackDuration.setText(result);
    }


    private String timeFormatter(long time) {
        long min = time / 60;
        long secs = time % 60;
        return String.format("%d:%d", min, secs);
    }


    private void initializeImageView(Track track) {
        Picasso.with(getActivity())
                .load(track.getImageUrl())
                .resize(320, 320)
                .centerCrop()
                .placeholder(R.drawable.dark_placeholder)
                .error(R.drawable.dark_placeholder)
                .into(mAlbumCover);
    }



}
