package com.codepath.apps.allotweets.model;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

import java.util.Date;

/**
 * Created by ALLO on 1/8/16.
 */
@Parcel
public class TwitterUser {

    @SerializedName("id")
    Long userId;

    @SerializedName("name")
    String name;

    @SerializedName("screen_name")
    String screenname;

    @SerializedName("description")
    String description;

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

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getScreenname() {
        return "@" + screenname;
    }

    public void setScreenname(String screenname) {
        this.screenname = screenname;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getProfileSidebarFillColor() {
        return profileSidebarFillColor;
    }

    public void setProfileSidebarFillColor(String profileSidebarFillColor) {
        this.profileSidebarFillColor = profileSidebarFillColor;
    }

    public boolean isProfileBackgroundTile() {
        return profileBackgroundTile;
    }

    public void setProfileBackgroundTile(boolean profileBackgroundTile) {
        this.profileBackgroundTile = profileBackgroundTile;
    }

    public String getProfileSidebarBorderColor() {
        return profileSidebarBorderColor;
    }

    public void setProfileSidebarBorderColor(String profileSidebarBorderColor) {
        this.profileSidebarBorderColor = profileSidebarBorderColor;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public boolean isFollowRequestSent() {
        return followRequestSent;
    }

    public void setFollowRequestSent(boolean followRequestSent) {
        this.followRequestSent = followRequestSent;
    }

    public boolean isVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }

    public boolean isShowAllInlineMedia() {
        return showAllInlineMedia;
    }

    public void setShowAllInlineMedia(boolean showAllInlineMedia) {
        this.showAllInlineMedia = showAllInlineMedia;
    }

    public boolean isFollowing() {
        return following;
    }

    public void setFollowing(boolean following) {
        this.following = following;
    }

    public int getFriendsCount() {
        return friendsCount;
    }

    public void setFriendsCount(int friendsCount) {
        this.friendsCount = friendsCount;
    }
}
