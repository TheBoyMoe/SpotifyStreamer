package com.example.spotifystreamer.model;


public class Track {

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

    public void setTrackTitle(String trackTitle) {
        mTrackTitle = trackTitle;
    }

    @Override
    public String toString() {
        return String.format("Track %s, by %s from the album %s",
                getTrackTitle(), getArtistName(), getAlbumTitle());
    }





}
