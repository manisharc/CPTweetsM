package com.codepath.apps.CPTweetsM.activities;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.astuetz.PagerSlidingTabStrip;
import com.bumptech.glide.Glide;
import com.codepath.apps.CPTweetsM.R;
import com.codepath.apps.CPTweetsM.TwitterApplication;
import com.codepath.apps.CPTweetsM.adapters.TweetsPagerAdapter;
import com.codepath.apps.CPTweetsM.databinding.ActivityTimelineBinding;
import com.codepath.apps.CPTweetsM.fragments.ComposeTweetFragment;
import com.codepath.apps.CPTweetsM.fragments.HomeTimelineFragment;
import com.codepath.apps.CPTweetsM.fragments.TweetsListFragment;
import com.codepath.apps.CPTweetsM.models.Tweet;
import com.codepath.apps.CPTweetsM.models.User;
import com.codepath.apps.CPTweetsM.network.NetworkStatus;
import com.codepath.apps.CPTweetsM.network.TwitterClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;
import jp.wasabeef.glide.transformations.CropCircleTransformation;

public class TimelineActivity extends AppCompatActivity implements TweetsListFragment.TweetsListActionListener, ComposeTweetFragment.ComposeTweetDialogListener {

    private ActivityTimelineBinding binding;
    TwitterClient client;
    private boolean isOnline = true;
    private Toolbar toolbar;
    ViewPager vpPager;
    User currentUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);
        client = TwitterApplication.getRestClient();
        binding = DataBindingUtil.setContentView(this, R.layout.activity_timeline);
        // Set a Toolbar to replace the ActionBar.
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        //toolbar = binding.toolbar;
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setElevation(0);
        getSupportActionBar().setLogo(R.drawable.twitter);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setTitle("");
        getCurrentUser();
        NetworkStatus networkStatus = NetworkStatus.getSharedInstance();
        isOnline = networkStatus.checkNetworkStatus(getApplicationContext());
        vpPager = binding.viewpager;
        vpPager.setAdapter(new TweetsPagerAdapter(getSupportFragmentManager()));
        PagerSlidingTabStrip tabStrip = binding.tabs;
        tabStrip.setViewPager(vpPager);
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

    @Override
    public void onProfilePicClick(User user) {
        if (isOnline) {
            Intent i = new Intent(this, ProfileActivity.class);
            i.putExtra("screen_name", user.getScreenName());
            startActivity(i);
        } else
            Toast.makeText(getApplicationContext(), "You are offline. Can't see details.", Toast.LENGTH_LONG).show();

    }

    public void onFinishComposeDialog(Tweet newTweet) {
        HomeTimelineFragment homeTimeline = (HomeTimelineFragment) getSupportFragmentManager()
                .findFragmentByTag("android:switcher:" + R.id.viewpager + ":" +
                        "0");
        Fragment currentTimeline = getSupportFragmentManager()
                .findFragmentByTag("android:switcher:" + R.id.viewpager + ":" +
                        vpPager.getCurrentItem());

        if (currentTimeline != null) {
            if (!(currentTimeline instanceof HomeTimelineFragment)) {
                vpPager.setCurrentItem(0);
            }
            homeTimeline.addTweet(newTweet);
        }
    }

    // Menu icons are inflated just as they were with actionbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public void getCurrentUser(){
        client.getUserInfo(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                currentUser = User.fromJSON(response);
                ImageView profileView = (ImageView) toolbar.findViewById(R.id.miProfile);
                Glide.with(getApplicationContext()).load(currentUser.getProfileImageUrl())
                        .bitmapTransform(new CropCircleTransformation(getApplicationContext()))
                        .into(profileView);
            }
        });
    }


    public void onProfileViewToolbar(View view) {
        // Launch the profile view
        Intent i = new Intent(this, ProfileActivity.class);
        if (currentUser != null)
            i.putExtra("screen_name", currentUser.getScreenName());
        startActivity(i);
    }
}
