package com.woyuce.activity.Controller.OpenClass;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.woyuce.activity.AppContext;
import com.woyuce.activity.BaseActivity;
import com.woyuce.activity.R;
import com.woyuce.activity.Utils.Http.Volley.HttpUtil;
import com.woyuce.activity.Utils.LogUtil;

import java.io.IOException;

/**
 * Created by Administrator on 2016/9/21.
 */
public class OpenContentActivity extends BaseActivity implements View.OnClickListener, SeekBar.OnSeekBarChangeListener,
        MediaPlayer.OnCompletionListener, MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener, MediaPlayer.OnBufferingUpdateListener {

    private MediaPlayer mp;
    private ImageView imgbackhome, imgplay, imgpause, imgback, imgforward;
    private TextView txtcontent, txtTimeTotal, txtTimeCurrent;
    private SeekBar progressbar;

    private String localAudioUrl, localAudioTitle;
    private boolean isfinish = false; // isplay,isPause,isRelease;
    private int now, current;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mp.release();
        HttpUtil.removeTag("post");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_content);

        initView();
        initEvent();
    }

    private void initView() {
        Intent it_audio = getIntent();
        localAudioUrl = it_audio.getStringExtra("url"); // 先拿到AudioUrl,录音中直接拿此值进行Prepare
        localAudioTitle = it_audio.getStringExtra("title");

        txtcontent = (TextView) findViewById(R.id.txt_mediaplay_content);
        txtTimeTotal = (TextView) findViewById(R.id.txt_mediaplay_totaltime);
        txtTimeCurrent = (TextView) findViewById(R.id.txt_mediaplay_currenttime);
        imgbackhome = (ImageView) findViewById(R.id.img_back);
        imgplay = (ImageView) findViewById(R.id.img_music_play);
        imgpause = (ImageView) findViewById(R.id.img_music_pause);
        imgback = (ImageView) findViewById(R.id.img_music_back);
        imgforward = (ImageView) findViewById(R.id.img_music_forward);
        progressbar = (SeekBar) findViewById(R.id.progress_mediaplay);

        imgbackhome.setOnClickListener(this);
        imgplay.setOnClickListener(this);
        imgpause.setOnClickListener(this);
        imgback.setOnClickListener(this);
        imgforward.setOnClickListener(this);
        progressbar.setOnSeekBarChangeListener(this);

        mp = new MediaPlayer();
        mp.setOnCompletionListener(this);
        mp.setOnPreparedListener(this);
        mp.setOnErrorListener(this);
        mp.setOnBufferingUpdateListener(this);
    }

    private void initEvent() {
        if (mp.isPlaying() == false) {
            progressdialogshow(this);
        }
        txtcontent.setText(localAudioTitle);
        txtTimeCurrent.setText("00:00:00");
        txtTimeTotal.setText("00:00:00");

        new Thread() { // 开启子线程，以防堵塞，应该有更好的方式
            @Override
            public void run() {
                try {
                    mp.setDataSource(localAudioUrl.trim());
                    mp.prepare();
                } catch (IllegalArgumentException | SecurityException | IllegalStateException | IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    // mediaplayer方法
    @Override
    public void onPrepared(MediaPlayer mp) { // 这里的Toast应该改为转圈，考虑用BaseActivity解决
        getDrution();
        progressdialogcancel();
    }

    @Override
    public void onCompletion(MediaPlayer mp) { // 播放结束, 重设时间为0,进度条为0,循环播放,
        // isfinish为true
        LogUtil.e("complete", "I am complete");
        txtTimeCurrent.setText("00:00:00");
        progressbar.setProgress(0);
        isfinish = true;
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) { // 加载错误则重置
        mp.reset();
        return false;
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) { // 在此方法中
        // 监听mp当前currentposition，同时设置bar的移动位置，当前播放的时间位置
        if (isfinish == false) {
            getCurrent();
        }
    }

    // 获取当前时长和总时长
    private void getCurrent() {
        progressbar.setProgress(mp.getCurrentPosition());
        int time = mp.getCurrentPosition() / 1000;
        int h, m, s;
        h = time / 60 / 60;
        m = (time - 60 * (h * 60)) / 60;
        s = time % 60;
        String timer = h + ":" + m + ":" + s;
        txtTimeCurrent.setText(timer);
    }

    private void getDrution() {
        progressbar.setMax(mp.getDuration());
        int time = mp.getDuration() / 1000;
        int h, m, s;
        h = time / 60 / 60;
        m = (time - 60 * (h * 60)) / 60;
        s = time % 60;
        String timer = h + ":" + m + ":" + s;
        txtTimeTotal.setText(timer);
    }

    // seekbar方法
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        now = progress;
        current = mp.getCurrentPosition();
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        mp.seekTo(seekBar.getProgress());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_back:
                finish();
                break;
            case R.id.img_music_back:
                progressbar.setProgress(now - 50000);
                mp.seekTo(current - 50000);
                break;
            case R.id.img_music_forward:
                progressbar.setProgress(now + 50000);
                mp.seekTo(current + 50000);
                break;
            case R.id.img_music_play:
                mp.start();
                isfinish = false;
                break;
            case R.id.img_music_pause:
                mp.pause();
                break;
        }
    }
}