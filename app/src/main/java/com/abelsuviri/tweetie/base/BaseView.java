package com.abelsuviri.tweetie.base;

/**
 * Base interface where we have all common methods used at different activities.
 *
 * @author Abel Suviri
 */

public interface BaseView {
    void showProgress();

    void hideProgress();

    void onError(String error);
}
