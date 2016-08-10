package com.codepath.apps.allotweets.network;

import android.content.Context;

import com.codepath.apps.allotweets.model.Message;
import com.codepath.apps.allotweets.model.Tweet;
import com.codepath.apps.allotweets.model.TwitterUser;
import com.codepath.apps.allotweets.network.callbacks.FavoriteTweetCallback;
import com.codepath.apps.allotweets.network.callbacks.MessagesCallback;
import com.codepath.apps.allotweets.network.callbacks.PostTweetCallback;
import com.codepath.apps.allotweets.network.callbacks.RetweetCallback;
import com.codepath.apps.allotweets.network.callbacks.TimelineCallback;
import com.codepath.apps.allotweets.network.callbacks.TwitterUserCallback;
import com.codepath.apps.allotweets.network.callbacks.TwitterUsersCallback;
import com.codepath.apps.allotweets.network.request.FavoriteTweetRequest;
import com.codepath.apps.allotweets.network.request.RetweetRequest;
import com.codepath.apps.allotweets.network.request.TimelineRequest;
import com.codepath.apps.allotweets.network.request.TweetRequest;
import com.codepath.apps.allotweets.network.request.TwitterUsersRequest;
import com.codepath.apps.allotweets.network.response.TwitterUsersResponse;
import com.codepath.apps.allotweets.network.utils.Utils;
import com.codepath.oauth.OAuthBaseClient;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

import org.scribe.builder.api.Api;
import org.scribe.builder.api.TwitterApi;

import java.util.ArrayList;

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
     * Authenticated user profile
     *
     * @param callback Callback
     */
    public void getAuthenticatedUserProfile(final TwitterUserCallback callback) {
        String apiUrl = getApiUrl("account/verify_credentials.json");

        RequestParams params = new RequestParams();

        client.get(apiUrl, params, new TextHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                Gson gson = Utils.getGson();
                TwitterUser user = gson.fromJson(responseString, TwitterUser.class);
                callback.onSuccess(user);
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
     * @param callback Callback
     */
    public void getUserProfile(Long userId,
                               final TwitterUserCallback callback) {
        String apiUrl = getApiUrl("users/show.json");

        RequestParams params = new RequestParams();
        params.put("user_id", userId);

        client.get(apiUrl, params, new TextHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                Gson gson = Utils.getGson();
                TwitterUser user = gson.fromJson(responseString, TwitterUser.class);
                callback.onSuccess(user);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                callback.onError(new TwitterError(throwable != null ? throwable.getMessage() : null));
            }
        });
    }

    /**
     * Home Timeline
     *
     * @param request  Request
     * @param callback Callback
     */
    public void getHomeTimeline(TimelineRequest request,
                                final TimelineCallback callback) {
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
                Gson gson = Utils.getGson();
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
     * Mentions Timeline
     *
     * @param request  Request
     * @param callback Callback
     */
    public void getMentionsTimeline(TimelineRequest request,
                                    final TimelineCallback callback) {
        String apiUrl = getApiUrl("statuses/mentions_timeline.json");

        RequestParams params = new RequestParams();
        if (request != null) {
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
                Gson gson = Utils.getGson();
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
     * User Timeline
     *
     * @param request  Request
     * @param callback Callback
     */
    public void getUserTimeline(TimelineRequest request,
                                final TimelineCallback callback) {
        String apiUrl = getApiUrl("statuses/user_timeline.json");

        RequestParams params = new RequestParams();
        if (request != null) {
            if (request.getSinceId() != null) {
                params.put("since_id", request.getSinceId());
            }
            if (request.getMaxId() != null) {
                params.put("max_id", request.getMaxId());
            }
            if (request.getUserId() != null) {
                params.put("user_id", request.getUserId());
            }
        }

        client.get(apiUrl, params, new TextHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                Gson gson = Utils.getGson();
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
     * Favorites Timeline
     *
     * @param request  Request
     * @param callback Callback
     */
    public void getFavoritesTimeline(TimelineRequest request,
                                     final TimelineCallback callback) {
        String apiUrl = getApiUrl("favorites/list.json");

        RequestParams params = new RequestParams();
        if (request != null) {
            if (request.getSinceId() != null) {
                params.put("since_id", request.getSinceId());
            }
            if (request.getMaxId() != null) {
                params.put("max_id", request.getMaxId());
            }
            if (request.getUserId() != null) {
                params.put("user_id", request.getUserId());
            }
        }

        client.get(apiUrl, params, new TextHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                Gson gson = Utils.getGson();
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
     * Search tweets
     *
     * @param request  Request
     * @param callback Callback
     */
    public void searchTweets(TimelineRequest request,
                             final TimelineCallback callback) {
        String apiUrl = getApiUrl("search/tweets.json");

        RequestParams params = new RequestParams();
        if (request != null) {
            if (request.getSinceId() != null) {
                params.put("since_id", request.getSinceId());
            }
            if (request.getMaxId() != null) {
                params.put("max_id", request.getMaxId());
            }
            if (request.getQuery() != null) {
                params.put("q", request.getQuery());
            }
        }

        client.get(apiUrl, params, new TextHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                Gson gson = Utils.getGson();
                JsonObject jsonObject = new Gson().fromJson(responseString, JsonObject.class);
                ArrayList<Tweet> tweets = gson.fromJson(jsonObject.get("statuses").getAsJsonArray(),
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
     * Direct messages
     *
     * @param request  Request
     * @param callback Callback
     */
    public void getDirectMessages(TimelineRequest request,
                                  final MessagesCallback callback) {
        String apiUrl = getApiUrl("direct_messages.json");

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
                Gson gson = Utils.getGson();
                ArrayList<Message> messages = gson.fromJson(responseString,
                        new TypeToken<ArrayList<Message>>() {
                        }.getType());
                callback.onSuccess(messages);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                callback.onError(new TwitterError(throwable != null ? throwable.getMessage() : null));
            }
        });
    }

    /**
     * Create Tweet
     *
     * @param request  Request
     * @param callback Callback
     */
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
                Gson gson = Utils.getGson();
                Tweet tweet = gson.fromJson(responseString, Tweet.class);
                callback.onSuccess(tweet);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                callback.onError(new TwitterError(throwable != null ? throwable.getMessage() : null));
            }
        });
    }

    /**
     * Retweet
     *
     * @param request  Request
     * @param callback Callback
     */
    public void retweet(final RetweetRequest request,
                        final RetweetCallback callback) {
        String apiUrl;
        if (request.isUndo()) {
            apiUrl = getApiUrl(String.format("statuses/unretweet/%1$s.json", request.getTweetId().toString()));
        } else {
            apiUrl = getApiUrl(String.format("statuses/retweet/%1$s.json", request.getTweetId().toString()));
        }

        RequestParams params = new RequestParams();
        params.put("id", request.getTweetId());

        client.post(apiUrl, params, new TextHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                Gson gson = Utils.getGson();
                Tweet tweet = gson.fromJson(responseString, Tweet.class);
                callback.onSuccess(tweet);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                callback.onError(new TwitterError(throwable != null ? throwable.getMessage() : null));
            }
        });
    }

    /**
     * Mark tweet as favorite
     *
     * @param request  Request
     * @param callback Callback
     */
    public void markAsFavorite(FavoriteTweetRequest request,
                               final FavoriteTweetCallback callback) {
        String apiUrl;
        if (request.isUndo()) {
            apiUrl = getApiUrl("favorites/destroy.json");
        } else {
            apiUrl = getApiUrl("favorites/create.json");
        }

        RequestParams params = new RequestParams();
        params.put("id", request.getTweetId());

        client.post(apiUrl, params, new TextHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                Gson gson = Utils.getGson();
                Tweet tweet = gson.fromJson(responseString, Tweet.class);
                callback.onSuccess(tweet);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                callback.onError(new TwitterError(throwable != null ? throwable.getMessage() : null));
            }
        });
    }

    /**
     * Get user's friends (following)
     *
     * @param request  Request
     * @param callback Callback
     */
    public void getFollowing(TwitterUsersRequest request,
                             final TwitterUsersCallback callback) {
        String apiUrl = getApiUrl("friends/list.json");

        RequestParams params = new RequestParams();
        if (request != null) {
            if (request.getUserId() != null) {
                params.put("user_id", request.getUserId());
            }
            if (request.getCursor() != null) {
                params.put("cursor", request.getCursor());
            }
        }

        client.get(apiUrl, params, new TextHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                callback.onSuccess(Utils.getGson().fromJson(responseString, TwitterUsersResponse.class));
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                callback.onError(new TwitterError(throwable != null ? throwable.getMessage() : null));
            }
        });
    }

    /**
     * Get user's followers
     *
     * @param request  Request
     * @param callback Callback
     */
    public void getFollowers(TwitterUsersRequest request,
                             final TwitterUsersCallback callback) {
        String apiUrl = getApiUrl("followers/list.json");

        RequestParams params = new RequestParams();
        if (request != null) {
            if (request.getUserId() != null) {
                params.put("user_id", request.getUserId());
            }
            if (request.getCursor() != null) {
                params.put("cursor", request.getCursor());
            }
        }

        client.get(apiUrl, params, new TextHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                callback.onSuccess(Utils.getGson().fromJson(responseString, TwitterUsersResponse.class));
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                callback.onError(new TwitterError(throwable != null ? throwable.getMessage() : null));
            }
        });
    }

    /**
     * Follow user
     *
     * @param user     TwitterUser
     * @param callback Callback
     */
    public void followUser(TwitterUser user,
                           final TwitterUserCallback callback) {
        String apiUrl = getApiUrl("friendships/create.json");

        RequestParams params = new RequestParams();
        params.put("user_id", user.getUserId());

        client.post(apiUrl, params, new TextHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                Gson gson = Utils.getGson();
                TwitterUser user = gson.fromJson(responseString, TwitterUser.class);
                callback.onSuccess(user);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                callback.onError(new TwitterError(throwable != null ? throwable.getMessage() : null));
            }
        });
    }

    /**
     * Unfollow user
     *
     * @param user     TwitterUser
     * @param callback Callback
     */
    public void unfollowUser(TwitterUser user,
                             final TwitterUserCallback callback) {
        String apiUrl = getApiUrl("friendships/destroy.json");

        RequestParams params = new RequestParams();
        params.put("user_id", user.getUserId());

        client.post(apiUrl, params, new TextHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                Gson gson = Utils.getGson();
                TwitterUser user = gson.fromJson(responseString, TwitterUser.class);
                callback.onSuccess(user);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                callback.onError(new TwitterError(throwable != null ? throwable.getMessage() : null));
            }
        });
    }
}