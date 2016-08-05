package com.codepath.apps.allotweets.network.request;

/**
 * Created by ALLO on 2/8/16.
 */
public class TweetRequest {

    private String status; // This is the tweet's text

    private Long inReplyToTweetId; // in_reply_to_status_id

    private Double latitude; // lat

    private Double longitude; // long

    public TweetRequest() {

    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getInReplyToTweetId() {
        return inReplyToTweetId;
    }

    public void setInReplyToTweetId(Long inReplyToTweetId) {
        this.inReplyToTweetId = inReplyToTweetId;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }
}
