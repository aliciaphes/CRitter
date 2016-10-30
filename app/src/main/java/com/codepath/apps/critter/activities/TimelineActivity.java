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
import org.json.JSONException;
import org.json.JSONObject;

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
                        composeFragment.dismiss();

                        //postTweet(tweet); //todo: UNCOMMENT!!

                        Tweet t = null;
                        try {
                            t = Tweet.fromJSON(new JSONObject("{\n" +
                                    "  \"coordinates\": null,\n" +
                                    "  \"favorited\": false,\n" +
                                    "  \"created_at\": \"Wed Sep 05 00:37:15 +0000 2012\",\n" +
                                    "  \"truncated\": false,\n" +
                                    "  \"id_str\": \"243145735212777472\",\n" +
                                    "  \"entities\": {\n" +
                                    "    \"urls\": [\n" +
                                    "\n" +
                                    "    ],\n" +
                                    "    \"hashtags\": [\n" +
                                    "      {\n" +
                                    "        \"text\": \"peterfalk\",\n" +
                                    "        \"indices\": [\n" +
                                    "          35,\n" +
                                    "          45\n" +
                                    "        ]\n" +
                                    "      }\n" +
                                    "    ],\n" +
                                    "    \"user_mentions\": [\n" +
                                    "\n" +
                                    "    ]\n" +
                                    "  },\n" +
                                    "  \"in_reply_to_user_id_str\": null,\n" +
                                    "  \"text\": \"Maybe he'll finally find his keys. #peterfalk\",\n" +
                                    "  \"contributors\": null,\n" +
                                    "  \"retweet_count\": 0,\n" +
                                    "  \"id\": 243145735212777472,\n" +
                                    "  \"in_reply_to_status_id_str\": null,\n" +
                                    "  \"geo\": null,\n" +
                                    "  \"retweeted\": false,\n" +
                                    "  \"in_reply_to_user_id\": null,\n" +
                                    "  \"place\": null,\n" +
                                    "  \"user\": {\n" +
                                    "    \"name\": \"Jason Costa\",\n" +
                                    "    \"profile_sidebar_border_color\": \"86A4A6\",\n" +
                                    "    \"profile_sidebar_fill_color\": \"A0C5C7\",\n" +
                                    "    \"profile_background_tile\": false,\n" +
                                    "    \"profile_image_url\": \"http://a0.twimg.com/profile_images/1751674923/new_york_beard_normal.jpg\",\n" +
                                    "    \"created_at\": \"Wed May 28 00:20:15 +0000 2008\",\n" +
                                    "    \"location\": \"\",\n" +
                                    "    \"is_translator\": true,\n" +
                                    "    \"follow_request_sent\": false,\n" +
                                    "    \"id_str\": \"14927800\",\n" +
                                    "    \"profile_link_color\": \"FF3300\",\n" +
                                    "    \"entities\": {\n" +
                                    "      \"url\": {\n" +
                                    "        \"urls\": [\n" +
                                    "          {\n" +
                                    "            \"expanded_url\": \"http://www.jason-costa.blogspot.com/\",\n" +
                                    "            \"url\": \"http://t.co/YCA3ZKY\",\n" +
                                    "            \"indices\": [\n" +
                                    "              0,\n" +
                                    "              19\n" +
                                    "            ],\n" +
                                    "            \"display_url\": \"jason-costa.blogspot.com\"\n" +
                                    "          }\n" +
                                    "        ]\n" +
                                    "      },\n" +
                                    "      \"description\": {\n" +
                                    "        \"urls\": []\n" +
                                    "      }\n" +
                                    "    },\n" +
                                    "    \"default_profile\": false,\n" +
                                    "    \"contributors_enabled\": false,\n" +
                                    "    \"url\": \"http://t.co/YCA3ZKY\",\n" +
                                    "    \"favourites_count\": 883,\n" +
                                    "    \"utc_offset\": -28800,\n" +
                                    "    \"id\": 14927800,\n" +
                                    "    \"profile_image_url_https\": \"https://si0.twimg.com/profile_images/1751674923/new_york_beard_normal.jpg\",\n" +
                                    "    \"profile_use_background_image\": true,\n" +
                                    "    \"listed_count\": 150,\n" +
                                    "    \"profile_text_color\": \"333333\",\n" +
                                    "    \"protected\": false,\n" +
                                    "    \"lang\": \"en\",\n" +
                                    "    \"followers_count\": 8760,\n" +
                                    "    \"time_zone\": \"Pacific Time (US & Canada)\",\n" +
                                    "    \"profile_background_image_url_https\": \"https://si0.twimg.com/images/themes/theme6/bg.gif\",\n" +
                                    "    \"verified\": false,\n" +
                                    "    \"profile_background_color\": \"709397\",\n" +
                                    "    \"notifications\": false,\n" +
                                    "    \"description\": \"Platform at Twitter\",\n" +
                                    "    \"geo_enabled\": true,\n" +
                                    "    \"statuses_count\": 5532,\n" +
                                    "    \"default_profile_image\": false,\n" +
                                    "    \"friends_count\": 166,\n" +
                                    "    \"profile_background_image_url\": \"http://a0.twimg.com/images/themes/theme6/bg.gif\",\n" +
                                    "    \"show_all_inline_media\": true,\n" +
                                    "    \"screen_name\": \"jasoncosta\",\n" +
                                    "    \"following\": false\n" +
                                    "  }}"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        int currentSize = tweets.size();
                        tweets.add(0, t);
                        tweetsAdapter.notifyItemRangeInserted(currentSize, 1);
                        scrollListener.resetState();

                        Toast.makeText(getBaseContext(), "Twitter was added", Toast.LENGTH_SHORT).show();

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


    private void postTweet(String tweet) {
        twitterClient.postTweet(new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                //super.onSuccess(statusCode, headers, response);

                //get new tweet that was generated
                Tweet t = Tweet.fromJSON(response);

                int currentSize = tweets.size();
                tweets.add(0, t);
                tweetsAdapter.notifyItemRangeInserted(currentSize, tweets.size());

                Toast.makeText(getBaseContext(), "Twitter was added", Toast.LENGTH_SHORT).show();

                //take user back to timeline
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }
        }, tweet);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_timeline, menu);
        return true;
    }


}
