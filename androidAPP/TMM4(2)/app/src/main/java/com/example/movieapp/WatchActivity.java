package com.example.movieapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.widget.VideoView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

public class WatchActivity extends AppCompatActivity {

    private static final int STORAGE_PERMISSION_CODE = 101; // permission code (approve)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.watch_screen);
        VideoView videoView = findViewById(R.id.videoView);
        Button btnPlayPause = findViewById(R.id.btnPlayPause);
        Button btnRestart = findViewById(R.id.btnRestart);
        findViewById(R.id.right_arrow_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // move back to home
                Intent intent = new Intent(WatchActivity.this,
                        SignedHomeActivity.class);
                startActivity(intent);
            }
        });

        String videoId = getIntent().getStringExtra("VIDEO_ID");

        if (videoId == null || videoId.isEmpty()) {
            Toast.makeText(this, R.string.video_id_missing, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        if (!hasStoragePermission()) {
            requestStoragePermission();
            return;
        }

        // definr the URI
        Uri videoUri = getVideoUriById(videoId);
        if (videoUri == null) {
            Toast.makeText(this, getString(R.string.video_not_found, videoId), Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        videoView.setVideoURI(videoUri);
        btnPlayPause.setOnClickListener(v -> {
            if (videoView.isPlaying()) {
                videoView.pause();
                btnPlayPause.setText(R.string.play);
            } else {
                videoView.start();
                btnPlayPause.setText(R.string.pause);
            }
        });

        btnRestart.setOnClickListener(v -> {
            videoView.seekTo(0);
            videoView.start();
            btnPlayPause.setText(R.string.pause);
        });

        videoView.setOnCompletionListener(mp -> btnPlayPause.setText(R.string.play));

        videoView.setOnErrorListener((mp, what, extra) -> {
            Toast.makeText(this, R.string.video_play_error, Toast.LENGTH_SHORT).show();
            return true;
        });
    }

    //  get URI of video
    private Uri getVideoUriById(String videoId) {
        String videoUrl = "https://localhost:3000/uploads/movies/" + videoId + ".mp4";
        return Uri.parse(videoUrl);
    }

    // check if there is permission
    private boolean hasStoragePermission() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }
    // request permission
    private void requestStoragePermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, R.string.permission_granted, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, R.string.permission_denied, Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }
}
