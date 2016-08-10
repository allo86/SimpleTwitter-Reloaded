package com.codepath.apps.allotweets.ui.main;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;

import com.codepath.apps.allotweets.R;
import com.codepath.apps.allotweets.ui.timeline.HomeTimelineFragment;
import com.codepath.apps.allotweets.ui.timeline.MentionsTimelineFragment;
import com.codepath.apps.allotweets.ui.timeline.MessagesFragment;

/**
 * FragmentPagerAdapter used in the MainActivity
 * <p/>
 * Created by ALLO on 7/8/16.
 */
public class MainFragmentPagerAdapter extends FragmentPagerAdapter {
    private static final int PAGE_COUNT = 3;

    private Context context;

    public MainFragmentPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new HomeTimelineFragment();
            case 1:
                return new MentionsTimelineFragment();
            case 2:
                return new MessagesFragment();
            default:
                return new HomeTimelineFragment();
        }
    }

    @Override
    public CharSequence getPageTitle(int position) {
        Drawable image = ContextCompat.getDrawable(context, getDrawableResource(position));
        image.setBounds(0, 0, image.getIntrinsicWidth(), image.getIntrinsicHeight());
        SpannableString sb = new SpannableString(" ");
        ImageSpan imageSpan = new ImageSpan(image, ImageSpan.ALIGN_BOTTOM);
        sb.setSpan(imageSpan, 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return sb;
    }

    private int getDrawableResource(int position) {
        switch (position) {
            case 0:
                return R.drawable.ic_home;
            case 1:
                return R.drawable.ic_mention;
            case 2:
                return R.drawable.ic_message;
            default:
                return R.drawable.ic_home;
        }
    }
}
