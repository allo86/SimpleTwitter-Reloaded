package com.codepath.apps.allotweets.network.request;

/**
 * Created by ALLO on 1/8/16.
 */
public class HomeTimelineRequest {

    private int count = 50;

    private Long sinceId;

    public HomeTimelineRequest() {

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
}
