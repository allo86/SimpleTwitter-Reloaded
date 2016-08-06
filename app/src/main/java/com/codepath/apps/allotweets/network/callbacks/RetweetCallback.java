package com.codepath.apps.allotweets.network.callbacks;

import com.codepath.apps.allotweets.model.Tweet;
import com.codepath.apps.allotweets.network.TwitterError;

/**
 * Created by ALLO on 6/8/16.
 */
public interface RetweetCallback {

    void onSuccess(Tweet retweet);

    void onError(TwitterError error);
}
