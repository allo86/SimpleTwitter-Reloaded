package com.codepath.apps.allotweets.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

import java.util.ArrayList;

/**
 * Created by ALLO on 3/8/16.
 */
@Parcel
public class Entities {

    @SerializedName("media")
    @Expose
    ArrayList<Media> media;

    public Entities() {

    }

    public ArrayList<Media> getMedia() {
        return media;
    }

    public void setMedia(ArrayList<Media> media) {
        this.media = media;
    }
}
