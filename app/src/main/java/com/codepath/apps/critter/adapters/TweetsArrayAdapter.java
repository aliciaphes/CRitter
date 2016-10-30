package com.codepath.apps.critter.adapters;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.apps.critter.R;
import com.codepath.apps.critter.models.Tweet;
import com.codepath.apps.critter.util.Utilities;
import com.squareup.picasso.Picasso;

import java.util.List;


public class TweetsArrayAdapter extends ArrayAdapter<Tweet>{


    public TweetsArrayAdapter(Context context, List<Tweet> tweets) {
        super(context, 0 ,tweets);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Tweet tweet = getItem(position);

        if(convertView == null){
            //false means 'do not insert into parent yet'
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_tweet2, parent, false);
        }
        ImageView ivProfileImage = (ImageView) convertView.findViewById(R.id.iv_profile_image);
        //clear image in case it had a previous value
        ivProfileImage.setImageResource(android.R.color.transparent);//Color.parseColor("#80000000")
        //then load image with Picasso
        Picasso.with(getContext()).load(tweet.getUser().getProfileURL()).into(ivProfileImage);

        TextView tvUserName = (TextView) convertView.findViewById(R.id.tv_username);
        tvUserName.setText(tweet.getUser().getName());

        TextView tvHandle = (TextView) convertView.findViewById(R.id.tv_handle);
        tvHandle.setText(tweet.getUser().getScreenName());

        TextView tvTimestamp = (TextView) convertView.findViewById(R.id.tv_timestamp);
        tvTimestamp.setText(Utilities.getRelativeTimeAgo(tweet.getCreatedAt()));

        TextView tvBody = (TextView) convertView.findViewById(R.id.tv_body);
        tvBody.setText(tweet.getBody());

        return convertView;

        //todo: better implement viewholder pattern
    }
}
