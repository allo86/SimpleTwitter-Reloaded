package com.codepath.apps.allotweets.ui.timeline;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.codepath.apps.allotweets.R;
import com.codepath.apps.allotweets.model.Tweet;
import com.codepath.apps.allotweets.model.TwitterUser;
import com.codepath.apps.allotweets.network.TwitterError;
import com.codepath.apps.allotweets.network.callbacks.HomeTimelineCallback;
import com.codepath.apps.allotweets.network.request.HomeTimelineRequest;
import com.codepath.apps.allotweets.network.utils.Utils;
import com.codepath.apps.allotweets.ui.base.BaseActivity;
import com.codepath.apps.allotweets.ui.compose.ComposeTweetFragment;
import com.codepath.apps.allotweets.ui.details.TweetDetailActivity;
import com.codepath.apps.allotweets.ui.utils.DividerItemDecoration;
import com.codepath.apps.allotweets.ui.utils.EndlessRecyclerViewScrollListener;
import com.codepath.apps.allotweets.ui.utils.LinearLayoutManager;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import butterknife.BindView;
import butterknife.OnClick;

public class TimelineActivity extends BaseActivity implements ComposeTweetFragment.OnComposeTweetFragmentListener {

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

        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST);
        mRecyclerView.addItemDecoration(itemDecoration);

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        EndlessRecyclerViewScrollListener endlessListener = new EndlessRecyclerViewScrollListener(mLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to the bottom of the list
                Log.d(TAG_LOG, "loadNextPage: " + String.valueOf(page));
                loadTimeline(null, mTweets.get(totalItemsCount - 1).getTweetId());
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
                if (mTweets != null) {
                    loadTimeline(mTweets.get(0).getTweetId(), null);
                } else {
                    loadTimeline(null, null);
                }
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
            if (Utils.isOnline()) {
                loadTimeline(null, null);
            } else {
                // TODO: Load SQLite
                loadOfflineTweets();
            }
        }
    }

    private void loadTimeline(Long sinceId, Long maxId) {
        Toast.makeText(TimelineActivity.this, R.string.loading_more_tweets, Toast.LENGTH_SHORT).show();

        HomeTimelineRequest request = new HomeTimelineRequest();
        request.setSinceId(sinceId);
        request.setMaxId(maxId);

        mTwitterClient.getHomeTimeline(request, new HomeTimelineCallback() {
            @Override
            public void onSuccess(ArrayList<Tweet> tweets) {
                // Save in local database
                saveTweets(tweets);

                // Process tweets
                processTweets(tweets);
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

    private void processTweets(ArrayList<Tweet> tweets) {
        if (tweets != null) {
            if (mTweets == null) {
                mTweets = new ArrayList<>();
            }
            for (Tweet tweet : tweets) {
                if (!mTweets.contains(tweet)) {
                    mTweets.add(tweet);
                }
            }
            Collections.sort(mTweets, new Comparator<Tweet>() {
                public int compare(Tweet t1, Tweet t2) {
                    return t2.getCreatedAt().compareTo(t1.getCreatedAt());
                }
            });
        }
        mAdapter.notifyDataSetChanged(mTweets);

        updateToolbarBehaviour();

        // Now we call setRefreshing(false) to signal refresh has finished
        mSwipeToRefresh.setRefreshing(false);
    }

    private void loadOfflineTweets() {
        Toast.makeText(this, R.string.loading_offline_tweets, Toast.LENGTH_LONG).show();
        processTweets(new ArrayList<>(Tweet.recentTweets()));
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
        Intent intent = new Intent(this, TweetDetailActivity.class);
        intent.putExtra(TweetDetailActivity.TWEET, Parcels.wrap(tweet));
        startActivityForResult(intent, TweetDetailActivity.REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == TweetDetailActivity.REQUEST_CODE) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                boolean refreshTweets = data.getExtras().getBoolean(TweetDetailActivity.REFRESH_TWEETS);
                if (refreshTweets) {
                    loadTimeline(null, null);
                }
            }
        }
    }

    @OnClick(R.id.fab_tweet)
    public void composeTweet() {
        FragmentManager fm = getSupportFragmentManager();
        ComposeTweetFragment editNameDialogFragment = ComposeTweetFragment.newInstance();
        editNameDialogFragment.show(fm, "compose_tweet");
    }

    @Override
    public void onStatusUpdated(Tweet tweet) {
        mTweets.add(0, tweet);
        mAdapter.notifyDataSetChanged(mTweets);
        mLayoutManager.scrollToPosition(0);
    }

    private void saveTweets(ArrayList<Tweet> tweets) {
        if (tweets != null && tweets.size() > 0) {
            for (Tweet tweet : tweets) {
                // Twitter User
                TwitterUser dbTwitterUser = TwitterUser.byUserId(tweet.getUser().getUserId());
                if (dbTwitterUser != null) {
                    // Twitter User already existe. It may have been updated
                    dbTwitterUser.setProfileImageUrl(tweet.getUser().getProfileImageUrl());
                    dbTwitterUser.setName(tweet.getUser().getName());
                    dbTwitterUser.setScreenname(tweet.getUser().getName());
                    dbTwitterUser.save();
                    Log.d(TAG_LOG, "user updated: " + tweet.getUser().getUserId());
                } else {
                    // New Twitter User. Insert new record
                    dbTwitterUser = tweet.getUser();
                    dbTwitterUser.save();
                    Log.d(TAG_LOG, "user inserted: " + tweet.getUser().getUserId());
                }
                tweet.setUser(dbTwitterUser);

                // Tweet
                Tweet dbTweet = Tweet.byTweetId(tweet.getTweetId());
                if (dbTweet != null) {
                    // Tweet already existed. It may have been updated
                    dbTweet.setText(tweet.getText());
                    dbTweet.setCreatedAt(tweet.getCreatedAt());
                    dbTweet.setFavorite(tweet.isFavorite());
                    dbTweet.setRetweeted(tweet.isRetweeted());
                    dbTweet.save();
                    Log.d(TAG_LOG, "tweet updated: " + tweet.getTweetId());
                } else {
                    // New Tweet. Insert new record
                    dbTweet = tweet;
                    dbTweet.save();
                    Log.d(TAG_LOG, "tweet inserted: " + tweet.getTweetId());
                }
            }
        }
    }
}
