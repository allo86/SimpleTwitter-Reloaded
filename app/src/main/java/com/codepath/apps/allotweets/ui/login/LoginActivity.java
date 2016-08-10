package com.codepath.apps.allotweets.ui.login;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.view.Menu;
import android.widget.Toast;

import com.codepath.apps.allotweets.R;
import com.codepath.apps.allotweets.TwitterApplication;
import com.codepath.apps.allotweets.data.DataManager;
import com.codepath.apps.allotweets.model.TwitterUser;
import com.codepath.apps.allotweets.network.TwitterClient;
import com.codepath.apps.allotweets.network.TwitterError;
import com.codepath.apps.allotweets.network.callbacks.TwitterUserCallback;
import com.codepath.apps.allotweets.network.utils.Utils;
import com.codepath.apps.allotweets.ui.main.MainActivity;
import com.codepath.oauth.OAuthLoginActionBarActivity;
import com.google.gson.Gson;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginActivity extends OAuthLoginActionBarActivity<TwitterClient> {

    private ProgressDialog pd;

    @BindView(R.id.bt_log_in)
    AppCompatButton btLogIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ButterKnife.bind(this);

        pd = new ProgressDialog(this);
        pd.setTitle(R.string.loading);
        pd.setMessage(getString(R.string.loading_user_profile));
        pd.setCancelable(false);
    }

    // Inflate the menu; this adds items to the action bar if it is present.
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.login, menu);
        return true;
    }

    @OnClick(R.id.bt_log_in)
    public void didTapLogIn() {
        loginToRest();
    }

    // OAuth authenticated successfully, launch primary authenticated activity
    // i.e Display application "homepage"
    @Override
    public void onLoginSuccess() {
        if (pd != null && !pd.isShowing()) {
            pd.show();
        }

        if (Utils.isOnline()) {
            TwitterClient twitterClient = TwitterApplication.getRestClient();
            twitterClient.getAuthenticatedUserProfile(new TwitterUserCallback() {
                @Override
                public void onSuccess(TwitterUser user) {
                    // Save user in SharedPreferences
                    saveUserInPrefs(user);

                    DataManager.sharedInstance().setUser(user);
                    goToMainActivity();
                }

                @Override
                public void onError(TwitterError error) {
                    Toast.makeText(LoginActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
                    DataManager.sharedInstance().setUser(getUserFromPrefs());
                    goToMainActivity();
                }
            });
        } else {
            // Get profile from shared preferences
            DataManager.sharedInstance().setUser(getUserFromPrefs());
            goToMainActivity();
        }
    }

    // OAuth authentication flow failed, handle the error
    // i.e Display an error dialog or toast
    @Override
    public void onLoginFailure(Exception e) {
        e.printStackTrace();
    }

    // Click handler method for the button used to start OAuth flow
    // Uses the client to initiate OAuth authorization
    // This should be tied to a button used to login
    public void loginToRest() {
        getClient().connect();
    }

    private void goToMainActivity() {
        Intent i = new Intent(this, MainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);

        if (pd != null && pd.isShowing()) {
            pd.dismiss();
        }

        finish();
    }

    private void saveUserInPrefs(TwitterUser user) {
        SharedPreferences mPrefs = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        Gson gson = Utils.getGson();
        String json = gson.toJson(user);
        prefsEditor.putString("user", json);
        prefsEditor.apply();
    }

    private TwitterUser getUserFromPrefs() {
        SharedPreferences mPrefs = getPreferences(MODE_PRIVATE);
        Gson gson = Utils.getGson();
        String json = mPrefs.getString("user", "");
        return gson.fromJson(json, TwitterUser.class);
    }

}
