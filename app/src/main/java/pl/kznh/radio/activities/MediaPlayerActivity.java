package pl.kznh.radio.activities;

import android.app.Notification;
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
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.concurrent.TimeUnit;

import pl.aprilapps.switcher.Switcher;
import pl.kznh.radio.R;
import pl.kznh.radio.services.RecordPlayerService;
import pl.kznh.radio.utils.Constants;
import pl.kznh.radio.utils.TypefaceSpan;

public class MediaPlayerActivity extends AppCompatActivity implements View.OnClickListener, SeekBar.OnSeekBarChangeListener{

    private String mTitle;

    private String mSpeaker;

    private String mUrl;

    private int mLength;

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

    private static MediaPlayerActivity mMediaPlayerActivity;

    private boolean mIsNewIntent;

    private Switcher mSwitcher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_player);
        setActionBarTitle(R.string.title_activity_media_player);
        ((TextView)findViewById(R.id.errorTextView)).setTypeface(Constants.robotoCondensed);
        ((TextView)findViewById(R.id.progressTextView)).setTypeface(Constants.robotoCondensed);
        mSwitcher = new Switcher.Builder(this)
                .addContentView(findViewById(R.id.view_content))
                .addProgressView(findViewById(R.id.progress_view)) //progress view member
                .addErrorView(findViewById(R.id.error_view))
                .setErrorLabel((TextView) findViewById(R.id.errorTextView))
                .build();
        mSwitcher.showProgressViewImmediately();
        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mMediaPlayerActivity = this;
        settingRecordParameters();

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

        mTitleView.setTypeface(Constants.robotoCondensed);
        mSpeakerView.setTypeface(Constants.robotoCondensed);
        mProgressTimeView.setTypeface(Constants.robotoCondensed);
        mLengthView.setTypeface(Constants.robotoCondensed);
        mChooseRadioButton.setTypeface(Constants.robotoCondensed);


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

        bindPlayerService();
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
            mSwitcher.showContentView();
            setViewsVisible(true);
            mSeekBar.setSecondaryProgress(0);
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
            mSwitcher.showErrorView(String.format("%s\n%s [%s,%s]", getString(R.string.error_unknown), getString(R.string.error_code), what, extra));
            mTitleView.setVisibility(View.INVISIBLE);
            mSpeakerView.setVisibility(View.INVISIBLE);
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
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        settingRecordParameters();
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

        bindPlayerService();
    }

    @Override
    protected void onStop() {
        super.onStop();
        try {
            unbindService(mServiceConnection);
        } catch (IllegalArgumentException e){
            e.printStackTrace();
        }
        if (RecordPlayerService.isServiceRunning) {
            showNotification();
        }
    }

    private void showNotification() {
        Intent stopServiceIntent = new Intent(this, RecordPlayerService.class);
        stopServiceIntent.putExtra(Constants.EXTRA_ACTION_FROM_NOTIFICATION, Constants.ACTION_FROM_NOTIFICATION_STOP_SERVICE);

        Intent pausePlayerIntent = new Intent(this, RecordPlayerService.class);
        pausePlayerIntent.putExtra(Constants.EXTRA_ACTION_FROM_NOTIFICATION, Constants.ACTION_FROM_NOTIFICATION_PAUSE);

        Intent playPlayerIntent = new Intent(this, RecordPlayerService.class);
        playPlayerIntent.putExtra(Constants.EXTRA_ACTION_FROM_NOTIFICATION, Constants.ACTION_FROM_NOTIFICATION_PLAY);

        NotificationCompat.Builder mBuilder =
                (NotificationCompat.Builder) new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.icon_white)
                        .setLargeIcon(getLargeIcon(this))
                        .setContentTitle(mSpeaker)
                        .setColor(ContextCompat.getColor(this, R.color.primary))
                        .setContentText(mTitle)
                        .setPriority(Notification.PRIORITY_MAX)
                        .setShowWhen(false)
                        .setWhen(0)
                        .addAction(R.drawable.icon_notification_play, "", PendingIntent.getService(this, 4000, playPlayerIntent, PendingIntent.FLAG_UPDATE_CURRENT))
                        .addAction(R.drawable.icon_notification_pause, "", PendingIntent.getService(this, 3000, pausePlayerIntent, PendingIntent.FLAG_UPDATE_CURRENT))
                        .addAction(R.drawable.icon_notification_exit, "", PendingIntent.getService(this, 5000, stopServiceIntent, PendingIntent.FLAG_UPDATE_CURRENT))
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
        PendingIntent resultPendingIntent = PendingIntent.getActivity(this, 1000, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
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


    private void bindPlayerService() {
        Intent intent = new Intent(this, RecordPlayerService.class);
        intent.putExtra(Constants.EXTRA_URL, mUrl);
        mServiceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                mService = ((RecordPlayerService.MyBinder) service).getService();
                //Log.i("SERVICE", "RECONNECTED" + mService.toString());
                if (mIsNewIntent){
                    setViewsVisible(false);
                    mSwitcher.showProgressView();
                    mPlayPauseButton.setImageResource(R.drawable.icon_play);
                    mTitleView.setText(mTitle);
                    mSpeakerView.setText(mSpeaker);
                    mService.releaseMediaPlayer();
                    mService.setUrl(mUrl);
                    mService.initializePlayer();
                }
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {

            }
        };
        bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
        if (!RecordPlayerService.isServiceRunning){
            startService(intent);
        }
    }

    private void settingRecordParameters() {
        Bundle extras = getIntent().getExtras();
        mTitle = extras.getString(Constants.EXTRA_TITLE);
        mSpeaker = extras.getString(Constants.EXTRA_SPEAKER);
        //mLength = extras.getString(RecordsFragment.EXTRA_LENGTH);
        mUrl = extras.getString(Constants.EXTRA_URL);
        mIsRadio = extras.getBoolean(Constants.EXTRA_IS_RADIO);
        mIsNewIntent = extras.getBoolean(Constants.EXTRA_IS_NEW_INTENT);
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
                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, --currentVolume, AudioManager.FLAG_SHOW_UI);
                }
                break;
            case R.id.volumeUPButton:
                if (currentVolume < maxVolume){
                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, ++currentVolume, AudioManager.FLAG_SHOW_UI);
                }
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
                mSwitcher.showProgressView();
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

    public static void staticFinish(){
        if (mMediaPlayerActivity != null)mMediaPlayerActivity.finish();
    }
}
