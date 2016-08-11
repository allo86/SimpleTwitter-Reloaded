package com.codepath.apps.allotweets.eventbus;

import com.codepath.apps.allotweets.model.Tweet;

/**
 * Created by ALLO on 10/8/16.
 */
public class TweetEvent {

    private Tweet tweet;

    public TweetEvent(Tweet tweet) {
        this.tweet = tweet;
    }

    public Tweet getTweet() {
        return tweet;
    }

    public void setTweet(Tweet tweet) {
        this.tweet = tweet;
    }
}
