package com.codepath.apps.CPTweetsM.fragments;

import android.os.Bundle;
import android.widget.Toast;

import com.codepath.apps.CPTweetsM.TwitterApplication;
import com.codepath.apps.CPTweetsM.database.TweetDatabase;
import com.codepath.apps.CPTweetsM.models.Tweet;
import com.codepath.apps.CPTweetsM.models.Tweet_Table;
import com.codepath.apps.CPTweetsM.models.User;
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

import java.util.List;

import cz.msebera.android.httpclient.Header;

/**
 * Created by chmanish on 11/3/16.
 */
public class HomeTimelineFragment extends TweetsListFragment{

    private TwitterClient client;
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        client = TwitterApplication.getRestClient(); //singleton client

        if (isOnline)
        // Retrieve from the network
            populateTimeline(false, true);
        else {
            Toast.makeText(getContext(), "Cannot retrieve tweets, since you are offline!", Toast.LENGTH_LONG).show();
            //Retrieve from the database
            int curSize = adapter.getItemCount();
            //get from database
            List<Tweet> tweetsFromDb = getAllTweetsFromDatabase();
            tweets.addAll(tweetsFromDb);
            adapter.notifyItemRangeInserted(curSize, (tweetsFromDb.size())-1);
        }



    }

    // Send an api request to get the timeline json
    // Fill the view by creating the tweet objects from the json
    @Override
    protected void populateTimeline(final boolean isRefresh, final boolean isFirstCall) {
        super.populateTimeline(isRefresh, isFirstCall);
        client.getHomeTimeline(new JsonHttpResponseHandler(){
            // Success

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                int curSize = adapter.getItemCount();
                tweets.addAll(Tweet.fromJSONArray(response));
                max_id_home = tweets.get(tweets.size()-1).getUid();

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
        }, max_id_home);
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

    public void addTweet(Tweet newTweet) {
        tweets.addFirst(newTweet);
        //Update database here
        newTweet.save();
        adapter.notifyItemInserted(0);
        rvTweets.scrollToPosition(0);   // index 0 position
    }
}
