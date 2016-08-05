package com.codepath.apps.allotweets.network.callbacks;

import com.codepath.apps.allotweets.model.Tweet;
import com.codepath.apps.allotweets.network.TwitterError;

/**
 * Created by ALLO on 2/8/16.
 */
public interface PostTweetCallback {

    void onSuccess(Tweet tweet);

    void onError(TwitterError error);
}
