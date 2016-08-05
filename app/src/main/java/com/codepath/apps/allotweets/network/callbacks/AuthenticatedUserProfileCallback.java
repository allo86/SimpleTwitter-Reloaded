package com.codepath.apps.allotweets.network.callbacks;

import com.codepath.apps.allotweets.model.TwitterUser;
import com.codepath.apps.allotweets.network.TwitterError;

/**
 * Created by ALLO on 2/8/16.
 */
public interface AuthenticatedUserProfileCallback {

    void onSuccess(TwitterUser user);

    void onError(TwitterError error);
}
