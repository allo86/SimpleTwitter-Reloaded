package com.codepath.apps.allotweets.model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

import java.util.Date;
import java.util.List;

/**
 * Created by ALLO on 1/8/16.
 */
@Parcel(analyze = TwitterUser.class)
@Table(name = "users")
public class TwitterUser extends Model {

    @SerializedName("id")
    @Expose
    @Column(name = "user_id", unique = true, onUniqueConflict = Column.ConflictAction.REPLACE)
    Long userId;

    @SerializedName("name")
    @Expose
    @Column(name = "name")
    String name;

    @SerializedName("screen_name")
    @Expose
    @Column(name = "screen_name")
    String screenname;

    @SerializedName("description")
    @Expose
    @Column(name = "description")
    String description;

    @SerializedName("url")
    @Expose
    @Column(name = "url")
    String url;

    @SerializedName("profile_sidebar_fill_color")
    @Expose
    String profileSidebarFillColor;

    @SerializedName("profile_background_tile")
    @Expose
    boolean profileBackgroundTile;

    @SerializedName("profile_sidebar_border_color")
    @Expose
    String profileSidebarBorderColor;

    @SerializedName("profile_image_url")
    @Expose
    @Column(name = "profile_image_url")
    String profileImageUrl;

    @SerializedName("created_at")
    @Expose
    Date createdAt;

    @SerializedName("location")
    @Expose
    String location;

    @SerializedName("follow_request_sent")
    @Expose
    boolean followRequestSent;

    @SerializedName("verified")
    @Expose
    boolean verified;

    @SerializedName("show_all_inline_media")
    @Expose
    boolean showAllInlineMedia;

    @SerializedName("following")
    @Expose
    boolean following;

    @SerializedName("friends_count")
    @Expose
    int friendsCount;

    // Used to return tweets from another table based on the foreign key
    public List<Tweet> tweets() {
        return getMany(Tweet.class, "user");
    }

    public TwitterUser() {
        super();
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

    // Record Finders
    public static TwitterUser byUserId(Long userId) {
        return new Select().from(TwitterUser.class).where("user_id = ?", userId).executeSingle();
    }
}
