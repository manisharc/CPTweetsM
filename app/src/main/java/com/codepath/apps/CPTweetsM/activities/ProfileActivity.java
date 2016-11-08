package com.codepath.apps.CPTweetsM.activities;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.apps.CPTweetsM.R;
import com.codepath.apps.CPTweetsM.TwitterApplication;
import com.codepath.apps.CPTweetsM.databinding.ActivityProfileBinding;
import com.codepath.apps.CPTweetsM.fragments.UserTimelineFragment;
import com.codepath.apps.CPTweetsM.models.User;
import com.codepath.apps.CPTweetsM.network.TwitterClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class ProfileActivity extends AppCompatActivity {

    private ActivityProfileBinding binding;
    TwitterClient client;
    User user;
    private Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_profile);
        client = TwitterApplication.getRestClient();
        String screenName = getIntent().getStringExtra("screen_name");

        if (screenName == null) {
            client.getUserInfo(new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    user = User.fromJSON(response);
                    populateProfileHeader(user);
                }
            });
        }
        else {
            client.getOtherUserInfo(new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    user = User.fromJSON(response);
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
        ImageView ivProfileBanner = binding.ivProfileBanner;
        TextView tvName = binding.tvName;
        TextView tvTagline = binding.tvTagline;
        TextView tvFollowing = binding.tvFollowingCount;
        TextView tvFollowers = binding.tvFollowersCount;
        TextView tvUserName = binding.tvUserName;

        ivProfileBanner.setImageResource(android.R.color.transparent);
        if (user.getProfileBannerUrl() != null)
            Glide.with(this).load(user.getProfileBannerUrl()).into(ivProfileBanner);
        tvName.setText(user.getName());
        tvTagline.setText(user.getTagline());
        tvFollowers.setText(Integer.toString(user.getFollowersCount()));
        tvFollowing.setText(Integer.toString(user.getFollowingCount()));
        tvUserName.setText(user.getScreenName());


    }
}
