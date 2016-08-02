package com.codepath.apps.allotweets.model;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

/**
 * Created by ALLO on 1/8/16.
 */
public class TwitterUser {

    @SerializedName("id")
    Long userId;

    @SerializedName("name")
    String name;

    @SerializedName("url")
    String url;

    @SerializedName("profile_sidebar_fill_color")
    String profileSidebarFillColor;

    @SerializedName("profile_background_tile")
    boolean profileBackgroundTile;

    @SerializedName("profile_sidebar_border_color")
    String profileSidebarBorderColor;

    @SerializedName("profile_image_url")
    String profileImageUrl;

    @SerializedName("created_at")
    Date createdAt;

    @SerializedName("location")
    String location;

    @SerializedName("follow_request_sent")
    boolean followRequestSent;

    @SerializedName("verified")
    boolean verified;

    @SerializedName("show_all_inline_media")
    boolean showAllInlineMedia;

    @SerializedName("following")
    boolean following;

    @SerializedName("friends_count")
    int friendsCount;

    public TwitterUser() {

    }
}
