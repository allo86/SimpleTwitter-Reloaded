package com.codepath.apps.allotweets.network.callbacks;

import com.codepath.apps.allotweets.model.Message;
import com.codepath.apps.allotweets.network.TwitterError;

import java.util.ArrayList;

/**
 * Created by ALLO on 9/8/16.
 */
public interface MessagesCallback {

    void onSuccess(ArrayList<Message> messages);

    void onError(TwitterError error);
}
