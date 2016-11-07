package com.codepath.apps.CPTweetsM.fragments;

import android.os.Bundle;
import android.widget.Toast;

import com.codepath.apps.CPTweetsM.TwitterApplication;
import com.codepath.apps.CPTweetsM.models.Tweet;
import com.codepath.apps.CPTweetsM.network.TwitterClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

/**
 * Created by chmanish on 11/5/16.
 */
public class MentionsTimelineFragment extends TweetsListFragment{
    private TwitterClient client;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        client = TwitterApplication.getRestClient(); //singleton client

        if (isOnline)
            // Retrieve from the network
            populateTimeline(false, true);
        else {

            Toast.makeText(getContext(), "Cannot retrieve mentions, since you are offline!", Toast.LENGTH_LONG).show();
        }
    }

    // Send an api request to get the timeline json
    // Fill the view by creating the tweet objects from the json
    @Override
    protected void populateTimeline(final boolean isRefresh, final boolean isFirstCall) {
        super.populateTimeline(isRefresh, isFirstCall);
        client.getMentionsTimeline(new JsonHttpResponseHandler(){
            // Success

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                int curSize = adapter.getItemCount();
                tweets.addAll(Tweet.fromJSONArray(response));
                max_id_mentions = tweets.get(tweets.size()-1).getUid();

                adapter.notifyItemRangeInserted(curSize, (Tweet.fromJSONArray(response)).size());
                if (isRefresh)
                    swipeContainer.setRefreshing(false);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {

            }
        }, max_id_mentions);
    }

}
