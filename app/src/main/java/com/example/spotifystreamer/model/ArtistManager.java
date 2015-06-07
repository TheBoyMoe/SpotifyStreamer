package com.example.spotifystreamer.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Singleton for managing artist objects
 */
public class ArtistManager {

    private static ArtistManager sArtistManager;
    private List<Artist> mList;


    // private constructor
    private ArtistManager() {
        mList = new ArrayList<>();
    }


    // return an instance of the ArtistManager object
    public static ArtistManager getArtistManager() {
        if(sArtistManager == null) {
            sArtistManager = new ArtistManager();
        }
        return sArtistManager;
    }


    public void addArtist(Artist artist) {
        if(artist != null)
            mList.add(artist);
    }


    public boolean removeArtist(Artist artist) {
        boolean success = false;
        if(artist != null) {
            if(!mList.isEmpty()) {

                for (int i = 0; i < mList.size(); i++) {
                    if(mList.get(i).equals(artist)) {
                        mList.remove(i);
                        success = true;
                    }
                }
            }
        }

        return success;
    }

    // retrieve arraylist of artist objects
    public List<Artist> getList() {
        return mList;
    }


    // clear the arraylist
    public void clear() {
        mList.clear();
    }

}
