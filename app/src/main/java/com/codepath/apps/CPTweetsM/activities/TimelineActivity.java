package com.codepath.apps.CPTweetsM.activities;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.codepath.apps.CPTweetsM.DividerItemDecoration;
import com.codepath.apps.CPTweetsM.EndlessRecyclerViewScrollListener;
import com.codepath.apps.CPTweetsM.ItemClickSupport;
import com.codepath.apps.CPTweetsM.R;
import com.codepath.apps.CPTweetsM.TweetDatabase;
import com.codepath.apps.CPTweetsM.TwitterApplication;
import com.codepath.apps.CPTweetsM.adapters.TweetsAdapter;
import com.codepath.apps.CPTweetsM.fragments.ComposeTweetFragment;
import com.codepath.apps.CPTweetsM.models.Tweet;
import com.codepath.apps.CPTweetsM.models.Tweet_Table;
import com.codepath.apps.CPTweetsM.models.User;
import com.codepath.apps.CPTweetsM.network.NetworkStatus;
import com.codepath.apps.CPTweetsM.network.TwitterClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.raizlabs.android.dbflow.config.DatabaseDefinition;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.sql.language.Delete;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.database.DatabaseWrapper;
import com.raizlabs.android.dbflow.structure.database.transaction.ITransaction;
import com.raizlabs.android.dbflow.structure.database.transaction.ProcessModelTransaction;
import com.raizlabs.android.dbflow.structure.database.transaction.Transaction;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.LinkedList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class TimelineActivity extends AppCompatActivity implements ComposeTweetFragment.ComposeTweetDialogListener {

    private TwitterClient client;
    private LinkedList<Tweet> tweets;
    private TweetsAdapter adapter;
    public static long max_id = 0;
    private SwipeRefreshLayout swipeContainer;
    private RecyclerView rvTweets;
    private boolean isOnline = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setElevation(0);
        getSupportActionBar().setLogo(R.mipmap.ic_launcher);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setTitle("");
        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);
        //getSupportActionBar().setTitle("");
        //getSupportActionBar().setDisplayShowHomeEnabled(true);
        //getSupportActionBar().setLogo(R.mipmap.twitter_logo);
        //getSupportActionBar().setDisplayUseLogoEnabled(true);
        client = TwitterApplication.getRestClient(); //singleton client
        NetworkStatus networkStatus = NetworkStatus.getSharedInstance();
        isOnline = networkStatus.checkNetworkStatus(getApplicationContext());
        setupViews();
        if (isOnline)
            // Retrieve from the network
            populateTimeline(false, true);
        else {
            //Retrieve from the database
            int curSize = adapter.getItemCount();
            //get from database
            List<Tweet> tweetsFromDb = getAllTweetsFromDatabase();
            tweets.addAll(tweetsFromDb);
            adapter.notifyItemRangeInserted(curSize, (tweetsFromDb.size())-1);
        }
    }

    public void setupViews(){
        rvTweets = (RecyclerView) findViewById(R.id.rvTweets);
        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        tweets = new LinkedList<Tweet>();
        adapter = new TweetsAdapter(this, tweets);
        rvTweets.setAdapter(adapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rvTweets.setLayoutManager(linearLayoutManager);
        rvTweets.addItemDecoration(
                new DividerItemDecoration(this, R.drawable.divider));

        // Add on click listener later

        rvTweets.addOnScrollListener(new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                if (isOnline)
                    populateTimeline(false, false);

            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fabCompose);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isOnline) {
                    FragmentManager fm = getSupportFragmentManager();
                    ComposeTweetFragment editNameDialogFragment = ComposeTweetFragment.newInstance();
                    editNameDialogFragment.show(fm, "fragment_compose_tweet");
                }
                else
                    Toast.makeText(getApplicationContext(), "You are offline. Can't update tweet", Toast.LENGTH_LONG).show();

            }
        });

        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Refresh list of tweets
                if (isOnline) {
                    int size = tweets.size();
                    tweets.clear();
                    adapter.notifyItemRangeRemoved(0, size);
                    //Clear database
                    max_id = 0;
                    populateTimeline(true, false);
                }
                else {
                    Toast.makeText(getApplicationContext(), "You are offline. Can't refresh tweets", Toast.LENGTH_LONG).show();


                }
            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        ItemClickSupport.addTo(rvTweets).setOnItemClickListener(
                new ItemClickSupport.OnItemClickListener() {
                    @Override
                    public void onItemClicked(RecyclerView recyclerView, int position, View v) {

                        /* Approach 1 - new activity which shows webview
                        Intent i = new Intent(getApplicationContext(), ArticleActivity.class);
                        Article article = articles.get(position);
                        i.putExtra("article", article);
                        startActivity(i);*/
                    }
                }
        );



    }

    @Override
    public void onFinishComposeDialog(Tweet newTweet) {
        tweets.addFirst(newTweet);
        //Update database here
        newTweet.save();
        adapter.notifyItemInserted(0);
        rvTweets.scrollToPosition(0);   // index 0 position
    }


    private void addToDataBase(){
        FlowManager.getDatabase(TweetDatabase.class)
                .beginTransactionAsync(new ProcessModelTransaction.Builder<>(
                        new ProcessModelTransaction.ProcessModel<Tweet>() {
                            @Override
                            public void processModel(Tweet tweet) {
                                User user = tweet.getUser();
                                user.save();
                                tweet.save();
                            }
                        }).addAll(tweets).build())  // add elements (can also handle multiple)
                .error(new Transaction.Error() {
                    @Override
                    public void onError(Transaction transaction, Throwable error) {

                    }
                })
                .success(new Transaction.Success() {
                    @Override
                    public void onSuccess(Transaction transaction) {

                    }
                }).build().execute();

    }

    private void deleteTables(){
        DatabaseDefinition database = FlowManager.getDatabase(TweetDatabase.class);
        Transaction transaction = database.beginTransactionAsync(new ITransaction() {
            @Override
            public void execute(DatabaseWrapper databaseWrapper) {
                Delete.table(User.class);
                Delete.table(Tweet.class);
            }


        }).build();
        transaction.execute(); // execute
        transaction.cancel();
        // attempt to cancel before its run. If it's already ran, this call has no effect.


    }

    private List<Tweet> getAllTweetsFromDatabase(){
        // Order the return based on createdDate
        List<Tweet> tweetList = SQLite.select().
                from(Tweet.class).orderBy(Tweet_Table.uid, false).queryList();
        return tweetList;
    }
    // Send an api request to get the timeline json
    // Fill the view by creating the tweet objects from the json
    private void populateTimeline(final boolean isRefresh, final boolean isFirstCall) {
            client.getHomeTimeline(new JsonHttpResponseHandler(){
                // Success

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                    int curSize = adapter.getItemCount();
                    tweets.addAll(Tweet.fromJSONArray(response));
                    max_id = tweets.get(tweets.size()-1).getUid();

                    //Update database here
                    if (isFirstCall | isRefresh){
                        deleteTables();

                    }
                    addToDataBase();
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
