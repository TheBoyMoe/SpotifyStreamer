package com.example.spotifystreamer.view;


import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.spotifystreamer.R;
import com.example.spotifystreamer.model.Track;
import com.squareup.picasso.Picasso;

import java.util.List;

public class TracksArrayAdapter extends ArrayAdapter<Track>{

    private static final String LOG_TAG = TracksArrayAdapter.class.getSimpleName();
    private final boolean L = false;

    private List<Track> mList;


    public TracksArrayAdapter(Context context, List<Track> tracks) {
        super(context, 0, tracks);
        mList = tracks;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // retrieve the artist object for this position
        Track track = getItem(position);
        if(L) Log.i(LOG_TAG, track.toString());

        // inflate a new view if there isn't one available to be recycled
        if(convertView == null) {
            convertView = LayoutInflater.from(getContext())
                    .inflate(R.layout.list_view_item_track_two, parent, false);
        }

        // cache the view's elements
        ImageView iv = (ImageView) convertView.findViewById(R.id.image_view_track_thumbnail);
        TextView ttv = (TextView) convertView.findViewById(R.id.text_view_track_title);
        TextView atv = (TextView) convertView.findViewById(R.id.text_view_album_title);

        // download and display the thumbnail image
        String url = track.getThumbnailUrl();

        // Use Square's Picasso plugin to fetch and display the image
        Picasso.with(getContext())
                .load(url)
                .resize(80, 80)
                .centerCrop()
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.placeholder)
                .into(iv);

        // set the artist name on the text view
        ttv.setText(track.getTrackTitle());
        atv.setText(track.getAlbumTitle());

        return convertView;
    }


    @Override
    public Track getItem(int position) {
        return (mList != null? mList.get(position) : null);
    }


    @Override
    public int getCount() {
        return (mList != null? mList.size() : 0);
    }


    public void updateView(List<Track> list) {
        mList = list;
        notifyDataSetChanged();
    }


    @Override
    public void clear() {
        super.clear();
        mList.clear();
    }


}
