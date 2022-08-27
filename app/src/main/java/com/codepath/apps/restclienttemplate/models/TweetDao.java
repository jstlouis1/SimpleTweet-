package com.codepath.apps.restclienttemplate.models;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.ArrayList;
import java.util.List;

@Dao
public interface TweetDao {

    @Query("SELECT Tweet.body AS tweet_body, Tweet.createdAt AS tweet_createdAt, " +
            "Tweet.favorite_count AS tweet_favorite_count, Tweet.retweet_count AS tweet_retweetCount, " +
            " Tweet.favorited AS tweet_favorited, Tweet.retweeted AS tweet_retweeted, Tweet.id AS tweet_id, " +
            "User.*, Entities.* FROM Tweet, Entities " +
            "INNER JOIN User ON Tweet.userId ==  User.id  ORDER BY Tweet.createdAt DESC LIMIT 20")
    List<TweetWithUser> recentItems();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertModel(Tweet... tweets);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertModel(User... users);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertModel(Entities... entities);

}
