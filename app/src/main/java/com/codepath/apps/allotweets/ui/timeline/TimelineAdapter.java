package com.codepath.apps.allotweets.ui.timeline;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.codepath.apps.allotweets.R;
import com.codepath.apps.allotweets.model.Media;
import com.codepath.apps.allotweets.model.Tweet;
import com.codepath.apps.allotweets.ui.base.TextView;
import com.codepath.apps.allotweets.ui.utils.DynamicHeightImageView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;

/**
 * TimelineAdapter
 * <p/>
 * Created by ALLO on 1/8/16.
 */
public class TimelineAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final String TAG_LOG = TimelineAdapter.class.getCanonicalName();

    public static int BASE_TWEET = 1;
    public static int PHOTO_TWEET = 2;
    public static int VIDEO_TWEET = 3;

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
        } else if (viewType == PHOTO_TWEET) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_timeline_photo_tweet, parent, false);
            return new TimelineTweetPhotoViewHolder(view);
        } else if (viewType == VIDEO_TWEET) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_timeline_video_tweet, parent, false);
            return new TimelineTweetViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder != null) {
            if (holder instanceof TimelineTweetVideoViewHolder) {
                ((TimelineTweetVideoViewHolder) holder).configureViewWithTweet(mTweets.get(position));
            } else if (holder instanceof TimelineTweetPhotoViewHolder) {
                ((TimelineTweetPhotoViewHolder) holder).configureViewWithTweet(mTweets.get(position));
            } else if (holder instanceof TimelineTweetViewHolder) {
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
        Tweet tweet = mTweets.get(position);
        if (tweet.hasVideo()) {
            return VIDEO_TWEET;
        } else if (tweet.hasPhoto()) {
            return PHOTO_TWEET;
        } else {
            return BASE_TWEET;
        }
    }

    public void notifyDataSetChanged(ArrayList<Tweet> tweets) {
        this.mTweets = new ArrayList<>(tweets);
        notifyDataSetChanged();
    }

    public class TimelineTweetViewHolder extends RecyclerView.ViewHolder {

        protected Tweet tweet;

        @BindView(R.id.iv_avatar)
        ImageView ivAvatar;

        @BindView(R.id.tv_name)
        TextView tvName;

        @BindView(R.id.tv_user)
        TextView tvUser;

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

            Picasso.with(ivAvatar.getContext())
                    .load(tweet.getUser().getProfileImageUrl())
                    .placeholder(R.drawable.ic_twitter)
                    .fit()
                    .transform(new RoundedCornersTransformation(10, 10))
                    .into(ivAvatar);

            tvName.setText(tweet.getUser().getName());
            tvUser.setText(tweet.getUser().getScreennameForDisplay());
            tvText.setText(tweet.getText());
            tvDate.setText(tweet.getRelativeTimeAgo());
        }
    }

    public class TimelineTweetPhotoViewHolder extends TimelineTweetViewHolder {

        @BindView(R.id.iv_photo)
        DynamicHeightImageView ivPhoto;

        @BindView(R.id.pb_image)
        ProgressBar pbImage;

        public TimelineTweetPhotoViewHolder(View itemView) {
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
            super.configureViewWithTweet(tweet);

            pbImage.setVisibility(View.VISIBLE);
            ivPhoto.setImageDrawable(null);

            Media photo = tweet.getPhoto();
            ivPhoto.setHeightRatio(((double) photo.getSize().getHeight()) / photo.getSize().getWidth());
            Picasso.with(ivPhoto.getContext()).load(photo.getMediaUrl())
                    .into(ivPhoto, new Callback() {
                        @Override
                        public void onSuccess() {
                            pbImage.setVisibility(View.GONE);
                        }

                        @Override
                        public void onError() {
                            Log.d(TAG_LOG, "error");
                        }
                    });
        }
    }

    public class TimelineTweetVideoViewHolder extends TimelineTweetViewHolder {

        public TimelineTweetVideoViewHolder(View itemView) {
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
            super.configureViewWithTweet(tweet);
        }
    }
}
