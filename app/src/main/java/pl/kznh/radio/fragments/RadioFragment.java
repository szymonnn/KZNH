package pl.kznh.radio.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

import pl.kznh.radio.R;

/**
 * Created by SzymonN on 2015-11-30.
 */
public class RadioFragment extends Fragment {
    private MediaPlayer mMediaPlayer;
    private ImageButton mPlayPause;
    private TextView mErrorTextView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_radio, container, false);
        mPlayPause = (ImageButton) view.findViewById(R.id.playPauseButton);
        mErrorTextView = (TextView) view.findViewById(R.id.errorTextView);
        mPlayPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mMediaPlayer.isPlaying() ){
                    mPlayPause.setImageResource(R.drawable.play);
                    mMediaPlayer.pause();
                } else if (mPlayPause.getAnimation() == null){
                    mPlayPause.setImageResource(R.drawable.pause);
                    mMediaPlayer.start();
                }
            }
        });
        Button changeRadioButton = (Button) view.findViewById(R.id.changeRadioButton);
        changeRadioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createChooseRadioDialog();
            }
        });
        initializePlayer(getString(R.string.kznh_url));
        createChooseRadioDialog();
        setActionBarTitle(getResources().getStringArray(R.array.radio_names_array)[0]);

        final AudioManager audioManager =
                (AudioManager)getActivity().getSystemService(Context.AUDIO_SERVICE);

        SeekBar sbVolumeBooster = (SeekBar)view.findViewById(R.id.volumeBar);

        sbVolumeBooster.setMax(audioManager
                .getStreamMaxVolume(AudioManager.STREAM_MUSIC));

        sbVolumeBooster.setProgress(audioManager
                .getStreamVolume(AudioManager.STREAM_MUSIC));


        sbVolumeBooster.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar arg0) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar arg0) {
            }

            @Override
            public void onProgressChanged(SeekBar arg0, int progress, boolean arg2) {
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
                        progress, 0);  // o can also be changed with AudioManager.FLAG_PLAY_SOUND
            }
        });
        return view;
    }

    private void createChooseRadioDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final String [] radioNames = getResources().getStringArray(R.array.radio_names_array);
        builder.setItems(radioNames, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String [] urlArray = getResources().getStringArray(R.array.radio_url_array);
                mMediaPlayer.release();
                mMediaPlayer =null;
                setActionBarTitle(radioNames[which]);
                initializePlayer(urlArray[which]);
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mMediaPlayer != null) {
            mMediaPlayer.release();
        }
    }
    private void initializePlayer(String url) {
        mErrorTextView.setText("");
        mPlayPause.setImageResource(R.drawable.progress);
        final Animation rotateAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.rotate_animation);
        mPlayPause.startAnimation(rotateAnimation);
        mMediaPlayer = new MediaPlayer();
        try
        {
            mMediaPlayer.setDataSource(url);
        }
        catch (IllegalArgumentException | IllegalStateException | IOException illegalargumentexception)
        {
            illegalargumentexception.printStackTrace();
        }
        mMediaPlayer.prepareAsync();
        mMediaPlayer.setOnPreparedListener(new android.media.MediaPlayer.OnPreparedListener() {
            public void onPrepared(MediaPlayer mediaplayer) {
                mPlayPause.clearAnimation();
                mPlayPause.setImageResource(R.drawable.play);
                mPlayPause.setClickable(true);
            }
        });
        mMediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, final int what, final int extra) {
                mPlayPause.clearAnimation();
                mPlayPause.setImageResource(android.R.color.transparent);
                mErrorTextView.setText(getString(R.string.error_unknown));
                mErrorTextView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        Toast.makeText(getActivity(), "Kod błędu: " + "(" + what + ", " + extra + ")", Toast.LENGTH_SHORT).show();
                        return false;
                    }
                });
                return true;
            }
        });
    }

    public void setActionBarTitle (int titleRes) {
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(titleRes);
    }

    public void setActionBarTitle (String title) {
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(title);
    }
}
