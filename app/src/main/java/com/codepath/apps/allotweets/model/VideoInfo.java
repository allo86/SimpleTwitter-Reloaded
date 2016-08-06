package com.codepath.apps.allotweets.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

import java.util.ArrayList;

/**
 * Created by ALLO on 3/8/16.
 */
@Parcel
public class VideoInfo {

    @SerializedName("duration_millis")
    @Expose
    Long durationMillis;

    @SerializedName("variants")
    @Expose
    ArrayList<VideoVariant> variants;

    public VideoInfo() {

    }

    public Long getDurationMillis() {
        return durationMillis;
    }

    public void setDurationMillis(Long durationMillis) {
        this.durationMillis = durationMillis;
    }

    public ArrayList<VideoVariant> getVariants() {
        return variants;
    }

    public void setVariants(ArrayList<VideoVariant> variants) {
        this.variants = variants;
    }
}
