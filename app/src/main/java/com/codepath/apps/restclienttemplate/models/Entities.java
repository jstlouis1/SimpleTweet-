package com.codepath.apps.restclienttemplate.models;

import android.util.Log;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Entity
@Parcel
public class Entities {

    @ColumnInfo
    @PrimaryKey
    public Long idEntities;

    @ColumnInfo
    public String media_url;

    @ColumnInfo
    public String type1;

    public Entities() {}

    public static Entities fromJson(JSONObject jsonObject) throws JSONException {
        Entities entities = new Entities();
        // Check if a cover media is available
        if (!jsonObject.has("media")) {
            entities.media_url = "";
            entities.type1 = "";
        } else if(jsonObject.has("media")) {
            final JSONArray media_array = jsonObject.getJSONArray("media");
            entities.idEntities = media_array.getJSONObject(0).getLong("id");
            entities.media_url = media_array.getJSONObject(0).getString("media_url_https");
            entities.type1 = media_array.getJSONObject(0).getString("type");
        }
        return entities;
    }

    public static List<Entities> fromJsonTweetArray(List<Tweet> tweetsFromNetwork) {
        List<Entities> entities = new ArrayList<>();
        for (int i = 0; i < tweetsFromNetwork.size(); i++){
            entities.add(tweetsFromNetwork.get(i).entities);
        }
        return entities;
    }
}
