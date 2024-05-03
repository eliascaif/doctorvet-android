package com.xionce.doctorvetServices;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import android.widget.VideoView;

import com.xionce.doctorvetServices.utilities.HelperClass;

public class ViewFullScreenVideo extends AppCompatActivity {
    //TODO: Unificar esta forma de ver imagenes y las de intents a apps locales

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_full_screen_video);

        String videoUrl = getIntent().getStringExtra(HelperClass.INTENT_VIDEO_URL);

        final VideoView videoView;
        videoView = findViewById(R.id.video_fullscreen);
        videoView.setVideoPath(videoUrl);
        videoView.start();
    }
}
