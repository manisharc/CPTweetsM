package com.codepath.apps.CPTweetsM;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.codepath.apps.CPTweetsM.adapters.TweetsAdapter;
import com.codepath.apps.CPTweetsM.fragments.ComposeTweetFragment;
import com.codepath.apps.CPTweetsM.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.LinkedList;

import cz.msebera.android.httpclient.Header;

public class TimelineActivity extends AppCompatActivity implements ComposeTweetFragment.ComposeTweetDialogListener {

    private TwitterClient client;
    private LinkedList<Tweet> tweets;
    private TweetsAdapter adapter;
    public static long max_id = 0;
    private SwipeRefreshLayout swipeContainer;
    private RecyclerView rvTweets;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);
        client = TwitterApplication.getRestClient(); //singleton client
        setupViews();
        populateTimeline(false);
    }

    public void setupViews(){
        rvTweets = (RecyclerView) findViewById(R.id.rvTweets);
        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        tweets = new LinkedList<>();
        adapter = new TweetsAdapter(this, tweets);
        rvTweets.setAdapter(adapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rvTweets.setLayoutManager(linearLayoutManager);

        // Add on click listener later

        rvTweets.addOnScrollListener(new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                populateTimeline(false);

            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fabCompose);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fm = getSupportFragmentManager();
                ComposeTweetFragment editNameDialogFragment = ComposeTweetFragment.newInstance();
                editNameDialogFragment.show(fm, "fragment_compose_tweet");
            }
        });

        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Refresh list of tweets
                int size = tweets.size();
                tweets.clear();
                adapter.notifyItemRangeRemoved(0, size);
                max_id = 0;
                populateTimeline(true);
            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);


    }

    @Override
    public void onFinishComposeDialog(Tweet newTweet) {
        //Toast.makeText(this, newTweet, Toast.LENGTH_LONG).show();
        tweets.addFirst(newTweet);
        adapter.notifyItemInserted(0);
        rvTweets.scrollToPosition(0);   // index 0 position
    }
    // Send an api request to get the timeline json
    // Fill the view by creating the tweet objects from the json
    private void populateTimeline(final boolean isRefresh) {
            client.getHomeTimeline(new JsonHttpResponseHandler(){
                // Success

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                    JSONArray tweetJsonResults = null;
                    int curSize = adapter.getItemCount();
                    tweets.addAll(Tweet.fromJSONArray(response));
                    max_id = tweets.get(tweets.size()-1).getUid();
                    adapter.notifyItemRangeInserted(curSize, (Tweet.fromJSONArray(response)).size());
                    if (isRefresh)
                        swipeContainer.setRefreshing(false);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {

                }
            }, max_id);
    }
}
