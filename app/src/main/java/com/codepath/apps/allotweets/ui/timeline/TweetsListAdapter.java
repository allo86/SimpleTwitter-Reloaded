package com.codepath.apps.allotweets.ui.timeline;

import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.codepath.apps.allotweets.R;
import com.codepath.apps.allotweets.model.Hashtag;
import com.codepath.apps.allotweets.model.Media;
import com.codepath.apps.allotweets.model.Tweet;
import com.codepath.apps.allotweets.model.TwitterUser;
import com.codepath.apps.allotweets.ui.base.TextView;
import com.codepath.apps.allotweets.ui.utils.DynamicHeightImageView;
import com.codepath.apps.allotweets.ui.utils.PatternEditableBuilder;

import java.util.ArrayList;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

/**
 * Tweets List Adapter
 * <p/>
 * Created by ALLO on 1/8/16.
 */
public class TweetsListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final String TAG_LOG = TweetsListAdapter.class.getCanonicalName();

    public static int BASE_TWEET = 1;
    public static int PHOTO_TWEET = 2;
    public static int VIDEO_TWEET = 3;

    public interface OnTimelineAdapterListener {
        void didSelectTweet(Tweet tweet);

        void didSelectReplyTweet(Tweet tweet);

        void didSelectRetweet(Tweet tweet);

        void didSelectMarkAsFavorite(Tweet tweet);

        void didSelectUser(TwitterUser user);

        void didSelectHashtag(Hashtag hashtag);
    }

    private ArrayList<Tweet> mTweets;
    private OnTimelineAdapterListener mListener;

    public TweetsListAdapter(ArrayList<Tweet> tweets, OnTimelineAdapterListener listener) {
        this.mTweets = tweets;
        this.mListener = listener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == BASE_TWEET) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_tweet_list, parent, false);
            return new TimelineTweetViewHolder(view);
        } else if (viewType == PHOTO_TWEET) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_photo_tweet_list, parent, false);
            return new TimelineTweetPhotoViewHolder(view);
        } else if (viewType == VIDEO_TWEET) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_video_tweet_list, parent, false);
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

        @BindView(R.id.bt_reply)
        ImageView btReply;

        @BindView(R.id.tv_retweet)
        TextView tvRetweet;

        @BindView(R.id.pb_retweet)
        ProgressBar pbRetweet;

        @BindView(R.id.tv_favorite)
        TextView tvFavorite;

        @BindView(R.id.pb_favorite)
        ProgressBar pbFavorite;

        public TimelineTweetViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mListener != null) mListener.didSelectTweet(tweet);
                }
            });

            btReply.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mListener != null) mListener.didSelectReplyTweet(tweet);
                }
            });

            tvRetweet.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    pbRetweet.setVisibility(View.VISIBLE);
                    tvRetweet.setVisibility(View.GONE);
                    if (mListener != null) mListener.didSelectRetweet(tweet);
                }
            });

            tvFavorite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    pbFavorite.setVisibility(View.VISIBLE);
                    tvFavorite.setVisibility(View.GONE);
                    if (mListener != null) mListener.didSelectMarkAsFavorite(tweet);
                }
            });

            ivAvatar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mListener != null) mListener.didSelectUser(tweet.getUser());
                }
            });
        }

        public void configureViewWithTweet(final Tweet tweet) {
            this.tweet = tweet;

            /*
            Picasso.with(ivAvatar.getContext())
                    .load(tweet.getUser().getProfileImageUrl())
                    .placeholder(R.drawable.ic_twitter)
                    .fit()
                    .transform(new RoundedCornersTransformation(10, 10))
                    .into(ivAvatar);
            */
            if (tweet.getUser() != null) {
                Glide.with(ivAvatar.getContext())
                        .load(tweet.getUser().getProfileImageUrl())
                        .placeholder(R.drawable.ic_twitter)
                        .bitmapTransform(new RoundedCornersTransformation(ivAvatar.getContext(), 3, 3))
                        .into(ivAvatar);

                tvName.setText(tweet.getUser().getName());
                tvUser.setText(tweet.getUser().getScreennameForDisplay());
            }

            tvText.setText(tweet.getText());
            tvDate.setText(tweet.getRelativeTimeAgo());

            int resourceRetweet = R.drawable.ic_retweet;
            if (tweet.getRetweetedStatus() != null) {
                if (tweet.getRetweetedStatus().isRetweeted()) {
                    resourceRetweet = R.drawable.ic_retweet_done;
                }
            } else {
                if (tweet.isRetweeted()) {
                    resourceRetweet = R.drawable.ic_retweet_done;
                }
            }
            tvRetweet.setCompoundDrawablesWithIntrinsicBounds(tvRetweet.getContext().getResources().getDrawable(resourceRetweet),
                    null, null, null);

            int favoriteRetweet = tweet.isFavorite() ? R.drawable.ic_favorite_done : R.drawable.ic_favorite;
            tvFavorite.setCompoundDrawablesWithIntrinsicBounds(tvFavorite.getContext().getResources().getDrawable(favoriteRetweet),
                    null, null, null);

            pbRetweet.setVisibility(View.GONE);
            tvRetweet.setVisibility(View.VISIBLE);
            tvRetweet.setText(String.valueOf(tweet.getRetweetedStatus() != null ? tweet.getRetweetedStatus().getRetweetCount() : tweet.getRetweetCount()));

            pbFavorite.setVisibility(View.GONE);
            tvFavorite.setVisibility(View.VISIBLE);
            tvFavorite.setText(String.valueOf(tweet.getRetweetedStatus() != null ? tweet.getRetweetedStatus().getFavoriteCount() : tweet.getFavoriteCount()));

            updateRetweet();
            updateFavorite();

            // Style clickable spans based on pattern
            new PatternEditableBuilder().
                    addPattern(Pattern.compile("\\@(\\w+)"), ContextCompat.getColor(tvText.getContext(), R.color.colorPrimary),
                            new PatternEditableBuilder.SpannableClickedListener() {
                                @Override
                                public void onSpanClicked(String text) {
                                    String screename = text.substring(1);
                                    for (TwitterUser twitterUser : tweet.getEntities().getUserMentions()) {
                                        if (twitterUser.getScreenname().equals(screename)) {
                                            if (mListener != null)
                                                mListener.didSelectUser(twitterUser);
                                        }
                                    }
                                }
                            }).into(tvText);
            new PatternEditableBuilder().
                    addPattern(Pattern.compile("\\#(\\w+)"), ContextCompat.getColor(tvText.getContext(), R.color.colorPrimary),
                            new PatternEditableBuilder.SpannableClickedListener() {
                                @Override
                                public void onSpanClicked(String text) {
                                    String sHashtag = text.substring(1);
                                    for (Hashtag hashtag : tweet.getEntities().getHashtags()) {
                                        if (hashtag.getText().equals(sHashtag)) {
                                            if (mListener != null)
                                                mListener.didSelectHashtag(hashtag);
                                        }
                                    }
                                }
                            }).into(tvText);
        }

        private void updateRetweet() {
            int resourceRetweet = R.drawable.ic_retweet;
            if (tweet.getRetweetedStatus() != null) {
                if (tweet.getRetweetedStatus().isRetweeted()) {
                    resourceRetweet = R.drawable.ic_retweet_done;
                }
            } else {
                if (tweet.isRetweeted()) {
                    resourceRetweet = R.drawable.ic_retweet_done;
                }
            }
            tvRetweet.setCompoundDrawablesWithIntrinsicBounds(tvRetweet.getContext().getResources().getDrawable(resourceRetweet),
                    null, null, null);

            pbRetweet.setVisibility(View.GONE);
            tvRetweet.setVisibility(View.VISIBLE);
            tvRetweet.setText(String.valueOf(tweet.getRetweetedStatus() != null ? tweet.getRetweetedStatus().getRetweetCount() : tweet.getRetweetCount()));
        }

        private void updateFavorite() {
            int favoriteRetweet = tweet.isFavorite() ? R.drawable.ic_favorite_done : R.drawable.ic_favorite;
            tvFavorite.setCompoundDrawablesWithIntrinsicBounds(tvFavorite.getContext().getResources().getDrawable(favoriteRetweet),
                    null, null, null);

            pbFavorite.setVisibility(View.GONE);
            tvFavorite.setVisibility(View.VISIBLE);
            tvFavorite.setText(String.valueOf(tweet.getRetweetedStatus() != null ? tweet.getRetweetedStatus().getFavoriteCount() : tweet.getFavoriteCount()));
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
            /*
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
            */
            Glide.with(ivPhoto.getContext())
                    .load(photo.getMediaUrl())
                    .placeholder(R.drawable.ic_twitter)
                    .into(new SimpleTarget<GlideDrawable>() {
                        @Override
                        public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
                            ivPhoto.setImageDrawable(resource);
                            pbImage.setVisibility(View.GONE);
                        }

                        @Override
                        public void onLoadFailed(Exception e, Drawable errorDrawable) {
                            Log.d(TAG_LOG, e.getMessage());
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
