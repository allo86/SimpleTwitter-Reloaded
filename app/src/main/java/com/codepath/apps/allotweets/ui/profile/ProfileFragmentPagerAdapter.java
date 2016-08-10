package com.codepath.apps.allotweets.ui.profile;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.codepath.apps.allotweets.R;
import com.codepath.apps.allotweets.model.TwitterUser;
import com.codepath.apps.allotweets.ui.timeline.UserFavoritesFragment;
import com.codepath.apps.allotweets.ui.timeline.UserTimelineFragment;

/**
 * FragmentPagerAdapter used in the ProfileActivity
 * <p/>
 * Created by ALLO on 7/8/16.
 */
public class ProfileFragmentPagerAdapter extends FragmentPagerAdapter {
    private static final int PAGE_COUNT = 2;

    private Context context;
    private TwitterUser user;

    public ProfileFragmentPagerAdapter(FragmentManager fm, Context context, TwitterUser user) {
        super(fm);
        this.context = context;
        this.user = user;
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return UserTimelineFragment.newInstance(user);
            case 1:
                return UserFavoritesFragment.newInstance(user);
            default:
                return UserTimelineFragment.newInstance(user);
        }
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return context.getString(R.string.tab_tweets);
            case 1:
                return context.getString(R.string.tab_likes);
            default:
                return context.getString(R.string.tab_tweets);
        }
    }
}
