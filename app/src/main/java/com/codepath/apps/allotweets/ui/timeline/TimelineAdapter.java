package com.codepath.apps.allotweets.ui.timeline;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codepath.apps.allotweets.R;
import com.codepath.apps.allotweets.model.Tweet;
import com.codepath.apps.allotweets.ui.base.TextView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by ALLO on 1/8/16.
 */
public class TimelineAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static int BASE_TWEET = 1;

    public interface OnTimelineAdapterListener {
        void didSelectTweet(Tweet tweet);
    }

    private ArrayList<Tweet> mTweets;
    private OnTimelineAdapterListener mListener;

    public TimelineAdapter(ArrayList<Tweet> tweets, OnTimelineAdapterListener listener) {
        this.mTweets = tweets;
        this.mListener = listener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == BASE_TWEET) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_timeline_tweet, parent, false);
            return new TimelineTweetViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder != null) {
            if (holder instanceof TimelineTweetViewHolder) {
                ((TimelineTweetViewHolder) holder).configureViewWithTweet(mTweets.get(position));
            }
        }
    }

    @Override
    public int getItemCount() {
        return this.mTweets != null ? this.mTweets.size() : 0;
    }

    @Override
    public int getItemViewType(int position) {
        return BASE_TWEET;
    }

    public void notifyDataSetChanged(ArrayList<Tweet> tweets) {
        this.mTweets = new ArrayList<>(tweets);
        notifyDataSetChanged();
    }

    public class TimelineTweetViewHolder extends RecyclerView.ViewHolder {

        private Tweet tweet;

        @BindView(R.id.tv_text)
        TextView tvText;

        @BindView(R.id.tv_date)
        TextView tvDate;

        public TimelineTweetViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mListener != null) mListener.didSelectTweet(tweet);
                }
            });
        }

        public void configureViewWithTweet(Tweet tweet) {
            this.tweet = tweet;

            tvText.setText(tweet.getText());
            tvDate.setText(tweet.getRelativeTimeAgo());
        }
    }
}
