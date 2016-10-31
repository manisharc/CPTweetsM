package com.codepath.apps.CPTweetsM.models;

import android.text.format.DateUtils;

import com.codepath.apps.CPTweetsM.TweetDatabase;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

// Parse the JSON + Store the data, encapsulate state logic or display logic
@Table(database = TweetDatabase.class)
public class Tweet extends BaseModel {
    @Column
    private String body;
    @PrimaryKey
    @Column
    private long uid; // unique id for the tweet
    @Column
    @ForeignKey(saveForeignKeyModel = true)
    private User user;
    @Column
    private String createdAt;

    private int mediaType;
    private static int MEDIA_TYPE_PHOTO = 0;
    private static int MEDIA_TYPE_VIDEO = 1;

    private String imageUrl;
    private String videoUrl;


    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public int getMediaType() {
        return mediaType;
    }

    public void setMediaType(int mediaType) {
        this.mediaType = mediaType;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getBody() {
        return body;

    }

    public long getUid() {
        return uid;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public User getUser() {
        return user;
    }
    // Deserialize the JSON
    public static Tweet fromJSON(JSONObject jsonObject){
        Tweet tweet = new Tweet();

        try {
            tweet.body = jsonObject.getString("text");
            tweet.uid = jsonObject.getLong("id");
            tweet.createdAt = jsonObject.getString("created_at");
            tweet.user= User.fromJSON(jsonObject.getJSONObject("user"));

            if (jsonObject.has("extended_entities")) {
                JSONObject jsonExtendedEntitiesObject = jsonObject.getJSONObject("extended_entities");

                if (jsonExtendedEntitiesObject.has("media")) {
                    JSONArray jsonMediaArray = jsonExtendedEntitiesObject.getJSONArray("media");

                    if (jsonMediaArray.length() > 0) {
                        JSONObject jsonMediaObject = jsonMediaArray.getJSONObject(0);

                        if (jsonMediaObject.has("type") && jsonMediaObject.has("media_url")) {
                            String type = jsonMediaObject.getString("type");
                            if (type.equals("photo")) {
                                tweet.mediaType = MEDIA_TYPE_PHOTO;

                            }
                            else if (type.equals("video")) {
                                tweet.mediaType = MEDIA_TYPE_VIDEO;
                                if (jsonMediaObject.has("video_info")) {
                                    JSONObject jsonVideoInfoObject = jsonMediaObject.getJSONObject("video_info");
                                    if (jsonVideoInfoObject.has("variants")) {
                                        JSONArray jsonVariantsArray = jsonVideoInfoObject.getJSONArray("variants");
                                        if (jsonVariantsArray.length() > 0) {
                                            JSONObject jsonVariantObject = jsonVariantsArray.getJSONObject(0);
                                            if (jsonVariantObject.has("url")) {
                                                tweet.videoUrl = jsonVariantObject.getString("url");
                                            }
                                        }

                                    }
                                }

                            }
                            tweet.imageUrl = jsonMediaObject.getString("media_url");
                        }


                    }
                }
            }



        } catch (JSONException e) {
            e.printStackTrace();
        }

        return tweet;
    }

    public static ArrayList<Tweet> fromJSONArray(JSONArray jsonArray){
        ArrayList<Tweet> tweets = new ArrayList<>();
        for (int i=0; i < jsonArray.length(); i++){
            try {
                JSONObject tweetJson = jsonArray.getJSONObject(i);
                Tweet tweet = Tweet.fromJSON(tweetJson);
                if (tweet != null){
                    tweets.add(tweet);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                continue;
            }
        }

        return tweets;
    }

    // getRelativeTimeAgo("Mon Apr 01 21:16:23 +0000 2014");
    public String getRelativeTimeAgo(String rawJsonDate) {
        String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
        SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
        sf.setLenient(true);

        String relativeDate = "";
        try {
            long dateMillis = sf.parse(rawJsonDate).getTime();
            relativeDate = DateUtils.getRelativeTimeSpanString(dateMillis,
                    System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS).toString();
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }

        return relativeDate;
    }


}
