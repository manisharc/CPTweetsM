package com.codepath.apps.CPTweetsM;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.codepath.apps.CPTweetsM.adapters.TweetsAdapter;
import com.codepath.apps.CPTweetsM.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class TimelineActivity extends AppCompatActivity {

    private TwitterClient client;
    private ArrayList<Tweet> tweets;
    private TweetsAdapter adapter;
    public static int since_id=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);
        client = TwitterApplication.getRestClient(); //singleton client
        setupViews();
        populateTimeline();
    }

    public void setupViews(){
        RecyclerView rvTweets = (RecyclerView) findViewById(R.id.rvTweets);
        tweets = new ArrayList<>();
        adapter = new TweetsAdapter(this, tweets);
        rvTweets.setAdapter(adapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rvTweets.setLayoutManager(linearLayoutManager);

        // Add on click listener later

        rvTweets.addOnScrollListener(new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                //populateTimeline();

            }
        });

    }

    // Send an api request to get the timeline json
    // Fill the listview by creating the tweet objects from the json
    private void populateTimeline() {
            client.getHomeTimeline(new JsonHttpResponseHandler(){
                // Success

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                    JSONArray tweetJsonResults = null;
                    //since_id+= 25;
                    int curSize = adapter.getItemCount();
                    tweets.addAll(Tweet.fromJSONArray(response));
                    adapter.notifyItemRangeInserted(curSize, (Tweet.fromJSONArray(response)).size());
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {

                }
            }, since_id);
    }
}
