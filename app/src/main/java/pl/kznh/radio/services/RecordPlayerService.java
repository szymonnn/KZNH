package pl.kznh.radio.services;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import java.io.IOException;

import pl.kznh.radio.activities.MediaPlayerActivity;
import pl.kznh.radio.fragments.RecordsFragment;

public class RecordPlayerService extends Service {

    public static final String NOTIFICATION = "pl.kznh.radio.services";

    public static final String MEDIA_PLAYER_RESULT_WHAT = "mp-what";

    public static final String MEDIA_PLAYER_RESULT_EXTRA = "mp-extra";

    public static final String MEDIA_PLAYER_ACTION = "action";

    public static final int ACTION_ON_ERROR = 0;

    public static final int ACTION_ON_BUFFERING_UPDATE = 1;

    public static final String MEDIA_PLAYER_BUFFERING_PERCENTAGE = "buffering-percentage";

    private MediaPlayer mMediaPlayer;

    private String mTitle;

    private String mSpeaker;

    private String mLength;

    private String mUrl;

    private IBinder mBinder = new MyBinder();

    public static boolean isServiceRunning;

    public RecordPlayerService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("SERVICE", "NOT RUNNING - INITIALIZING");
        if (intent != null) {
            Bundle extras = intent.getExtras();
            mUrl = extras.getString(RecordsFragment.EXTRA_URL);
            initializePlayer();
            isServiceRunning = true;
        } else {
            isServiceRunning = false;
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
        Log.i("SERVICE", "RUNNING - REINITIALIZING");
        reInitializePlayer();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return true;
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        stopSelf();
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancel(MediaPlayerActivity.NOTIFICATION_ID);
        isServiceRunning = false;
        Log.i("SERVICE", "DESTROYED");
    }

    private void reInitializePlayer() {
        Log.i("SERVICE", "reInitializePlayer()");
        handleMediaPlayerErrorListener(ACTION_ON_ERROR, 0, 0);
        mMediaPlayer.setOnPreparedListener(new android.media.MediaPlayer.OnPreparedListener() {
            public void onPrepared(MediaPlayer mediaplayer) {
                handleMediaPlayerErrorListener(ACTION_ON_ERROR, 0, 0);
            }
        });
        mMediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, final int what, final int extra) {
                handleMediaPlayerErrorListener(ACTION_ON_ERROR, what, extra);
                return false;
            }
        });
        mMediaPlayer.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
            @Override
            public void onBufferingUpdate(MediaPlayer mp, int percent) {
                handleMediaPlayerBufferingUpdateListener(ACTION_ON_BUFFERING_UPDATE, percent);
            }
        });
    }

    public void initializePlayer() {
        mMediaPlayer = null;
        mMediaPlayer = new MediaPlayer();
        try
        {
            mMediaPlayer.setDataSource(mUrl);
        }
        catch (IllegalArgumentException | IllegalStateException | IOException illegalargumentexception)
        {
            illegalargumentexception.printStackTrace();
        }
        mMediaPlayer.prepareAsync();
        mMediaPlayer.setOnPreparedListener(new android.media.MediaPlayer.OnPreparedListener() {
            public void onPrepared(MediaPlayer mediaplayer) {
                handleMediaPlayerErrorListener(ACTION_ON_ERROR, 0, 0);
            }
        });
        mMediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, final int what, final int extra) {
                handleMediaPlayerErrorListener(ACTION_ON_ERROR, what, extra);
                return false;
            }
        });
        mMediaPlayer.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
            @Override
            public void onBufferingUpdate(MediaPlayer mp, int percent) {
                handleMediaPlayerBufferingUpdateListener(ACTION_ON_BUFFERING_UPDATE, percent);
            }
        });
    }

    public void stopMediaPlayer() {
        mMediaPlayer.stop();
    }

    public void pauseMediaPlayer () {
        mMediaPlayer.pause();
    }

    public void startMediaPlayer () {
        mMediaPlayer.start();
    }

    public boolean isMediaPlayerPlaying(){
        return mMediaPlayer.isPlaying();
    }

    public void seekTo (int msc){
        mMediaPlayer.seekTo(msc);
    }

    public int getLength (){
        return mMediaPlayer.getDuration();
    }

    public int getCurrentPosition (){
        return mMediaPlayer.getCurrentPosition();
    }

    public void setUrl (String url){
        mUrl = url;
    }

    private void handleMediaPlayerErrorListener(int action, int what, int extra) {
        Intent intent = new Intent(NOTIFICATION);
        intent.putExtra(MEDIA_PLAYER_ACTION, action);
        intent.putExtra(MEDIA_PLAYER_RESULT_EXTRA, extra);
        intent.putExtra(MEDIA_PLAYER_RESULT_WHAT, what);
        sendBroadcast(intent);
    }

    private void handleMediaPlayerBufferingUpdateListener(int action, int percent) {
        Intent intent = new Intent(NOTIFICATION);
        intent.putExtra(MEDIA_PLAYER_ACTION, action);
        intent.putExtra(MEDIA_PLAYER_BUFFERING_PERCENTAGE, percent);
        sendBroadcast(intent);
    }

    public class MyBinder extends Binder {
        public RecordPlayerService getService() {
            return RecordPlayerService.this;
        }
    }
}
