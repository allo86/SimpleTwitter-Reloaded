package com.codepath.apps.allotweets.ui.timeline;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.codepath.apps.allotweets.R;
import com.codepath.apps.allotweets.model.Message;
import com.codepath.apps.allotweets.model.TwitterUser;
import com.codepath.apps.allotweets.network.TwitterError;
import com.codepath.apps.allotweets.network.callbacks.MessagesCallback;
import com.codepath.apps.allotweets.network.request.TimelineRequest;
import com.codepath.apps.allotweets.network.utils.Utils;
import com.codepath.apps.allotweets.ui.base.BaseActivity;
import com.codepath.apps.allotweets.ui.base.BaseFragment;
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
 * Messages
 * <p/>
 * Created by ALLO on 9/8/16.
 */
public class MessagesFragment extends BaseFragment {

    @BindView(R.id.rv_list)
    RecyclerView mRecyclerView;

    @BindView(R.id.swipe_container)
    public SwipeRefreshLayout mSwipeToRefresh;

    MessagesAdapter mAdapter;
    private ArrayList<Message> mMessages;
    private LinearLayoutManager mLayoutManager;

    private BaseActivity activity;

    public MessagesFragment() {
        // Required empty public constructor
    }

    @Override
    protected int getLayoutResourceID() {
        return R.layout.fragment_base_list;
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
                loadMessages(null, mMessages.get(totalItemsCount - 1).getMessageId());
            }
        };
        mRecyclerView.addOnScrollListener(endlessListener);

        mAdapter = new MessagesAdapter(mMessages, new MessagesAdapter.OnMessagesAdapterListener() {
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
                if (mMessages != null) {
                    loadMessages(mMessages.get(0).getMessageId(), null);
                } else {
                    loadMessages(null, null);
                }
            }
        });
        mSwipeToRefresh.setColorSchemeResources(R.color.colorPrimary,
                R.color.colorPrimaryDark);

        updateToolbarBehaviour();
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
    protected void initializeDataFromArguments(Bundle args) {
    }

    @Override
    protected void showData() {
        if (mMessages != null) {
            mAdapter.notifyDataSetChanged(mMessages);
        } else {
            if (Utils.isOnline()) {
                loadMessages(null, null);
            } else {
                Toast.makeText(getActivity(), R.string.error_no_internet_data, Toast.LENGTH_SHORT).show();
            }
        }
    }

    protected void loadMessages(Long sinceMessageId, Long maxMessageId) {
        Toast.makeText(getActivity(), R.string.loading_more_tweets, Toast.LENGTH_SHORT).show();

        TimelineRequest request = new TimelineRequest();
        request.setSinceId(sinceMessageId);
        request.setMaxId(maxMessageId);

        mTwitterClient.getDirectMessages(request, new MessagesCallback() {
            @Override
            public void onSuccess(ArrayList<Message> messages) {
                // Process messages
                processMessages(messages);
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

    protected void processMessages(ArrayList<Message> messages) {
        if (messages != null) {
            if (mMessages == null) {
                mMessages = new ArrayList<>();
            }
            for (Message message : messages) {
                if (mMessages.contains(message)) {
                    mMessages.set(mMessages.indexOf(message), message);
                } else {
                    mMessages.add(message);
                }
            }
            Collections.sort(mMessages, new Comparator<Message>() {
                public int compare(Message t1, Message t2) {
                    return t2.getCreatedAt().compareTo(t1.getCreatedAt());
                }
            });
        }
        mAdapter.notifyDataSetChanged(mMessages);

        updateToolbarBehaviour();

        // Now we call setRefreshing(false) to signal refresh has finished
        mSwipeToRefresh.setRefreshing(false);
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

    /* Adapter actions */

    private void goToProfile(TwitterUser user) {
        Intent intent = new Intent(getActivity(), ProfileActivity.class);
        intent.putExtra(ProfileActivity.TWITTER_USER, Parcels.wrap(user));
        startActivityForResult(intent, TweetDetailActivity.REQUEST_CODE);
    }

}
