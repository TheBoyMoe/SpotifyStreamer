package com.example.spotifystreamer.model;

/**
 * POJO for holding the Artist information
 */
public class Artist {

    private String mId;
    private String mName;
    private String mImageUrl;

    public Artist() {}


    public Artist(String id, String name, String url) {
        mId = id;
        mName = name;
        mImageUrl = url;

    }


    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getImageUrl() {
        return mImageUrl;
    }


    @Override
    public String toString() {
        return String.format("Name: %s, url: %s, id: %s", getName(), getImageUrl(), getId());
    }




}
