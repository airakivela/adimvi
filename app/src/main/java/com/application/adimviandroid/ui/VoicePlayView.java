package com.application.adimviandroid.ui;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.application.adimviandroid.R;
import com.application.adimviandroid.models.RoomMessageModel;
import com.application.adimviandroid.types.ImagePlaceHolderType;
import com.application.adimviandroid.utils.AppUtil;
import com.application.adimviandroid.utils.DirManager;
import com.application.adimviandroid.utils.DownloadUtil;
import com.application.adimviandroid.utils.StringUtil;
import com.bumptech.glide.Glide;

import java.io.IOException;

public class VoicePlayView extends LinearLayout implements View.OnClickListener {

    private final Context mContext;
    private RoomMessageModel model;
    private VoicePlayViewListener listener;

    private MediaPlayer mediaPlayer;
    private Runnable runnable;
    private Handler handler;

    private final ImageView imgUser;
    private final ImageView imgPlay;
    private final ImageView imgBgUser;
    private final LinearLayout lltCancel;
    private final SeekBar seekBar;
    private final TextView txtUserName;
    private final TextView txtDuration;

    private long duration = 0;
    private boolean isPause = false;
    private long pausedLength = 0;

    public VoicePlayView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mContext = context;

        setOrientation(LinearLayout.VERTICAL);
        LayoutInflater.from(context).inflate(R.layout.layout_voice_play, this, true);

        imgBgUser = findViewById(R.id.imgBigUser);
        imgUser = findViewById(R.id.imgUser);
        seekBar = findViewById(R.id.seekBar);
        seekBar.setOnTouchListener((v, event) -> true);
        lltCancel = findViewById(R.id.lltCancel);
        txtUserName = findViewById(R.id.txtVoiceUser);
        txtDuration = findViewById(R.id.txtDuration);
        imgPlay = findViewById(R.id.imgPlay);

        imgPlay.setOnClickListener(this);
        lltCancel.setOnClickListener(this);
    }

    public void initView(RoomMessageModel model, VoicePlayViewListener listener) {
        this.model = model;
        this.listener = listener;

        mediaPlayer = new MediaPlayer();
        handler = new Handler();

        duration = Long.parseLong(model.content);
        txtDuration.setText(StringUtil.longToString(duration));
        txtUserName.setText(model.userName);
        AppUtil.loadImageByUrl(mContext, imgUser, model.senderAvatar, ImagePlaceHolderType.USERIMAGE);
        AppUtil.loadImageByUrlWithBlur(mContext, imgBgUser, model.senderAvatar, ImagePlaceHolderType.USERIMAGE);
    }

    private void onPlayPause() {
        if (!mediaPlayer.isPlaying()) {
            if (isPause) {
                mediaPlayer.seekTo((int) pausedLength);
                mediaPlayer.start();
                seekBar.setProgress((int) pausedLength);
                Glide.with(mContext).load(R.drawable.ic_pause).into(imgPlay);
            } else {
                Glide.with(mContext).load(R.drawable.gif_load).into(imgPlay);
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mediaPlayer.reset();
                try {
                    if (!DirManager.isExistAudioFile(model).isEmpty()) {
                        mediaPlayer.setDataSource(DirManager.isExistAudioFile(model));
                    } else {
                        Uri uri = Uri.parse(model.extra);
                        new DownloadUtil().execute(model);
                        mediaPlayer.setDataSource(mContext, uri);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                mediaPlayer.prepareAsync();

                mediaPlayer.setOnPreparedListener(mp -> {
                    seekBar.setMax(mp.getDuration());
                    mediaPlayer.start();
                    Glide.with(mContext).load(R.drawable.ic_pause).into(imgPlay);
                    updateSeekBar();
                });
            }
        } else {
            isPause = true;
            pausedLength = mediaPlayer.getCurrentPosition();
            mediaPlayer.pause();
            Glide.with(mContext).load(R.drawable.ic_arrow_play).into(imgPlay);
        }
        mediaPlayer.setOnCompletionListener(mp -> {
            stop();
        });

    }

    private void stop() {
        txtDuration.setText(StringUtil.longToString(duration));
        seekBar.setProgress(0);
        isPause = false;
        pausedLength = 0;
        mediaPlayer.stop();
        handler.removeCallbacks(runnable);
        Glide.with(mContext).load(R.drawable.ic_arrow_play).into(imgPlay);
    }

    private void updateSeekBar() {
        int curPos = mediaPlayer.getCurrentPosition();
        seekBar.setProgress(curPos);
        txtDuration.setText(StringUtil.longToString(duration - mediaPlayer.getCurrentPosition()));
        runnable = () -> updateSeekBar();
        handler.postDelayed(runnable, 1000);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imgPlay:
                onPlayPause();
                break;
            case R.id.lltCancel:
                stop();
                mediaPlayer.release();
                isPause = false;
                pausedLength = 0;
                listener.hideView();
                break;
        }
    }

    public interface VoicePlayViewListener {
        void hideView();
    }
}
