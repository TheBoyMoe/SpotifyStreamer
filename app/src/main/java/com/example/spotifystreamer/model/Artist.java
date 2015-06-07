package com.example.spotifystreamer.model;

/**
 * POJO for holding the Artist information
 */
public class Artist {

    private String mId;
    private String mName;
    private String mUrl;
    private int mWidth;
    private int mHeight;

    public Artist() {}


    public Artist(String id, String name, String url, int width, int height) {
        mId = id;
        mName = name;
        mUrl = url;
        mWidth = width;
        mHeight = height;
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

    public String getUrl() {
        return mUrl;
    }

    public void setUrl(String url) {
        mUrl = url;
    }

    public int getWidth() {
        return mWidth;
    }

    public void setWidth(int width) {
        mWidth = width;
    }

    public int getHeight() {
        return mHeight;
    }

    public void setHeight(int height) {
        mHeight = height;
    }


    @Override
    public String toString() {
        return String.format("Name: %s, url: %s", getName(), getUrl());
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Artist artist = (Artist) o;

        if (getWidth() != artist.getWidth()) return false;
        if (getHeight() != artist.getHeight()) return false;
        if (!getId().equals(artist.getId())) return false;
        if (!getName().equals(artist.getName())) return false;
        return getUrl().equals(artist.getUrl());

    }

    @Override
    public int hashCode() {
        int result = getId().hashCode();
        result = 31 * result + getName().hashCode();
        result = 31 * result + getUrl().hashCode();
        result = 31 * result + getWidth();
        result = 31 * result + getHeight();
        return result;
    }


}
