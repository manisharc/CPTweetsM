package com.codepath.apps.CPTweetsM.activities;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.apps.CPTweetsM.R;
import com.codepath.apps.CPTweetsM.databinding.ActivityTweetDetailBinding;
import com.codepath.apps.CPTweetsM.models.Tweet;

/**
 * Created by chmanish on 10/30/16.
 */
public class TweetDetailActivity extends AppCompatActivity {

    private ActivityTweetDetailBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_tweet_detail);

        Tweet tweet = (Tweet) getIntent().getParcelableExtra("tweet");
        if(tweet != null) {
            ImageView ivProfileImage = binding.ivProfileImage;
            TextView tvName = binding.tvName;
            TextView tvUserName = binding.tvUserName;
            TextView tvBody = binding.tvBody;
            TextView tvCreatedTime =binding.tvCreatedTime;
            ImageView ivImage =binding.ivImage;
            TextView tvFavoriteCount = binding.tvFavoriteCount;
            TextView tvLikes = binding.tvLikes;
            TextView tvRetweetCount = binding.tvRetweetCount;
            TextView tvRetweet = binding.tvRetweet;

            tvName.setText(tweet.getUser().getName());
            tvUserName.setText(tweet.getUser().getScreenName());
            tvBody.setText(tweet.getBody());
            tvCreatedTime.setText(tweet.getCreatedAt());
            ivProfileImage.setImageResource(android.R.color.transparent);
            Glide.with(this).load(tweet.getUser().getProfileImageUrl()).into(ivProfileImage);
            //Set ivProfileImage
            if (tweet.getMediaType() == 0){
                ivImage.setVisibility(View.VISIBLE);
                ivImage.setImageResource(android.R.color.transparent);
                Glide.with(this).load(tweet.getImageUrl()).into(ivImage);
            }
            if (tweet.getFavoriteCount() > 0){
                tvFavoriteCount.setText(Integer.toString(tweet.getFavoriteCount()));
            }
            else {
                tvFavoriteCount.setVisibility(View.GONE);
                tvLikes.setVisibility(View.GONE);
            }

            if (tweet.getRetweetCount() > 0){
                tvRetweetCount.setText(Integer.toString(tweet.getRetweetCount()));
            }
            else {
                tvRetweetCount.setVisibility(View.GONE);
                tvRetweet.setVisibility(View.GONE);
            }
        }


    }
}
