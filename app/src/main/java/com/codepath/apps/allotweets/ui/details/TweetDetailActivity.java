package com.codepath.apps.allotweets.ui.details;

import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.codepath.apps.allotweets.R;
import com.codepath.apps.allotweets.model.Hashtag;
import com.codepath.apps.allotweets.model.Media;
import com.codepath.apps.allotweets.model.Tweet;
import com.codepath.apps.allotweets.model.TwitterUser;
import com.codepath.apps.allotweets.network.TwitterError;
import com.codepath.apps.allotweets.network.callbacks.FavoriteTweetCallback;
import com.codepath.apps.allotweets.network.callbacks.RetweetCallback;
import com.codepath.apps.allotweets.network.request.FavoriteTweetRequest;
import com.codepath.apps.allotweets.network.request.RetweetRequest;
import com.codepath.apps.allotweets.ui.base.BaseActivity;
import com.codepath.apps.allotweets.ui.base.TextView;
import com.codepath.apps.allotweets.ui.compose.ComposeTweetFragment;
import com.codepath.apps.allotweets.ui.profile.ProfileActivity;
import com.codepath.apps.allotweets.ui.search.SearchActivity;
import com.codepath.apps.allotweets.ui.utils.DynamicHeightImageView;
import com.codepath.apps.allotweets.ui.utils.DynamicHeightVideoPlayerView;
import com.codepath.apps.allotweets.ui.utils.PatternEditableBuilder;
import com.volokh.danylo.video_player_manager.manager.PlayerItemChangeListener;
import com.volokh.danylo.video_player_manager.manager.SingleVideoPlayerManager;
import com.volokh.danylo.video_player_manager.manager.VideoPlayerManager;
import com.volokh.danylo.video_player_manager.meta.MetaData;

import org.parceler.Parcels;

import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.OnClick;
import icepick.State;

public class TweetDetailActivity extends BaseActivity implements ComposeTweetFragment.OnComposeTweetFragmentListener {

    public static final int REQUEST_CODE = 1337;

    public static final String TWEET = "TWEET";
    public static final String REFRESH_TWEETS = "REFRESH_TWEETS";

    @BindView(R.id.iv_avatar)
    ImageView ivAvatar;

    @BindView(R.id.tv_name)
    TextView tvName;

    @BindView(R.id.tv_screenname)
    TextView tvScreenname;

    @BindView(R.id.tv_status)
    TextView tvStatus;

    @BindView(R.id.tv_date)
    TextView tvDate;

    @BindView(R.id.tv_retweets)
    TextView tvRetweets;

    @BindView(R.id.tv_likes)
    TextView tvLikes;

    @BindView(R.id.bt_retweet)
    ImageView btRetweet;

    @BindView(R.id.bt_favorite)
    ImageView btFavorite;

    @BindView(R.id.photo_container)
    RelativeLayout photoContainer;

    @BindView(R.id.iv_photo)
    DynamicHeightImageView ivPhoto;

    @BindView(R.id.pb_image)
    ProgressBar pbImage;

    @BindView(R.id.pb_retweet)
    ProgressBar pbRetweet;

    @BindView(R.id.pb_favorite)
    ProgressBar pbFavorite;

    @BindView(R.id.video_container)
    RelativeLayout videoContainer;

    @BindView(R.id.video_player)
    DynamicHeightVideoPlayerView videoPlayer;

    private VideoPlayerManager<MetaData> mVideoPlayerManager = new SingleVideoPlayerManager(new PlayerItemChangeListener() {
        @Override
        public void onPlayerItemChanged(MetaData metaData) {

        }
    });

    @State(TweetDetailActivityBundler.class)
    Tweet mTweet;

    @State
    boolean refreshTweets;

    @Override
    protected int getLayoutResourceID() {
        return R.layout.activity_tweet_detail;
    }

    @Override
    protected void initializeUI() {
        setTitle(R.string.tweet);
    }

    @Override
    protected void initializeDataFromIntentBundle(Bundle extras) {
        mTweet = Parcels.unwrap(extras.getParcelable(TWEET));
    }

    @Override
    protected void showData() {
        /*
        Picasso.with(ivAvatar.getContext())
                .load(mTweet.getUser().getProfileImageUrl())
                .placeholder(R.drawable.ic_twitter_gray)
                .fit()
                .transform(new RoundedCornersTransformation(10, 10))
                .into(ivAvatar);
        */
        Glide.with(this)
                .load(mTweet.getUser().getProfileImageUrl())
                .placeholder(R.drawable.ic_twitter_gray)
                .into(new SimpleTarget<GlideDrawable>() {
                    @Override
                    public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
                        ivAvatar.setImageDrawable(resource);
                    }

                    @Override
                    public void onLoadFailed(Exception e, Drawable errorDrawable) {
                        Log.d(TAG_LOG, e.getMessage());
                    }
                });

        tvName.setText(mTweet.getUser().getName());
        tvScreenname.setText(mTweet.getUser().getScreennameForDisplay());
        tvStatus.setText(mTweet.getText());
        tvDate.setText(mTweet.getFormattedCreatedAtDate());

        new PatternEditableBuilder().
                addPattern(Pattern.compile("\\@(\\w+)"), ContextCompat.getColor(tvStatus.getContext(), R.color.colorPrimary),
                        new PatternEditableBuilder.SpannableClickedListener() {
                            @Override
                            public void onSpanClicked(String text) {
                                String screename = text.substring(1);
                                for (TwitterUser twitterUser : mTweet.getEntities().getUserMentions()) {
                                    if (twitterUser.getScreenname().equals(screename)) {
                                        goToProfile(twitterUser);
                                    }
                                }
                            }
                        }).into(tvStatus);
        new PatternEditableBuilder().
                addPattern(Pattern.compile("\\#(\\w+)"), ContextCompat.getColor(tvStatus.getContext(), R.color.colorPrimary),
                        new PatternEditableBuilder.SpannableClickedListener() {
                            @Override
                            public void onSpanClicked(String text) {
                                String sHashtag = text.substring(1);
                                for (Hashtag hashtag : mTweet.getEntities().getHashtags()) {
                                    if (hashtag.getText().equals(sHashtag)) {
                                        goToSearch(hashtag);
                                    }
                                }
                            }
                        }).into(tvStatus);

        photoContainer.setVisibility(View.GONE);
        videoContainer.setVisibility(View.GONE);

        if (mTweet.hasVideo()) {
            videoContainer.setVisibility(View.VISIBLE);
            videoPlayer.setHeightRatio(mTweet.getVideoInfo().getAspectRatio());
            mVideoPlayerManager.playNewVideo(null, videoPlayer, mTweet.getVideo().getUrl());
        } else if (mTweet.hasPhoto()) {
            photoContainer.setVisibility(View.VISIBLE);

            pbImage.setVisibility(View.VISIBLE);
            ivPhoto.setImageDrawable(null);

            Media photo = mTweet.getPhoto();
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
            Glide.with(this).load(photo.getMediaUrl())
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

        updateRetweet();
        updateFavorite();
    }

    private void updateRetweet() {
        btRetweet.setVisibility(View.VISIBLE);
        pbRetweet.setVisibility(View.INVISIBLE);

        btRetweet.setImageResource(mTweet.isRetweeted() ? R.drawable.ic_retweet_done : R.drawable.ic_retweet);

        int retweetCount = mTweet.getRetweetedStatus() != null ? mTweet.getRetweetedStatus().getRetweetCount() : mTweet.getRetweetCount();

        Spannable spanRetweets = new SpannableString(getString(R.string.number_of_retweets, String.valueOf(retweetCount)));
        spanRetweets.setSpan(new ForegroundColorSpan(ContextCompat.getColor(this, R.color.black)),
                0,
                String.valueOf(retweetCount).length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spanRetweets.setSpan(new StyleSpan(Typeface.BOLD),
                0,
                String.valueOf(retweetCount).length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        tvRetweets.setText(spanRetweets);
    }

    private void updateFavorite() {
        btFavorite.setVisibility(View.VISIBLE);
        pbFavorite.setVisibility(View.INVISIBLE);

        btFavorite.setImageResource(mTweet.isFavorite() ? R.drawable.ic_favorite_done : R.drawable.ic_favorite);

        int favoriteCount = mTweet.getRetweetedStatus() != null ? mTweet.getRetweetedStatus().getFavoriteCount() : mTweet.getFavoriteCount();

        Spannable spanLikes = new SpannableString(getString(R.string.number_of_likes, String.valueOf(favoriteCount)));
        spanLikes.setSpan(new ForegroundColorSpan(ContextCompat.getColor(this, R.color.black)),
                0,
                String.valueOf(favoriteCount).length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spanLikes.setSpan(new StyleSpan(Typeface.BOLD),
                0,
                String.valueOf(favoriteCount).length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        tvLikes.setText(spanLikes);
    }

    @OnClick(R.id.bt_reply)
    public void reply() {
        FragmentManager fm = getSupportFragmentManager();
        ComposeTweetFragment editNameDialogFragment = ComposeTweetFragment.newInstance(this, mTweet);
        editNameDialogFragment.show(fm, "compose_tweet");
    }

    @OnClick(R.id.bt_retweet)
    public void retweet() {
        btRetweet.setVisibility(View.INVISIBLE);
        pbRetweet.setVisibility(View.VISIBLE);

        final RetweetRequest request = new RetweetRequest();
        if (mTweet.isRetweeted()) {
            // Undo retweet
            request.setUndo(true);
            request.setTweetId(mTweet.getRetweetedStatus() != null ? mTweet.getRetweetedStatus().getTweetId() : mTweet.getTweetId());
        } else {
            // Retweet
            request.setUndo(false);
            request.setTweetId(mTweet.getTweetId());
        }

        mTwitterClient.retweet(request, new RetweetCallback() {
            @Override
            public void onSuccess(Tweet retweet) {
                // Weird bug ... this request does not return the favorites information
                // Fix
                int favoriteCount = mTweet.getRetweetedStatus() != null ? mTweet.getRetweetedStatus().getFavoriteCount() : mTweet.getFavoriteCount();

                if (request.isUndo()) {
                    mTweet.setRetweetCount(mTweet.getRetweetCount() - 1);
                    mTweet.setRetweetedStatus(retweet);
                    mTweet.setRetweeted(false);
                } else {
                    mTweet.setRetweetCount(mTweet.getRetweetCount() + 1);
                    mTweet.setRetweetedStatus(retweet);
                    mTweet.setRetweeted(true);
                }
                mTweet.setFavoriteCount(favoriteCount);
                if (mTweet.getRetweetedStatus() != null)
                    mTweet.getRetweetedStatus().setFavoriteCount(favoriteCount);
                updateRetweet();
            }

            @Override
            public void onError(TwitterError error) {
                Toast.makeText(TweetDetailActivity.this, R.string.error_retweet, Toast.LENGTH_LONG).show();
                updateRetweet();
            }
        });
    }

    @OnClick(R.id.bt_favorite)
    public void favorite() {
        btFavorite.setVisibility(View.INVISIBLE);
        pbFavorite.setVisibility(View.VISIBLE);

        final FavoriteTweetRequest request = new FavoriteTweetRequest(mTweet.getTweetId(),
                mTweet.isFavorite());
        mTwitterClient.markAsFavorite(request, new FavoriteTweetCallback() {
            @Override
            public void onSuccess(Tweet retweet) {
                mTweet = retweet;
                updateFavorite();
            }

            @Override
            public void onError(TwitterError error) {
                Toast.makeText(TweetDetailActivity.this, R.string.error_favorite, Toast.LENGTH_LONG).show();
                updateFavorite();
            }
        });
    }

    @OnClick(R.id.iv_avatar)
    public void goToProfile() {
        goToProfile(mTweet.getUser());
    }

    private void goToProfile(TwitterUser twitterUser) {
        Intent intent = new Intent(this, ProfileActivity.class);
        intent.putExtra(ProfileActivity.TWITTER_USER, Parcels.wrap(twitterUser));
        startActivity(intent);
    }

    private void goToSearch(Hashtag hashtag) {
        Intent intent = new Intent(this, SearchActivity.class);
        intent.putExtra(SearchActivity.QUERY, hashtag.getTextForDisplay());
        startActivity(intent);
    }

    @OnClick(R.id.fab_share)
    public void shareTweet() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, mTweet.getText());
        startActivity(Intent.createChooser(shareIntent, getString(R.string.share_title)));
    }

    @Override
    public void onStatusUpdated(Tweet tweet) {
        refreshTweets = true;
    }

    @Override
    public void onBackPressed() {
        Intent data = new Intent();
        data.putExtra(TWEET, Parcels.wrap(mTweet));
        data.putExtra(REFRESH_TWEETS, refreshTweets);
        setResult(RESULT_OK, data);
        finish();
    }
}
