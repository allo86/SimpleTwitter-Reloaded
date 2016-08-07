package com.codepath.apps.allotweets.ui.timeline;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.codepath.apps.allotweets.R;
import com.codepath.apps.allotweets.data.DataManager;
import com.codepath.apps.allotweets.model.Tweet;
import com.codepath.apps.allotweets.model.TwitterUser;
import com.codepath.apps.allotweets.network.TwitterError;
import com.codepath.apps.allotweets.network.callbacks.FavoriteTweetCallback;
import com.codepath.apps.allotweets.network.callbacks.HomeTimelineCallback;
import com.codepath.apps.allotweets.network.callbacks.RetweetCallback;
import com.codepath.apps.allotweets.network.request.FavoriteTweetRequest;
import com.codepath.apps.allotweets.network.request.HomeTimelineRequest;
import com.codepath.apps.allotweets.network.request.RetweetRequest;
import com.codepath.apps.allotweets.network.utils.Utils;
import com.codepath.apps.allotweets.ui.base.BaseActivity;
import com.codepath.apps.allotweets.ui.base.TextView;
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
import jp.wasabeef.glide.transformations.CropCircleTransformation;

public class TimelineActivity extends BaseActivity implements ComposeTweetFragment.OnComposeTweetFragmentListener {

    @BindView(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;

    @BindView(R.id.navigation_view)
    NavigationView mNavigationView;

    @BindView(R.id.swipe_container)
    SwipeRefreshLayout mSwipeToRefresh;

    @BindView(R.id.rv_timeline)
    RecyclerView mRecyclerView;

    // Navigation header
    private ImageView ivAvatar;
    private TextView tvScreenname;

    private ActionBarDrawerToggle mActionBarDrawerToggle;

    private TimelineAdapter mAdapter;
    private ArrayList<Tweet> mTweets;
    private LinearLayoutManager mLayoutManager;

    @Override
    protected int getLayoutResourceID() {
        return R.layout.activity_timeline;
    }

    @Override
    protected void initializeUI() {
        // Navigation Drawer
        setUpNavigationDrawer();

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

            @Override
            public void didSelectReplyTweet(Tweet tweet) {
                replyTweet(tweet);
            }

            @Override
            public void didSelectRetweet(Tweet tweet) {
                retweet(tweet);
            }

            @Override
            public void didSelectMarkAsFavorite(Tweet tweet) {
                markAsFavorite(tweet);
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
        // Show user info in header
        Glide.with(this)
                .load(DataManager.sharedInstance().getUser().getProfileImageUrl())
                .placeholder(R.drawable.ic_twitter)
                .bitmapTransform(new CropCircleTransformation(this))
                .into(ivAvatar);
        tvScreenname.setText(DataManager.sharedInstance().getUser().getScreennameForDisplay());

        if (mTweets != null) {
            mAdapter.notifyDataSetChanged(mTweets);
        } else {
            if (Utils.isOnline()) {
                loadTimeline(null, null);
            } else {
                loadOfflineTweets();
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mActionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // `onPostCreate` called when activity start-up is complete after `onStart()`
    // NOTE! Make sure to override the method with only a single `Bundle` argument
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Sync the toggle state after onRestoreInstanceState has occurred.
        mActionBarDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        // Pass any configuration change to the drawer toggles
        mActionBarDrawerToggle.onConfigurationChanged(newConfig);
    }

    /**
     * Method that sets initial configuration for Navigation Drawer
     */
    private void setUpNavigationDrawer() {
        // Navigation Drawer
        mNavigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        selectDrawerItem(menuItem);
                        return true;
                    }
                });

        // Hamburguer icon
        mActionBarDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close);
        mDrawerLayout.addDrawerListener(mActionBarDrawerToggle);

        // Navigation View Header
        ivAvatar = (ImageView) mNavigationView.getHeaderView(0).findViewById(R.id.iv_avatar);
        tvScreenname = (TextView) mNavigationView.getHeaderView(0).findViewById(R.id.tv_screename);
    }

    /**
     * Navigation to selected drawer item
     *
     * @param menuItem
     */
    private void selectDrawerItem(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.nav_profile:
                Toast.makeText(this, "profile", Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_help:
                Toast.makeText(this, "help", Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
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
                if (mTweets.contains(tweet)) {
                    mTweets.set(mTweets.indexOf(tweet), tweet);
                } else {
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
                Tweet tweet = Parcels.unwrap(data.getExtras().getParcelable(TweetDetailActivity.TWEET));
                updateTweetInAdapter(tweet);

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

    private void replyTweet(Tweet tweet) {
        if (Utils.isOnline()) {
            FragmentManager fm = getSupportFragmentManager();
            ComposeTweetFragment editNameDialogFragment = ComposeTweetFragment.newInstance(tweet);
            editNameDialogFragment.show(fm, "compose_tweet");
        } else {
            Toast.makeText(TimelineActivity.this, getString(R.string.error_no_internet), Toast.LENGTH_SHORT).show();
        }
    }

    private void retweet(final Tweet tweet) {
        final RetweetRequest request = new RetweetRequest();
        if (tweet.isRetweeted()) {
            // Undo retweet
            request.setUndo(true);
            request.setTweetId(tweet.getRetweetedStatus() != null ? tweet.getRetweetedStatus().getTweetId() : tweet.getTweetId());
        } else {
            // Retweet
            request.setUndo(false);
            request.setTweetId(tweet.getTweetId());
        }

        mTwitterClient.retweet(request, new RetweetCallback() {
            @Override
            public void onSuccess(Tweet retweet) {
                // Weird bug ... this request does not return the favorites information
                // Fix
                int favoriteCount = tweet.getRetweetedStatus() != null ? tweet.getRetweetedStatus().getFavoriteCount() : tweet.getFavoriteCount();

                if (request.isUndo()) {
                    tweet.setRetweetCount(tweet.getRetweetCount() - 1);
                    tweet.setRetweetedStatus(null);
                    tweet.setRetweeted(false);
                } else {
                    tweet.setRetweetCount(tweet.getRetweetCount() + 1);
                    tweet.setRetweetedStatus(retweet);
                    tweet.setRetweeted(true);
                }
                tweet.setFavoriteCount(favoriteCount);
                if (tweet.getRetweetedStatus() != null)
                    tweet.getRetweetedStatus().setFavoriteCount(favoriteCount);
                updateTweetInAdapter(tweet);
            }

            @Override
            public void onError(TwitterError error) {
                Toast.makeText(TimelineActivity.this, R.string.error_retweet, Toast.LENGTH_SHORT).show();
                updateTweetInAdapter(tweet);
            }
        });
    }

    private void markAsFavorite(final Tweet tweet) {
        final FavoriteTweetRequest request = new FavoriteTweetRequest(tweet.getTweetId(), tweet.isFavorite());

        mTwitterClient.markAsFavorite(request, new FavoriteTweetCallback() {
            @Override
            public void onSuccess(Tweet retweet) {
                tweet.setFavorite(retweet.isFavorite());
                tweet.setFavoriteCount(retweet.getFavoriteCount());
                if (tweet.getRetweetedStatus() != null) {
                    tweet.getRetweetedStatus().setFavoriteCount(tweet.getFavoriteCount());
                }
                updateTweetInAdapter(retweet);
                /*
                if (tweet.getRetweetedStatus() != null) {
                    tweet.setRetweetedStatus(retweet);
                    updateTweetInAdapter(tweet);
                } else {
                    updateTweetInAdapter(retweet);
                }
                */
            }

            @Override
            public void onError(TwitterError error) {
                Toast.makeText(TimelineActivity.this, R.string.error_favorite, Toast.LENGTH_LONG).show();
                updateTweetInAdapter(tweet);
            }
        });
    }

    private void updateTweetInAdapter(Tweet tweet) {
        if (mTweets != null) {
            if (mTweets.indexOf(tweet) != -1) {
                int index = mTweets.indexOf(tweet);
                mTweets.set(index, tweet);
                mAdapter.notifyItemChanged(index);
            }
        }
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
                    dbTwitterUser.setScreenname(tweet.getUser().getScreenname());
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
