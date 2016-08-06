package com.codepath.apps.allotweets.ui.details;

import android.content.Intent;
import android.graphics.Typeface;
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

import com.codepath.apps.allotweets.R;
import com.codepath.apps.allotweets.model.Media;
import com.codepath.apps.allotweets.model.Tweet;
import com.codepath.apps.allotweets.network.TwitterError;
import com.codepath.apps.allotweets.network.callbacks.FavoriteTweetCallback;
import com.codepath.apps.allotweets.network.callbacks.RetweetCallback;
import com.codepath.apps.allotweets.network.request.FavoriteTweetRequest;
import com.codepath.apps.allotweets.network.request.RetweetRequest;
import com.codepath.apps.allotweets.ui.base.BaseActivity;
import com.codepath.apps.allotweets.ui.base.TextView;
import com.codepath.apps.allotweets.ui.compose.ComposeTweetFragment;
import com.codepath.apps.allotweets.ui.utils.DynamicHeightImageView;
import com.codepath.apps.allotweets.ui.utils.DynamicHeightVideoPlayerView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.volokh.danylo.video_player_manager.manager.PlayerItemChangeListener;
import com.volokh.danylo.video_player_manager.manager.SingleVideoPlayerManager;
import com.volokh.danylo.video_player_manager.manager.VideoPlayerManager;
import com.volokh.danylo.video_player_manager.meta.MetaData;

import org.parceler.Parcels;

import butterknife.BindView;
import butterknife.OnClick;
import icepick.State;
import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;

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
        Picasso.with(ivAvatar.getContext())
                .load(mTweet.getUser().getProfileImageUrl())
                .placeholder(R.drawable.ic_twitter_gray)
                .fit()
                .transform(new RoundedCornersTransformation(10, 10))
                .into(ivAvatar);

        tvName.setText(mTweet.getUser().getName());
        tvScreenname.setText(mTweet.getUser().getScreennameForDisplay());
        tvStatus.setText(mTweet.getText());
        tvDate.setText(mTweet.getFormattedCreatedAtDate());

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

        updateRetweet();
        updateFavorite();
    }

    private void updateRetweet() {
        btRetweet.setVisibility(View.VISIBLE);
        pbRetweet.setVisibility(View.INVISIBLE);

        btRetweet.setImageResource(mTweet.isRetweeted() ? R.drawable.ic_retweet_done : R.drawable.ic_retweet);

        Spannable spanRetweets = new SpannableString(getString(R.string.number_of_retweets, String.valueOf(mTweet.getRetweetCount())));
        spanRetweets.setSpan(new ForegroundColorSpan(ContextCompat.getColor(this, R.color.black)),
                0,
                String.valueOf(mTweet.getRetweetCount()).length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spanRetweets.setSpan(new StyleSpan(Typeface.BOLD),
                0,
                String.valueOf(mTweet.getRetweetCount()).length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        tvRetweets.setText(spanRetweets);
    }

    private void updateFavorite() {
        btFavorite.setVisibility(View.VISIBLE);
        pbFavorite.setVisibility(View.INVISIBLE);

        btFavorite.setImageResource(mTweet.isFavorite() ? R.drawable.ic_favorite_done : R.drawable.ic_favorite);

        Spannable spanLikes = new SpannableString(getString(R.string.number_of_likes, String.valueOf(mTweet.getFavoriteCount())));
        spanLikes.setSpan(new ForegroundColorSpan(ContextCompat.getColor(this, R.color.black)),
                0,
                String.valueOf(mTweet.getFavoriteCount()).length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spanLikes.setSpan(new StyleSpan(Typeface.BOLD),
                0,
                String.valueOf(mTweet.getFavoriteCount()).length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        tvLikes.setText(spanLikes);
    }

    @OnClick(R.id.bt_reply)
    public void reply() {
        FragmentManager fm = getSupportFragmentManager();
        ComposeTweetFragment editNameDialogFragment = ComposeTweetFragment.newInstance(mTweet);
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
            request.setTweetId(mTweet.getRetweetedStatus().getTweetId());
        } else {
            // Retweet
            request.setUndo(false);
            request.setTweetId(mTweet.getTweetId());
        }

        mTwitterClient.retweet(request, new RetweetCallback() {
            @Override
            public void onSuccess(Tweet retweet) {
                if (request.isUndo()) {
                    mTweet.setRetweetCount(mTweet.getRetweetCount() - 1);
                    mTweet.setRetweetedStatus(null);
                    mTweet.setRetweeted(false);
                } else {
                    mTweet.setRetweetCount(mTweet.getRetweetCount() + 1);
                    mTweet.setRetweetedStatus(retweet);
                    mTweet.setRetweeted(true);
                }
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

        FavoriteTweetRequest request = new FavoriteTweetRequest(mTweet.getTweetId(),
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
