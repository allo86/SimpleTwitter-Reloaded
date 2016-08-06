package com.codepath.apps.allotweets.ui.compose;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatButton;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.codepath.apps.allotweets.R;
import com.codepath.apps.allotweets.TwitterApplication;
import com.codepath.apps.allotweets.data.DataManager;
import com.codepath.apps.allotweets.model.Tweet;
import com.codepath.apps.allotweets.network.TwitterClient;
import com.codepath.apps.allotweets.network.TwitterError;
import com.codepath.apps.allotweets.network.callbacks.PostTweetCallback;
import com.codepath.apps.allotweets.network.request.TweetRequest;
import com.codepath.apps.allotweets.ui.base.EditText;
import com.codepath.apps.allotweets.ui.base.TextView;

import org.parceler.Parcels;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ComposeTweetFragment.OnComposeTweetFragmentListener} interface
 * to handle interaction events.
 * Use the {@link ComposeTweetFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ComposeTweetFragment extends DialogFragment {

    private static final String TAG_LOG = ComposeTweetFragment.class.getCanonicalName();

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnComposeTweetFragmentListener {

        void onStatusUpdated(Tweet tweet);

    }

    private OnComposeTweetFragmentListener mListener;

    private static final String TWEET = "TWEET";

    private static final int TWEET_LIMIT = 140;

    private Tweet mTweet;

    private Unbinder unbinder;

    @BindView(R.id.iv_profile)
    ImageView ivAvatar;

    @BindView(R.id.tv_in_reply)
    TextView tvInReply;

    @BindView(R.id.et_status)
    EditText inputStatus;

    @BindView(R.id.tv_count)
    TextView tvCount;

    @BindView(R.id.bt_tweet)
    AppCompatButton btTweet;

    private String status;

    public ComposeTweetFragment() {
        // Required empty public constructor
    }

    public static ComposeTweetFragment newInstance() {
        return new ComposeTweetFragment();
    }

    public static ComposeTweetFragment newInstance(Tweet tweet) {
        ComposeTweetFragment fragment = new ComposeTweetFragment();
        Bundle args = new Bundle();
        args.putParcelable(TWEET, Parcels.wrap(tweet));
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mTweet = Parcels.unwrap(getArguments().getParcelable(TWEET));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_compose_tweet, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        unbinder = ButterKnife.bind(this, view);

        initializeUI();

        // Keyboard
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        // Hide title bar
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        if (DataManager.sharedInstance().getUser() != null) {
            ivAvatar.setVisibility(View.VISIBLE);
            /*
            Picasso.with(ivAvatar.getContext())
                    .load(DataManager.sharedInstance().getUser().getProfileImageUrl())
                    .placeholder(R.drawable.ic_twitter_gray)
                    .fit()
                    .transform(new RoundedCornersTransformation(10, 10))
                    .into(ivAvatar);
             */
            Glide.with(view.getContext())
                    .load(DataManager.sharedInstance().getUser().getProfileImageUrl())
                    .placeholder(R.drawable.ic_twitter_gray)
                    .into(new SimpleTarget<GlideDrawable>() {
                        @Override
                        public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
                            ivAvatar.setImageDrawable(resource);
                        }

                        @Override
                        public void onLoadFailed(Exception e, Drawable errorDrawable) {
                            Log.d(TAG_LOG, e.getMessage());
                        }
                    });
        } else {
            ivAvatar.setVisibility(View.GONE);
        }

        if (mTweet != null) {
            tvInReply.setText(getString(R.string.in_reply_to, mTweet.getUser().getScreennameForDisplay()));
            tvInReply.setVisibility(View.VISIBLE);
        } else {
            tvInReply.setVisibility(View.GONE);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnComposeTweetFragmentListener) {
            mListener = (OnComposeTweetFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnComposeTweetFragmentListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    private void initializeUI() {
        inputStatus.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                status = editable != null ? editable.toString() : null;
                updateCount();
                validateEnableTweet();
            }
        });

        updateCount();
        validateEnableTweet();
    }

    private void updateCount() {
        int count = status != null ? status.trim().length() : 0;
        tvCount.setText(String.valueOf(TWEET_LIMIT - count));
    }

    private void validateEnableTweet() {
        btTweet.setEnabled(status != null && status.trim().length() > 0);
    }

    @OnClick(R.id.bt_close)
    public void exit() {
        dismiss();
    }

    @OnClick(R.id.bt_tweet)
    public void postTweet() {
        TweetRequest request = new TweetRequest();
        request.setStatus(status);

        TwitterClient client = TwitterApplication.getRestClient();
        client.postTweet(request, new PostTweetCallback() {
            @Override
            public void onSuccess(Tweet tweet) {
                mListener.onStatusUpdated(tweet);
                dismiss();
            }

            @Override
            public void onError(TwitterError error) {

            }
        });
    }
}
