package com.codepath.apps.allotweets.ui.details;

import android.os.Bundle;

import com.codepath.apps.allotweets.model.Tweet;

import org.parceler.Parcels;

import icepick.Bundler;

/**
 * Created by ALLO on 6/8/16.
 */
public class TweetDetailActivityBundler implements Bundler<Tweet> {

    @Override
    public void put(String s, Tweet tweet, Bundle bundle) {
        bundle.putParcelable("tweet", Parcels.wrap(tweet));
    }

    @Override
    public Tweet get(String s, Bundle bundle) {
        return Parcels.unwrap(bundle.getParcelable("tweet"));
    }

}
