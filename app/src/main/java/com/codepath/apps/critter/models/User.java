package com.codepath.apps.critter.models;


import org.json.JSONException;
import org.json.JSONObject;


public class User {
    private String name;
    private long userID;
    private String screenName; //handle
    private String profileURL;


    public String getName() {
        return name;
    }

    public long getUserID() {
        return userID;
    }

    public String getScreenName() {
        return screenName;
    }

    public String getProfileURL() {
        return profileURL;
    }

    //deserialize and return a user
    public static User fromJSON(JSONObject jsonObject) {
        User user = new User();

        try {
            user.name = jsonObject.getString("name");
            user.userID = jsonObject.getLong("id");
            user.screenName = jsonObject.getString("screen_name");
            user.profileURL = jsonObject.getString("profile_image_url");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return user;
    }

}
