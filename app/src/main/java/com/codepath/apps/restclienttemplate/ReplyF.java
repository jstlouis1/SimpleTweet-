package com.codepath.apps.restclienttemplate;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.apps.restclienttemplate.models.User;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.json.JSONException;
import org.parceler.Parcels;

import okhttp3.Headers;

public class ReplyF extends AppCompatDialogFragment {

    public static final String TAG = "ReplyFragment";
    public static final int MAX_TWEET_LENGTH = 140;
    public static final String TEXT = "reply";
    EditText etReplyFragment;
    Button btnReplyFragment;
    ImageButton btnCancelReply;
    TwitterClient client;
    TextView tvNameReply, tvUsernameReply;
    ImageView profileImageReply;
    TextView tvInReply;
    Context context;

    // constructor
    public ReplyF() {}


    public static ReplyF newInstance(String title) {
        ReplyF frag = new ReplyF();
        Bundle args = new Bundle();
        args.putString("title", title);
        frag.setArguments(args);
        return frag;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_r, container, false);
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Get field from view
        client = TwitterApp.getRestClient(context);
        etReplyFragment = view.findViewById(R.id.etReplyFragment);
        btnReplyFragment = view.findViewById(R.id.btnReplyFragment);
        btnCancelReply = view.findViewById(R.id.btnCancelReply);
        tvNameReply = view.findViewById(R.id.tvNameReply);
        tvUsernameReply = view.findViewById(R.id.tvUsernameReply);
        profileImageReply = view.findViewById(R.id.profileImageReply);
        tvInReply = view.findViewById(R.id.tvInReply);

        // get user info
        Bundle bundle = getArguments();
        User currentUserInfo = Parcels.unwrap(bundle.getParcelable("CurrentUserInfo"));
        Tweet tweet = Parcels.unwrap(bundle.getParcelable("tweet"));


        tvInReply.setText("In reply to" + tweet.user.name);
        etReplyFragment.setText(tweet.user.screenName);
        tvNameReply.setText(currentUserInfo.name);
        tvUsernameReply.setText(currentUserInfo.screenName);
        Glide.with(getContext())
                .load(currentUserInfo.profileImageUrl)
                .transform(new CircleCrop())
                .into(profileImageReply);

        // Fetch arguments from bundle and set title
        String title = getArguments().getString("title", "Enter Name");
        getDialog().setTitle(title);

        // Set click on button
        btnReplyFragment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                publishTweet();
                dismiss();
            }
        });


        // click to cancel and save draft
        btnCancelReply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        getDialog().getWindow().setLayout(1400,2200);
    }


    private void publishTweet(){
        String tweetContent = etReplyFragment.getText().toString();
        if (tweetContent.isEmpty()){
            Toast.makeText(context, "Sorry your tweet cannot be empty", Toast.LENGTH_LONG).show();
            return;
        }
        if (tweetContent.length() > MAX_TWEET_LENGTH){
            Toast.makeText(context, "Sorry your tweet is too long", Toast.LENGTH_LONG).show();
            return;
        }

        // Make an API call to Twitter to publish the tweet
        client.publishTweet(tweetContent, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                Log.i(TAG, "onSuccess to publish tweet");
                try {
                    Tweet tweet = Tweet.fromJson(json.jsonObject);
                    Log.i(TAG, "Published tweet says: " + tweet.body);
                }catch (JSONException e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.e(TAG, "onFailure to publish tweet", throwable);
            }
        });
    }
}
