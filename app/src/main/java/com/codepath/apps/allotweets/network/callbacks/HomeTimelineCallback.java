package com.codepath.apps.allotweets.network.callbacks;

import com.codepath.apps.allotweets.model.Tweet;
import com.codepath.apps.allotweets.network.TwitterError;

import java.util.ArrayList;

/**
 * Created by ALLO on 1/8/16.
 */
public interface HomeTimelineCallback {

    void onSuccess(ArrayList<Tweet> tweets);

    void onError(TwitterError error);

}
