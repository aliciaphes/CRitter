package com.codepath.apps.critter.activities;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;

import com.codepath.apps.critter.R;
import com.codepath.apps.critter.TwitterApplication;
import com.codepath.apps.critter.TwitterClient;
import com.codepath.apps.critter.adapters.TweetsAdapter;
import com.codepath.apps.critter.fragments.ComposeFragment;
import com.codepath.apps.critter.listeners.EndlessRecyclerViewScrollListener;
import com.codepath.apps.critter.listeners.PostTwitterListener;
import com.codepath.apps.critter.models.Tweet;
import com.codepath.apps.critter.util.DummyData;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;


public class TimelineActivity extends AppCompatActivity {

    private long index = -1L;

    private TwitterClient twitterClient;
    private ArrayList<Tweet> tweets;

    private LinearLayoutManager linearLayoutManager;

    private EndlessRecyclerViewScrollListener scrollListener;

    //private z_TweetsArrayAdapter tweetsAdapter;
    private TweetsAdapter tweetsAdapter;

    //private ListView lvTweets;
    private RecyclerView rvTweets;

    private ComposeFragment composeFragment;

    SwipeRefreshLayout swipeContainer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        rvTweets = (RecyclerView) findViewById(R.id.rvTweets);

        tweets = new ArrayList<>();

        //tweetsAdapter = new z_TweetsArrayAdapter(this, tweets);
        tweetsAdapter = new TweetsAdapter(this, tweets);

        rvTweets.setAdapter(tweetsAdapter);

        // Set layout manager to position the items
        linearLayoutManager = new LinearLayoutManager(this);
        rvTweets.setLayoutManager(linearLayoutManager);

        enableInfiniteScroll();

        twitterClient = TwitterApplication.getRestClient();//get singleton client

        //first call, max_id is -1 so it won't be included as parameter in the API call
        populateTimeline(index);
        //populateDummyTimeline();

        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        setRefreshOnSwipe();

        enableClickableTweets();

        setupComposeBehavior();
    }


    private void setRefreshOnSwipe() {
//        SwipeRefreshLayout.OnRefreshListener onRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//                //behavior
//            }
//        };
//        swipeContainer.setOnRefreshListener(onRefreshListener);


        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                int size = tweets.size();
                tweets.clear();
                //tweetsAdapter.clear();
                //notify the changes
                tweetsAdapter.notifyItemRangeRemoved(0, size);

                //reset index and call get home timeline again
                index = -1L;
                populateTimeline(index);

            }
        });

    }


    private void enableClickableTweets() {
//        ItemClickSupport.addTo(rvTweets).setOnItemClickListener(
//                new ItemClickSupport.OnItemClickListener() {
//                    @Override
//                    public void onItemClicked(RecyclerView recyclerView, int position, View v) {
//                        //todo: launch intent
//                    }
//                }
//        );
    }


    private void setupComposeBehavior() {
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_compose);
        fab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showComposeDialog();
                composeFragment.setCustomObjectListener(new PostTwitterListener() {
                    @Override
                    public void onPostTwitter(String tweetBody) {
                        composeFragment.dismiss();

                        //postTweet(tweetBody);
                        /** BEGIN
                         This block is to be deleted, only used to avoid tweeting every time I test.
                         ALSO DO NOT FORGET TO UNCOMMENT THE ABOVE CALL TO postTweet!!
                         */
                        try {
                            //create dummy tweet
                            Tweet newTweet = Tweet.fromJSON(new JSONObject(DummyData.DUMMY_TWEET));

                            refreshTimelineAndScrollUp(newTweet);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        /** END */
                    }
                });
            }
        });
    }


    private void showComposeDialog() {
        FragmentManager fm = getSupportFragmentManager();
        composeFragment = ComposeFragment.newInstance("Compose tweet");
        composeFragment.show(fm, "fragment_add_tweet");
    }


    private void enableInfiniteScroll() {
        scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(long max_id, int totalItemsCount, RecyclerView view) {
                // Triggered only when new data needs to be appended to the list of tweets
                populateTimeline(index);
                //populateDummyTimeline();
            }
        };

        // Add the scroll listener to RecyclerView
        rvTweets.addOnScrollListener(scrollListener);
    }

    private void updateIndex() {
        //calculate new value for max_id for the API call: get the latest tweet and subtract 1
        Tweet endTweet = tweets.get(tweets.size() - 1);
        long endTweetId = endTweet.getTweetID();
        if (endTweetId != 0L) {//make the API call only if it's possible to decrement the ID
            index = --endTweetId;
        }
    }


    //todo: improve API calls by adding since_id

    //todo: change bar title


    //send API request to get the timeline JSON
    //and fill the recyclerview with the data retrieved
    //by creating tweet objects
    private void populateTimeline(long maxId) {
        twitterClient.getHomeTimeline(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                // deserialize the response and create models
                ArrayList<Tweet> tweetList = Tweet.fromJSONArray(response);

                //load data into the view:

                //store reference to current size
                int currentSize = tweets.size();

                //add retrieved tweets to existing list
                tweets.addAll(tweetList);

                //visually refresh the list
                tweetsAdapter.notifyItemRangeInserted(currentSize, tweetList.size());

                if (!tweetList.isEmpty()) {
                    updateIndex();
                }
                swipeContainer.setRefreshing(false);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }
        }, maxId);
    }


    private void populateDummyTimeline() {
        ArrayList<Tweet> tweetList = null;
        try {
            tweetList = Tweet.fromJSONArray(new JSONArray(DummyData.DUMMY_TIMELINE));
            int currentSize = tweets.size();
            tweets.addAll(tweetList);
            tweetsAdapter.notifyItemRangeInserted(currentSize, tweetList.size());
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    private void postTweet(String tweet) {
        twitterClient.postTweet(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                //super.onSuccess(statusCode, headers, response);

                //get new tweet that was generated
                Tweet newTweet = Tweet.fromJSON(response);
                //newTweet = Tweet.fromJSON(new JSONObject(Utilities.dummyTweet));

                refreshTimelineAndScrollUp(newTweet);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }
        }, tweet);
    }


    private void refreshTimelineAndScrollUp(Tweet newTweet) {
        tweets.add(0, newTweet);

        tweetsAdapter.notifyItemInserted(0);

        Toast.makeText(getBaseContext(), "Twitter was added", Toast.LENGTH_SHORT).show();

        //make sure we take new tweet is displayed on timeline:
        rvTweets.scrollToPosition(0);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_timeline, menu);
        return true;
    }


}
