package com.codepath.apps.allotweets.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

/**
 * Created by ALLO on 9/8/16.
 */
@Parcel
public class Hashtag {

    @SerializedName("text")
    @Expose
    String text;

    public Hashtag() {

    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getTextForDisplay() {
        return "#" + text;
    }
}
