package com.example.spotifystreamer.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 *  Used the Parcelabler tool @ http://www.parcelabler.com
 *  by Dallas Gutauckis to build the Parcelable methods
 */

public class Track implements Parcelable {

    private String mArtistId;
    private String mArtistName;
    private String mTrackTitle;
    private String mAlbumTitle;
    private String mImageUrl;
    private String mThumbnailUrl;
    private String mPreviewUrl;


    public Track() {}

    public Track(String artistId,
                 String artistName,
                 String trackTitle,
                 String albumTitle,
                 String imageUrl,
                 String thumbnailUrl,
                 String previewUrl) {

        mArtistId = artistId;
        mArtistName = artistName;
        mTrackTitle = trackTitle;
        mAlbumTitle = albumTitle;
        mImageUrl = imageUrl;
        mThumbnailUrl = thumbnailUrl;
        mPreviewUrl = previewUrl;
    }


    public String getArtistName() {
        return mArtistName;
    }

    public String getTrackTitle() {
        return mTrackTitle;
    }

    public String getAlbumTitle() {
        return mAlbumTitle;
    }
    
    public String getThumbnailUrl() {
        return mThumbnailUrl;
    }

    public String getArtistId() {
        return mArtistId;
    }

    public void setTrackTitle(String trackTitle) {
        mTrackTitle = trackTitle;
    }

    @Override
    public String toString() {
        return String.format("Track %s, by %s from the album %s",
                getTrackTitle(), getArtistName(), getAlbumTitle());
    }


    // methods req'd to implement the parcelable interface
    protected Track(Parcel in) {
        mArtistId = in.readString();
        mArtistName = in.readString();
        mTrackTitle = in.readString();
        mAlbumTitle = in.readString();
        mImageUrl = in.readString();
        mThumbnailUrl = in.readString();
        mPreviewUrl = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mArtistId);
        dest.writeString(mArtistName);
        dest.writeString(mTrackTitle);
        dest.writeString(mAlbumTitle);
        dest.writeString(mImageUrl);
        dest.writeString(mThumbnailUrl);
        dest.writeString(mPreviewUrl);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Track> CREATOR = new Parcelable.Creator<Track>() {
        @Override
        public Track createFromParcel(Parcel in) {
            return new Track(in);
        }

        @Override
        public Track[] newArray(int size) {
            return new Track[size];
        }
    };



}
