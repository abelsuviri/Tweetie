package com.abelsuviri.tweetie.mvp.timeline.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.abelsuviri.tweetie.R;
import com.abelsuviri.tweetie.adapter.TweetAdapter;
import com.abelsuviri.tweetie.adapter.holder.HolderInterface;
import com.abelsuviri.tweetie.base.BaseActivity;
import com.abelsuviri.tweetie.model.Tweet;
import com.abelsuviri.tweetie.mvp.login.activity.MainActivity;
import com.abelsuviri.tweetie.mvp.timeline.presenter.TimelinePresenter;
import com.abelsuviri.tweetie.mvp.timeline.view.TimelineView;
import com.abelsuviri.tweetie.mvp.tweet.activity.TweetActivity;
import com.abelsuviri.tweetie.utils.Constants;
import com.abelsuviri.tweetie.utils.NetworkUtils;
import com.google.gson.Gson;

import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import twitter4j.Status;
import twitter4j.auth.AccessToken;

/**
 * The TimelineActivity shows the user or another user timeline.
 *
 * @author Abel Suviri
 */

public class TimelineActivity extends BaseActivity implements TimelineView, HolderInterface {
    @BindView(R.id.refreshLayout)
    SwipeRefreshLayout mRefreshLayout;

    @BindView(R.id.timelineList)
    RecyclerView mTimelineList;

    private TimelinePresenter mTimelinePresenter;
    private AccessToken mAccessToken;
    private long mSomeoneId;
    private boolean isSomeoneTimeline = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);

        ButterKnife.bind(this);
        mTimelinePresenter = new TimelinePresenter(this);

        mRefreshLayout.setOnRefreshListener(() -> {
            loadTweets(true);
            mRefreshLayout.setEnabled(NetworkUtils.checkConnected(this));
        });

        setRecyclerView();
        loadTweets(false);
    }

    /**
     * If another user timeline is displayed the back event will load the user timeline. If user
     * timeline is displayed the back event will show a dialog to just close the app or to logout.
     */
    @Override
    public void onBackPressed() {
        if (isSomeoneTimeline) {
            isSomeoneTimeline = false;
            loadTweets(false);
        } else {
            mTimelinePresenter.closeDialog(this);
        }
    }

    /**
     * Shows the progress dialog.
     */
    @Override
    public void showProgress() {
        showProgressDialog();
    }

    /**
     * Hides the progress dialog.
     */
    @Override
    public void hideProgress() {
        hideProgressDialog();
        mRefreshLayout.setRefreshing(false);
    }

    /**
     * This method sets the adapter to the RecyclerView. That means we are printing the tweets
     * timeline.
     *
     * @param statuses This is a list of all the tweets.
     */
    @Override
    public void showTweetList(List<Status> statuses) {
        mTimelineList.setAdapter(new TweetAdapter(statuses, this, this));
    }

    /**
     * This method sets the adapter to the RecyclerView when device is disconnected from the network
     * and print all the favourites tweets stored at the database.
     *
     * @param tweets This is a list of all the favourite tweets stored at the database.
     */
    @Override
    public void showFavTweets(List<Tweet> tweets) {
        mTimelineList.setAdapter(new TweetAdapter(tweets, this, this, true));
    }

    /**
     * When an error occurs we show an error message in a SnackBar.
     *
     * @param error This is the error message.
     */
    @Override
    public void onError(String error) {
        if (mAccessToken != null) {
            Snackbar.make(mTimelineList, String.format(Locale.getDefault(),
                getString(R.string.requestError), error), Snackbar.LENGTH_INDEFINITE)
                .setAction(getString(R.string.retry), OnClick -> {
                    loadTweets(false);
                }).show();
        } else {
            Snackbar.make(mTimelineList, String.format(Locale.getDefault(),
                getString(R.string.requestError), error), Snackbar.LENGTH_INDEFINITE).show();
        }
    }

    /**
     * This is the callback from when a profile was clicked and we are going to open the timeline of
     * this profile.
     *
     * @param userId This is the user id of the profile clicked.
     */
    @Override
    public void onProfileClick(long userId) {
        mSomeoneId = userId;
        isSomeoneTimeline = true;
        mTimelinePresenter.getAnotherTimeline(userId, false);
    }

    /**
     * This is the callback from when a tweet was clicked and we are going to open this tweet in a
     * screen only for it.
     *
     * @param status This is all the tweet content.
     */
    @Override
    public void onTweetClick(Status status) {
        Intent intent = new Intent(this, TweetActivity.class);
        intent.putExtra(Constants.STATUS, status);
        startActivity(intent);
        overridePendingTransition(R.anim.from_bottom, R.anim.hold);
    }

    /**
     * This is the callback from when we click on the fav icon.
     *
     * @param setFav This boolean returns if we will set the tweet as favourite or not.
     * @param tweetId This is the tweet id.
     */
    @Override
    public void onFavClick(boolean setFav, long tweetId) {
        if (isSomeoneTimeline) {
            mTimelinePresenter.markAsFavourite(setFav, tweetId, null, mSomeoneId);
        } else {
            mTimelinePresenter.markAsFavourite(setFav, tweetId, mAccessToken, 0);
        }
    }

    /**
     * When a user will logout we clear the preferences to do not have the users tokens next time we
     * open the app and avoid redirect to the timeline.
     */
    @Override
    public void logoutUser() {
        mSharedPreferences.edit().clear().apply();
        startActivity(new Intent(this, MainActivity.class));
        overridePendingTransition(R.anim.right_to_left, R.anim.center_to_left);
        finish();
    }

    /**
     * It just close the Activity.
     */
    @Override
    public void closeApp() {
        finish();
    }

    /**
     * Here we are setting the layout manager and the dividers for the RecyclerView.
     */
    private void setRecyclerView() {
        mTimelineList.setLayoutManager(new LinearLayoutManager(this,
            LinearLayoutManager.VERTICAL, false));
        DividerItemDecoration dividerItemDecoration =
            new DividerItemDecoration(mTimelineList.getContext(), LinearLayoutManager.VERTICAL);
        mTimelineList.addItemDecoration(dividerItemDecoration);
    }

    /**
     * When we have a favourite tweet we save it into our database.
     *
     * @param status This is all the tweet information.
     */
    @Override
    public void onSaveFav(Status status) {
        mTimelinePresenter.saveFavs(status, this);
    }

    /**
     * When we have a tweet that was favourite but now we have unchecked it the we remove it from
     * the database.
     *
     * @param status This is all the tweet information.
     */
    @Override
    public void onDeleteFav(Status status) {
        mTimelinePresenter.deleteFav(status);
    }

    /**
     * This method makes the call to get all the tweets of the timeline.
     *
     * @param refresh If we are refreshing the list it will shows only the SwipeRefreshLayout
     *                progress instead the progress dialog.
     */
    private void loadTweets(boolean refresh) {
        if (mAccessToken == null) {
            if (getIntent().hasExtra(Constants.ACCESS_TOKEN)) {
                String tokenExtra = getIntent().getStringExtra(Constants.ACCESS_TOKEN);
                mAccessToken = new Gson().fromJson(tokenExtra, AccessToken.class);
            } else {
                mTimelinePresenter.loadFavs(this);
            }
        }

        if (NetworkUtils.checkConnected(this)) {
            if (isSomeoneTimeline) {
                mTimelinePresenter.getAnotherTimeline(mSomeoneId, refresh);
            } else {
                mTimelinePresenter.getTimeline(mAccessToken, refresh);
            }
        }
    }
}
