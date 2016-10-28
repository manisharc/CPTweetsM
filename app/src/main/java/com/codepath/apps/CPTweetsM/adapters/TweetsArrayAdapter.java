package com.codepath.apps.CPTweetsM.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.apps.CPTweetsM.R;
import com.codepath.apps.CPTweetsM.models.Tweet;
import com.squareup.picasso.Picasso;

import java.util.List;

public class TweetsArrayAdapter extends ArrayAdapter<Tweet>{


    public TweetsArrayAdapter(Context context, List<Tweet> objects) {
        super(context, android.R.layout.simple_list_item_1, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Tweet tweet = getItem(position);
        if (convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_tweet, parent, false);

        }
        ImageView ivProfileImage = (ImageView) convertView.findViewById(R.id.ivProfileImage);
        TextView tvUserName = (TextView) convertView.findViewById(R.id.tvUserName);
        TextView tvBody = (TextView) convertView.findViewById(R.id.tvBody);
        TextView tvCreatedTime = (TextView) convertView.findViewById(R.id.tvCreatedTime);

        tvUserName.setText(tweet.getUser().getScreenName());
        tvBody.setText(tweet.getBody());
        tvCreatedTime.setText(tweet.getRelativeTimeAgo(tweet.getCreatedAt()));

        ivProfileImage.setImageResource(android.R.color.transparent);

        Picasso.with(getContext()).load(tweet.getUser().getProfileImageUrl()).into(ivProfileImage);
        return convertView;
    }
}
