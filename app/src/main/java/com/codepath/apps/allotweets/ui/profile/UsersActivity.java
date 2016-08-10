package com.codepath.apps.allotweets.ui.profile;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.codepath.apps.allotweets.R;
import com.codepath.apps.allotweets.model.TwitterUser;
import com.codepath.apps.allotweets.network.TwitterError;
import com.codepath.apps.allotweets.network.callbacks.TwitterUsersCallback;
import com.codepath.apps.allotweets.network.request.TwitterUsersRequest;
import com.codepath.apps.allotweets.network.response.TwitterUsersResponse;
import com.codepath.apps.allotweets.network.utils.Utils;
import com.codepath.apps.allotweets.ui.base.BaseActivity;
import com.codepath.apps.allotweets.ui.utils.DividerItemDecoration;
import com.codepath.apps.allotweets.ui.utils.EndlessRecyclerViewScrollListener;
import com.codepath.apps.allotweets.ui.utils.LinearLayoutManager;

import org.parceler.Parcels;

import java.util.ArrayList;

import butterknife.BindView;

/**
 * Following/Followers Activity
 */
public class UsersActivity extends BaseActivity {

    public enum Mode {
        FOLLOWING,
        FOLLOWERS
    }

    public static final String MODE = "MODE";
    public static final String TWITTER_USER = "TWITTER_USER";

    Mode mode;
    TwitterUser mUser;

    @BindView(R.id.rv_users)
    RecyclerView mRecyclerView;

    @BindView(R.id.swipe_container)
    SwipeRefreshLayout mSwipeToRefresh;

    UsersAdapter mAdapter;
    private ArrayList<TwitterUser> mUsers;
    private LinearLayoutManager mLayoutManager;

    private String mNextCursor;

    @Override
    protected int getLayoutResourceID() {
        return R.layout.activity_users;
    }

    @Override
    protected void initializeUI() {
        setTitle(mode == Mode.FOLLOWERS ? R.string.followers : R.string.following);

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
                loadUsers();
            }
        };
        mRecyclerView.addOnScrollListener(endlessListener);

        mAdapter = new UsersAdapter(mUsers, new UsersAdapter.OnUsersAdapterListener() {
            @Override
            public void didSelectUser(TwitterUser twitterUser) {
                goToProfile(twitterUser);
            }
        });
        mRecyclerView.setAdapter(mAdapter);

        // Swipe to refresh
        mSwipeToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadUsers();
            }
        });
        mSwipeToRefresh.setColorSchemeResources(R.color.colorPrimary,
                R.color.colorPrimaryDark);

        updateToolbarBehaviour();
    }

    @Override
    protected void initializeDataFromIntentBundle(Bundle extras) {
        mode = (Mode) extras.getSerializable(MODE);
        mUser = Parcels.unwrap(extras.getParcelable(TWITTER_USER));
    }

    @Override
    protected void showData() {
        if (mUsers != null) {
            mAdapter.notifyDataSetChanged(mUsers);
        } else {
            if (Utils.isOnline()) {
                loadUsers();
            } else {
                Toast.makeText(this, R.string.error_no_internet_action, Toast.LENGTH_LONG).show();
                onBackPressed();
            }
        }
    }

    private void loadUsers() {
        TwitterUsersRequest request = new TwitterUsersRequest();
        request.setUserId(mUser.getUserId());
        request.setCursor(mNextCursor);

        if (mode == Mode.FOLLOWERS) {
            mTwitterClient.getFollowers(request, new TwitterUsersCallback() {
                @Override
                public void onSuccess(TwitterUsersResponse response) {
                    processResponse(response);
                }

                @Override
                public void onError(TwitterError error) {
                    processError(error);
                }
            });
        } else {
            mTwitterClient.getFollowing(request, new TwitterUsersCallback() {
                @Override
                public void onSuccess(TwitterUsersResponse response) {
                    processResponse(response);
                }

                @Override
                public void onError(TwitterError error) {
                    processError(error);
                }
            });
        }
    }

    private void processResponse(TwitterUsersResponse twitterUsersResponse) {
        mNextCursor = twitterUsersResponse.getNextCursor();

        ArrayList<TwitterUser> users = twitterUsersResponse.getUsers();
        if (users != null) {
            if (mUsers == null) {
                mUsers = new ArrayList<>();
            }
            for (TwitterUser user : users) {
                if (mUsers.contains(user)) {
                    mUsers.set(mUsers.indexOf(user), user);
                } else {
                    mUsers.add(user);
                }
            }
        }
        mAdapter.notifyDataSetChanged(mUsers);

        updateToolbarBehaviour();

        // Now we call setRefreshing(false) to signal refresh has finished
        mSwipeToRefresh.setRefreshing(false);
    }

    private void processError(TwitterError error) {
        Toast.makeText(this, error.getMessage(), Toast.LENGTH_LONG).show();
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

    /* Navigations */

    private void goToProfile(TwitterUser twitterUser) {
        Intent intent = new Intent(this, ProfileActivity.class);
        intent.putExtra(ProfileActivity.TWITTER_USER, Parcels.wrap(twitterUser));
        startActivity(intent);
    }
}
