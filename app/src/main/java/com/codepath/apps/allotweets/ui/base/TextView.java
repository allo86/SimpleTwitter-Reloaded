package com.codepath.apps.allotweets.ui.base;

import android.content.Context;
import android.util.AttributeSet;

/**
 * Created by ALLO on 1/8/16.
 */
public class TextView extends android.widget.TextView {

    public TextView(Context context) {
        super(context);
        init();
    }

    public TextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    protected void init() {

    }
}
