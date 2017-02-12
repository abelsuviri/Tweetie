package com.abelsuviri.tweetie.mvp.tweet.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.abelsuviri.tweetie.R;
import com.abelsuviri.tweetie.utils.Constants;
import com.abelsuviri.tweetie.utils.DateUtils;
import com.abelsuviri.tweetie.utils.PictureDownloader;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import twitter4j.MediaEntity;
import twitter4j.Status;

public class TweetActivity extends AppCompatActivity {
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

    @BindView(R.id.tweetPicture)
    ImageView mTweetPic;

    private Status mStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tweet);

        ButterKnife.bind(this);

        mStatus = (Status) getIntent().getSerializableExtra(Constants.STATUS);

        fillLayout();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.hold, R.anim.to_bottom);
    }

    private void fillLayout() {
        mProfileName.setText(mStatus.getUser().getName());
        mProfileLink.setText(String.format(Locale.getDefault(), getString(R.string.user),
            mStatus.getUser().getScreenName()));
        mPublishDate.setText(DateUtils.getTweetDate(mStatus.getCreatedAt()));
        mTweetText.setText(mStatus.getText());

        PictureDownloader.downloadPicture(this, mStatus.getUser().getOriginalProfileImageURLHttps(),
            mProfilePicture);

        for (MediaEntity mediaEntity : mStatus.getMediaEntities()) {
            PictureDownloader.downloadPicture(this, mediaEntity.getMediaURLHttps(), mTweetPic);
        }

        getSupportActionBar().setTitle(mProfileLink.getText().toString());
    }
}
