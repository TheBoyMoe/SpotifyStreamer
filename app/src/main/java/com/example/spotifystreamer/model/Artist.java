package com.example.spotifystreamer.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Parcelable POJO for holding the Artist information
 *
 * Used the Parcelabler tool @ http://www.parcelabler.com
 * by Dallas Gutauckis to build the Parcelable methods
 */
public class Artist implements Parcelable{

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

    // methods required for the Parcelable interface
    protected Artist(Parcel in) {
        mId = in.readString();
        mName = in.readString();
        mImageUrl = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mId);
        dest.writeString(mName);
        dest.writeString(mImageUrl);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Artist> CREATOR = new Parcelable.Creator<Artist>() {
        @Override
        public Artist createFromParcel(Parcel in) {
            return new Artist(in);
        }

        @Override
        public Artist[] newArray(int size) {
            return new Artist[size];
        }
    };


}
