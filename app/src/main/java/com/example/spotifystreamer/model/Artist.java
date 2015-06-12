package com.example.spotifystreamer.model;

/**
 * POJO for holding the Artist information
 */
public class Artist {

    private String mId;
    private String mName;
    private String mUrl;

    public Artist() {}


    public Artist(String id, String name, String url) {
        mId = id;
        mName = name;
        mUrl = url;

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


    @Override
    public String toString() {
        return String.format("Name: %s, url: %s", getName(), getUrl());
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Artist artist = (Artist) o;

        if (getId() != null ? !getId().equals(artist.getId()) : artist.getId() != null)
            return false;
        if (getName() != null ? !getName().equals(artist.getName()) : artist.getName() != null)
            return false;
        return !(getUrl() != null ? !getUrl().equals(artist.getUrl()) : artist.getUrl() != null);

    }

    @Override
    public int hashCode() {
        int result = getId() != null ? getId().hashCode() : 0;
        result = 31 * result + (getName() != null ? getName().hashCode() : 0);
        result = 31 * result + (getUrl() != null ? getUrl().hashCode() : 0);
        return result;
    }



}
