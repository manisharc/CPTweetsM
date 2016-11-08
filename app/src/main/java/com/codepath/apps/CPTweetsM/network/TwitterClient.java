package com.codepath.apps.CPTweetsM.network;

import android.content.Context;

import com.codepath.oauth.OAuthBaseClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.scribe.builder.api.Api;
import org.scribe.builder.api.TwitterApi;

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
	public static final String REST_URL = "https://api.twitter.com/1.1/";
	public static final String REST_CONSUMER_KEY = "IZTNXDxbcmH6wqb9FTG3iBjkk";
	public static final String REST_CONSUMER_SECRET = "sLJAGyiwGLHfyqtrJdUC5pN7MaRs4BWyBhXIhxMHq86Z69vgU3";
	public static final String REST_CALLBACK_URL = "oauth://cpsimpletweets";


	public TwitterClient(Context context) {
		super(context, REST_API_CLASS, REST_URL, REST_CONSUMER_KEY, REST_CONSUMER_SECRET, REST_CALLBACK_URL);
	}
	/* 1. Define the endpoint URL with getApiUrl and pass a relative path to the endpoint
	 * 	  i.e getApiUrl("statuses/home_timeline.json");
	 * 2. Define the parameters to pass to the request (query or body)
	 *    i.e RequestParams params = new RequestParams("foo", "bar");
	 * 3. Define the request method and make a call to the client
	 *    i.e client.get(apiUrl, params, handler);
	 *    i.e client.post(apiUrl, params, handler);
	 */

	public void getHomeTimeline(AsyncHttpResponseHandler handler, long max_id){
		String apiUrl = getApiUrl("statuses/home_timeline.json");
		RequestParams params = new RequestParams();
		params.put("count", 25);
		if (max_id != 0) {
			params.put("max_id", max_id - 1);
		}
		getClient().get(apiUrl, params, handler);
	}

	public void getMentionsTimeline(AsyncHttpResponseHandler handler, long max_id){
		String apiUrl = getApiUrl("statuses/mentions_timeline.json");
		RequestParams params = new RequestParams();
		params.put("count", 25);
		if (max_id != 0) {
			params.put("max_id", max_id - 1);
		}
		getClient().get(apiUrl, params, handler);
	}

	public void postTweet(AsyncHttpResponseHandler handler, String tweetString, String in_reply_to_status_id){
		String apiUrl = getApiUrl("statuses/update.json");
		RequestParams params = new RequestParams();
		params.put("status", tweetString);
		if (in_reply_to_status_id != null)
			params.put("in_reply_to_status_id", in_reply_to_status_id);
		getClient().post(apiUrl, params, handler);

	}

	public void getUserTimeline(AsyncHttpResponseHandler handler, long max_id, String screenName){
		String apiUrl = getApiUrl("statuses/user_timeline.json");
		RequestParams params = new RequestParams();
		params.put("count", 25);
		if (max_id != 0) {
			params.put("max_id", max_id - 1);
		}
		params.put("screen_name", screenName);
		getClient().get(apiUrl, params, handler);
	}

	public void getUserInfo(AsyncHttpResponseHandler handler){
		String apiUrl = getApiUrl("account/verify_credentials.json");
		getClient().get(apiUrl, null, handler);

	}

	public void getOtherUserInfo(AsyncHttpResponseHandler handler, String screenName){
		String apiUrl = getApiUrl("users/show.json");
		RequestParams params = new RequestParams();
		params.put("screen_name", screenName);
		getClient().get(apiUrl, params, handler);

	}



	// Compose tweets

}