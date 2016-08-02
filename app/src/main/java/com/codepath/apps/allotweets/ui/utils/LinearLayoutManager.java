package com.codepath.apps.allotweets.ui.utils;

import android.content.Context;
import android.util.AttributeSet;

/**
 * Created by ALLO on 1/8/16.
 */
public class LinearLayoutManager extends android.support.v7.widget.LinearLayoutManager {

    private boolean enableVerticalScroll = true;

    public LinearLayoutManager(Context context) {
        super(context);
    }

    public LinearLayoutManager(Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
    }

    public LinearLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    /**
     * Disable predictive animations. There is a bug in RecyclerView which causes views that
     * are being reloaded to pull invalid ViewHolders from the internal recycler stack if the
     * adapter size has decreased since the ViewHolder was recycled.
     */
    @Override
    public boolean supportsPredictiveItemAnimations() {
        return false;
    }

    @Override
    public boolean canScrollVertically() {
        return this.enableVerticalScroll;
    }

    public void setEnableVerticalScroll(boolean enableVerticalScroll) {
        this.enableVerticalScroll = enableVerticalScroll;
    }
}
