package com.abelsuviri.tweetie.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.abelsuviri.tweetie.R;
import com.abelsuviri.tweetie.adapter.holder.HolderInterface;
import com.abelsuviri.tweetie.adapter.holder.TweetHolder;
import com.abelsuviri.tweetie.model.Tweet;

import java.util.List;

import twitter4j.Status;

/**
 * This is the RecyclerView adapter to show Twitter timeline.
 *
 * @author Abel Suviri
 */

public class TweetAdapter extends RecyclerView.Adapter<TweetHolder> {
    private List<Status> mStatuses;
    private List<Tweet> mTweets;
    private Context mContext;
    private HolderInterface mHolderInterface;
    private boolean isCustom = false;

    /**
     * This is the adapter constructor when device is connected.
     * @param statuses List of tweets (twitter4j object).
     * @param context Activity context.
     * @param holderInterface Reference to the ViewHolder interface.
     */
    public TweetAdapter(List<Status> statuses, Context context, HolderInterface holderInterface) {
        this.mStatuses = statuses;
        this.mContext = context;
        this.mHolderInterface = holderInterface;
    }

    /**
     * This is the adapter constructor when device is disconnected
     * @param tweets List of tweets (Realm object).
     * @param context Activity context.
     * @param holderInterface Reference to the ViewHolder interface.
     * @param custom Boolean added to allow have two constructors and know we are loading the tweets
     *               stored at the database.
     */
    public TweetAdapter(List<Tweet> tweets, Context context, HolderInterface holderInterface,
                        boolean custom) {
        this.mTweets = tweets;
        this.mContext = context;
        this.mHolderInterface = holderInterface;
        this.isCustom = custom;
    }

    @Override
    public TweetHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.tweet_list,
            parent, false);
        return new TweetHolder(itemView, mContext, mHolderInterface);
    }

    @Override
    public void onBindViewHolder(TweetHolder holder, int position) {
        if (isCustom) {
            Tweet item = mTweets.get(position);
            holder.bindTweets(item);
        } else {
            Status item = mStatuses.get(position);
            holder.bindTweets(item);
        }
    }

    @Override
    public int getItemCount() {
        if (isCustom) {
            return mTweets.size();
        } else {
            return mStatuses.size();
        }
    }
}
