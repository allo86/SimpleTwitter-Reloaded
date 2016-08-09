package com.codepath.apps.allotweets.ui.timeline;

import android.os.Bundle;
import android.widget.Toast;

import com.codepath.apps.allotweets.R;
import com.codepath.apps.allotweets.model.Tweet;
import com.codepath.apps.allotweets.model.TwitterUser;
import com.codepath.apps.allotweets.network.TwitterError;
import com.codepath.apps.allotweets.network.callbacks.TimelineCallback;
import com.codepath.apps.allotweets.network.request.TimelineRequest;
import com.codepath.apps.allotweets.ui.base.BaseTimelineFragment;

import org.parceler.Parcels;

import java.util.ArrayList;

/**
 * User Timeline
 * <p/>
 * Created by ALLO on 7/8/16.
 */
public class UserTimelineFragment extends BaseTimelineFragment {

    public static UserTimelineFragment newInstance(TwitterUser user) {
        UserTimelineFragment fragment = new UserTimelineFragment();
        Bundle args = new Bundle();
        args.putParcelable(BaseTimelineFragment.TWITTER_USER, Parcels.wrap(user));
        fragment.setArguments(args);
        return fragment;
    }

    TwitterUser mUser;

    @Override
    protected void loadTweets(Long sinceTweetId, Long maxTweetId) {
        Toast.makeText(getActivity(), R.string.loading_more_tweets, Toast.LENGTH_SHORT).show();

        TimelineRequest request = new TimelineRequest();
        request.setSinceId(sinceTweetId);
        request.setMaxId(maxTweetId);
        request.setUserId(mUser.getUserId());

        mTwitterClient.getUserTimeline(request, new TimelineCallback() {
            @Override
            public void onSuccess(ArrayList<Tweet> tweets) {
                // Save in local database
                saveTweets(tweets);

                // Process tweets
                processTweets(tweets);
            }

            @Override
            public void onError(TwitterError error) {
                Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_LONG).show();

                //updateToolbarBehaviour();

                // Now we call setRefreshing(false) to signal refresh has finished
                mSwipeToRefresh.setRefreshing(false);
            }
        });
    }

    @Override
    protected void initializeDataFromArguments(Bundle args) {
        mUser = Parcels.unwrap(args.getParcelable(BaseTimelineFragment.TWITTER_USER));
    }
}
