package com.abelsuviri.tweetie.utils;

import android.content.Context;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

/**
 * This class is used to download pictures.
 *
 * @author Abel Suviri
 */

public class PictureDownloader {
    /**
     * This method downloads pictures and sets to a provided ImageView.
     *
     * @param context Context needed to create the Picasso instance.
     * @param url Url of the picture to download.
     * @param view Reference to the ImageView where we are going to load the picture.
     */
    public static void downloadPicture(Context context, String url, ImageView view) {
        Picasso.with(context).load(url).into(view);
    }
}
