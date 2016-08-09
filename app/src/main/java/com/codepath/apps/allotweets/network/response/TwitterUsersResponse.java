package com.codepath.apps.allotweets.network.response;

import com.codepath.apps.allotweets.model.TwitterUser;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

import java.util.ArrayList;

/**
 * Created by ALLO on 8/8/16.
 */
@Parcel
public class TwitterUsersResponse {

    @SerializedName("previous_cursor_str")
    @Expose
    String previousCursor;

    @SerializedName("next_cursor_str")
    @Expose
    String nextCursor;

    @SerializedName("users")
    @Expose
    ArrayList<TwitterUser> users;

    public TwitterUsersResponse() {

    }

    public String getPreviousCursor() {
        return previousCursor;
    }

    public void setPreviousCursor(String previousCursor) {
        this.previousCursor = previousCursor;
    }

    public String getNextCursor() {
        return nextCursor;
    }

    public void setNextCursor(String nextCursor) {
        this.nextCursor = nextCursor;
    }

    public ArrayList<TwitterUser> getUsers() {
        return users;
    }

    public void setUsers(ArrayList<TwitterUser> users) {
        this.users = users;
    }
}
