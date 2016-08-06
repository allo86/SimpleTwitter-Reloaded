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

    @SerializedName("aspect_Ratio")
    @Expose
    ArrayList<Integer> aspectRatio;

    @SerializedName("duration_millis")
    @Expose
    Long durationMillis;

    @SerializedName("variants")
    @Expose
    ArrayList<VideoVariant> variants;

    public VideoInfo() {

    }

    public double getAspectRatio() {
        if (aspectRatio != null && aspectRatio.size() >= 2) {
            return ((double) aspectRatio.get(0) / (double) aspectRatio.get(1));
        }
        return 0;
    }

    public void setAspectRatio(ArrayList<Integer> aspectRatio) {
        this.aspectRatio = aspectRatio;
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
