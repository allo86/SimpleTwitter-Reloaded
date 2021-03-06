package com.codepath.apps.allotweets.ui.timeline;

import android.os.Bundle;
import android.widget.Toast;

import com.codepath.apps.allotweets.R;
import com.codepath.apps.allotweets.eventbus.TweetEvent;
import com.codepath.apps.allotweets.model.Tweet;
import com.codepath.apps.allotweets.network.TwitterError;
import com.codepath.apps.allotweets.network.callbacks.TimelineCallback;
import com.codepath.apps.allotweets.network.request.TimelineRequest;
import com.codepath.apps.allotweets.ui.base.BaseTimelineFragment;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;

/**
 * Mentions
 * <p/>
 * Created by ALLO on 7/8/16.
 */
public class MentionsTimelineFragment extends BaseTimelineFragment {
    @Override
    protected void loadTweets(Long sinceTweetId, Long maxTweetId) {
        Toast.makeText(getActivity(), R.string.loading_more_results, Toast.LENGTH_SHORT).show();

        TimelineRequest request = new TimelineRequest();
        request.setSinceId(sinceTweetId);
        request.setMaxId(maxTweetId);

        mTwitterClient.getMentionsTimeline(request, new TimelineCallback() {
            @Override
            public void onSuccess(ArrayList<Tweet> tweets) {
                // Save in local database
                //saveTweets(tweets);

                // Process tweets
                processTweets(tweets);
            }

            @Override
            public void onError(TwitterError error) {
                Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_LONG).show();

                updateToolbarBehaviour();

                // Now we call setRefreshing(false) to signal refresh has finished
                mSwipeToRefresh.setRefreshing(false);
            }
        });
    }

    @Override
    protected void initializeDataFromArguments(Bundle args) {

    }

    /* EventBus */
    @Override
    @Subscribe
    public void onEvent(TweetEvent event) {
        // Do nothing by now
    }
    /* EventBus */
}
