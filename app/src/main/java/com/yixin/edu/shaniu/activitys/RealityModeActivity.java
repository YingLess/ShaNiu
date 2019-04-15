package com.yixin.edu.shaniu.activitys;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.yixin.edu.shaniu.R;

public class RealityModeActivity extends AppCompatActivity {
    LoginActivity loginActivity=new LoginActivity();
    private TextView realityText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reality_mode);
//        realityText = ( findViewById(R.id.reality_recognize_text));
//        //        /设置视频背景的代码代码
//        final VideoView videoview=(VideoView)findViewById(R.id.videoview);
//        final String videopath = Uri.parse("android.resource://"+getPackageName()+"/"+R.raw.bqxzs).toString();
//        videoview.setVideoPath(videopath);
//        videoview.start();
//        videoview.setOnPreparedListener(new MediaPlayer.OnPreparedListener(){
//            @Override
//            public void onPrepared(MediaPlayer mediaPlayer) {
//                mediaPlayer.start();
//                mediaPlayer.setLooping(true);
//            }
//        });
//        videoview.setOnCompletionListener(new MediaPlayer.OnCompletionListener(){
//            @Override
//            public void onCompletion(MediaPlayer mediaPlayer) {
//                videoview.setVideoPath(videopath);
//                videoview.start();
//            }
//        });
//
//        MediaPlayer play = MediaPlayer.create(RealityModeActivity.this,
//                R.raw.bqxzs);
//        play.setVolume(0.1f, 0.1f);//声道
//        play.start();
////        给MediaPlayer对象加上播放完毕的监听：
//
//        play.setOnCompletionListener( new MediaPlayer.OnCompletionListener() {
//
//            @Override
//            public void onCompletion(MediaPlayer mp) {
//                Log.d("tag", "播放完毕");
//                //根据需要添加自己的代码。。。
//                loginActivity.speak(realityText);
//
//            }
//
//        });

    }
}
