package com.codepath.apps.CPTweetsM.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.codepath.apps.CPTweetsM.R;
import com.codepath.apps.CPTweetsM.databinding.ActivityTimelineBinding;
import com.codepath.apps.CPTweetsM.fragments.ComposeTweetFragment;
import com.codepath.apps.CPTweetsM.fragments.HomeTimelineFragment;
import com.codepath.apps.CPTweetsM.fragments.TweetsListFragment;
import com.codepath.apps.CPTweetsM.models.Tweet;
import com.codepath.apps.CPTweetsM.network.NetworkStatus;

public class TimelineActivity extends AppCompatActivity implements TweetsListFragment.TweetsListActionListener, ComposeTweetFragment.ComposeTweetDialogListener {

    private ActivityTimelineBinding binding;

    private boolean isOnline = true;
    HomeTimelineFragment homeTimelineFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);
        //binding = DataBindingUtil.setContentView(this, R.layout.activity_timeline);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setElevation(0);
        getSupportActionBar().setLogo(R.drawable.twitter);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setTitle("");

        NetworkStatus networkStatus = NetworkStatus.getSharedInstance();
        isOnline = networkStatus.checkNetworkStatus(getApplicationContext());
        homeTimelineFragment = (HomeTimelineFragment) getSupportFragmentManager()
                .findFragmentById(R.id.fooFragment);


    }

    @Override
    public void onAddNewTweet() {
        if (isOnline) {
            FragmentManager fm = getSupportFragmentManager();
            ComposeTweetFragment editNameDialogFragment = ComposeTweetFragment.newInstance(null);
            editNameDialogFragment.show(fm, "fragment_compose_tweet");
        } else
            Toast.makeText(getApplicationContext(), "You are offline. Can't update tweet", Toast.LENGTH_LONG).show();

    }

    @Override
    public void onReplyTweet(Tweet tweet) {
        if (isOnline) {
            FragmentManager fm = getSupportFragmentManager();
            ComposeTweetFragment editNameDialogFragment = ComposeTweetFragment.newInstance(tweet);
            editNameDialogFragment.show(fm, "fragment_compose_tweet");
        } else
            Toast.makeText(getApplicationContext(), "You are offline. Can't reply to tweets", Toast.LENGTH_LONG).show();

    }


    @Override
    public void onItemClickDetailView(Tweet tweet) {
        if (isOnline) {
            Intent i = new Intent(getApplicationContext(), TweetDetailActivity.class);
            i.putExtra("tweet", tweet);
            startActivity(i);
        } else
            Toast.makeText(getApplicationContext(), "You are offline. Can't see details.", Toast.LENGTH_LONG).show();

    }


    public void onFinishComposeDialog(Tweet newTweet) {

        if (homeTimelineFragment != null && homeTimelineFragment.isInLayout()) {
            homeTimelineFragment.addTweet(newTweet);
        }
    }






}
