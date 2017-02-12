package com.abelsuviri.tweetie.base;

/**
 * @author Abel Suviri
 */

public interface BaseView {
    void showProgress();

    void hideProgress();

    void onError(String error);
}
