package com.codepath.apps.allotweets.network.callbacks;

import com.codepath.apps.allotweets.network.TwitterError;
import com.codepath.apps.allotweets.network.response.TwitterUsersResponse;

/**
 * Created by ALLO on 8/8/16.
 */
public interface TwitterUsersCallback {

    void onSuccess(TwitterUsersResponse response);

    void onError(TwitterError error);
}
