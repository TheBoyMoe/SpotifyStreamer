package com.example.spotifystreamer.utils;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.spotifystreamer.R;
import com.example.spotifystreamer.model.Artist;
import com.example.spotifystreamer.model.Track;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class Utils {

    private static final String LOG_TAG = Utils.class.getSimpleName();
    private static final boolean L = false;

    // ensure the Utils class can not be instantiated
    private Utils() {
        throw new AssertionError();
    }

    // download the search results
    public static String downloadJSONSearchResults(String query) {

        // Will contain the raw JSON response as a string.
        String jsonStrResults = null;

        // url query parameters
        String type = "Artist"; // other types: album, track, playlist

        try {

            final String URL_BASE = "https://api.spotify.com/v1/search?";
            final String QUERY_PARAM = "q";
            final String TYPE_PARAM = "type";

            // use UriBuilder to build the query
            Uri uri = Uri.parse(URL_BASE).buildUpon()
                    .appendQueryParameter(QUERY_PARAM, query)
                    .appendQueryParameter(TYPE_PARAM, type)
                    .build();

            URL url = new URL(uri.toString());
            if(L) Log.i(LOG_TAG, "Spotify url: " + url);

            if(url != null)
                jsonStrResults = executeConnectionAndDownload(url);

        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Error creating the url: " + e.getMessage());
        }

        return jsonStrResults;
    }



    // parse the json search results string
    public static List<Artist> parseJSONSearchResults(String jsonStrResults) {

        List<Artist> artistList = new ArrayList<>();

        // retrieve the following JSON values
        final String TOTAL_OBJECT = "total"; // total number of records returned
        final String ARTISTS_OBJECT = "artists";
        final String ITEMS_ARRAY = "items";
        final String ARTIST_ID_ATTRIBUTE = "id";
        final String ARTIST_NAME_ATTRIBUTE = "name";
        final String IMAGES_ARRAY = "images";
        final String THUMBNAIL_URL_ATTRIBUTE = "url";


        String id = "", name = "", url = "";
        int total = 0;

        try {
            JSONObject jsonObject = new JSONObject(jsonStrResults);

            JSONObject artistsObject;
            JSONArray artists;

            artistsObject = jsonObject.getJSONObject(ARTISTS_OBJECT);

            // check that results are actually returned
            total = artistsObject.getInt(TOTAL_OBJECT);

            if (total > 0) {
                if (L) Log.i(LOG_TAG, "Total number of records returned: " + total);

                if (artistsObject.has(ITEMS_ARRAY)) {
                    artists = artistsObject.getJSONArray(ITEMS_ARRAY);

                    // iterate through the array of artist JSON objects
                    for (int i = 0; i < artists.length(); i++) {
                        JSONObject artist = artists.getJSONObject(i);

                        if (artist.has(ARTIST_ID_ATTRIBUTE))
                            id = artist.getString(ARTIST_ID_ATTRIBUTE);
                        if (artist.has(ARTIST_NAME_ATTRIBUTE))
                            name = artist.getString(ARTIST_NAME_ATTRIBUTE);

                        // retrieve the image data
                        JSONArray images = artist.getJSONArray(IMAGES_ARRAY);

                        JSONObject image = null;

                        if (images != null && images.length() > 0) {
                            if (images.length() == 1) {
                                image = images.getJSONObject(0);
                            } else if (images.length() >= 2) {
                                image = images.getJSONObject(images.length() - 2);
                            }
                            if (image.has(THUMBNAIL_URL_ATTRIBUTE))
                                url = image.getString(THUMBNAIL_URL_ATTRIBUTE);

                        } else {
                            url = "no image found";
                        }

                        // instantiate an artist pojo and add it to the manager
                        Artist artistPojo = new Artist(id, name, url);
                        artistList.add(artistPojo);
                    }
                }

            } else {
                Log.d(LOG_TAG, "No results found for search term");
            }

        } catch (JSONException e) {
            Log.e(LOG_TAG, "Failure parsing the JSON data: " + e.getMessage());
        }

        return artistList;
    }



    // download the artist's top ten tracks
    public static String downloadJSONArtistResults(String artistId, String country) {

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Will contain the raw JSON response as a string.
        String jsonResultsStr = null;

        try {

            final String URL_BASE = "https://api.spotify.com/v1/artists/";
            final String COUNTRY_PARAM = "country";

            // use StringBuilder to build the base url
            StringBuilder sb = new StringBuilder(URL_BASE);
            sb.append(artistId);
            sb.append("/top-tracks?");
            String baseUrl = sb.toString();

            // use UriBuilder to build the query
            Uri uri = Uri.parse(baseUrl).buildUpon()
                    .appendQueryParameter(COUNTRY_PARAM, country)
                    .build();

            URL url = new URL(uri.toString());
            if(L) Log.i(LOG_TAG, "Spotify url: " + url);

            if(url != null)
                jsonResultsStr = executeConnectionAndDownload(url);

        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Error creating the url: " + e.getMessage());
        }

        return jsonResultsStr;
    }



    public static List<Track> parseJSONTrackResults(String jsonResultsStr,
                                                    String artistName, String artistId) {

        List<Track> trackList = new ArrayList<>();

        // retrieve the following JSON values
        final String TRACKS_ARRAY = "tracks";
        final String ALBUM_OBJECT = "album";
        final String ALBUM_TITLE_ATTRIBUTE = "name";
        final String TRACK_TITLE_ATTRIBUTE = "name";
        final String PREVIEW_URL_ATTRIBUTE = "preview_url";
        final String IMAGES_ARRAY = "images";
        final String IMAGE_URL_ATTRIBUTE = "url";

        String trackTitle = "", albumTitle = "",
                previewImageUrl = "", thumbnailUrl = "", previewUrl = "";

        try {
            JSONObject jsonObject = new JSONObject(jsonResultsStr);
            if(jsonObject != null && jsonObject.has(TRACKS_ARRAY)) {

                JSONArray tracksArray = jsonObject.getJSONArray(TRACKS_ARRAY);

                if(tracksArray != null && tracksArray.length() > 0) {

                    for (int i = 0; i < tracksArray.length(); i++) {
                        JSONObject trackObject = tracksArray.getJSONObject(i);
                        if(trackObject.has(TRACK_TITLE_ATTRIBUTE))
                            trackTitle = trackObject.getString(TRACK_TITLE_ATTRIBUTE);
                        if(trackObject.has(PREVIEW_URL_ATTRIBUTE))
                            previewUrl = trackObject.getString(PREVIEW_URL_ATTRIBUTE);

                        // retrieve the album images & title
                        JSONObject album = trackObject.getJSONObject(ALBUM_OBJECT);
                        if(album != null) {
                            if(album.has(ALBUM_TITLE_ATTRIBUTE))
                                albumTitle = album.getString(ALBUM_TITLE_ATTRIBUTE);
                            if(album.has(IMAGES_ARRAY)) {
                                JSONArray images = album.getJSONArray(IMAGES_ARRAY);
                                if(images != null && images.length() > 0) {
                                    JSONObject largeImage = images.getJSONObject(0);
                                    if (largeImage != null && largeImage.has(IMAGE_URL_ATTRIBUTE))
                                        previewImageUrl = largeImage.getString(IMAGE_URL_ATTRIBUTE);

                                    JSONObject mediumImage = images.getJSONObject(1);
                                    if(mediumImage != null && mediumImage.has(IMAGE_URL_ATTRIBUTE))
                                        thumbnailUrl = mediumImage.getString(IMAGE_URL_ATTRIBUTE);
                                }
                            }

                        }

                        // create a track object & add it to the ArrayList
                        Track track = new Track(artistId, artistName,
                                trackTitle, albumTitle, previewImageUrl, thumbnailUrl, previewUrl);
                        trackList.add(track);
                    }
                }


            }

        } catch (JSONException e) {
            Log.e(LOG_TAG, "Failure parsing the JSON data: " + e.getMessage());
        }

        return trackList;
    }




    // instantiate the http connection to the remote server, download the json data
    private static String executeConnectionAndDownload(URL url) {

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Create the request to OpenWeatherMap, and open the connection
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                return null;
            }

            // return the json string for parsing
            return buffer.toString();

        } catch (IOException e) {
            Log.e(LOG_TAG, "Download failure, connection error: " + e.getMessage());
            return null;

        } finally{
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing the stream: " + e.getMessage());
                }
            }
        }


    }




    // AsyncTask responsible for downloading Image Thumbnails
    public static class DownloadImageTask extends AsyncTask<Void, Void, Bitmap> {

        private ImageView mImageView;
        private String mUrl;

        public DownloadImageTask(ImageView imageView, String urlStr) {
            mImageView = imageView;
            mUrl = urlStr;
        }

        @Override
        protected Bitmap doInBackground(Void... voids) {
            Bitmap bitmap = null;
            InputStream in = null;

            // establish the connection and download the bitmap
            try {
                URL url = new URL(mUrl);
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
            else
                mImageView.setImageResource(R.drawable.placeholder);
        }

    }




    // display a Toast message to the user
    public static void showToast(Context context, String string) {
        Toast.makeText(context, string, Toast.LENGTH_SHORT).show();
    }



}
