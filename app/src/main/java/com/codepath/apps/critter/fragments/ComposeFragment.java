package com.codepath.apps.critter.fragments;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.codepath.apps.critter.R;
import com.codepath.apps.critter.listeners.PostTwitterListener;


public class ComposeFragment extends DialogFragment {

    private PostTwitterListener postTwitterlistener;
    private static View composeFragment;




    public ComposeFragment() {
        // Empty constructor is required for DialogFragment
    }


    public static ComposeFragment newInstance(String title) {
        ComposeFragment frag = new ComposeFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        frag.setArguments(args);
        return frag;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Set to adjust screen height automatically, when soft keyboard appears on screen
        //getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        composeFragment = inflater.inflate(R.layout.fragment_compose_tweet, container);
        return composeFragment;
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setButtonsBehavior(view);
//        // Get field from view
//        mEditText = (EditText) view.findViewById(R.id.txt_your_name);
//        // Fetch arguments from bundle and set title
//        String title = getArguments().getString("title", "Enter Name");
//        getDialog().setTitle(title);
//        // Show soft keyboard automatically and request focus to field
//        mEditText.requestFocus();
//        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

    }


    public void setCustomObjectListener(PostTwitterListener listener) {
        postTwitterlistener = listener;
    }


    private void setButtonsBehavior(View v) {
        Button btnCancel = (Button) v.findViewById(R.id.cancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        Button btnPost = (Button) v.findViewById(R.id.post);
        btnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText composeTweet = (EditText) composeFragment.findViewById(R.id.compose_tweet);
                String tweetText = composeTweet.getText().toString();

                if (tweetText.isEmpty()) {
                    Toast.makeText(getContext(), "Come on, tweet something!", Toast.LENGTH_SHORT).show();
                } else {
                    postTwitterlistener.onPostTwitter(tweetText);
                }
            }
        });
    }

    public void onResume() {
        // Get existing layout params for the window
        ViewGroup.LayoutParams params = getDialog().getWindow().getAttributes();
        // Assign window properties to fill the parent
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.MATCH_PARENT;
        getDialog().getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);
        // Call super onResume after sizing
        super.onResume();
    }
}
