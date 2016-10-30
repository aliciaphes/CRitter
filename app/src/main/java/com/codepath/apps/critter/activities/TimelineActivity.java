package com.codepath.apps.critter.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;

import com.codepath.apps.critter.R;
import com.codepath.apps.critter.TwitterApplication;
import com.codepath.apps.critter.TwitterClient;
import com.codepath.apps.critter.adapters.TweetsAdapter;
import com.codepath.apps.critter.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;





public class TimelineActivity extends AppCompatActivity {

    private TwitterClient twitterClient;
    private ArrayList<Tweet> tweets;

    //private TweetsArrayAdapter tweetsAdapter;
    private TweetsAdapter tweetsAdapter;

    //private ListView lvTweets;
    private RecyclerView rvTweets;


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
        rvTweets.setLayoutManager(new LinearLayoutManager(this));

        twitterClient = TwitterApplication.getRestClient();//get singleton client
        populateTimeline();

    }

    //todo: pagination


    //todo: empty space at end of recyclerview


    //todo: change bar title


    //send API request to get the timeline JSON
    //and fill the listview with the data retrieved
    //by creating tweet objects
    private void populateTimeline() {
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
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_timeline, menu);
        return true;
    }


}
