package com.codepath.apps.CPTweetsM.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.apps.CPTweetsM.R;
import com.codepath.apps.CPTweetsM.models.Tweet;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by chmanish on 10/27/16.
 */
public class TweetsAdapter extends RecyclerView.Adapter<TweetsAdapter.ViewHolder> {


    // Store a member variable for the articles
    private List<Tweet> mTweets;
    // Store the context for easy access
    private Context mContext;
        // Define listener member variable
        private static OnItemClickListener listener;
        // Define the listener interface
        public interface OnItemClickListener {
            void onItemClick(View itemView, int position);
        }
        // Define the method that allows the parent activity or fragment to define the listener
        public void setOnItemClickListener(OnItemClickListener listener) {
            this.listener = listener;
        }


        // Provide a direct reference to each of the views within a data item
        // Used to cache the views within the item layout for fast access
        public static class ViewHolder extends RecyclerView.ViewHolder {
            private TextView tvUserName;
            private TextView tvBody;
            private TextView tvCreatedTime;
            private ImageView ivProfileImage;


            // We also create a constructor that accepts the entire item row
            // and does the view lookups to find each subview
            public ViewHolder(final View itemView) {
                // Stores the itemView in a public final member variable that can be used
                // to access the context from any ViewHolder instance.
                super(itemView);
                ivProfileImage = (ImageView) itemView.findViewById(R.id.ivProfileImage);
                tvUserName = (TextView) itemView.findViewById(R.id.tvUserName);
                tvBody = (TextView) itemView.findViewById(R.id.tvBody);
                tvCreatedTime = (TextView) itemView.findViewById(R.id.tvCreatedTime);

                // Setup the click listener
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Triggers click upwards to the adapter on click
                        if (listener != null) {
                            int position = getAdapterPosition();
                            if (listener != null && position != RecyclerView.NO_POSITION) {
                                listener.onItemClick(itemView, position);
                            }
                        }
                    }
                });
            }
        }


        // Pass in the article array into the constructor
        public TweetsAdapter(Context context, List<Tweet> tweets) {
            mTweets = tweets;
            mContext = context;
        }

        // Easy access to the context object in the recyclerview
        private Context getContext() {
            return mContext;
        }

        // Usually involves inflating a layout from XML and returning the holder
        @Override
        public TweetsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            Context context = parent.getContext();
            LayoutInflater inflater = LayoutInflater.from(context);

            // Inflate the custom layout
            View tweetView = inflater.inflate(R.layout.item_tweet, parent, false);

            // Return a new holder instance
            ViewHolder viewHolder = new ViewHolder(tweetView);
            return viewHolder;
        }

        // Involves populating data into the item through holder
        @Override
        public void onBindViewHolder(TweetsAdapter.ViewHolder viewHolder, int position) {
            // Get the data model based on position
            Tweet tweet = mTweets.get(position);

            // Set item views based on your views and data model
            TextView tvUserName = viewHolder.tvUserName;
            TextView tvBody = viewHolder.tvBody;
            TextView tvCreatedTime = viewHolder.tvCreatedTime;
            ImageView ivProfileImage = viewHolder.ivProfileImage;

            tvUserName.setText(tweet.getUser().getScreenName());
            tvBody.setText(tweet.getBody());
            tvCreatedTime.setText(tweet.getRelativeTimeAgo(tweet.getCreatedAt()));

            ivProfileImage.setImageResource(android.R.color.transparent);

            Picasso.with(getContext()).load(tweet.getUser().getProfileImageUrl()).into(ivProfileImage);


        }
        // Returns the total count of items in the list
        @Override
        public int getItemCount() {
            return mTweets.size();
        }
}


