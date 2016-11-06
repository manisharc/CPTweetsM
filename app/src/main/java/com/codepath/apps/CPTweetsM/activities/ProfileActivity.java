package com.codepath.apps.CPTweetsM.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.apps.CPTweetsM.R;
import com.codepath.apps.CPTweetsM.TwitterApplication;
import com.codepath.apps.CPTweetsM.fragments.UserTimelineFragment;
import com.codepath.apps.CPTweetsM.models.User;
import com.codepath.apps.CPTweetsM.network.TwitterClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class ProfileActivity extends AppCompatActivity {

    TwitterClient client;
    User user;
    private Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        client = TwitterApplication.getRestClient();

        String screenName = getIntent().getStringExtra("screen_name");

        if (screenName == null) {
            client.getUserInfo(new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    user = User.fromJSON(response);
                    // My current user account's info
                    //if (user.getScreenName() != null)
                    //getSupportActionBar().setTitle(user.getScreenName());
                    populateProfileHeader(user);
                }
            });
        }
        else {
            client.getOtherUserInfo(new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    user = User.fromJSON(response);
                    // My current user account's info
                    //if (user.getScreenName() != null)
                    //getSupportActionBar().setTitle(user.getScreenName());
                    populateProfileHeader(user);
                }
            }, screenName);

        }

        if (savedInstanceState == null){
            UserTimelineFragment fragmentUserTimeline = UserTimelineFragment.newInstance(screenName);
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.flContainer, fragmentUserTimeline);
            ft.commit();

        }


    }

    void populateProfileHeader(User user){
        ImageView ivProfileImage = (ImageView) findViewById(R.id.ivProfileImage);
        TextView tvName = (TextView) findViewById(R.id.tvName);
        TextView tvTagline = (TextView) findViewById(R.id.tvTagline);
        TextView tvFollowing = (TextView) findViewById(R.id.tvFollowingCount);
        TextView tvFollowers = (TextView) findViewById(R.id.tvFollowersCount);

        ivProfileImage.setImageResource(android.R.color.transparent);
        Glide.with(this).load(user.getProfileImageUrl()).into(ivProfileImage);
        tvName.setText(user.getName());
        tvTagline.setText(user.getTagline());
        tvFollowers.setText(user.getFollowersCount() + " Followers   ");
        tvFollowing.setText(user.getFollowingCount() + " Following");


    }
}
