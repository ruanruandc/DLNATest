package com.meizu.ruandongchuan.dlnatest.view.activity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;

import com.meizu.ruandongchuan.dlnatest.R;
import com.meizu.ruandongchuan.dlnatest.service.DLNAService;
import com.meizu.ruandongchuan.dlnatest.util.DLNAUtil;
import com.meizu.ruandongchuan.dlnatest.util.HandlerController;

import org.cybergarage.upnp.std.av.renderer.AVTransport;

import java.util.Timer;
import java.util.TimerTask;

import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.widget.MediaController;
import io.vov.vitamio.widget.VideoView;

public class VideoActivity extends AppCompatActivity {
    private VideoView mVideoView;
    private MediaController mMediaController;


    public  long duration = 0;
    public  long curPosition = 0;

    private HandlerController handlerController;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int what = msg.what;
            switch (what) {
                case HandlerController.STOP:
                    mVideoView.stopPlayback();
                    finish();
                    break;
                case HandlerController.PLAY:
                    mVideoView.start();
                    break;
                case HandlerController.PAUSE:
                    mVideoView.pause();
                    break;
                case HandlerController.SEEK:
                    seek(msg.getData());
                    break;
            }
        }
    };
    private DLNAService mDlnaService;
    private AVTransport mAvTransport;
    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            DLNAService.DLNABinder binder = (DLNAService.DLNABinder) service;
            mDlnaService = binder.getService();
            mAvTransport = mDlnaService.getmMediaRenderer().getAVTransport();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mDlnaService = null;
        }
    };

    private Timer timer;
    private TimerTask timerTask;
    private Uri mCurrentUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        bind();
        initView();
        initData();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (!intent.getData().equals(mCurrentUri)){
            mVideoView.setVideoURI(intent.getData());
        }
        mVideoView.start();
    }

    private void bind(){
        Intent intent = new Intent(this, DLNAService.class);
        bindService(intent, mServiceConnection, BIND_AUTO_CREATE);
    }


    private void initView() {
        mVideoView = (VideoView) findViewById(R.id.video_view);
    }

    private void initData() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        handlerController = new HandlerController(VideoActivity.class.getSimpleName(), handler);
        mMediaController = new MediaController(this);
        mMediaController.setMediaPlayer(mVideoView);
        mVideoView.setMediaController(mMediaController);
        mVideoView.setHardwareDecoder(true);
        Intent intent = getIntent();
        mCurrentUri = intent.getData();
        mVideoView.setVideoURI(mCurrentUri);
        mVideoView.start();
        mVideoView.requestFocus();
        mVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mAvTransport.setmCurPlayState(AVTransport.STOPPED);
            }
        });
        updateInfo();
    }

    private void seek(Bundle bundle){
        String time = bundle.getString("seek");
        if ( time == null || time.isEmpty() )
            return;
        int msecs = DLNAUtil.parseTimeStringToMSecs(time);
        if ( ( msecs < 0 ) || ( ( msecs > duration ) && ( duration > 0 ) ) ) {
            return;
        }
        //Debug.d(TAG, "*********seek to: " + time + ",   msecs=" + msecs + "mDuration:" + mDuration);

        mVideoView.seekTo ( msecs );
    }

    private void updateInfo() {
        timer = new Timer();
        timerTask = new TimerTask() {
            @Override
            public void run() {
                duration = mVideoView.getDuration();
                curPosition = mVideoView.getCurrentPosition();
                Log.i("updateInfo", "duration=" + duration + "curPosition=" + curPosition);
                if (mAvTransport != null) {
                    mAvTransport.updatePositionInfo(DLNAUtil.timeFormatToString((int) curPosition),
                            DLNAUtil.timeFormatToString((int) duration));
                }
            }
        };
        timer.scheduleAtFixedRate(timerTask, 0, 500);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.i("VideoActivity","onkeydown="+keyCode);
        switch (keyCode){
            case KeyEvent.KEYCODE_DPAD_CENTER:
                if (mVideoView.isPlaying()){
                    mVideoView.pause();
                    mMediaController.show();
                }else {
                    mVideoView.start();
                    mMediaController.hide();
                }
                break;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        unbindService(mServiceConnection);
        Log.d("VideoActivity", "airplay VideoPlayerActivity onDestroy");
        handlerController.destroy();
        if (mVideoView != null)
            mVideoView.stopPlayback();

        if (timerTask != null) {
            timerTask.cancel();
            timerTask = null;
        }
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        super.onDestroy();
    }
}
