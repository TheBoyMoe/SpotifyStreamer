package com.example.spotifystreamer.model;


import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.spotifystreamer.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ArtistsArrayAdapter extends ArrayAdapter<Artist>{

    private static final String LOG_TAG = ArtistsArrayAdapter.class.getSimpleName();
    private final boolean L = false;

    private List<Artist> mArtists;


    public ArtistsArrayAdapter(Context context, List<Artist> artists) {
        super(context, 0, artists);
        mArtists = artists;
    }

    // build each list view item
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // retrieve the artist object for this position
        Artist artist = getItem(position);
        if(L) Log.i(LOG_TAG, artist.toString());

        // inflate a new view if there isn't one available to be recycled
        if(convertView == null) {
            convertView = LayoutInflater.from(getContext())
                    .inflate(R.layout.list_view_item_artist, parent, false);
        }

        // cache the view's elements
        ImageView iv = (ImageView) convertView.findViewById(R.id.image_view_artist_thumbnail);
        TextView tv = (TextView) convertView.findViewById(R.id.text_view_artist_name);

        // download and display the thumbnail image
        String url = artist.getImageUrl();

        // Use Square's Picasso plugin to fetch and display the image
        Picasso.with(getContext())
                .load(url)
                .resize(120, 120)
                .centerCrop()
                .placeholder(R.drawable.dark_placeholder)
                .error(R.drawable.dark_placeholder)
                .into(iv);

        // set the artist name on the text view
        tv.setText(artist.getName());

        return convertView;
    }


    @Override
    public Artist getItem(int position) {
        return (mArtists != null? mArtists.get(position) : null);
    }

    @Override
    public int getCount() {
        return (mArtists != null? mArtists.size() : 0);
    }


    public void updateView(List<Artist> list) {
        mArtists = list;
        notifyDataSetChanged();
    }


    @Override
    public void clear() {
        super.clear();
        mArtists.clear();
    }


}
