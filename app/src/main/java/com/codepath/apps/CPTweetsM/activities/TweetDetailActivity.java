package com.codepath.apps.CPTweetsM.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.apps.CPTweetsM.R;
import com.codepath.apps.CPTweetsM.models.Tweet;

/**
 * Created by chmanish on 10/30/16.
 */
public class TweetDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tweet_detail);

        Tweet tweet = (Tweet) getIntent().getParcelableExtra("tweet");
        if(tweet != null) {
            ImageView ivProfileImage = (ImageView) findViewById(R.id.ivProfileImage);
            TextView tvName = (TextView) findViewById(R.id.tvName);
            TextView tvUserName = (TextView) findViewById(R.id.tvUserName);
            TextView tvBody = (TextView) findViewById(R.id.tvBody);
            TextView tvCreatedTime = (TextView) findViewById(R.id.tvCreatedTime);
            ImageView ivImage = (ImageView) findViewById(R.id.ivImage);
            TextView tvFavoriteCount = (TextView) findViewById(R.id.tvFavoriteCount);

            tvName.setText(tweet.getUser().getName());
            tvUserName.setText(tweet.getUser().getScreenName());
            tvBody.setText(tweet.getBody());
            tvCreatedTime.setText(tweet.getCreatedAt());
            ivProfileImage.setImageResource(android.R.color.transparent);
            Glide.with(this).load(tweet.getUser().getProfileImageUrl()).into(ivProfileImage);
            //Set ivProfileImage
            if(tweet.getMediaType() == 0){
                ivImage.setVisibility(View.VISIBLE);
                ivImage.setImageResource(android.R.color.transparent);
                Glide.with(this).load(tweet.getImageUrl()).into(ivImage);
            }
        }


    }
}
