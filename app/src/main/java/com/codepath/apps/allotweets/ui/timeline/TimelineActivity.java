package com.codepath.apps.allotweets.ui.timeline;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.codepath.apps.allotweets.R;
import com.codepath.apps.allotweets.model.Tweet;
import com.codepath.apps.allotweets.network.TwitterError;
import com.codepath.apps.allotweets.network.callbacks.HomeTimelineCallback;
import com.codepath.apps.allotweets.network.request.HomeTimelineRequest;
import com.codepath.apps.allotweets.ui.base.BaseActivity;
import com.codepath.apps.allotweets.ui.utils.EndlessRecyclerViewScrollListener;
import com.codepath.apps.allotweets.ui.utils.LinearLayoutManager;

import java.util.ArrayList;

import butterknife.BindView;

public class TimelineActivity extends BaseActivity {

    @BindView(R.id.swipe_container)
    SwipeRefreshLayout mSwipeToRefresh;

    @BindView(R.id.rv_timeline)
    RecyclerView mRecyclerView;

    private TimelineAdapter mAdapter;
    private ArrayList<Tweet> mTweets;
    private LinearLayoutManager mLayoutManager;

    @Override
    protected int getLayoutResourceID() {
        return R.layout.activity_timeline;
    }

    @Override
    protected void initializeUI() {
        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        EndlessRecyclerViewScrollListener endlessListener = new EndlessRecyclerViewScrollListener(mLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to the bottom of the list
                Log.d(TAG_LOG, "loadNextPage: " + String.valueOf(page));
                loadTimeline(mTweets.get(totalItemsCount - 1).getTweetId());
            }
        };
        mRecyclerView.addOnScrollListener(endlessListener);

        mAdapter = new TimelineAdapter(mTweets, new TimelineAdapter.OnTimelineAdapterListener() {
            @Override
            public void didSelectTweet(Tweet tweet) {
                goToTweetDetails(tweet);
            }
        });
        mRecyclerView.setAdapter(mAdapter);

        // Swipe to refresh
        mSwipeToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadTimeline(null);
            }
        });
        mSwipeToRefresh.setColorSchemeResources(R.color.colorPrimary,
                R.color.colorPrimaryDark);

        updateToolbarBehaviour();
    }

    @Override
    protected void initializeDataFromIntentBundle(Bundle extras) {

    }

    @Override
    protected void showData() {
        if (mTweets != null) {
            mAdapter.notifyDataSetChanged(mTweets);
        } else {
            loadTimeline(null);
        }
    }

    private void loadTimeline(Long sinceId) {
        HomeTimelineRequest request = new HomeTimelineRequest();
        request.setSinceId(sinceId);

        mTwitterClient.getHomeTimeline(request, new HomeTimelineCallback() {
            @Override
            public void onSuccess(ArrayList<Tweet> tweets) {
                if (tweets != null) {
                    if (mTweets == null) {
                        mTweets = new ArrayList<>();
                    }
                    for (Tweet tweet : tweets) {
                        if (!mTweets.contains(tweet)) {
                            mTweets.add(tweet);
                        }
                    }
                    mAdapter.notifyDataSetChanged(mTweets);
                }
                mAdapter.notifyDataSetChanged(tweets);

                updateToolbarBehaviour();

                // Now we call setRefreshing(false) to signal refresh has finished
                mSwipeToRefresh.setRefreshing(false);
            }

            @Override
            public void onError(TwitterError error) {
                Toast.makeText(TimelineActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();

                updateToolbarBehaviour();

                // Now we call setRefreshing(false) to signal refresh has finished
                mSwipeToRefresh.setRefreshing(false);
            }
        });
    }

    private void updateToolbarBehaviour() {
        int lastItem = mLayoutManager.findLastCompletelyVisibleItemPosition();
        int count = mAdapter.getItemCount();
        boolean enabled = lastItem < count - 1;
        if (enabled) {
            turnOnToolbarScrolling();
        } else {
            turnOffToolbarScrolling();
        }
    }

    private void goToTweetDetails(Tweet tweet) {

    }
}
