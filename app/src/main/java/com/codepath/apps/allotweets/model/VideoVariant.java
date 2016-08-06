package com.codepath.apps.allotweets.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

/**
 * Created by ALLO on 4/8/16.
 */
@Parcel
public class VideoVariant {

    @SerializedName("bitrate")
    @Expose
    int bitrate;

    @SerializedName("content_type")
    @Expose
    String contentType;

    @SerializedName("url")
    @Expose
    String url;

    public VideoVariant() {

    }

    public int getBitrate() {
        return bitrate;
    }

    public void setBitrate(int bitrate) {
        this.bitrate = bitrate;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
