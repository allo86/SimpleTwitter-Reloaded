package com.codepath.apps.allotweets.network;

import android.content.Context;

import com.codepath.apps.allotweets.model.Size;
import com.codepath.apps.allotweets.model.Tweet;
import com.codepath.apps.allotweets.model.TwitterUser;
import com.codepath.apps.allotweets.network.callbacks.AuthenticatedUserProfileCallback;
import com.codepath.apps.allotweets.network.callbacks.HomeTimelineCallback;
import com.codepath.apps.allotweets.network.callbacks.PostTweetCallback;
import com.codepath.apps.allotweets.network.deserializer.DateDeserializer;
import com.codepath.apps.allotweets.network.deserializer.SizeDeserializer;
import com.codepath.apps.allotweets.network.request.HomeTimelineRequest;
import com.codepath.apps.allotweets.network.request.TweetRequest;
import com.codepath.oauth.OAuthBaseClient;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

import org.scribe.builder.api.Api;
import org.scribe.builder.api.TwitterApi;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Date;

import cz.msebera.android.httpclient.Header;

/*
 * 
 * This is the object responsible for communicating with a REST API. 
 * Specify the constants below to change the API being communicated with.
 * See a full list of supported API classes: 
 *   https://github.com/fernandezpablo85/scribe-java/tree/master/src/main/java/org/scribe/builder/api
 * Key and Secret are provided by the developer site for the given API i.e dev.twitter.com
 * Add methods for each relevant endpoint in the API.
 * 
 * NOTE: You may want to rename this object based on the service i.e TwitterClient or FlickrClient
 * 
 */
public class TwitterClient extends OAuthBaseClient {
    public static final Class<? extends Api> REST_API_CLASS = TwitterApi.class;
    public static final String REST_URL = "https://api.twitter.com/1.1";
    public static final String REST_CONSUMER_KEY = "2AY4dxW2C3qTCin1tW2NTbLcm";
    public static final String REST_CONSUMER_SECRET = "01T9HE8psiQ2BZ13sqZ6iF7QYFg3qcrXVqxJ5U3tNjd78rqaab";
    public static final String REST_CALLBACK_URL = "oauth://cpallotweets";

    public TwitterClient(Context context) {
        super(context, REST_API_CLASS, REST_URL, REST_CONSUMER_KEY, REST_CONSUMER_SECRET, REST_CALLBACK_URL);
    }

    /*
    // CHANGE THIS
    // DEFINE METHODS for different API endpoints here
    public void getInterestingnessList(AsyncHttpResponseHandler handler) {
        String apiUrl = getApiUrl("?nojsoncallback=1&method=flickr.interestingness.getList");
        // Can specify query string params directly or through RequestParams.
        RequestParams params = new RequestParams();
        params.put("format", "json");
        client.get(apiUrl, params, handler);
    }
    */

    // METHOD == ENDPOINT

	/* 1. Define the endpoint URL with getApiUrl and pass a relative path to the endpoint
     * 	  i.e getApiUrl("statuses/home_timeline.json");
	 * 2. Define the parameters to pass to the request (query or body)
	 *    i.e RequestParams params = new RequestParams("foo", "bar");
	 * 3. Define the request method and make a call to the client
	 *    i.e client.get(apiUrl, params, handler);
	 *    i.e client.post(apiUrl, params, handler);
	 */

    /**
     * Home Timeline
     *
     * @param request
     * @param callback
     */
    public void getHomeTimeline(HomeTimelineRequest request,
                                final HomeTimelineCallback callback) {
        String apiUrl = getApiUrl("statuses/home_timeline.json");

        RequestParams params = new RequestParams();
        if (request != null) {
            params.put("count", request.getCount());
            if (request.getSinceId() != null) {
                params.put("since_id", request.getSinceId());
            }
            if (request.getMaxId() != null) {
                params.put("max_id", request.getMaxId());
            }
        }

        client.get(apiUrl, params, new TextHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                Gson gson = getGson();
                ArrayList<Tweet> tweets = gson.fromJson(responseString,
                        new TypeToken<ArrayList<Tweet>>() {
                        }.getType());
                callback.onSuccess(tweets);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                callback.onError(new TwitterError(throwable != null ? throwable.getMessage() : null));
            }
        });
    }

    /**
     * Authenticated user profile
     *
     * @param callback
     */
    public void getAuthenticatedUserProfile(final AuthenticatedUserProfileCallback callback) {
        String apiUrl = getApiUrl("account/verify_credentials.json");

        RequestParams params = new RequestParams();

        client.get(apiUrl, params, new TextHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                Gson gson = getGson();
                TwitterUser user = gson.fromJson(responseString, TwitterUser.class);
                callback.onSuccess(user);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                callback.onError(new TwitterError(throwable != null ? throwable.getMessage() : null));
            }
        });
    }

    public void postTweet(TweetRequest request,
                          final PostTweetCallback callback) {
        String apiUrl = getApiUrl("statuses/update.json");

        RequestParams params = new RequestParams();
        if (request != null) {
            if (request.getStatus() != null && !"".equals(request.getStatus())) {
                params.put("status", request.getStatus());
            }
            if (request.getInReplyToTweetId() != null) {
                params.put("in_reply_to_status_id", request.getInReplyToTweetId());
            }
            if (request.getLatitude() != null) {
                params.put("lat", request.getLatitude());
            }
            if (request.getLongitude() != null) {
                params.put("long", request.getLongitude());
            }
        }

        client.post(apiUrl, params, new TextHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                Gson gson = getGson();
                Tweet tweet = gson.fromJson(responseString, Tweet.class);
                callback.onSuccess(tweet);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                callback.onError(new TwitterError(throwable != null ? throwable.getMessage() : null));
            }
        });
    }

    private Gson getGson() {
        return new GsonBuilder()
                .excludeFieldsWithModifiers(Modifier.FINAL, Modifier.TRANSIENT, Modifier.STATIC)
                .excludeFieldsWithoutExposeAnnotation()
                .registerTypeAdapter(Date.class, new DateDeserializer())
                .registerTypeAdapter(Size.class, new SizeDeserializer())
                .create();
    }
}