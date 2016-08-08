package com.codepath.apps.allotweets.ui.profile;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.codepath.apps.allotweets.R;
import com.codepath.apps.allotweets.model.TwitterUser;
import com.codepath.apps.allotweets.ui.base.BaseActivity;
import com.codepath.apps.allotweets.ui.base.TextView;

import org.parceler.Parcels;

import butterknife.BindView;
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
        getSupportActionBar().setTitle(mUser.getName());
        getSupportActionBar().setSubtitle(mUser.getName());

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

        tvDescription.setText(mUser.getDescription());

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
        Spannable span = new SpannableString(getString(stringResource, String.valueOf(count)));
        span.setSpan(new ForegroundColorSpan(ContextCompat.getColor(this, R.color.white)),
                0,
                String.valueOf(count).length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        span.setSpan(new StyleSpan(Typeface.BOLD),
                0,
                String.valueOf(count).length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return span;
    }

    public TwitterUser getUser() {
        return mUser;
    }
}
