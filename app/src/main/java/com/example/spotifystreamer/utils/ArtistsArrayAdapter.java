package com.example.spotifystreamer.utils;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.spotifystreamer.R;
import com.example.spotifystreamer.model.Artist;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

public class ArtistsArrayAdapter extends ArrayAdapter<Artist>{

    private static final String LOG_TAG = ArtistsArrayAdapter.class.getSimpleName();
    private List<Artist> mArtists;


    public ArtistsArrayAdapter(Context context, List<Artist> artists) {
        super(context, 0, artists);
        mArtists = artists;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // retrieve the artist object for this position
        Artist artist = getItem(position);
        Log.i(LOG_TAG, artist.toString());

        // inflate a new view if there isn't one available to be recycled
        if(convertView == null) {
            convertView = LayoutInflater.from(getContext())
                    .inflate(R.layout.list_view_item, parent, false);
        }

        // cache the view's elements
        ImageView iv = (ImageView) convertView.findViewById(R.id.image_view_thumbnail);
        TextView tv = (TextView) convertView.findViewById(R.id.text_view_item);

        // download and display the thumbnail image
        String url = artist.getUrl();
        if(url != null && !(url.equals("no image found")))
            new DownloadImageTask(iv).execute(url);

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

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {

        private ImageView mImageView;

        public DownloadImageTask(ImageView imageView) {
            mImageView = imageView;
        }

        @Override
        protected Bitmap doInBackground(String... addresses) {
            Bitmap bitmap = null;
            InputStream in = null;

            // establish the connection and download the stream
            try {
                // declare the url and open the connection
                Log.d(LOG_TAG, "Url: " + addresses[0]);
                URL url = new URL(addresses[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                // connect and open the stream
                connection.connect();
                in = connection.getInputStream();

                // download and decode the bitmap
                bitmap = BitmapFactory.decodeStream(in);

            } catch (MalformedURLException e) {
                Log.e(LOG_TAG, "Error creating URL: " + e.getMessage());
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error connecting to remote server: " + e.getMessage());
            } finally {
                // close the connection
                if(in != null)
                    try {
                        in.close();
                    } catch (IOException e) {
                        Log.e(LOG_TAG, "Error closing connection: " + e.getMessage());
                    }
            }

            return bitmap;
        }


        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if(bitmap != null)
                mImageView.setImageBitmap(bitmap);
        }

    }



}
