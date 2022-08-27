package com.codepath.apps.restclienttemplate;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
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
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.apps.restclienttemplate.models.User;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.json.JSONException;
import org.parceler.Parcels;

import okhttp3.Headers;

public class FragmentC extends AppCompatDialogFragment{

    public static final String TAG = "ComposeFragment";
    public static final int MAX_TWEET_LENGTH = 140;
    public static final String KEY = "tweet";
    EditText editComposeFragment;
    Button btnTweetFragment;
    ImageButton btnCancelFragment;
    TextView tvName, tvUserName;
    ImageView profileImageCompose;
    TwitterClient client;
    Context context;

    public FragmentC() {}

    // Defines the listener interface
    public interface ComposeDialogListener {
        void onFinishComposeDialog(Tweet tweet);
    }

    public static FragmentC newInstance(String title) {
        FragmentC frag = new FragmentC();
        Bundle args = new Bundle();
        args.putString("title", title);
        frag.setArguments(args);
        return frag;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.c_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Get field from view
        client = TwitterApp.getRestClient(context);
        editComposeFragment = view.findViewById(R.id.etComposeFragment);
        btnTweetFragment = view.findViewById(R.id.btnTweetFragment);
        btnCancelFragment = view.findViewById(R.id.btnCancelFragment);
        profileImageCompose = view.findViewById(R.id.profileImageCompose);
        tvName = view.findViewById(R.id.tvName);
        tvUserName = view.findViewById(R.id.tvUserName);

        // get user info
        Bundle bundle = getArguments();
        User currentUserInfo = Parcels.unwrap(bundle.getParcelable("CurrentUserInfo"));

        tvName.setText(currentUserInfo.name);
        tvUserName.setText(currentUserInfo.screenName);
        Glide.with(getContext())
                .load(currentUserInfo.profileImageUrl)
                .transform(new CircleCrop())
                .into(profileImageCompose);


        // Fetch arguments from bundle and set title
        String title = getArguments().getString("title", "Enter Name");
        getDialog().setTitle(title);

        loadDraftData();

        // click to add tweet
        btnTweetFragment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {addTweet();}
        });

        // click to cancel and save draft
      btnCancelFragment.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
              openPrompt();
          }
      });


        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        getDialog().getWindow().setLayout(1400,2200);
    }

    private void loadDraftData() {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getContext());
        String draft = pref.getString(KEY, "");

        if(!draft.isEmpty()){
            editComposeFragment.setText(draft);
        }
    }


    // Method to show the prompt to save draft
    public void openPrompt(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
        alertDialogBuilder.setMessage("Save draft?");

        alertDialogBuilder.setPositiveButton("Save",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        saveDraft();
                    }
                });

        alertDialogBuilder.setNegativeButton("Delete",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dismiss();
            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }


    // Method to save draft
    private void saveDraft(){
        String tweetContent = editComposeFragment.getText().toString();
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getContext());
        SharedPreferences.Editor edit = pref.edit();
        edit.putString(KEY, tweetContent);
        edit.commit();
        dismiss();
    }


    // method to publish a tweet
    private void addTweet(){
        String tweetContent = editComposeFragment.getText().toString();
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
                    ComposeDialogListener listener = (ComposeDialogListener) getTargetFragment();
                    listener.onFinishComposeDialog(tweet);
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
        dismiss();
    }

}


