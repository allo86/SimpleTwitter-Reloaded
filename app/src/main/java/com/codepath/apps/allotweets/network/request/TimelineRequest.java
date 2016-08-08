package com.codepath.apps.allotweets.network.request;

/**
 * Created by ALLO on 1/8/16.
 */
public class TimelineRequest {

    private int count = 50;

    private Long sinceId;

    private Long maxId;

    private Long userId;

    public TimelineRequest() {

    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public Long getSinceId() {
        return sinceId;
    }

    public void setSinceId(Long sinceId) {
        this.sinceId = sinceId;
    }

    public Long getMaxId() {
        return maxId;
    }

    public void setMaxId(Long maxId) {
        this.maxId = maxId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
