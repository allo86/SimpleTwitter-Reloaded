package com.codepath.apps.allotweets.network.request;

/**
 * Created by ALLO on 6/8/16.
 */
public class RetweetRequest {

    private Long tweetId;

    private boolean undo;

    public RetweetRequest() {
    }

    public Long getTweetId() {
        return tweetId;
    }

    public void setTweetId(Long tweetId) {
        this.tweetId = tweetId;
    }

    public boolean isUndo() {
        return undo;
    }

    public void setUndo(boolean undo) {
        this.undo = undo;
    }
}
