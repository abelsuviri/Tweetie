package com.abelsuviri.tweetie.mvp.login.presenter;

import android.content.SharedPreferences;
import android.net.Uri;

import com.abelsuviri.tweetie.BuildConfig;
import com.abelsuviri.tweetie.mvp.login.view.LoginView;
import com.abelsuviri.tweetie.utils.Constants;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;

/**
 * This is the class with all the login for MainActivity.
 *
 * @author Abel Suviri
 */

public class LoginPresenter {
    private LoginView mLoginView;
    private Twitter mTwitter;
    private RequestToken mRequestToken;
    private SharedPreferences mSharedPreferences;
    private CompositeSubscription mCompositeSubscription;

    /**
     * Presenter constructor.
     *
     * @param loginView Reference to the login interface.
     * @param preferences SharedPreferences reference.
     */
    public LoginPresenter(LoginView loginView, SharedPreferences preferences) {
        this.mLoginView = loginView;
        this.mSharedPreferences = preferences;
    }

    /**
     * If the user did login we have the tokens stored at SharedPreferences. This method retrieves
     * those tokens and sends to MainActivity if user has to do login or it should redirect to the
     * timeline.
     */
    public void checkRememberedUser() {
        String userToken = mSharedPreferences.getString(Constants.USER_TOKEN, null);
        String secretToken = mSharedPreferences.getString(Constants.USER_SECRET_TOKEN, null);
        if (userToken != null) {
            mLoginView.onRememberedUser(userToken, secretToken);
        }
    }

    /**
     * This method makes the call to the API to get the authentication url for the API key of the
     * app.
     */
    public void getAuthentication() {
        mLoginView.showProgress();
        mCompositeSubscription = new CompositeSubscription();
        mTwitter = new TwitterFactory().getInstance();
        mTwitter.setOAuthConsumer(BuildConfig.API_KEY, BuildConfig.API_SECRET);

        mCompositeSubscription.add(Observable.create((Observable.OnSubscribe<RequestToken>)
            subscriber -> {
                try {
                    subscriber.onNext(mTwitter.getOAuthRequestToken());
                    subscriber.onCompleted();
                } catch (TwitterException e) {
                    subscriber.onError(e);
                }
            })
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(requestToken -> {
                mRequestToken = requestToken;
                mLoginView.onLaunchAuthentication(requestToken.getAuthenticationURL());
            }, error -> {
                mLoginView.hideProgress();
                mLoginView.onError(error.getMessage());
            })
        );
    }

    /**
     * This method gets the user tokens to authenticate that user.
     * Login method returns the user tokens we are going to store into the preferences to remember
     * the user and go straight to the timeline next time the user opens the app.
     *
     * @param twitterUri This is the callback url returned by the
     */
    public void getToken(Uri twitterUri) {
        if (twitterUri != null && twitterUri.toString().startsWith(Constants.TWITTER_URL)) {
            String verifier = twitterUri.getQueryParameter(Constants.VERIFIER);

            mCompositeSubscription.add(Observable.create((Observable.OnSubscribe<AccessToken>)
                subscriber -> {
                    try {
                        subscriber.onNext(mTwitter.getOAuthAccessToken(mRequestToken, verifier));
                        subscriber.onCompleted();
                    } catch (TwitterException e) {
                        subscriber.onError(e);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(accessToken -> {
                    mSharedPreferences.edit()
                        .putString(Constants.USER_TOKEN, accessToken.getToken())
                        .putString(Constants.USER_SECRET_TOKEN, accessToken.getTokenSecret())
                        .apply();
                    mLoginView.onLoginSuccess(accessToken);
                }, error -> mLoginView.onError(error.getMessage()))
            );
        }
    }
}
