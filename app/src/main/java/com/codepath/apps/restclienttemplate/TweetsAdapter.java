package com.codepath.apps.restclienttemplate;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.volokh.danylo.video_player_manager.ui.VideoPlayerView;


import org.parceler.Parcels;

import java.util.List;

public class TweetsAdapter extends RecyclerView.Adapter<TweetsAdapter.ViewHolder> {

    public static Context context;
    List<Tweet> listTweets;


    // Pass in the context and list of tweets
    public TweetsAdapter(Context context, List<Tweet> tweets) {
        this.context = context;
        this.listTweets = tweets;
    }


    // For each value inflate the layout
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_tweet, parent, false);
        return new ViewHolder(view);
    }

    // Bin values based on the position of the element
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // get data at position
        Tweet tweet = listTweets.get(position);
        // Bind the tweet with view holder
        holder.bind(tweet);
    }

    @Override
    public int getItemCount() {
        return listTweets.size();
    }



    // Method to clean all elements of the recycler
    public void clear(){
        listTweets.clear();
        notifyDataSetChanged();
    }

    // Method to add a list of tweets -- change to type used
    public void addAll(List<Tweet> tweets){
        listTweets.addAll(tweets);
        notifyDataSetChanged();
    }


    //Define viewHolder
    public static class ViewHolder extends RecyclerView.ViewHolder{

        ImageView ivProfileImage, imagePost;

        TextView tvScreenName, tvBody, tvUsername, tvTime, tvRetweet, tvLike, tvReply;
        RelativeLayout containerItem;
        VideoPlayerView videoPlayer;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            ivProfileImage = itemView.findViewById(R.id.ivProfileImage);
            tvScreenName = itemView.findViewById(R.id.tvScreenName);
            tvBody = itemView.findViewById(R.id.tvBody);
            tvUsername = itemView.findViewById(R.id.tvUsername);
            tvTime = itemView.findViewById(R.id.tvTime);
            tvRetweet = itemView.findViewById(R.id.tvRetweet);
            tvLike = itemView.findViewById(R.id.tvLike);
            containerItem = itemView.findViewById(R.id.containerItem);
            imagePost = itemView.findViewById(R.id.ivPostImage);
            videoPlayer = itemView.findViewById(R.id.video_player);
            tvReply = itemView.findViewById(R.id.tvReply);
        }

        public void bind(Tweet tweet) {
            tvBody.setText(tweet.body);
            tvScreenName.setText(tweet.user.name);
            tvUsername.setText("@"+tweet.user.screenName);
            tvTime.setText(Tweet.getFormattedTimestamp(tweet.createdAt));
            tvRetweet.setText(tweet.getRetweet_count());
            tvLike.setText(tweet.getFavorite_count());

            if (!tweet.entities.media_url.isEmpty()){
                imagePost.setVisibility(View.VISIBLE);
                Glide.with(context)
                        .load(tweet.entities.media_url)
                        .transform(new RoundedCorners(45))
                        .into(imagePost);
            }

            Glide.with(context)
                    .load(tweet.user.profileImageUrl)
                    .transform(new CircleCrop())
                    .into(ivProfileImage);


            // add video
//            if (!tweet.exEntities.videoUrl.isEmpty() && Objects.equals(tweet.exEntities.type2, "video")){
//                videoPlayer.setVisibility(View.VISIBLE);
//
//                videoPlayer.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//
//                        VideoPlayerManager<MetaData> mVideoPlayerManager = new SingleVideoPlayerManager(new PlayerItemChangeListener() {
//                            @Override
//                            public void onPlayerItemChanged(MetaData metaData) {
//
//                            }
//                        });
//
//                        mVideoPlayerManager.playNewVideo(null, videoPlayer, tweet.exEntities.videoUrl);
//                    }
//                });
//            }



            // add click on a tweet
            containerItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(context, DetailActivity.class);
                    i.putExtra("Tweet", Parcels.wrap(tweet));
                    context.startActivity(i);
                }
            });

            // click on icon like
            tvLike.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    clickIconLike(tweet);
                }
            });


            // click on icon retweet
            tvRetweet.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    clickIconRetweet(tweet);
                }
            });


            // click on reply icon and show the modal overlay (Reply)
            tvReply.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                        showReplyFragment(tweet);
                }
            });

        }

        // method to show the dialog reply
        private void showReplyFragment(Tweet tweet) {
            FragmentManager fm = ((FragmentActivity)context).getSupportFragmentManager();
            ReplyF replyFragment = ReplyF.newInstance("Some Title");

            // pass info of current user
            Bundle bundle = new Bundle();
            bundle.putParcelable("CurrentUserInfo", Parcels.wrap(TimeLineActivity.currentUser));
            bundle.putParcelable("tweet", Parcels.wrap(tweet));

            replyFragment.setArguments(bundle);
            replyFragment.show(fm, "fragment_reply");
        }

        // method to verify if user click on  retweet icon
        private void clickIconRetweet(Tweet tweet) {
            if (!tweet.retweeted){
                Drawable drawable = ContextCompat.getDrawable(context, R.drawable.green_retweet);
                drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                tvRetweet.setCompoundDrawables(drawable, null, null, null);
                ++tweet.retweet_count;
                tvRetweet.setText(String.valueOf(tweet.retweet_count));
                tweet.retweeted = true;
            }else {
                --tweet.retweet_count;

                Drawable drawable = ContextCompat.getDrawable(context, R.drawable.retweet);
                drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                tvRetweet.setCompoundDrawables(drawable, null, null, null);

                tvRetweet.setText(String.valueOf(tweet.retweet_count));
                tweet.retweeted = false;
            }
        }

        // method to verify if user click on heart icon
        private void clickIconLike(Tweet tweet) {
            if (!tweet.favorited){
                Drawable drawable = ContextCompat.getDrawable(context, R.drawable.red_heart);
                drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                tvLike.setCompoundDrawables(drawable, null, null, null);

                ++tweet.favorite_count;
                tvLike.setText(String.valueOf(tweet.favorite_count));
                tweet.favorited = true;
            }else {
                --tweet.favorite_count;

                Drawable drawable = ContextCompat.getDrawable(context, R.drawable.cards_heart_outline);
                drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                tvLike.setCompoundDrawables(drawable, null, null, null);

                tvLike.setText(String.valueOf(tweet.favorite_count));
                tweet.favorited = false;
            }
        }

    }

}
