package com.abelsuviri.tweetie.mvp.timeline.presenter;

import android.content.Context;
import android.support.v7.app.AlertDialog;

import com.abelsuviri.tweetie.BuildConfig;
import com.abelsuviri.tweetie.R;
import com.abelsuviri.tweetie.model.Tweet;
import com.abelsuviri.tweetie.mvp.timeline.view.TimelineView;
import com.abelsuviri.tweetie.utils.Constants;
import com.abelsuviri.tweetie.utils.DateUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

/**
 * @author Abel Suviri
 */

public class TimelinePresenter {

    private TimelineView mTimelineView;
    private Twitter mTwitter;
    private CompositeSubscription mCompositeSubscription;

    public TimelinePresenter(TimelineView timelineView) {
        this.mTimelineView = timelineView;
    }

    public void getTimeline(AccessToken token, boolean refresh) {
        if (!refresh) {
            mTimelineView.showProgress();
        }

        mCompositeSubscription = new CompositeSubscription();
        Configuration configuration = new ConfigurationBuilder()
        .setOAuthConsumerKey(BuildConfig.API_KEY)
        .setOAuthConsumerSecret(BuildConfig.API_SECRET)
        .setOAuthAccessToken(token.getToken())
        .setOAuthAccessTokenSecret(token.getTokenSecret())
        .build();

        TwitterFactory factory = new TwitterFactory(configuration);
        mTwitter = factory.getInstance();

        mCompositeSubscription.add(Observable.create((Observable.OnSubscribe<List<Status>>)
            subscriber -> {
                try {
                    subscriber.onNext(mTwitter.getHomeTimeline());
                    subscriber.onCompleted();
                } catch (TwitterException e) {
                    subscriber.onError(e);
                }
            })
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(statuses -> {
                mTimelineView.showTweetList(statuses);
                mTimelineView.hideProgress();
            }, error -> {
                mTimelineView.hideProgress();
                mTimelineView.onError(error.getMessage());
            })
        );
    }

    public void getAnotherTimeline(long id, boolean refresh) {
        if (!refresh) {
            mTimelineView.showProgress();
        }

        mCompositeSubscription.add(Observable.create((Observable.OnSubscribe<List<Status>>)
            subscriber -> {
                try {
                    subscriber.onNext(mTwitter.getUserTimeline(id));
                    subscriber.onCompleted();
                } catch (TwitterException e) {
                    subscriber.onError(e);
                }
            })
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(statuses -> {
                mTimelineView.showTweetList(statuses);
                mTimelineView.hideProgress();
            }, error -> {
                mTimelineView.hideProgress();
                mTimelineView.onError(error.getMessage());
            })
        );
    }

    public void markAsFavourite(boolean setFav, long tweetId, AccessToken token, long userId) {
        Observable.create((Observable.OnSubscribe<String>) subscriber -> {
            try {
                if (setFav) {
                    mTwitter.createFavorite(tweetId);
                } else {
                    mTwitter.destroyFavorite(tweetId);
                }

                if (token != null) {
                    getTimeline(token, true);
                } else {
                    getAnotherTimeline(userId, true);
                }
            } catch (TwitterException e) {
                mTimelineView.onError(e.getMessage());
            }
        })
        .subscribeOn(Schedulers.newThread())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe();
    }

    public void closeDialog(Context context) {
        new AlertDialog.Builder(context)
            .setMessage(context.getString(R.string.closeDialogText))
            .setPositiveButton(context.getString(R.string.logout), (dialog, which) -> {
                mTimelineView.logoutUser();
            })
            .setNegativeButton(context.getString(R.string.close), ((dialog, which) -> {
                mTimelineView.closeApp();
            })).show();
    }

    public void saveFavs(Status status, Context context) {
        Tweet tweet = new Tweet();
        tweet.setId(status.getId());
        tweet.setProfilePic(status.getUser().getOriginalProfileImageURLHttps());
        tweet.setUserName(status.getUser().getName());
        tweet.setUserLink(String.format(Locale.getDefault(), context.getString(R.string.user),
            status.getUser().getScreenName()));
        tweet.setDate(DateUtils.getTweetDate(status.getCreatedAt()));
        tweet.setTweet(status.getText());

        Realm realm = Realm.getDefaultInstance();

        try {
            realm.beginTransaction();
            realm.copyToRealm(tweet);
            realm.commitTransaction();
        } catch (Exception e) {
            //That means the item exits in the database so we close pending transaction.
        }

        realm.close();
    }

    public void loadFavs(Context context) {
        Realm realm = Realm.getDefaultInstance();
        RealmQuery<Tweet> query = realm.where(Tweet.class);
        RealmResults<Tweet> result = query.findAll();

        List<Tweet> tweets = new ArrayList<>();
        tweets.addAll(result);

        if (tweets.size() > 0) {
            mTimelineView.showFavTweets(tweets);
        } else {
            mTimelineView.onError(context.getString(R.string.offline));
        }
    }

    public void deleteFav(Status status) {
        Realm realm = Realm.getDefaultInstance();

        RealmQuery<Tweet> query = realm.where(Tweet.class).equalTo(Constants.ID, status.getId());
        RealmResults<Tweet> results = query.findAll();
        realm.executeTransaction(realm1 -> {
            results.deleteAllFromRealm();
        });

        realm.close();
    }
}
