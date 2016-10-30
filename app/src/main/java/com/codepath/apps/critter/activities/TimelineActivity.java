package com.codepath.apps.critter.activities;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
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
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;


public class TimelineActivity extends AppCompatActivity {

    private TwitterClient twitterClient;
    private ArrayList<Tweet> tweets;

    LinearLayoutManager linearLayoutManager;

    private EndlessRecyclerViewScrollListener scrollListener;

    //private TweetsArrayAdapter tweetsAdapter;
    private TweetsAdapter tweetsAdapter;

    //private ListView lvTweets;
    private RecyclerView rvTweets;

    private ComposeFragment composeFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //lvTweets = (ListView) findViewById(R.id.lvTweets);
        rvTweets = (RecyclerView) findViewById(R.id.rvTweets);

        tweets = new ArrayList<>();

        //tweetsAdapter = new TweetsArrayAdapter(this, tweets);
        tweetsAdapter = new TweetsAdapter(this, tweets);

        //lvTweets.setAdapter(tweetsAdapter);
        rvTweets.setAdapter(tweetsAdapter);

        // Set layout manager to position the items
        linearLayoutManager = new LinearLayoutManager(this);
        rvTweets.setLayoutManager(linearLayoutManager);


        enableInfiniteScroll();


        twitterClient = TwitterApplication.getRestClient();//get singleton client
        populateTimeline(-1L);//first call, max_id won't be included as parameter in the API call

        setupComposeBehavior();


    }

    private void setupComposeBehavior() {
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_compose);
        fab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showComposeDialog();
                composeFragment.setCustomObjectListener(new PostTwitterListener() {
                    @Override
                    public void onPostTwitter(String tweet) {
                        Toast.makeText(getBaseContext(), tweet, Toast.LENGTH_SHORT).show();
                    }
                });
            }


        });


    }





    private void showComposeDialog() {
        FragmentManager fm = getSupportFragmentManager();
        composeFragment = ComposeFragment.newInstance("Some Title");
        composeFragment.show(fm, "fragment_edit_name");
    }



    private void enableInfiniteScroll() {
        scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(long max_id, int totalItemsCount, RecyclerView view) {
                // Triggered only when new data needs to be appended to the list of tweets

                //calculate new value for max_id: get the latest tweet and subtract 1
                Tweet t = tweets.get(tweets.size() - 1);
                long newMaxId = t.getTweetID();
                if (newMaxId != 0L) {//make the API call only if it's possible to decrement the ID
                    populateTimeline(--newMaxId);
                }
            }
        };

        // Add the scroll listener to RecyclerView
        rvTweets.addOnScrollListener(scrollListener);
    }


    //todo: http://guides.codepath.com/android/Sending-and-Managing-Network-Requests

    //todo: empty space at end of recyclerview

    //todo: improve API calls by adding since_id

    //todo: change bar title


    //send API request to get the timeline JSON
    //and fill the recyclerview with the data retrieved
    //by creating tweet objects
    private void populateTimeline(long maxId) {
        twitterClient.getHomeTimeline(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                // deserialize the response
                // create models
                // load data into the view
                ArrayList<Tweet> tweetList = Tweet.fromJSONArray(response);

                int currentSize = tweets.size();

                //add to existing list
                //tweetsAdapter.addAll(tweets);
                tweets.addAll(tweetList);

                //tweetsAdapter.notifyDataSetChanged();//does nothing
                tweetsAdapter.notifyItemRangeInserted(currentSize, tweetList.size());
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }
        }, maxId);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_timeline, menu);
        return true;
    }


}
