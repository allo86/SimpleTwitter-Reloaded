package com.codepath.apps.allotweets.ui.base;

import android.content.Context;
import android.util.AttributeSet;

/**
 * Created by ALLO on 1/8/16.
 */
public class EditText extends android.widget.EditText {

    public EditText(Context context) {
        super(context);
        init();
    }

    public EditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public EditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }


    private void init() {

    }
}
