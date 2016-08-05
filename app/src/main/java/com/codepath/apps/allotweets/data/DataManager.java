package com.codepath.apps.allotweets.data;

import com.codepath.apps.allotweets.model.TwitterUser;

/**
 * Created by ALLO on 4/8/16.
 */
public class DataManager {

    private static final DataManager instance = new DataManager();

    protected DataManager() {
    }

    public static DataManager sharedInstance() {
        return instance;
    }

    private TwitterUser mUser;

    public TwitterUser getUser() {
        return mUser;
    }

    public void setUser(TwitterUser user) {
        this.mUser = user;
    }
}
