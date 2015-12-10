//package pl.kznh.radio.activities;
//
//import android.content.Context;
//import android.content.Intent;
//import android.media.AudioManager;
//import android.media.MediaPlayer;
//import android.net.Uri;
//import android.os.Bundle;
//import android.support.v7.app.AppCompatActivity;
//import android.view.Menu;
//import android.view.MenuItem;
//import android.view.View;
//import android.view.animation.Animation;
//import android.view.animation.AnimationUtils;
//import android.widget.Button;
//import android.widget.ImageButton;
//import android.widget.SeekBar;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import java.io.IOException;
//
//import pl.kznh.radio.R;
//
//public class MainActivity extends AppCompatActivity {
//    MediaPlayer player;
//    ImageButton mPlayPause;
//    TextView mErrorTextVew;
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.fragment_radio);
//        mPlayPause = (ImageButton) findViewById(R.id.playPauseButton);
//        mErrorTextVew = (TextView) findViewById(R.id.errorTextView);
//        mPlayPause.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (player.isPlaying() ){
//                    mPlayPause.setImageResource(R.drawable.play);
//                    player.pause();
//                } else if (mPlayPause.getAnimation() == null){
//                    mPlayPause.setImageResource(R.drawable.pause);
//                    player.start();
//                }
//            }
//        });
//        Button kznhButton = (Button) findViewById(R.id.changeRadioButton);
//        kznhButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(Intent.ACTION_VIEW);
//                intent.setData(Uri.parse("http://kznh.pl"));
//                startActivity(intent);
//            }
//        });
//        initializePlayer(getString(R.string.kznh_url));
//
//        final AudioManager audioManager =
//                (AudioManager)getSystemService(Context.AUDIO_SERVICE);
//
//        SeekBar sbVolumeBooster = (SeekBar)findViewById(R.id.volumeBar);
//
//        sbVolumeBooster.setMax(audioManager
//                .getStreamMaxVolume(AudioManager.STREAM_MUSIC));
//
//        sbVolumeBooster.setProgress(audioManager
//                .getStreamVolume(AudioManager.STREAM_MUSIC));
//
//
//        sbVolumeBooster.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//            @Override
//            public void onStopTrackingTouch(SeekBar arg0) {
//            }
//
//            @Override
//            public void onStartTrackingTouch(SeekBar arg0) {
//            }
//
//            @Override
//            public void onProgressChanged(SeekBar arg0, int progress, boolean arg2) {
//                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
//                        progress, 0);  // o can also be changed with AudioManager.FLAG_PLAY_SOUND
//            }
//        });
//    }
//
//    private void initializePlayer(String url) {
//        mErrorTextVew.setText("");
//        mPlayPause.setImageResource(R.drawable.progress);
//        final Animation rotateAnimation = AnimationUtils.loadAnimation(this, R.anim.rotate_animation);
//        mPlayPause.startAnimation(rotateAnimation);
//        player = new MediaPlayer();
//        try
//        {
//            player.setDataSource(url);
//        }
//        catch (IllegalArgumentException | IllegalStateException | IOException illegalargumentexception)
//        {
//            illegalargumentexception.printStackTrace();
//        }
//        player.prepareAsync();
//        player.setOnPreparedListener(new android.media.MediaPlayer.OnPreparedListener() {
//            public void onPrepared(MediaPlayer mediaplayer) {
//                mPlayPause.clearAnimation();
//                mPlayPause.setImageResource(R.drawable.play);
//                mPlayPause.setClickable(true);
//            }
//        });
//        player.setOnErrorListener(new MediaPlayer.OnErrorListener() {
//            @Override
//            public boolean onError(MediaPlayer mp, final int what, final int extra) {
//                mPlayPause.clearAnimation();
//                mPlayPause.setImageResource(android.R.color.transparent);
//                mErrorTextVew.setText(getString(R.string.error_unknown));
//                mErrorTextVew.setOnLongClickListener(new View.OnLongClickListener() {
//                    @Override
//                    public boolean onLongClick(View v) {
//                        Toast.makeText(MainActivity.this, "Kod błędu: " + "(" + what + ", " + extra + ")", Toast.LENGTH_SHORT).show();
//                        return false;
//                    }
//                });
//                return true;
//            }
//        });
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        if (player != null) {
//            player.release();
//        }
//    }
//
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_main, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        mPlayPause.setImageResource(R.drawable.play);
//        switch (id) {
//            case R.id.action_kznh:
//                player.release();
//                player=null;
//                setActionBarTitle(R.string.action_kznh);
//                initializePlayer(getString(R.string.kznh_url));
//                return true;
//            case R.id.action_pielgrzym_kazania:
//                player.release();
//                player=null;
//                setActionBarTitle(R.string.action_pielgrzym_kazania);
//                initializePlayer(getString(R.string.kazania_url));
//                return true;
//            case R.id.action_pielgrzym_en:
//                player.release();
//                player=null;
//                setActionBarTitle(R.string.action_pielgrzym_en);
//                initializePlayer(getString(R.string.angielskie_url));
//                return true;
//            case R.id.action_pielgrzym_pl:
//                player.release();
//                player=null;
//                setActionBarTitle(R.string.action_pielgrzym_pl);
//                initializePlayer(getString(R.string.polskie_url));
//                return true;
//        }
//        return super.onOptionsItemSelected(item);
//    }
//
//    public void setActionBarTitle (int titleRes) {
//            getSupportActionBar().setTitle(titleRes);
//    }
//}
