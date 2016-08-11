package com.codepath.apps.allotweets.model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;
import com.codepath.apps.allotweets.ui.utils.TimeFormatter;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Tweet
 * <p/>
 * Created by ALLO on 1/8/16.
 */
@Parcel(analyze = Tweet.class)
@Table(name = "tweets")
public class Tweet extends Model {

    @SerializedName("id")
    @Expose
    @Column(name = "tweet_id", unique = true, onUniqueConflict = Column.ConflictAction.REPLACE)
    Long tweetId;

    @SerializedName("text")
    @Expose
    @Column(name = "text")
    String text;

    @SerializedName("coordinates")
    @Expose
    Coordinates coordinates;

    @SerializedName("truncated")
    @Expose
    boolean truncated;

    @SerializedName("created_at")
    @Expose
    @Column(name = "created_at")
    Date createdAt;

    @SerializedName("source")
    @Expose
    String source;

    @SerializedName("favorited")
    @Expose
    @Column(name = "favorite")
    boolean favorite;

    @SerializedName("retweeted")
    @Expose
    @Column(name = "retweeted")
    boolean retweeted;

    @SerializedName("retweet_count")
    @Expose
    @Column(name = "retweet_count")
    int retweetCount;

    @SerializedName("retweeted_status")
    @Expose
    Tweet retweetedStatus;

    @SerializedName("favorite_count")
    @Expose
    @Column(name = "favorite_count")
    int favoriteCount;

    @SerializedName("user")
    @Expose
    @Column(name = "users", onUpdate = Column.ForeignKeyAction.CASCADE, onDelete = Column.ForeignKeyAction.CASCADE)
    TwitterUser user;

    @SerializedName("entities")
    @Expose
    Entities entities;

    @SerializedName("extended_entities")
    @Expose
    Entities extendedEntities;

    public Tweet() {
        super();
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

    public Tweet getRetweetedStatus() {
        return retweetedStatus;
    }

    public void setRetweetedStatus(Tweet retweetedStatus) {
        this.retweetedStatus = retweetedStatus;
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

    public Entities getExtendedEntities() {
        return extendedEntities;
    }

    public void setExtendedEntities(Entities extendedEntities) {
        this.extendedEntities = extendedEntities;
    }

    /* Helpers */

    public String getFormattedCreatedAtDate() {
        if (getCreatedAt() != null) {
            return DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM, Locale.getDefault()).format(getCreatedAt());
        }
        return null;
    }

    public String getRelativeTimeAgo() {
        //long dateMillis = getCreatedAt().getTime();
        //return DateUtils.getRelativeTimeSpanString(dateMillis, System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS).toString();
        return TimeFormatter.getTimeDifference(getCreatedAt());
    }

    public boolean hasVideo() {
        return extendedEntities != null && extendedEntities.hasVideo();
    }

    public boolean hasPhoto() {
        return entities != null && entities.hasPhoto();
    }

    public Media getPhoto() {
        return entities.getPhoto();
    }

    public VideoVariant getVideo() {
        return extendedEntities.getVideo();
    }

    public VideoInfo getVideoInfo() {
        return extendedEntities.getVideoInfo();
    }

    // Record Finders
    public static Tweet byTweetId(Long tweetId) {
        return new Select().from(Tweet.class).where("tweet_id = ?", tweetId).executeSingle();
    }

    public static List<Tweet> recentTweets() {
        return new Select().from(Tweet.class).orderBy("tweet_id DESC").limit("300").execute();
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
