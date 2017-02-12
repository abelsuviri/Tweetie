package com.abelsuviri.tweetie.adapter.holder;

import twitter4j.Status;

/**
 * This is the interface to communicate RecyclerView ViewHolder and TimelineActivity.
 *
 * @author Abel Suviri
 */

public interface HolderInterface {
    void onProfileClick(long userId);

    void onTweetClick(Status status);

    void onFavClick(boolean setFav, long tweetId);

    void onSaveFav(Status status);

    void onDeleteFav(Status status);
}
