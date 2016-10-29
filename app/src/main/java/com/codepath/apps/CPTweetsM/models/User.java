package com.codepath.apps.CPTweetsM.models;

import com.codepath.apps.CPTweetsM.TweetDatabase;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.json.JSONException;
import org.json.JSONObject;
@Table(database = TweetDatabase.class)
public class User extends BaseModel{
    @Column
    private String name;

    @PrimaryKey
    @Column
    private long uid;

    @Column
    private String screenName;
    @Column
    private String profileImageUrl;

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public void setScreenName(String screenName) {
        this.screenName = screenName;
    }

    public void setName(String name) {
        this.name = name;
    }
    public void setUid(long uid) {
        this.uid = uid;
    }
    public String getName() {
        return name;
    }

    public String getScreenName() {
        return screenName;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public long getUid() {
        return uid;
    }

    public long getUser() {
        return uid;
    }

    public static User fromJSON(JSONObject jsonObject){
        User u = new User();

        try {
            u.name = jsonObject.getString("name");
            u.screenName = "@" + (jsonObject.getString("screen_name"));
            u.profileImageUrl = jsonObject.getString("profile_image_url");
            u.uid = jsonObject.getLong("id");
            u.profileImageUrl = jsonObject.getString("profile_image_url_https");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return u;
    }
}
