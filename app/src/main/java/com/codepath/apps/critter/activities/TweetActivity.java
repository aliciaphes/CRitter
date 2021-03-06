package com.codepath.apps.critter.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.apps.critter.R;
import com.codepath.apps.critter.models.Tweet;
import com.codepath.apps.critter.util.Utilities;
import com.squareup.picasso.Picasso;

import org.parceler.Parcels;

import static com.raizlabs.android.dbflow.config.FlowManager.getContext;

public class TweetActivity extends AppCompatActivity {

    private final int FONT_SIZE = 14;

    private Tweet currentTweet;

    private ImageView ivProfileImage;
    private TextView tvUserName;
    private TextView tvHandle;
    private TextView tvTimestamp;
    private TextView tvBody;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_tweet);

        setUpViews();

        //retrieve tweet from intent
        currentTweet = Parcels.unwrap(getIntent().getParcelableExtra("tweet"));

        //populate view from tweet
        if (currentTweet != null) {
            setUpValues();
        }
    }


    private void setUpViews() {
        ivProfileImage = (ImageView) findViewById(R.id.iv_profile_image);
        tvUserName = (TextView) findViewById(R.id.tv_username);
        tvHandle = (TextView) findViewById(R.id.tv_handle);
        tvTimestamp = (TextView) findViewById(R.id.tv_timestamp);
        tvBody = (TextView) findViewById(R.id.tv_body);
    }

    private void setUpValues() {
        //clear image in case it had a previous value
        ivProfileImage.setImageResource(android.R.color.transparent);
        //then load image with Picasso
        Picasso.with(getContext()).load(currentTweet.getUser().getProfileURL())
                .placeholder(R.drawable.placeholder)
                .into(ivProfileImage);

        tvUserName.setText(currentTweet.getUser().getName());
        tvUserName.setTextSize(FONT_SIZE);

        tvHandle.setText("@" + currentTweet.getUser().getScreenName());
        tvHandle.setTextSize(FONT_SIZE);

        tvTimestamp.setText(Utilities.getRelativeTimeAgo(currentTweet.getCreatedAt()));
        tvTimestamp.setTextSize(FONT_SIZE);

        tvBody.setText(currentTweet.getBody());
        tvBody.setTextSize(FONT_SIZE);
    }


}
