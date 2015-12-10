package pl.kznh.radio.activities;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

import pl.kznh.radio.R;
import pl.kznh.radio.fragments.RecordsFragment;

public class MediaPlayerActivity extends AppCompatActivity {

    private static final String BASE_URL = "http://kznh.pl/";

    private String mTitle;

    private String mSpeaker;

    private String mLength;

    private String mUrl;

    private TextView mErrorTextVew;

    private ImageView mProgressView;

    private MediaPlayer mMediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_player);
        settingRecordParameters();
        mErrorTextVew = (TextView) findViewById(R.id.errorTextView);
        mProgressView = (ImageView) findViewById(R.id.progressView);

        initializePlayer();
    }

    private void settingRecordParameters() {
        Bundle extras = getIntent().getExtras();
        mTitle = extras.getString(RecordsFragment.EXTRA_TITLE);
        mSpeaker = extras.getString(RecordsFragment.EXTRA_SPEAKER);
        mLength = extras.getString(RecordsFragment.EXTRA_LENGTH);
        mUrl = extras.getString(RecordsFragment.EXTRA_URL);
    }

    private void initializePlayer() {
        mErrorTextVew.setText("");
        setProgressBarVisible(true);
        mMediaPlayer = new MediaPlayer();
        try
        {
            mMediaPlayer.setDataSource(BASE_URL + mUrl);
        }
        catch (IllegalArgumentException | IllegalStateException | IOException illegalargumentexception)
        {
            illegalargumentexception.printStackTrace();
        }
        mMediaPlayer.prepareAsync();
        mMediaPlayer.setOnPreparedListener(new android.media.MediaPlayer.OnPreparedListener() {
            public void onPrepared(MediaPlayer mediaplayer) {
                setProgressBarVisible(false);
            }
        });
        mMediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, final int what, final int extra) {
                setProgressBarVisible(false);
                mErrorTextVew.setText(getString(R.string.error_unknown));
                mErrorTextVew.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        Toast.makeText(MediaPlayerActivity.this, "Kod błędu: " + "(" + what + ", " + extra + ")", Toast.LENGTH_SHORT).show();
                        return false;
                    }
                });
                return true;
            }
        });
    }

    private void setProgressBarVisible(boolean b) {
        if (b) {
            Animation rotateAnimation = AnimationUtils.loadAnimation(this, R.anim.rotate_animation);
            mProgressView.startAnimation(rotateAnimation);
        } else {
            mProgressView.setVisibility(View.INVISIBLE);
        }
    }
}
