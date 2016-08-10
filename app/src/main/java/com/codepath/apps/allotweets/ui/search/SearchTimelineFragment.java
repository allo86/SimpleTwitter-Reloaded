package com.codepath.apps.allotweets.ui.search;

import android.os.Bundle;
import android.widget.Toast;

import com.codepath.apps.allotweets.R;
import com.codepath.apps.allotweets.model.Tweet;
import com.codepath.apps.allotweets.network.TwitterError;
import com.codepath.apps.allotweets.network.callbacks.TimelineCallback;
import com.codepath.apps.allotweets.network.request.TimelineRequest;
import com.codepath.apps.allotweets.ui.base.BaseTimelineFragment;

import java.util.ArrayList;

import icepick.State;

/**
 * Search Timeline
 * <p/>
 * Created by ALLO on 8/8/16.
 */
public class SearchTimelineFragment extends BaseTimelineFragment {

    public static final String QUERY = "QUERY";

    public static SearchTimelineFragment newInstance(String query) {
        SearchTimelineFragment fragment = new SearchTimelineFragment();
        Bundle args = new Bundle();
        args.putString(QUERY, query);
        fragment.setArguments(args);
        return fragment;
    }

    @State
    String mQuery;

    @Override
    protected void loadTweets(Long sinceTweetId, Long maxTweetId) {
        Toast.makeText(getActivity(), R.string.loading_more_results, Toast.LENGTH_SHORT).show();

        TimelineRequest request = new TimelineRequest();
        request.setSinceId(sinceTweetId);
        request.setMaxId(maxTweetId);
        request.setQuery(mQuery);

        mTwitterClient.searchTweets(request, new TimelineCallback() {
            @Override
            public void onSuccess(ArrayList<Tweet> tweets) {
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
        mQuery = args.getString(QUERY);
    }
}
