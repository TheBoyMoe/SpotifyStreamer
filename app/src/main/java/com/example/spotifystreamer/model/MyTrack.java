package com.example.spotifystreamer.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 *
 * Parcelable POJO for holding the track information
 *
 * Used the Parcelabler tool @ http://www.parcelabler.com
 * by Dallas Gutauckis to build the Parcelable methods
 *
 */

public class MyTrack implements Parcelable {

    private String mArtistId;
    private String mArtistName;
    private String mTrackTitle;
    private String mAlbumTitle;
    private String mImageUrl;
    private String mThumbnailUrl;
    private String mPreviewUrl;
    private long mTrackDuration;


    public MyTrack() {}

    public MyTrack(String artistId,
                   String artistName,
                   String trackTitle,
                   String albumTitle,
                   String imageUrl,
                   String thumbnailUrl,
                   String previewUrl,
                   long trackDuration) {

        mArtistId = artistId;
        mArtistName = artistName;
        mTrackTitle = trackTitle;
        mAlbumTitle = albumTitle;
        mImageUrl = imageUrl;
        mThumbnailUrl = thumbnailUrl;
        mPreviewUrl = previewUrl;
        mTrackDuration = trackDuration;
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

    public String getImageUrl() {
        return mImageUrl;
    }

    public String getPreviewUrl() {
        return mPreviewUrl;
    }

    public long getTrackDuration() {
        return mTrackDuration;
    }

    public String getArtistId() {
        return mArtistId;
    }

    public void setTrackTitle(String trackTitle) {
        mTrackTitle = trackTitle;
    }

    @Override
    public String toString() {
        return String.format("MyTrack %s, by %s from the album %s",
                getTrackTitle(), getArtistName(), getAlbumTitle());
    }


    // methods req'd to implement the parcelable interface
    protected MyTrack(Parcel in) {
        mArtistId = in.readString();
        mArtistName = in.readString();
        mTrackTitle = in.readString();
        mAlbumTitle = in.readString();
        mImageUrl = in.readString();
        mThumbnailUrl = in.readString();
        mPreviewUrl = in.readString();
        mTrackDuration = in.readLong();
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
        dest.writeLong(mTrackDuration);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<MyTrack> CREATOR = new Parcelable.Creator<MyTrack>() {
        @Override
        public MyTrack createFromParcel(Parcel in) {
            return new MyTrack(in);
        }

        @Override
        public MyTrack[] newArray(int size) {
            return new MyTrack[size];
        }
    };



}
