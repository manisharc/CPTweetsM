package com.codepath.apps.CPTweetsM.fragments;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import com.codepath.apps.CPTweetsM.R;
import com.codepath.apps.CPTweetsM.adapters.TweetsAdapter;
import com.codepath.apps.CPTweetsM.databinding.FragmentTweetsListBinding;
import com.codepath.apps.CPTweetsM.models.Tweet;
import com.codepath.apps.CPTweetsM.models.User;
import com.codepath.apps.CPTweetsM.network.NetworkStatus;
import com.codepath.apps.CPTweetsM.utility.DividerItemDecoration;
import com.codepath.apps.CPTweetsM.utility.EndlessRecyclerViewScrollListener;
import com.codepath.apps.CPTweetsM.utility.ItemClickSupport;

import java.util.LinkedList;

/**
 * Created by chmanish on 11/3/16.
 */
public class TweetsListFragment extends Fragment  {

    private FragmentTweetsListBinding binding;
    public LinkedList<Tweet> tweets;
    public TweetsAdapter adapter;
    public long max_id = 0;
    protected SwipeRefreshLayout swipeContainer;
    private ImageButton ibReplyToTweet;
    protected RecyclerView rvTweets;
    protected FloatingActionButton fab;
    protected LinearLayoutManager linearLayoutManager;
    public boolean isOnline = true;

    public interface TweetsListActionListener {
        public void onAddNewTweet();
        public void onReplyTweet(Tweet tweet);
        public void onItemClickDetailView(Tweet tweet);
        public void onProfilePicClick(User user);

    }

    public TweetsListActionListener actionListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //return inflater.inflate(R.layout.fragment_tweets_list, container, false);

        binding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_tweets_list, container, false);
        View view = binding.getRoot();
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof TweetsListActionListener) {
            actionListener = (TweetsListActionListener)context;
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        tweets = new LinkedList<Tweet>();
        setupViews(view);
        NetworkStatus networkStatus = NetworkStatus.getSharedInstance();
        isOnline = networkStatus.checkNetworkStatus(getContext());
    }

    public void setupViews(View view) {
        rvTweets = binding.rvTweets;
        //rvTweets = (RecyclerView)  view.findViewById(R.id.rvTweets);
        swipeContainer = binding.swipeContainer;
        //swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainer);
        fab = binding.fabCompose;
        //fab = (FloatingActionButton) view.findViewById(R.id.fabCompose);
        adapter = new TweetsAdapter(getContext(), tweets);
        rvTweets.setAdapter(adapter);
        linearLayoutManager = new LinearLayoutManager(getActivity());
        rvTweets.setLayoutManager(linearLayoutManager);
        rvTweets.addItemDecoration(
                new DividerItemDecoration(getActivity(), R.drawable.divider));

        rvTweets.addOnScrollListener(new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                if (isOnline)
                    populateTimeline(false, false);

            }
        });

          /* adds the reply click support */
        adapter.setOnTweetClickListener(new TweetsAdapter.OnTweetClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if ( actionListener != null ) {
                    actionListener.onReplyTweet(tweets.get(position));
                }

            }
        });

        /* profile picture click support */
        adapter.setOnProfilePicClickListener(new TweetsAdapter.OnProfilePicClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if ( actionListener != null ) {
                    actionListener.onProfilePicClick(tweets.get(position).getUser());
                }

            }
        });

        /* adds the row click support */
        ItemClickSupport.addTo(rvTweets).setOnItemClickListener(
                new ItemClickSupport.OnItemClickListener() {
                    @Override
                    public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                        if ( actionListener != null ) {
                            actionListener.onItemClickDetailView(tweets.get(position));
                        }

                    }
                }
        );

        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Refresh list of tweets
                if (isOnline) {
                    int size = tweets.size();
                    tweets.clear();
                    adapter.notifyItemRangeRemoved(0, size);
                    //Clear database
                    max_id = 0;
                    populateTimeline(true, false);
                }
                else {
                    Toast.makeText(getContext(), "You are offline. Can't refresh tweets", Toast.LENGTH_LONG).show();


                }
            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ( actionListener != null ) {
                    actionListener.onAddNewTweet();
                }
            }
        });

    }

    protected void populateTimeline(final boolean isRefresh, final boolean isFirstCall){
        // Overridden in HomeTimeline
    }

}
