package com.example.audiostreamexample;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
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
import java.time.Duration;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    ProgressBar progressBar;
    TextView currentTextView, durationTextView;
    ImageButton togglePlayBtn;
    EditText serverUrlEditText;
    Button connectBtn;
    static MediaPlayer mediaPlayer;
    Timer timer;
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
        timer = new Timer();
        //초마다 현재 시간과 프로그레스 바를 업데이트 하기 위해 실행
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if(mediaPlayer == null) return;
                if(mediaPlayer.isPlaying()) {
                    runOnUiThread(() -> {
                        progressBar.setProgress(mediaPlayer.getCurrentPosition());
                        currentTextView.setText(formatMillis(mediaPlayer.getCurrentPosition()));
                    });
                }
            }
        }, 0, 1000);

        //재생 토글
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

        //연결 버튼 클릭
        connectBtn.setOnClickListener(v -> {
            try {
                mediaPlayer = new MediaPlayer();
                //재생 준비 완료
                mediaPlayer.setOnPreparedListener(mp -> {
                    durationTextView.setText(formatMillis(mp.getDuration()));
                    progressBar.setMax(mp.getDuration());
                    mp.start();
                    togglePlayBtn.setImageDrawable(ResourcesCompat.getDrawable(getResources(), android.R.drawable.ic_media_pause, null));
                });
                //재생 끝
                mediaPlayer.setOnCompletionListener(mp -> {
                    togglePlayBtn.setImageDrawable(ResourcesCompat.getDrawable(getResources(), android.R.drawable.ic_media_play, null));
                });
                //에러 발생
                mediaPlayer.setOnErrorListener((mp, what, extra) -> {
                    Snackbar.make(findViewById(R.id.mainLayout), "재생에 실패했습니다.", Snackbar.LENGTH_LONG);
                    return false;
                });

                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                //서버의 URL로 설정하면 HTTP로 파일이 불러와짐
                mediaPlayer.setDataSource(serverUrlEditText.getText().toString());
                //비동기 준비, 준비 완료시 콜백 실행
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

    public String formatMillis(long millis) {
        long s = millis / 1000;
        return String.format("%02d:%02d", s / 60, (s % 60));
    }

}