package com.codepath.apps.allotweets.model;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

/**
 * Created by ALLO on 3/8/16.
 */
@Parcel
public class Media {

    @SerializedName("id")
    Long mediaId;

    @SerializedName("media_url")
    String mediaUrl;

    @SerializedName("url")
    String url;

    @SerializedName("type")
    String type;

    @SerializedName("sizes")
    Size size;

    public Media() {

    }

    public Long getMediaId() {
        return mediaId;
    }

    public void setMediaId(Long mediaId) {
        this.mediaId = mediaId;
    }

    public String getMediaUrl() {
        return mediaUrl;
    }

    public void setMediaUrl(String mediaUrl) {
        this.mediaUrl = mediaUrl;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Size getSize() {
        return size;
    }

    public void setSize(Size size) {
        this.size = size;
    }
}
