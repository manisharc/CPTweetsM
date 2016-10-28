package com.codepath.apps.CPTweetsM.models;

import org.json.JSONException;
import org.json.JSONObject;

public class User {
    private String name;
    private long uid;
    private String screenName;

    public String getName() {
        return name;
    }

    public long getUid() {
        return uid;
    }

    public String getScreenName() {
        return screenName;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    private String profileImageUrl;

    public static User fromJSON(JSONObject jsonObject){
        User u = new User();

        try {
            u.name = jsonObject.getString("name");
            u.screenName = jsonObject.getString("screen_name");
            u.profileImageUrl = jsonObject.getString("profile_image_url");
            u.uid = jsonObject.getLong("id");
        } catch (JSONException e) {
            e.printStackTrace();
        }




        return u;
    }
}
