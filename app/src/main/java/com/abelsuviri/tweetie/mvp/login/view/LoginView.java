package com.abelsuviri.tweetie.mvp.login.view;

import com.abelsuviri.tweetie.base.BaseView;

import twitter4j.auth.AccessToken;

/**
 * This is the interface to communicate LoginPresenter and MainActivity
 *
 * @author Abel Suviri
 */

public interface LoginView extends BaseView {
    void onRememberedUser(String token, String secretToken);

    void onLaunchAuthentication(String twitterUrl);

    void onLoginSuccess(AccessToken accessToken);
}
