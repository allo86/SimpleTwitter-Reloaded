package com.codepath.apps.allotweets.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

/**
 * Created by ALLO on 3/8/16.
 */
@Parcel
public class Media {

    @SerializedName("id")
    @Expose
    Long mediaId;

    @SerializedName("media_url")
    @Expose
    String mediaUrl;

    @SerializedName("url")
    @Expose
    String url;

    @SerializedName("type")
    @Expose
    String type;

    @SerializedName("sizes")
    @Expose
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
