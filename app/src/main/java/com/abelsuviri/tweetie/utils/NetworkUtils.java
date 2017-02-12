package com.abelsuviri.tweetie.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * This class has a network util method.
 *
 * @author Abel Suviri
 */

public class NetworkUtils {
    /**
     * This method checks if the device has or not connection.
     *
     * @param context Context needed to get the connectivity service.
     * @return True if has connection otherwise false.
     */
    public static boolean checkConnected(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager)
            context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        return networkInfo != null && networkInfo.isConnected();
    }
}
