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

    @Override
    public void onBackPressed() {
        if (isSomeoneTimeline) {
            isSomeoneTimeline = false;
            loadTweets(false);
        } else {
            mTimelinePresenter.closeDialog(this);
        }
    }

    @Override
    public void showProgress() {
        showProgressDialog();
    }

    @Override
    public void hideProgress() {
        hideProgressDialog();
        mRefreshLayout.setRefreshing(false);
    }

    @Override
    public void showTweetList(List<Status> statuses) {
        mTimelineList.setAdapter(new TweetAdapter(statuses, this, this));
    }

    @Override
    public void showFavTweets(List<Tweet> tweets) {
        mTimelineList.setAdapter(new TweetAdapter(tweets, this, this, true));
    }

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

    @Override
    public void onProfileClick(long userId) {
        mSomeoneId = userId;
        isSomeoneTimeline = true;
        mTimelinePresenter.getAnotherTimeline(userId, false);
    }

    @Override
    public void onTweetClick(Status status) {
        Intent intent = new Intent(this, TweetActivity.class);
        intent.putExtra(Constants.STATUS, status);
        startActivity(intent);
        overridePendingTransition(R.anim.from_bottom, R.anim.hold);
    }

    @Override
    public void onFavClick(boolean setFav, long tweetId) {
        if (isSomeoneTimeline) {
            mTimelinePresenter.markAsFavourite(setFav, tweetId, null, mSomeoneId);
        } else {
            mTimelinePresenter.markAsFavourite(setFav, tweetId, mAccessToken, 0);
        }
    }

    @Override
    public void logoutUser() {
        mSharedPreferences.edit().clear().apply();
        startActivity(new Intent(this, MainActivity.class));
        overridePendingTransition(R.anim.right_to_left, R.anim.center_to_left);
        finish();
    }

    @Override
    public void closeApp() {
        finish();
    }

    private void setRecyclerView() {
        mTimelineList.setLayoutManager(new LinearLayoutManager(this,
            LinearLayoutManager.VERTICAL, false));
        DividerItemDecoration dividerItemDecoration =
            new DividerItemDecoration(mTimelineList.getContext(), LinearLayoutManager.VERTICAL);
        mTimelineList.addItemDecoration(dividerItemDecoration);
    }

    @Override
    public void onSaveFav(Status status) {
        mTimelinePresenter.saveFavs(status, this);
    }

    @Override
    public void onDeleteFav(Status status) {
        mTimelinePresenter.deleteFav(status);
    }

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
