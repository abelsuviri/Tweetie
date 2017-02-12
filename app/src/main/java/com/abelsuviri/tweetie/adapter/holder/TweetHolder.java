package com.abelsuviri.tweetie.adapter.holder;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.abelsuviri.tweetie.R;
import com.abelsuviri.tweetie.model.Tweet;
import com.abelsuviri.tweetie.utils.DateUtils;
import com.abelsuviri.tweetie.utils.PictureDownloader;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import twitter4j.Status;

/**
 * @author Abel Suviri
 */

public class TweetHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.profilePicture)
    ImageView mProfilePicture;

    @BindView(R.id.profileName)
    TextView mProfileName;

    @BindView(R.id.profileLink)
    TextView mProfileLink;

    @BindView(R.id.publishDate)
    TextView mPublishDate;

    @BindView(R.id.tweetText)
    TextView mTweetText;

    @BindView(R.id.favButton)
    ImageButton mFavButton;

    private Context mContext;
    private Status mStatus;
    private HolderInterface mHolderInterface;

    public TweetHolder(View itemView, Context context, HolderInterface holderInterface) {
        super(itemView);
        this.mContext = context;
        this.mHolderInterface = holderInterface;

        ButterKnife.bind(this, itemView);
    }

    public void bindTweets(Status status) {
        mStatus = status;
        mProfileName.setText(status.getUser().getName());
        mProfileLink.setText(String.format(Locale.getDefault(), mContext.getString(R.string.user),
            status.getUser().getScreenName()));
        mPublishDate.setText(DateUtils.getTweetDate(status.getCreatedAt()));
        mTweetText.setText(status.getText());

        PictureDownloader.downloadPicture(mContext,
            status.getUser().getOriginalProfileImageURLHttps(), mProfilePicture);

        if (mStatus.isFavorited()) {
            mFavButton.setImageResource(R.drawable.ic_favorite_enable);
            mHolderInterface.onSaveFav(status);
        } else {
            mFavButton.setImageResource(R.drawable.ic_favorite_disabled);
            mHolderInterface.onDeleteFav(status);
        }
    }

    public void bindTweets(Tweet tweet) {
        mProfileName.setText(tweet.getUserName());
        mProfileLink.setText(tweet.getUserLink());
        mPublishDate.setText(tweet.getDate());
        mTweetText.setText(tweet.getTweet());

        PictureDownloader.downloadPicture(mContext, tweet.getProfilePic(), mProfilePicture);
    }

    @OnClick(R.id.profilePicture)
    public void onProfileClick() {
        if (mStatus != null) {
            mHolderInterface.onProfileClick(mStatus.getUser().getId());
        }
    }

    @OnClick(R.id.profileName)
    public void onNameClick() {
        onProfileClick();
    }

    @OnClick(R.id.profileLink)
    public void onUserClick() {
        onProfileClick();
    }

    @OnClick(R.id.tweetText)
    public void onTweetClick() {
        if (mStatus != null) {
            mHolderInterface.onTweetClick(mStatus);
        }
    }

    @OnClick(R.id.favButton)
    public void onFavClick() {
        mHolderInterface.onFavClick(!mStatus.isFavorited(), mStatus.getId());
    }
}
