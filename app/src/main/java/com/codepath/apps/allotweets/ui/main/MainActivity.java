package com.codepath.apps.allotweets.ui.main;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.codepath.apps.allotweets.R;
import com.codepath.apps.allotweets.data.DataManager;
import com.codepath.apps.allotweets.model.Tweet;
import com.codepath.apps.allotweets.model.TwitterUser;
import com.codepath.apps.allotweets.ui.base.BaseActivity;
import com.codepath.apps.allotweets.ui.base.TextView;
import com.codepath.apps.allotweets.ui.compose.ComposeTweetFragment;
import com.codepath.apps.allotweets.ui.profile.ProfileActivity;
import com.codepath.apps.allotweets.ui.search.SearchActivity;

import org.parceler.Parcels;

import butterknife.BindView;
import butterknife.OnClick;
import icepick.State;
import jp.wasabeef.glide.transformations.CropCircleTransformation;

/**
 * Main Activity
 */
public class MainActivity extends BaseActivity implements ComposeTweetFragment.OnComposeTweetFragmentListener {

    @BindView(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;

    @BindView(R.id.navigation_view)
    NavigationView mNavigationView;

    @BindView(R.id.view_pager)
    ViewPager mViewPager;

    @BindView(R.id.sliding_tabs)
    TabLayout mTabLayout;

    // Toolbar drawer
    private ActionBarDrawerToggle mActionBarDrawerToggle;

    // Navigation header
    private ImageView ivAvatar;
    private TextView tvScreenname;

    @State
    int selectedTabPosition;

    @Override
    protected int getLayoutResourceID() {
        return R.layout.activity_main;
    }

    @Override
    protected void initializeUI() {
        // Navigation Drawer
        setUpNavigationDrawer();

        // TabLayout
        setUpTabs();
    }

    @Override
    protected void initializeDataFromIntentBundle(Bundle extras) {

    }

    @Override
    protected void showData() {
        // Show user info in header
        Glide.with(this)
                .load(DataManager.sharedInstance().getUser().getProfileImageUrl())
                .placeholder(R.drawable.ic_twitter)
                .bitmapTransform(new CropCircleTransformation(this))
                .into(ivAvatar);
        tvScreenname.setText(DataManager.sharedInstance().getUser().getScreennameForDisplay());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mActionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        } else if (item.getItemId() == R.id.ic_search) {
            goToSearch();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    // `onPostCreate` called when activity start-up is complete after `onStart()`
    // NOTE! Make sure to override the method with only a single `Bundle` argument
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Sync the toggle state after onRestoreInstanceState has occurred.
        mActionBarDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        // Pass any configuration change to the drawer toggles
        mActionBarDrawerToggle.onConfigurationChanged(newConfig);
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
     * Method that sets initial configuration for Navigation Drawer
     */
    private void setUpNavigationDrawer() {
        // Navigation Drawer
        mNavigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        selectDrawerItem(menuItem);
                        return true;
                    }
                });

        // Hamburguer icon
        mActionBarDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close);
        mDrawerLayout.addDrawerListener(mActionBarDrawerToggle);

        // Navigation View Header
        ivAvatar = (ImageView) mNavigationView.getHeaderView(0).findViewById(R.id.iv_avatar);
        tvScreenname = (TextView) mNavigationView.getHeaderView(0).findViewById(R.id.tv_screename);
    }

    /**
     * Method that sets initial configuration for TabLayout
     */
    private void setUpTabs() {
        mViewPager.setAdapter(new MainFragmentPagerAdapter(getSupportFragmentManager(), this));
        mTabLayout.setupWithViewPager(mViewPager);
    }

    /**
     * Navigation to selected drawer item
     *
     * @param menuItem MenuItem
     */
    private void selectDrawerItem(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.nav_profile:
                goToProfile(DataManager.sharedInstance().getUser());
                break;
            case R.id.nav_help:
                Toast.makeText(this, "help", Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
    }

    private void goToProfile(TwitterUser user) {
        Intent intent = new Intent(this, ProfileActivity.class);
        intent.putExtra(ProfileActivity.TWITTER_USER, Parcels.wrap(user));
        startActivity(intent);
    }

    private void goToSearch() {
        Intent intent = new Intent(this, SearchActivity.class);
        startActivity(intent);
    }

    /**
     * Method that open the dialog to update the user's status
     */
    @OnClick(R.id.fab_tweet)
    public void composeTweet() {
        FragmentManager fm = getSupportFragmentManager();
        ComposeTweetFragment editNameDialogFragment = ComposeTweetFragment.newInstance(this);
        editNameDialogFragment.show(fm, "compose_tweet");
    }

    /**
     * Callback with the user's new status
     *
     * @param tweet Tweet
     */
    @Override
    public void onStatusUpdated(Tweet tweet) {
        // TODO: Send new tweet to fragments in tab layout
        /*
        mTweets.add(0, tweet);
        mAdapter.notifyDataSetChanged(mTweets);
        mLayoutManager.scrollToPosition(0);
        */
    }

}
