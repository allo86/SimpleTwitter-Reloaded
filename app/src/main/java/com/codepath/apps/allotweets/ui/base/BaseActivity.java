package com.codepath.apps.allotweets.ui.base;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.codepath.apps.allotweets.R;
import com.codepath.apps.allotweets.TwitterApplication;
import com.codepath.apps.allotweets.network.TwitterClient;

import butterknife.ButterKnife;
import icepick.Icepick;

/**
 * Created by ALLO on 1/8/16.
 */
public abstract class BaseActivity extends AppCompatActivity {

    protected final String TAG_LOG = this.getClass().getCanonicalName();

    protected Toolbar toolbar;
    protected AppBarLayout appBarLayout;

    protected TwitterClient mTwitterClient;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResourceID());

        initializeUI();

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            initializeDataFromIntentBundle(extras);
        }

        Icepick.restoreInstanceState(this, savedInstanceState);

        mTwitterClient = TwitterApplication.getRestClient();

        showData();
    }

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        super.setContentView(layoutResID);
        ButterKnife.bind(this);

        customizeToolbar();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Icepick.saveInstanceState(this, outState);
    }

    private void customizeToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            if (getSupportActionBar() != null) {
                if (!isTaskRoot()) {
                    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                    getSupportActionBar().setDisplayShowHomeEnabled(true);
                }
            }
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
            appBarLayout = (AppBarLayout) findViewById(R.id.app_bar_layout);
        }
    }

    protected abstract int getLayoutResourceID();

    protected abstract void initializeUI();

    protected abstract void initializeDataFromIntentBundle(Bundle extras);

    protected abstract void showData();

    protected void turnOffToolbarScrolling() {
        if (toolbar != null && appBarLayout != null) {
            AppBarLayout.LayoutParams toolbarLayoutParams = (AppBarLayout.LayoutParams) toolbar.getLayoutParams();
            toolbarLayoutParams.setScrollFlags(0);
            toolbar.setLayoutParams(toolbarLayoutParams);
            CoordinatorLayout.LayoutParams appBarLayoutParams = (CoordinatorLayout.LayoutParams) appBarLayout.getLayoutParams();
            appBarLayoutParams.setBehavior(null);
            appBarLayout.setLayoutParams(appBarLayoutParams);
        }
    }

    protected void turnOnToolbarScrolling() {
        if (toolbar != null && appBarLayout != null) {
            AppBarLayout.LayoutParams toolbarLayoutParams = (AppBarLayout.LayoutParams) toolbar.getLayoutParams();
            if (toolbarLayoutParams.getScrollFlags() != (AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL | AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS)) {
                toolbarLayoutParams.setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL | AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS);
                toolbar.setLayoutParams(toolbarLayoutParams);
                CoordinatorLayout.LayoutParams appBarLayoutParams = (CoordinatorLayout.LayoutParams) appBarLayout.getLayoutParams();
                appBarLayoutParams.setBehavior(new AppBarLayout.Behavior());
                appBarLayout.setLayoutParams(appBarLayoutParams);
            }
        }
    }
}
