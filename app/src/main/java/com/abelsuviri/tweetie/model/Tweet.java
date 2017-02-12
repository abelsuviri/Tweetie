package com.abelsuviri.tweetie.model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * This class is the tweet table to store tweet information into the database.
 *
 * @author Abel Suviri
 */

public class Tweet extends RealmObject {
    @PrimaryKey private long mId;
    private String mProfilePic;
    private String mUserName;
    private String mUserLink;
    private String mDate;
    private String mTweet;

    public long getId() {
        return mId;
    }

    public void setId(long id) {
        this.mId = id;
    }

    public String getUserName() {
        return mUserName;
    }

    public void setUserName(String userName) {
        mUserName = userName;
    }

    public String getUserLink() {
        return mUserLink;
    }

    public void setUserLink(String userLink) {
        mUserLink = userLink;
    }

    public String getDate() {
        return mDate;
    }

    public void setDate(String date) {
        mDate = date;
    }

    public String getTweet() {
        return mTweet;
    }

    public void setTweet(String tweet) {
        mTweet = tweet;
    }

    public String getProfilePic() {
        return mProfilePic;
    }

    public void setProfilePic(String profilePic) {
        this.mProfilePic = profilePic;
    }
}
