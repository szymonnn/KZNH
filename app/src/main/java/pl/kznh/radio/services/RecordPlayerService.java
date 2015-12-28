package pl.kznh.radio.services;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;

import java.io.IOException;

import pl.kznh.radio.activities.MediaPlayerActivity;
import pl.kznh.radio.utils.Constants;

public class RecordPlayerService extends Service {

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
        //Log.i("SERVICE", "NOT RUNNING - INITIALIZING");
        if (intent != null) {
            Bundle extras = intent.getExtras();
            if (extras.getInt(Constants.EXTRA_ACTION_FROM_NOTIFICATION) == Constants.NOT_ACTION){
                mUrl = extras.getString(Constants.EXTRA_URL);
                initializePlayer();
                isServiceRunning = true;
            } else {
                switch(extras.getInt(Constants.EXTRA_ACTION_FROM_NOTIFICATION)){
                    case Constants.ACTION_FROM_NOTIFICATION_STOP_SERVICE:
                        releaseMediaPlayer();
                        stopSelf();
                        MediaPlayerActivity.staticFinish();
                        break;
                    case Constants.ACTION_FROM_NOTIFICATION_PLAY:
                        if (!isMediaPlayerPlaying())
                        startMediaPlayer();
                        break;
                    case Constants.ACTION_FROM_NOTIFICATION_PAUSE:
                        if (isMediaPlayerPlaying())
                        pauseMediaPlayer();
                        break;
                }
            }


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
        //Log.i("SERVICE", "RUNNING - REINITIALIZING");
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
        notificationManager.cancel(Constants.NOTIFICATION_ID);
        isServiceRunning = false;
        //Log.i("SERVICE", "DESTROYED");
    }

    private void reInitializePlayer() {
        //Log.i("SERVICE", "reInitializePlayer()");
        handleMediaPlayerErrorListener(Constants.ACTION_ON_ERROR, 0, 0);
        mMediaPlayer.setOnPreparedListener(new android.media.MediaPlayer.OnPreparedListener() {
            public void onPrepared(MediaPlayer mediaplayer) {
                handleMediaPlayerErrorListener(Constants.ACTION_ON_ERROR, 0, 0);
            }
        });
        mMediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, final int what, final int extra) {
                handleMediaPlayerErrorListener(Constants.ACTION_ON_ERROR, what, extra);
                return false;
            }
        });
        mMediaPlayer.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
            @Override
            public void onBufferingUpdate(MediaPlayer mp, int percent) {
                handleMediaPlayerBufferingUpdateListener(Constants.ACTION_ON_BUFFERING_UPDATE, percent);
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
                handleMediaPlayerErrorListener(Constants.ACTION_ON_ERROR, 0, 0);
            }
        });
        mMediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, final int what, final int extra) {
                handleMediaPlayerErrorListener(Constants.ACTION_ON_ERROR, what, extra);
                return false;
            }
        });
        mMediaPlayer.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
            @Override
            public void onBufferingUpdate(MediaPlayer mp, int percent) {
                handleMediaPlayerBufferingUpdateListener(Constants.ACTION_ON_BUFFERING_UPDATE, percent);
            }
        });
    }

    public void stopMediaPlayer() {
        mMediaPlayer.stop();
    }

    public void releaseMediaPlayer() {
        mMediaPlayer.release();
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
        int currentPosition = 0;
        try{
            currentPosition = mMediaPlayer.getCurrentPosition();
        } catch(IllegalStateException e){
            e.printStackTrace();
        }
        return currentPosition;
    }

    public void setUrl (String url){
        mUrl = url;
    }

    private void handleMediaPlayerErrorListener(int action, int what, int extra) {
        Intent intent = new Intent(Constants.NOTIFICATION);
        intent.putExtra(Constants.MEDIA_PLAYER_ACTION, action);
        intent.putExtra(Constants.MEDIA_PLAYER_RESULT_EXTRA, extra);
        intent.putExtra(Constants.MEDIA_PLAYER_RESULT_WHAT, what);
        sendBroadcast(intent);
    }

    private void handleMediaPlayerBufferingUpdateListener(int action, int percent) {
        Intent intent = new Intent(Constants.NOTIFICATION);
        intent.putExtra(Constants.MEDIA_PLAYER_ACTION, action);
        intent.putExtra(Constants.MEDIA_PLAYER_BUFFERING_PERCENTAGE, percent);
        sendBroadcast(intent);
    }

    public class MyBinder extends Binder {
        public RecordPlayerService getService() {
            return RecordPlayerService.this;
        }
    }
}
