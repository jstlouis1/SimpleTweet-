package com.codepath.apps.restclienttemplate.models;

import androidx.room.ColumnInfo;
import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.codepath.apps.restclienttemplate.TimeFormatter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.List;

@Entity(foreignKeys = @ForeignKey(entity=User.class, parentColumns="id", childColumns="userId"))
@Parcel
public class Tweet {

    @ColumnInfo
    @PrimaryKey
    public Long id;

    @ColumnInfo
    public String body;

    @ColumnInfo
    public String createdAt;

    @ColumnInfo
    public int  favorite_count;

    @ColumnInfo
    public int  retweet_count;

    @ColumnInfo
    public Boolean favorited;

    @ColumnInfo
    public Boolean retweeted;

    @ColumnInfo
    public long userId;

    @Ignore
    public User user;

    @Ignore
    public Entities entities;

    @Ignore
    public ExtendedEntities exEntities;

    public String getFavorite_count() {
        return String.valueOf(favorite_count);
    }

    public String getRetweet_count() {
        return String.valueOf(retweet_count);
    }

    public Tweet(){}


    public static Tweet fromJson(JSONObject jsonObject) throws JSONException {
        Tweet tweet = new Tweet();
        tweet.body = jsonObject.getString("text");
        tweet.createdAt = jsonObject.getString("created_at");
        tweet.id = jsonObject.getLong("id");
        tweet.favorite_count = jsonObject.getInt("favorite_count");
        tweet.retweet_count = jsonObject.getInt("retweet_count");
        tweet.retweeted = jsonObject.getBoolean("retweeted");
        tweet.favorited = jsonObject.getBoolean("favorited");

        User user = User.fromJson(jsonObject.getJSONObject("user"));
        tweet.user = user;
        tweet.userId = user.id;

        tweet.entities = Entities.fromJson(jsonObject.getJSONObject("entities"));

        if (jsonObject.has("extended_entities")){
            tweet.exEntities = ExtendedEntities.fromJson(jsonObject.getJSONObject("extended_entities"));
        }else {
            tweet.exEntities = new ExtendedEntities();
            tweet.exEntities.videoUrl = "";
            tweet.exEntities.type2 = "";
        }
        return tweet;
    }


    public static List<Tweet> fromJsonArray(JSONArray jsonArray) throws JSONException {
        List<Tweet> tweets = new ArrayList<>();
        for(int i = 0; i < jsonArray.length(); i++){
            tweets.add(fromJson(jsonArray.getJSONObject(i)));
        }
        return tweets;
    }

    public static String  getFormattedTimestamp(String createdAt){
        return TimeFormatter.getTimeDifference(createdAt);
    }

    public static String  getFormattedTime(String createdAt){
        return TimeFormatter.getTimeStamp(createdAt);
    }
}