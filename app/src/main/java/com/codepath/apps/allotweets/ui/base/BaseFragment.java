package com.codepath.apps.allotweets.ui.base;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codepath.apps.allotweets.TwitterApplication;
import com.codepath.apps.allotweets.network.TwitterClient;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by ALLO on 7/8/16.
 */
public abstract class BaseFragment extends Fragment {

    protected final String TAG_LOG = this.getClass().getCanonicalName();

    private Unbinder unbinder;

    protected TwitterClient mTwitterClient;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(getLayoutResourceID(), container, false);

        unbinder = ButterKnife.bind(this, view);

        mTwitterClient = TwitterApplication.getRestClient();

        initializeUI();
        showData();

        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            initializeDataFromArguments(getArguments());
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    protected abstract int getLayoutResourceID();

    protected abstract void initializeUI();

    protected abstract void initializeDataFromArguments(Bundle args);

    protected abstract void showData();
}
