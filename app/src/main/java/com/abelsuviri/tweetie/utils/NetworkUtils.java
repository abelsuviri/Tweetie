package com.abelsuviri.tweetie.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * @author Abel Suviri
 */

public class NetworkUtils {
    public static boolean checkConnected(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager)
            context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        return networkInfo != null && networkInfo.isConnected();
    }
}
