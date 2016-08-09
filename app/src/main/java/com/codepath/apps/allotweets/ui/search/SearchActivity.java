package com.codepath.apps.allotweets.ui.search;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ProgressBar;

import com.codepath.apps.allotweets.R;
import com.codepath.apps.allotweets.ui.base.BaseActivity;

import icepick.State;

/**
 * Search Tweets
 */
public class SearchActivity extends BaseActivity {

    private SearchView searchView;
    private MenuItem progressItem;

    @State
    String mTextFilter;

    @State
    boolean mFirstLoad;

    @Override
    protected int getLayoutResourceID() {
        return R.layout.activity_search;
    }

    @Override
    protected void initializeUI() {

    }

    @Override
    protected void initializeDataFromIntentBundle(Bundle extras) {

    }

    @Override
    protected void showData() {
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // Store instance of the menu item containing progress
        progressItem = menu.findItem(R.id.miActionProgress);
        // Extract the action-view from the menu item
        ProgressBar v = (ProgressBar) MenuItemCompat.getActionView(progressItem);
        // Return to finish
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.search, menu);

        final MenuItem searchItem = menu.findItem(R.id.search);
        searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setQueryHint(getString(R.string.hint_search));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // User pressed the search button
                mTextFilter = query;
                searchView.clearFocus();
                searchItem.collapseActionView();
                setTitle(query);
                startNewSearch();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        MenuItemCompat.setOnActionExpandListener(searchItem, new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                // Return true to collapse action view
                return true;
            }

            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                // Update text
                searchView.setQuery(mTextFilter, false);
                // Return true to expand action view
                return true;
            }
        });

        if (!mFirstLoad) {
            searchItem.expandActionView();
            mFirstLoad = true;
        }

        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void startNewSearch() {
        SearchTimelineFragment fragment = SearchTimelineFragment.newInstance(mTextFilter);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.commit();
    }

    private void showProgressBar() {
        // Show progress item
        if (progressItem != null) progressItem.setVisible(true);
    }

    private void hideProgressBar() {
        // Hide progress item
        if (progressItem != null) progressItem.setVisible(false);
    }
}
