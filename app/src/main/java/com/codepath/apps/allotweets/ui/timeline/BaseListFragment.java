package com.codepath.apps.allotweets.ui.timeline;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.codepath.apps.allotweets.R;
import com.codepath.apps.allotweets.model.Tweet;
import com.codepath.apps.allotweets.model.TwitterUser;
import com.codepath.apps.allotweets.network.TwitterError;
import com.codepath.apps.allotweets.network.callbacks.FavoriteTweetCallback;
import com.codepath.apps.allotweets.network.callbacks.RetweetCallback;
import com.codepath.apps.allotweets.network.request.FavoriteTweetRequest;
import com.codepath.apps.allotweets.network.request.RetweetRequest;
import com.codepath.apps.allotweets.network.utils.Utils;
import com.codepath.apps.allotweets.ui.base.BaseActivity;
import com.codepath.apps.allotweets.ui.base.BaseFragment;
import com.codepath.apps.allotweets.ui.compose.ComposeTweetFragment;
import com.codepath.apps.allotweets.ui.details.TweetDetailActivity;
import com.codepath.apps.allotweets.ui.profile.ProfileActivity;
import com.codepath.apps.allotweets.ui.utils.DividerItemDecoration;
import com.codepath.apps.allotweets.ui.utils.EndlessRecyclerViewScrollListener;
import com.codepath.apps.allotweets.ui.utils.LinearLayoutManager;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import butterknife.BindView;

/**
 * A simple {@link Fragment} subclass.
 */
public abstract class BaseListFragment extends BaseFragment implements ComposeTweetFragment.OnComposeTweetFragmentListener {

    public static final String TWITTER_USER = "TWITTER_USER";

    @BindView(R.id.rv_list)
    RecyclerView mRecyclerView;

    @BindView(R.id.swipe_container)
    SwipeRefreshLayout mSwipeToRefresh;

    TweetsListAdapter mAdapter;
    private ArrayList<Tweet> mTweets;
    private LinearLayoutManager mLayoutManager;

    private BaseActivity activity;

    @Override
    protected int getLayoutResourceID() {
        return R.layout.fragment_base_list;
    }

    @Override
    protected void showData() {
        if (mTweets != null) {
            mAdapter.notifyDataSetChanged(mTweets);
        } else {
            if (Utils.isOnline()) {
                loadTweets(null, null);
            } else {
                loadOfflineTweets();
            }
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof BaseActivity) {
            activity = (BaseActivity) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must extends from BaseActivity");
        }
    }

    @Override
    protected void initializeUI() {
        mRecyclerView.setHasFixedSize(true);

        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL_LIST);
        mRecyclerView.addItemDecoration(itemDecoration);

        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);

        EndlessRecyclerViewScrollListener endlessListener = new EndlessRecyclerViewScrollListener(mLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to the bottom of the list
                Log.d(TAG_LOG, "loadNextPage: " + String.valueOf(page));
                loadTweets(null, mTweets.get(totalItemsCount - 1).getTweetId());
            }
        };
        mRecyclerView.addOnScrollListener(endlessListener);

        mAdapter = new TweetsListAdapter(mTweets, new TweetsListAdapter.OnTimelineAdapterListener() {
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

            @Override
            public void didSelectUser(TwitterUser user) {
                goToProfile(user);
            }
        });
        mRecyclerView.setAdapter(mAdapter);

        // Swipe to refresh
        mSwipeToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (mTweets != null) {
                    loadTweets(mTweets.get(0).getTweetId(), null);
                } else {
                    loadTweets(null, null);
                }
            }
        });
        mSwipeToRefresh.setColorSchemeResources(R.color.colorPrimary,
                R.color.colorPrimaryDark);

        updateToolbarBehaviour();
    }

    /**
     * Method that should be implemented to load more results
     * (timeline, mentions, etc.)
     *
     * @param sinceTweetId Long
     * @param maxTweetId   Long
     */
    protected abstract void loadTweets(Long sinceTweetId, Long maxTweetId);

    protected void loadOfflineTweets() {
        Toast.makeText(getActivity(), R.string.loading_offline_tweets, Toast.LENGTH_LONG).show();
        processTweets(new ArrayList<>(Tweet.recentTweets()));
    }

    protected void processTweets(ArrayList<Tweet> tweets) {
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

    protected void saveTweets(ArrayList<Tweet> tweets) {
        if (tweets != null && tweets.size() > 0) {
            for (Tweet tweet : tweets) {
                /*
                // Twitter User
                TwitterUser dbTwitterUser = TwitterUser.byUserId(tweet.getUser().getUserId());
                if (dbTwitterUser != null) {
                    // Twitter User already existe. It may have been updated
                    dbTwitterUser.setName(tweet.getUser().getName());
                    dbTwitterUser.setScreenname(tweet.getUser().getScreenname());
                    dbTwitterUser.setDescription(tweet.getUser().getScreenname());
                    dbTwitterUser.setUrl(tweet.getUser().getUrl());
                    dbTwitterUser.setProfileImageUrl(tweet.getUser().getProfileImageUrl());
                    dbTwitterUser.setProfileBackgroundImageUrl(tweet.getUser().getProfileBackgroundImageUrl());
                    dbTwitterUser.setProfileBannerUrl(tweet.getUser().getProfileBannerUrl());
                    dbTwitterUser.setFriendsCount(tweet.getUser().getFriendsCount());
                    dbTwitterUser.setFollowersCount(tweet.getUser().getFollowersCount());
                    dbTwitterUser.setStatusesCount(tweet.getUser().getStatusesCount());
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
                */
                tweet.save();
            }
        }
    }

    /**
     * Method that updates the toolbar behaviour
     * depending if there are tweets in the list
     */
    protected void updateToolbarBehaviour() {
        int lastItem = mLayoutManager.findLastCompletelyVisibleItemPosition();
        int count = mAdapter.getItemCount();
        boolean enabled = lastItem < count - 1;
        if (enabled) {
            activity.turnOnToolbarScrolling();
        } else {
            activity.turnOffToolbarScrolling();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == TweetDetailActivity.REQUEST_CODE) {
            // Make sure the request was successful
            if (resultCode == Activity.RESULT_OK) {
                Tweet tweet = Parcels.unwrap(data.getExtras().getParcelable(TweetDetailActivity.TWEET));
                if (tweet != null) updateTweetInAdapter(tweet);

                boolean refreshTweets = data.getExtras().getBoolean(TweetDetailActivity.REFRESH_TWEETS);
                if (refreshTweets) {
                    loadTweets(null, null);
                }
            }
        }
    }

    /* Adapter actions */

    private void goToTweetDetails(Tweet tweet) {
        Intent intent = new Intent(getActivity(), TweetDetailActivity.class);
        intent.putExtra(TweetDetailActivity.TWEET, Parcels.wrap(tweet));
        startActivityForResult(intent, TweetDetailActivity.REQUEST_CODE);
    }

    private void replyTweet(Tweet tweet) {
        if (Utils.isOnline()) {
            FragmentManager fm = getActivity().getSupportFragmentManager();
            ComposeTweetFragment editNameDialogFragment = ComposeTweetFragment.newInstance(this, tweet);
            editNameDialogFragment.show(fm, "compose_tweet");
        } else {
            Toast.makeText(getActivity(), getString(R.string.error_no_internet), Toast.LENGTH_SHORT).show();
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
                Toast.makeText(getActivity(), R.string.error_retweet, Toast.LENGTH_SHORT).show();
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
            }

            @Override
            public void onError(TwitterError error) {
                Toast.makeText(getActivity(), R.string.error_favorite, Toast.LENGTH_LONG).show();
                updateTweetInAdapter(tweet);
            }
        });
    }

    private void goToProfile(TwitterUser user) {
        if (getActivity() instanceof ProfileActivity) {
            if (((ProfileActivity) getActivity()).getUser().equals(user)) {
                return;
            }
        }
        Intent intent = new Intent(getActivity(), ProfileActivity.class);
        intent.putExtra(ProfileActivity.TWITTER_USER, Parcels.wrap(user));
        startActivity(intent);
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

    /**
     * Callback with the user's reply to a tweet
     *
     * @param tweet Tweet
     */
    @Override
    public void onStatusUpdated(Tweet tweet) {
        // TODO: Do something here? Send new tweet to fragments in tab layout?
    }

}
