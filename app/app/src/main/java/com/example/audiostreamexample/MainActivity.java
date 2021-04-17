package com.example.audiostreamexample;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    ProgressBar progressBar;
    TextView currentTextView;
    TextView durationTextView;
    ImageButton togglePlayBtn;
    EditText serverUrlEditText;
    Button connectBtn;
    MediaPlayer mediaPlayer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progressBar = findViewById(R.id.progressBar);
        currentTextView = findViewById(R.id.currentTextView);
        durationTextView = findViewById(R.id.durationTextView);
        togglePlayBtn = findViewById(R.id.togglePlayBtn);
        serverUrlEditText = findViewById(R.id.serverUrlEditText);
        connectBtn = findViewById(R.id.connectBtn);
        togglePlayBtn.setOnClickListener(v -> {
            if(mediaPlayer.isPlaying()) {
                togglePlayBtn.setImageDrawable(ResourcesCompat.getDrawable(getResources(), android.R.drawable.ic_media_play, null));
                mediaPlayer.pause();
            }
            else {
                togglePlayBtn.setImageDrawable(ResourcesCompat.getDrawable(getResources(), android.R.drawable.ic_media_pause, null));
                mediaPlayer.start();
            }
        });

        connectBtn.setOnClickListener(v -> {
            try {
                mediaPlayer = new MediaPlayer();
                mediaPlayer.setOnPreparedListener(mp -> {
                    mp.start();
                    togglePlayBtn.setImageDrawable(ResourcesCompat.getDrawable(getResources(), android.R.drawable.ic_media_pause, null));
                });

                mediaPlayer.setOnErrorListener((mp, what, extra) -> {
                    Snackbar.make(findViewById(R.id.mainLayout), "재생에 실패했습니다.", Snackbar.LENGTH_LONG);
                    return false;
                });

                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mediaPlayer.setDataSource(serverUrlEditText.getText().toString());
                mediaPlayer.prepareAsync();
            } catch (IOException e) {
                e.printStackTrace();
                Snackbar.make(findViewById(R.id.mainLayout), "서버 연결에 실패했습니다", Snackbar.LENGTH_LONG);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mediaPlayer.release();
        mediaPlayer = null;
    }
}