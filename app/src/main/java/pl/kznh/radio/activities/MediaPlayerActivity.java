package pl.kznh.radio.activities;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.concurrent.TimeUnit;

import pl.kznh.radio.R;
import pl.kznh.radio.services.RecordPlayerService;
import pl.kznh.radio.utils.Constants;
import pl.kznh.radio.utils.TypefaceSpan;

public class MediaPlayerActivity extends AppCompatActivity implements View.OnClickListener, SeekBar.OnSeekBarChangeListener{

    private String mTitle;

    private String mSpeaker;

    private String mUrl;

    private int mLength;

    private TextView mErrorTextVew;

    private ImageView mProgressView;

    private RecordPlayerService mService;

    private BroadcastReceiver mReceiver;

    private TextView mTitleView;

    private TextView mSpeakerView;

    private TextView mProgressTimeView;

    private TextView mLengthView;

    private ImageButton mPlayPauseButton;

    private ImageButton mForwardButton;

    private ImageButton mBackwardButton;

    private ImageButton mVolumeUpButton;

    private ImageButton mVolumeDownButton;

    private SeekBar mSeekBar;

    private Toast mVolumeToast;

    private ServiceConnection mServiceConnection;

    private NotificationManager mNotificationManager;

    private boolean mIsRadio;

    private Button mChooseRadioButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_player);
        setActionBarTitle(R.string.title_activity_media_player);
        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        settingRecordParameters();

        mErrorTextVew = (TextView) findViewById(R.id.errorTextView);
        mProgressView = (ImageView) findViewById(R.id.progressView);
        mTitleView = (TextView) findViewById(R.id.titleView);
        mSpeakerView = (TextView) findViewById(R.id.speakerView);
        mProgressTimeView = (TextView) findViewById(R.id.progressTime);
        mLengthView = (TextView) findViewById(R.id.lengthView);
        mPlayPauseButton = (ImageButton) findViewById(R.id.playButton);
        mForwardButton = (ImageButton) findViewById(R.id.forwardButton);
        mBackwardButton = (ImageButton) findViewById(R.id.backwardButton);
        mVolumeDownButton = (ImageButton) findViewById(R.id.volumeDownButton);
        mVolumeUpButton = (ImageButton) findViewById(R.id.volumeUPButton);
        mSeekBar = (SeekBar) findViewById(R.id.seekBar);
        mChooseRadioButton = (Button) findViewById(R.id.changeRadioButton);

        mErrorTextVew.setTypeface(Constants.robotoCondensed);
        mTitleView.setTypeface(Constants.robotoCondensed);
        mSpeakerView.setTypeface(Constants.robotoCondensed);
        mProgressTimeView.setTypeface(Constants.robotoCondensed);
        mLengthView.setTypeface(Constants.robotoCondensed);
        mChooseRadioButton.setTypeface(Constants.robotoCondensed);



        setProgressBarVisible(true);
        mPlayPauseButton.setOnClickListener(this);

        mSeekBar.setOnSeekBarChangeListener(this);

        mChooseRadioButton.setOnClickListener(this);

        mTitleView.setText(mTitle);
        mSpeakerView.setText(mSpeaker);

        mVolumeDownButton.setOnClickListener(this);
        mVolumeUpButton.setOnClickListener(this);
        mForwardButton.setOnClickListener(this);
        mBackwardButton.setOnClickListener(this);

        configureBroadcastReceiver();

        if (RecordPlayerService.isServiceRunning){
            //Log.i("MEDIA PLAYER SERVICE", "is running");
            bindPlayerService();
        } else {
            //Log.i("MEDIA PLAYER SERVICE", "is not running");
            createMediaPlayerService();
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
            getMenuInflater().inflate(R.menu.media_player_menu, menu);
            return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_close) {
            mService.stopMediaPlayer();
            mService.stopSelf();
            mNotificationManager.cancelAll();
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void handleBufferingUpdate(Bundle bundle) {
        final int percent = bundle.getInt(Constants.MEDIA_PLAYER_BUFFERING_PERCENTAGE);
        mSeekBar.setSecondaryProgress((int) (mService.getLength() * (percent / 100.0f)));
    }

    private void handleError(Bundle bundle) {
        final int what = bundle.getInt(Constants.MEDIA_PLAYER_RESULT_WHAT);
        final int extra = bundle.getInt(Constants.MEDIA_PLAYER_RESULT_EXTRA);
        if (what == 0 && extra == 0) {
            // everything is ok
            mLength = mService.getLength();
            setProgressBarVisible(false);
            setViewsVisible(true);
            mSeekBar.setSecondaryProgress(0);
            mErrorTextVew.setVisibility(View.INVISIBLE);
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mSeekBar.setProgress(mService.getCurrentPosition());
                    if (RecordPlayerService.isServiceRunning) handler.postDelayed(this, 1000);
                }
            }, 1000);
            if (mService.isMediaPlayerPlaying()){
                mPlayPauseButton.setImageResource(R.drawable.icon_pause);
            }
        } else {
            // there was an error
            mErrorTextVew.setVisibility(View.VISIBLE);
            setProgressBarVisible(false);
            mTitleView.setVisibility(View.INVISIBLE);
            mSpeakerView.setVisibility(View.INVISIBLE);
            mErrorTextVew.setText(getString(R.string.error_unknown));
            mErrorTextVew.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    Toast.makeText(MediaPlayerActivity.this, "Kod błędu: " + "(" + what + ", " + extra + ")", Toast.LENGTH_SHORT).show();
                    return false;
                }
            });
        }
    }

    private void setViewsVisible(boolean shouldBeVisible) {
        if (shouldBeVisible) {
            mTitleView.setVisibility(View.VISIBLE);
            mSpeakerView.setVisibility(View.VISIBLE);
            mPlayPauseButton.setVisibility(View.VISIBLE);
            mVolumeUpButton.setVisibility(View.VISIBLE);
            mVolumeDownButton.setVisibility(View.VISIBLE);
            mBackwardButton.setVisibility(View.GONE);
            mForwardButton.setVisibility(View.GONE);
            mChooseRadioButton.setVisibility(View.VISIBLE);

            if (!mIsRadio) {
                mProgressTimeView.setVisibility(View.VISIBLE);
                mLengthView.setVisibility(View.VISIBLE);
                mSeekBar.setVisibility(View.VISIBLE);
                mBackwardButton.setVisibility(View.VISIBLE);
                mForwardButton.setVisibility(View.VISIBLE);
                mSeekBar.setMax(mLength);
                mLengthView.setText(String.format("%02d:%02d",
                        TimeUnit.MILLISECONDS.toMinutes(mLength),
                        TimeUnit.MILLISECONDS.toSeconds(mLength) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(mLength))
                ));
                mBackwardButton.setVisibility(View.VISIBLE);
                mForwardButton.setVisibility(View.VISIBLE);
                mChooseRadioButton.setVisibility(View.INVISIBLE);
            }
        } else {
            mTitleView.setVisibility(View.INVISIBLE);
            mSpeakerView.setVisibility(View.INVISIBLE);
            mPlayPauseButton.setVisibility(View.INVISIBLE);
            mVolumeDownButton.setVisibility(View.INVISIBLE);
            mVolumeUpButton.setVisibility(View.INVISIBLE);
            mBackwardButton.setVisibility(View.INVISIBLE);
            mForwardButton.setVisibility(View.INVISIBLE);
            mChooseRadioButton.setVisibility(View.INVISIBLE);
            mProgressTimeView.setVisibility(View.INVISIBLE);
            mLengthView.setVisibility(View.INVISIBLE);
            mSeekBar.setVisibility(View.INVISIBLE);
        }
    }



    private void configureBroadcastReceiver() {
        mReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                    Bundle bundle = intent.getExtras();
                    int actionId = bundle.getInt(Constants.MEDIA_PLAYER_ACTION);
                    switch (actionId) {
                        case Constants.ACTION_ON_ERROR:
                            handleError(bundle);
                            break;
                        case Constants.ACTION_ON_BUFFERING_UPDATE:
                            handleBufferingUpdate(bundle);
                            break;
                    }
            }
        };
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mReceiver, new IntentFilter(Constants.NOTIFICATION));
        mNotificationManager.cancelAll();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mReceiver);
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    protected void onStop() {
        super.onStop();
        try {
            unbindService(mServiceConnection);
        } catch (IllegalArgumentException e){
            e.printStackTrace();
        }
        if (RecordPlayerService.isServiceRunning)
        showNotification();
    }

    private void showNotification() {
        NotificationCompat.Builder mBuilder =
                (NotificationCompat.Builder) new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.icon_white)
                        .setLargeIcon(getLargeIcon(this))
                        .setContentTitle(mSpeaker)
                        .setColor(ContextCompat.getColor(this, R.color.primary))
                        .setContentText(mTitle)
                        .setOngoing(true)
                        .setAutoCancel(true);
        Intent resultIntent = new Intent(this, MediaPlayerActivity.class);
        resultIntent.putExtra(Constants.EXTRA_TITLE, mTitle);
        resultIntent.putExtra(Constants.EXTRA_SPEAKER, mSpeaker);
        resultIntent.putExtra(Constants.EXTRA_URL, mUrl);
        resultIntent.putExtra(Constants.EXTRA_IS_RADIO, mIsRadio);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(MediaPlayerActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        mNotificationManager.notify(Constants.NOTIFICATION_ID, mBuilder.build());
    }

    public static Bitmap getLargeIcon(Context context){
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP){
            return BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher);
        } else {
            return null;
        }
    }

    private void createMediaPlayerService() {
        Intent intent = new Intent(this, RecordPlayerService.class);
        intent.putExtra(Constants.EXTRA_URL, mUrl);
        mServiceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                mService = ((RecordPlayerService.MyBinder) service).getService();
                //Log.i("SERVICE", "CONNECTED" + mService.toString());
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {

            }
        };
        bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
        startService(intent);
    }

    private void bindPlayerService() {
        Intent intent = new Intent(this, RecordPlayerService.class);
        intent.putExtra(Constants.EXTRA_URL, mUrl);
        mServiceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                mService = ((RecordPlayerService.MyBinder) service).getService();
                //Log.i("SERVICE", "RECONNECTED" + mService.toString());
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {

            }
        };
        bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
    }

    private void settingRecordParameters() {
        Bundle extras = getIntent().getExtras();
        mTitle = extras.getString(Constants.EXTRA_TITLE);
        mSpeaker = extras.getString(Constants.EXTRA_SPEAKER);
        //mLength = extras.getString(RecordsFragment.EXTRA_LENGTH);
        mUrl = extras.getString(Constants.EXTRA_URL);
        mIsRadio = extras.getBoolean(Constants.EXTRA_IS_RADIO);
    }

    private void setProgressBarVisible(boolean b) {
        if (b) {
            Animation rotateAnimation = AnimationUtils.loadAnimation(this, R.anim.rotate_animation);
            mProgressView.startAnimation(rotateAnimation);
        } else {
            mProgressView.clearAnimation();
            mProgressView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        int currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        if (mVolumeToast != null){
            mVolumeToast.cancel();
        }

        switch(v.getId()){
            case R.id.volumeDownButton:
                if (currentVolume > 0){
                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, --currentVolume, 0);
                }
                showToast(currentVolume, maxVolume);
                break;
            case R.id.volumeUPButton:
                if (currentVolume < maxVolume){
                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, ++currentVolume, 0);
                }
                showToast(currentVolume, maxVolume);
                break;
            case R.id.playButton:
                if (mService.isMediaPlayerPlaying()) {
                    mService.pauseMediaPlayer();
                    mPlayPauseButton.setImageResource(R.drawable.icon_play);
                } else {
                    mService.startMediaPlayer();
                    mPlayPauseButton.setImageResource(R.drawable.icon_pause);
                }
                break;
            case R.id.forwardButton:
                if (mService.isMediaPlayerPlaying()) {
                    mService.seekTo(mService.getCurrentPosition() + 10000);
                    showToast("+");
                }
                break;
            case R.id.backwardButton:
                if (mService.isMediaPlayerPlaying()) {
                    mService.seekTo(mService.getCurrentPosition() - 10000);
                    showToast("-");
                }
                break;
            case R.id.changeRadioButton:
                    createChooseRadioDialog();
                break;
        }
    }

    private void showToast(int currentVolume, int maxVolume) {
        mVolumeToast = Toast.makeText(MediaPlayerActivity.this, getString(R.string.volume) + " " + currentVolume + "/" + maxVolume, Toast.LENGTH_SHORT);
        mVolumeToast.setGravity(Gravity.TOP, 0, 0);
        mVolumeToast.setMargin(0, 0.15f);
        mVolumeToast.show();
    }

    private void showToast(String sign) {
        mVolumeToast = Toast.makeText(MediaPlayerActivity.this, sign + "10 sekund", Toast.LENGTH_SHORT);
        mVolumeToast.setGravity(Gravity.TOP, 0, 0);
        mVolumeToast.setMargin(0, 0.15f);
        mVolumeToast.show();
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        mProgressTimeView.setText(String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(progress),
                TimeUnit.MILLISECONDS.toSeconds(progress) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(progress))));
        if (fromUser) {
            mService.seekTo(progress);
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    private void createChooseRadioDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final String [] radioNames = getResources().getStringArray(R.array.radio_names_array);
        final String [] radioOwners = getResources().getStringArray(R.array.radio_owners_array);
        builder.setItems(radioNames, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String [] urlArray = getResources().getStringArray(R.array.radio_url_array);
                setViewsVisible(false);
                setProgressBarVisible(true);
                mPlayPauseButton.setImageResource(R.drawable.icon_play);
                mSpeaker = radioOwners[which];
                mTitle = radioNames[which];
                mTitleView.setText(mTitle);
                mSpeakerView.setText(mSpeaker);
                mService.releaseMediaPlayer();
                mService.setUrl(urlArray[which]);
                mService.initializePlayer();

            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void setActionBarTitle (int titleRes) {
        SpannableString s = new SpannableString(getString(titleRes));
        s.setSpan(new TypefaceSpan(this, Constants.FONT_NAME), 0, s.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        ((AppCompatActivity) this).getSupportActionBar().setTitle(s);
    }
}
