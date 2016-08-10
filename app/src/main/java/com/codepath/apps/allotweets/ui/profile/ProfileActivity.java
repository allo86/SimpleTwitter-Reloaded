package com.codepath.apps.allotweets.ui.profile;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.codepath.apps.allotweets.R;
import com.codepath.apps.allotweets.data.DataManager;
import com.codepath.apps.allotweets.model.TwitterUser;
import com.codepath.apps.allotweets.network.TwitterError;
import com.codepath.apps.allotweets.network.callbacks.TwitterUserCallback;
import com.codepath.apps.allotweets.ui.base.BaseActivity;
import com.codepath.apps.allotweets.ui.base.TextView;

import org.parceler.Parcels;

import java.util.Locale;

import butterknife.BindView;
import butterknife.OnClick;
import icepick.State;

/**
 * Profile Activity
 * https://github.com/slidenerd/Android-Design-Support-Library-Demo
 */
public class ProfileActivity extends BaseActivity {

    public static final String TWITTER_USER = "TWITTER_USER";

    @BindView(R.id.iv_background)
    ImageView ivBackground;

    @BindView(R.id.iv_avatar)
    ImageView ivAvatar;

    @BindView(R.id.tv_description)
    TextView tvDescription;

    @BindView(R.id.tv_following)
    TextView tvFollowing;

    @BindView(R.id.tv_followers)
    TextView tvFollowers;

    @BindView(R.id.view_pager)
    ViewPager mViewPager;

    @BindView(R.id.sliding_tabs)
    TabLayout mTabLayout;

    @BindView(R.id.bt_follow_unfollow)
    ImageButton btFollowUnfollow;

    TwitterUser mUser;

    @State
    int selectedTabPosition;

    @Override
    protected int getLayoutResourceID() {
        return R.layout.activity_profile;
    }

    @Override
    protected void initializeUI() {
        setUpTabs();
    }

    @Override
    protected void initializeDataFromIntentBundle(Bundle extras) {
        mUser = Parcels.unwrap(extras.getParcelable(TWITTER_USER));
    }

    @Override
    protected void showData() {
        setTitle(mUser.getName());
        //getSupportActionBar().setTitle(mUser.getName());
        //getSupportActionBar().setSubtitle(mUser.getName());

        if (mUser.getCreatedAt() != null) {
            showUserInfo();
        } else {
            Log.d(TAG_LOG, "profile is incomplete, request it");
            mTwitterClient.getUserProfile(mUser.getUserId(), new TwitterUserCallback() {
                @Override
                public void onSuccess(TwitterUser user) {
                    mUser = user;
                    showUserInfo();
                }

                @Override
                public void onError(TwitterError error) {

                }
            });
        }
    }

    private void showUserInfo() {
        if (mUser.equals(DataManager.sharedInstance().getUser())
                || mUser.isFollowRequestSent()) {
            btFollowUnfollow.setVisibility(View.GONE);
        } else {
            updateFollowUnfollow();
        }

        if (mUser.hasProfileBackgroundImage()) {
            Glide.with(this)
                    .load(mUser.getProfileBannerUrl())
                    //.placeholder(R.drawable.ic_twitter)
                    .centerCrop()
                    .into(ivBackground);
        }

        Glide.with(this)
                .load(mUser.getProfileImageUrl())
                .placeholder(R.drawable.ic_twitter)
                .into(ivAvatar);

        if (mUser.getDescription() != null && !"".equals(mUser.getDescription())) {
            tvDescription.setVisibility(View.VISIBLE);
            tvDescription.setText(mUser.getDescription());
        } else {
            tvDescription.setVisibility(View.GONE);
        }

        tvFollowing.setText(getSpannableForCount(mUser.getFriendsCount(), R.string.number_of_following));
        tvFollowers.setText(getSpannableForCount(mUser.getFollowersCount(), R.string.number_of_followers));
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        selectedTabPosition = mTabLayout.getSelectedTabPosition();
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mViewPager.setCurrentItem(selectedTabPosition);
    }

    /**
     * Method that sets initial configuration for TabLayout
     */
    private void setUpTabs() {
        mViewPager.setAdapter(new ProfileFragmentPagerAdapter(getSupportFragmentManager(), this, mUser));
        mTabLayout.setupWithViewPager(mViewPager);
    }

    private Spannable getSpannableForCount(int count, int stringResource) {
        String sCount = withSuffix(count);
        Spannable span = new SpannableString(getString(stringResource, sCount));
        span.setSpan(new ForegroundColorSpan(ContextCompat.getColor(this, R.color.black)),
                0,
                sCount.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        span.setSpan(new StyleSpan(Typeface.BOLD),
                0,
                sCount.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return span;
    }

    private static String withSuffix(long count) {
        if (count < 1000) return "" + count;
        int exp = (int) (Math.log(count) / Math.log(1000));
        return String.format(Locale.getDefault(), "%.1f %c",
                count / Math.pow(1000, exp),
                "kMGTPE".charAt(exp - 1));
    }

    @OnClick(R.id.tv_following)
    public void goToFollowing() {
        Intent intent = new Intent(this, UsersActivity.class);
        intent.putExtra(UsersActivity.MODE, UsersActivity.Mode.FOLLOWING);
        intent.putExtra(UsersActivity.TWITTER_USER, Parcels.wrap(mUser));
        startActivity(intent);
    }

    @OnClick(R.id.tv_followers)
    public void goToFollowers() {
        Intent intent = new Intent(this, UsersActivity.class);
        intent.putExtra(UsersActivity.MODE, UsersActivity.Mode.FOLLOWERS);
        intent.putExtra(UsersActivity.TWITTER_USER, Parcels.wrap(mUser));
        startActivity(intent);
    }

    @OnClick(R.id.bt_follow_unfollow)
    public void followUnfollow() {
        if (mUser.isFollowing()) {
            mTwitterClient.unfollowUser(mUser, new TwitterUserCallback() {
                @Override
                public void onSuccess(TwitterUser user) {
                    mUser.setFollowing(false);
                    mUser.setFollowersCount(mUser.getFollowersCount() - 1);
                    updateFollowUnfollow();
                }

                @Override
                public void onError(TwitterError error) {
                    Toast.makeText(ProfileActivity.this, R.string.error_general_request, Toast.LENGTH_LONG).show();
                }
            });
        } else {
            mTwitterClient.followUser(mUser, new TwitterUserCallback() {
                @Override
                public void onSuccess(TwitterUser user) {
                    mUser.setFollowing(true);
                    mUser.setFollowersCount(mUser.getFollowersCount() + 1);
                    updateFollowUnfollow();
                }

                @Override
                public void onError(TwitterError error) {
                    Toast.makeText(ProfileActivity.this, R.string.error_general_request, Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    private void updateFollowUnfollow() {
        btFollowUnfollow.setVisibility(View.VISIBLE);
        if (mUser.isFollowing()) {
            btFollowUnfollow.setImageResource(R.drawable.ic_following);
        } else {
            btFollowUnfollow.setImageResource(R.drawable.ic_follow);
        }
    }

    public TwitterUser getUser() {
        return mUser;
    }
}
