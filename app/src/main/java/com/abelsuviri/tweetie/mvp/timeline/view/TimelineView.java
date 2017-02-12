package com.abelsuviri.tweetie.mvp.timeline.view;

import com.abelsuviri.tweetie.base.BaseView;
import com.abelsuviri.tweetie.model.Tweet;

import java.util.List;

import twitter4j.Status;

/**
 * @author Abel Suviri
 */

public interface TimelineView extends BaseView {
    void showTweetList(List<Status> statuses);

    void showFavTweets(List<Tweet> tweets);

    void logoutUser();

    void closeApp();
}
