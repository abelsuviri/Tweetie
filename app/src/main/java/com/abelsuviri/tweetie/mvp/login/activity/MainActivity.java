package com.abelsuviri.tweetie.mvp.login.activity;

import android.annotation.TargetApi;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.view.animation.AnimationUtils;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ViewFlipper;

import com.abelsuviri.tweetie.R;
import com.abelsuviri.tweetie.base.BaseActivity;
import com.abelsuviri.tweetie.mvp.login.presenter.LoginPresenter;
import com.abelsuviri.tweetie.mvp.login.view.LoginView;
import com.abelsuviri.tweetie.mvp.timeline.activity.TimelineActivity;
import com.abelsuviri.tweetie.utils.Constants;
import com.abelsuviri.tweetie.utils.NetworkUtils;
import com.google.gson.Gson;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import twitter4j.auth.AccessToken;

/**
 * The MainActivity contains the login into twitter.
 *
 * @author Abel Suviri
 */

public class MainActivity extends BaseActivity implements LoginView {
    @BindView(R.id.flipper)
    ViewFlipper mViewFlipper;

    @BindView(R.id.authenticationWebView)
    WebView mWebView;

    private LoginPresenter mLoginPresenter;
    private boolean isWebVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        mLoginPresenter = new LoginPresenter(this, mSharedPreferences);
        mLoginPresenter.checkRememberedUser();
    }

    /**
     * If authentication page is shown, back event will close the authentication page and you the
     * initial login screen.
     */
    @Override
    public void onBackPressed() {
        if (isWebVisible) {
            mViewFlipper.showPrevious();
            isWebVisible = false;
        } else {
            super.onBackPressed();
        }
    }

    /**
     * If user did login we open the timeline.
     * @param token User token needed to use Twitter API.
     * @param secretToken User secret token needed to use Twitter API.
     */
    @Override
    public void onRememberedUser(String token, String secretToken) {
        navigateToTimeline(new AccessToken(token, secretToken));
    }

    /**
     * Open the authentication screen on a WebView.
     * @param twitterUrl This is the url of the authentication page.
     */
    @Override
    public void onLaunchAuthentication(String twitterUrl) {
        showAuthentication(twitterUrl);
    }

    /**
     * Callback to open the timeline when login was success.
     * @param token Access token needed to retrieve the tweets.
     */
    @Override
    public void onLoginSuccess(AccessToken token) {
        navigateToTimeline(token);
    }

    /**
     * When there is an error we show it in a SnackBar.
     * @param error
     */
    @Override
    public void onError(String error) {
        Snackbar.make(mViewFlipper, String.format(Locale.getDefault(),
            getString(R.string.requestError), error), Snackbar.LENGTH_INDEFINITE)
            .setAction(getString(R.string.retry), OnClick -> {
                mLoginPresenter.getAuthentication();
            }).show();
    }

    /**
     * Shows thw progress dialog.
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
    }

    @OnClick(R.id.loginButton)
    public void onLoginButtonClick() {
        mLoginPresenter.getAuthentication();
    }

    /**
     * This method opens a WebView with a website to introduce our Twitter credentials to login into
     * this app.
     * @param twitterUrl This is the url to open the authentication page.
     */
    private void showAuthentication(String twitterUrl) {
        mViewFlipper.setInAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in));
        mViewFlipper.setOutAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_out));
        mViewFlipper.showNext();

        mWebView.loadUrl(twitterUrl);
        mWebView.setWebViewClient(new WebViewClient() {

            /*Deprecated method. As getUrl() method was added at API 21 I am overriding both methods
             to keep compatibility with older Android versions.*/
            @SuppressWarnings("deprecation")
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                isWebVisible = false;
                mViewFlipper.showPrevious();
                mLoginPresenter.getToken(Uri.parse(url));
                return super.shouldOverrideUrlLoading(view, url);
            }

            @TargetApi(Build.VERSION_CODES.N)
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                isWebVisible = false;
                mViewFlipper.showPrevious();
                mLoginPresenter.getToken(request.getUrl());
                return super.shouldOverrideUrlLoading(view, request);
            }

            @Override
            public void onPageFinished(WebView webView, String url) {
                super.onPageFinished(webView, url);
                isWebVisible = true;
                hideProgress();
            }
        });
    }

    /**
     * This method do the navigation to TimelineActivity.
     * @param token
     */
    private void navigateToTimeline(AccessToken token) {
        Intent intent = new Intent(this, TimelineActivity.class);
        if (NetworkUtils.checkConnected(this)) {
            intent.putExtra(Constants.ACCESS_TOKEN, new Gson().toJson(token));
        }

        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        overridePendingTransition(R.anim.left_to_right, R.anim.center_to_left);
        finish();
    }
}
