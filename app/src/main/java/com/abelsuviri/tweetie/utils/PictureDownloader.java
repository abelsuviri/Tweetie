package com.abelsuviri.tweetie.utils;

import android.content.Context;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

/**
 * @author Abel Suviri
 */

public class PictureDownloader {
    public static void downloadPicture(Context context, String url, ImageView view) {
        Picasso.with(context).load(url).into(view);
    }
}
