package com.codepath.apps.allotweets.model;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

import java.util.ArrayList;

/**
 * Created by ALLO on 1/8/16.
 */
@Parcel
public class Coordinates {

    @SerializedName("type")
    String type;

    @SerializedName("cordinates")
    ArrayList<Double> cordinates;

    public Coordinates() {

    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public ArrayList<Double> getCordinates() {
        return cordinates;
    }

    public void setCordinates(ArrayList<Double> cordinates) {
        this.cordinates = cordinates;
    }
}
