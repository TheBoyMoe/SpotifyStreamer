package com.example.spotifystreamer.utils;


import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import com.example.spotifystreamer.model.Artist;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class Utils {

    private static final String LOG_TAG = Utils.class.getSimpleName();
    private static final boolean L = true;


    public static String downloadJSONSearchResults(String query) {

        // These two need to be declared outside the try/catch
        // so that they can be closed in the finally block.
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Will contain the raw JSON response as a string.
        String resultsJsonStr = null;

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

            if(L) Log.i(LOG_TAG, "Spotify url url: " + url);

            // Create the request to OpenWeatherMap, and open the connection
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
            resultsJsonStr = buffer.toString();

            // Log.d(LOG_TAG, resultsJsonStr);

            // return the json string for parsing
            return resultsJsonStr;

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


    // parse the json results string
    public static List<Artist> parseJSONSearchResults(String jsonResults) {

        List<Artist> artistList = new ArrayList<>();

        // retrieve the following JSON values
        final String TOTAL_OBJECT = "total"; // total number of records returned
        final String ARTISTS_OBJECT = "artists";
        final String ITEMS_ARRAY = "items";
        final String ARTIST_ID_ATTRIBUTE = "id";
        final String ARTIST_NAME_ATTRIBUTE = "name";
        final String IMAGES_ARRAY = "images";
        final String IMAGE_HEIGHT_ATTRIBUTE = "height";
        final String IMAGE_WIDTH_ATTRIBUTE = "width";
        final String THUMBNAIL_URL_ATTRIBUTE = "url";


        String id, name, url;
        int width, height, total;

        try {
            JSONObject jsonObject = new JSONObject(jsonResults);
            JSONObject artistsObject = jsonObject.getJSONObject(ARTISTS_OBJECT);

            // check that results are actually returned
            total = artistsObject.getInt(TOTAL_OBJECT);

            if(total > 0) {
                if(L) Log.i(LOG_TAG, "Total number of records returned: " + total);

                JSONArray artists = artistsObject.getJSONArray(ITEMS_ARRAY);

                // iterate through the array of artist JSON objects
                for (int i = 0; i < artists.length(); i++) {
                    JSONObject artist = artists.getJSONObject(i);
                    id = artist.getString(ARTIST_ID_ATTRIBUTE);
                    name = artist.getString(ARTIST_NAME_ATTRIBUTE);

                    // retrieve the image data
                    JSONArray images = artist.getJSONArray(IMAGES_ARRAY);
                    JSONObject image = null;

                    if(images != null && images.length() > 0) {
                        if(images.length() == 1) {
                            image = images.getJSONObject(0);
                        } else if(images.length() >= 2) {
                            image = images.getJSONObject(images.length() - 2);
                        }
                        url = image.getString(THUMBNAIL_URL_ATTRIBUTE);
                        width = image.getInt(IMAGE_WIDTH_ATTRIBUTE);
                        height = image.getInt(IMAGE_HEIGHT_ATTRIBUTE);
                    } else {
                        // set an empty string for the image path, substituted for a placeholder later in the code
                        url = "no image found";
                        width = 0;
                        height = 0;
                    }

                    // instantiate an artist pojo and add it to the manager
                    Artist artistPojo = new Artist(id, name, url, width, height);
                    artistList.add(artistPojo);

                }
            } else {
                Log.d(LOG_TAG, "No results found for search term");
            }

        } catch (JSONException e) {
            Log.e(LOG_TAG, "Failure parsing the JSON data: " + e.getMessage());
        }

        return artistList;
    }


    public static void showToast(Context context, String string) {
        Toast.makeText(context, string, Toast.LENGTH_SHORT).show();
    }



}
