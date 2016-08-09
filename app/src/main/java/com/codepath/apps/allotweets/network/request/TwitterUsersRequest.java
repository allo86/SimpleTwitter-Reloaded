package com.codepath.apps.allotweets.network.request;

/**
 * Created by ALLO on 8/8/16.
 */
public class TwitterUsersRequest {

    private Long userId;

    private String cursor;

    public TwitterUsersRequest() {

    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getCursor() {
        return cursor;
    }

    public void setCursor(String cursor) {
        this.cursor = cursor;
    }
}
