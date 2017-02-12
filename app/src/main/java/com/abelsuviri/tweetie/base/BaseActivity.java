package com.abelsuviri.tweetie.base;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.abelsuviri.tweetie.R;
import com.abelsuviri.tweetie.utils.Constants;

import io.realm.Realm;

/**
 * Base activity where we have all common methods used at different activities.
 *
 * @author Abel Suviri
 */

public class BaseActivity extends AppCompatActivity {
    private ProgressDialog mProgressDialog;
    public SharedPreferences mSharedPreferences;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSharedPreferences = getSharedPreferences(Constants.APP_NAME, 0);
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage(getString(R.string.loading));
        Realm.init(this);
    }

    public void showProgressDialog() {
        mProgressDialog.show();
    }

    public void hideProgressDialog() {
        mProgressDialog.dismiss();
    }
}
