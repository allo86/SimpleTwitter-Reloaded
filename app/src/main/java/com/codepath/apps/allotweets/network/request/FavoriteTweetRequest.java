package com.codepath.apps.allotweets.network.request;

/**
 * Created by ALLO on 6/8/16.
 */
public class FavoriteTweetRequest {

    private Long tweetId;

    private boolean undo;

    public FavoriteTweetRequest() {

    }

    public FavoriteTweetRequest(Long tweetId, boolean undo) {
        this.tweetId = tweetId;
        this.undo = undo;
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
