package com.codepath.apps.allotweets.model;

import android.text.format.DateUtils;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Tweet
 * <p/>
 * Created by ALLO on 1/8/16.
 */
@Parcel
public class Tweet {

    @SerializedName("id")
    Long tweetId;

    @SerializedName("text")
    String text;

    @SerializedName("coordinates")
    Coordinates coordinates;

    @SerializedName("truncated")
    boolean truncated;

    @SerializedName("created_at")
    Date createdAt;

    @SerializedName("source")
    String source;

    @SerializedName("favorited")
    boolean favorite;

    @SerializedName("retweeted")
    boolean retweeted;

    @SerializedName("retweet_count")
    int retweetCount;

    @SerializedName("favorite_count")
    int favoriteCount;

    @SerializedName("user")
    TwitterUser user;

    @SerializedName("entities")
    Entities entities;

    public Tweet() {

    }

    public Long getTweetId() {
        return tweetId;
    }

    public void setTweetId(Long tweetId) {
        this.tweetId = tweetId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(Coordinates coordinates) {
        this.coordinates = coordinates;
    }

    public boolean isTruncated() {
        return truncated;
    }

    public void setTruncated(boolean truncated) {
        this.truncated = truncated;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }

    public boolean isRetweeted() {
        return retweeted;
    }

    public void setRetweeted(boolean retweeted) {
        this.retweeted = retweeted;
    }

    public int getRetweetCount() {
        return retweetCount;
    }

    public void setRetweetCount(int retweetCount) {
        this.retweetCount = retweetCount;
    }

    public int getFavoriteCount() {
        return favoriteCount;
    }

    public void setFavoriteCount(int favoriteCount) {
        this.favoriteCount = favoriteCount;
    }

    public TwitterUser getUser() {
        return user;
    }

    public void setUser(TwitterUser user) {
        this.user = user;
    }

    public Entities getEntities() {
        return entities;
    }

    public void setEntities(Entities entities) {
        this.entities = entities;
    }

    public String getFormattedCreatedAtDate() {
        if (getCreatedAt() != null) {
            return DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM, Locale.getDefault()).format(getCreatedAt());
        }
        return null;
    }

    public String getRelativeTimeAgo() {
        long dateMillis = getCreatedAt().getTime();
        return DateUtils.getRelativeTimeSpanString(dateMillis, System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS).toString();
    }

    @Override
    public boolean equals(Object object) {
        if (object != null && object instanceof Tweet) {
            Tweet tweet = (Tweet) object;
            return this.getTweetId().equals(tweet.getTweetId());
        }
        return false;
    }
}
