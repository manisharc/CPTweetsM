package com.codepath.apps.CPTweetsM.adapters;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.codepath.apps.CPTweetsM.R;
import com.codepath.apps.CPTweetsM.databinding.ItemTweetBinding;
import com.codepath.apps.CPTweetsM.models.Tweet;

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

    // Define listener member variable
    private static OnTweetClickListener tweetListener;
    // Define the listener interface
    public interface OnTweetClickListener {
        void onItemClick(View itemView, int position);
    }
    // Define the method that allows the parent activity or fragment to define the listener
    public void setOnTweetClickListener(OnTweetClickListener listener) {
        this.tweetListener = listener;
    }
    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private ItemTweetBinding binding;
        private TextView tvUserName;
        private TextView tvBody;
        private TextView tvName;
        private TextView tvCreatedTime;
        private ImageView ivProfileImage;
        private ImageView ivImage;
        private VideoView vvVideo;
        private ImageButton ibReplyToTweet;
        private TextView tvFavoriteCount;
        private ImageView ivFavorite;

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(final View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);
            binding = DataBindingUtil.bind(itemView);
            ivProfileImage = binding.ivProfileImage;
            tvName = binding.tvName;
            tvUserName = binding.tvUserName;
            tvBody = binding.tvBody;
            tvCreatedTime = binding.tvCreatedTime;
            ivImage = binding.ivImage;
            vvVideo = binding.vvVideo;
            tvFavoriteCount = binding.tvFavoriteCount;
            ivFavorite = binding.ivFavorite;

            ibReplyToTweet = binding.btnReply;
            // Setup the click listener for the item
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

            ibReplyToTweet.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Triggers click upwards to the adapter on click
                    if (tweetListener != null) {
                        int position = getAdapterPosition();
                        if (tweetListener != null && position != RecyclerView.NO_POSITION) {
                            tweetListener.onItemClick(itemView, position);
                        }
                    }
                }
            });

        }

        public ViewDataBinding getBinding() {
            return binding;
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
        TextView tvName = viewHolder.tvName;
        TextView tvCreatedTime = viewHolder.tvCreatedTime;
        ImageView ivProfileImage = viewHolder.ivProfileImage;
        ImageView ivImage = viewHolder.ivImage;
        VideoView vvVideo = viewHolder.vvVideo;
        ImageView ivFavorite = viewHolder.ivFavorite;
        TextView tvFavoriteCount = viewHolder.tvFavoriteCount;

        if (tweet != null) {
            tvName.setText(tweet.getUser().getName());
            tvUserName.setText(tweet.getUser().getScreenName());
            tvBody.setText(tweet.getBody());
            tvCreatedTime.setText(tweet.getRelativeTimeAgo(tweet.getCreatedAt()));
            ivProfileImage.setImageResource(android.R.color.transparent);
            Glide.with(getContext()).load(tweet.getUser().getProfileImageUrl()).into(ivProfileImage);
            if (tweet.isFavorited()){
                ivFavorite.setImageResource(R.drawable.ic_favorite_red_900_18dp);
            }
            if (tweet.getFavoriteCount() > 0){
                tvFavoriteCount.setText(Integer.toString(tweet.getFavoriteCount()));
            }
            else
                tvFavoriteCount.setText("");
            //image media
            if(tweet.getMediaType() == 0){
                ivImage.setVisibility(View.VISIBLE);

                vvVideo.setVisibility(View.GONE);
                ivImage.setImageResource(android.R.color.transparent);
                Glide.with(getContext()).load(tweet.getImageUrl()).into(ivImage);
            } //video media
            else if(tweet.getMediaType() == 1){
                vvVideo.setVideoPath(tweet.getVideoUrl());
                MediaController mediaController = new MediaController(getContext());
                mediaController.setAnchorView(vvVideo);
                vvVideo.setMediaController(mediaController);
                vvVideo.setVisibility(View.VISIBLE);
                ivImage.setVisibility(View.GONE);
                vvVideo.requestFocus();
                vvVideo.start();

            }

            else{
                ivImage.setVisibility(View.GONE);
                vvVideo.setVisibility(View.GONE);
            }


        }

    }
    // Returns the total count of items in the list
    @Override
    public int getItemCount() {
        return mTweets.size();
    }

}


