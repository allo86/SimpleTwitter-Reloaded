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
import android.widget.ImageView;

import com.codepath.apps.allotweets.R;
import com.codepath.apps.allotweets.model.Tweet;
import com.codepath.apps.allotweets.ui.base.BaseActivity;
import com.codepath.apps.allotweets.ui.base.TextView;
import com.codepath.apps.allotweets.ui.compose.ComposeTweetFragment;
import com.squareup.picasso.Picasso;

import org.parceler.Parcels;

import butterknife.BindView;
import butterknife.OnClick;
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

    private Tweet mTweet;

    private boolean refreshTweets;

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
        tvScreenname.setText(mTweet.getUser().getScreenname());
        tvStatus.setText(mTweet.getText());
        tvDate.setText(mTweet.getFormattedCreatedAtDate());

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

        updateRetweet();
        updateFavorite();
    }

    private void updateRetweet() {
        btRetweet.setImageResource(mTweet.isRetweeted() ? R.drawable.ic_retweet_done : R.drawable.ic_retweet);
    }

    private void updateFavorite() {
        btFavorite.setImageResource(mTweet.isFavorite() ? R.drawable.ic_favorite_done : R.drawable.ic_favorite);
    }

    @OnClick(R.id.bt_reply)
    public void reply() {
        FragmentManager fm = getSupportFragmentManager();
        ComposeTweetFragment editNameDialogFragment = ComposeTweetFragment.newInstance(mTweet);
        editNameDialogFragment.show(fm, "compose_tweet");
    }

    @OnClick(R.id.bt_retweet)
    public void retweet() {
        mTweet.setRetweeted(!mTweet.isRetweeted());
        updateRetweet();
    }

    @OnClick(R.id.bt_favorite)
    public void favorite() {
        mTweet.setFavorite(!mTweet.isFavorite());
        updateFavorite();
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
        if (refreshTweets) {
            Intent data = new Intent();
            data.putExtra(REFRESH_TWEETS, true);
            setResult(RESULT_OK, data);
            finish();
        } else {
            super.onBackPressed();
        }
    }
}
