package com.yixin.edu.shaniu;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.gson.Gson;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.cloud.SynthesizerListener;
import com.iflytek.cloud.ui.RecognizerDialog;
import com.iflytek.cloud.ui.RecognizerDialogListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class LoginActivity extends AppCompatActivity  implements View.OnClickListener {
    private final static String TAG = LoginActivity.class.getSimpleName();
    // 语音听写对象
    private SpeechRecognizer mRecognize;
    // 语音听写UI
    private RecognizerDialog mRecognizeDialog;
    // 用HashMap存储听写结果
    private HashMap<String, String> mRecognizeResults = new LinkedHashMap<String, String>();

    private TextView mResultText;
    private SharedPreferences mSharedPreferences;
    String Password="猪";
    String Pattern="开机模式";
    String Patterns="手机模式";
    String Patternss="真人模式";

    //58be0d21
    int anInt;
    boolean fag=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_login);
        // 定义获取录音的动态权限
        soundPermissions();
        mResultText = ( findViewById(R.id.xf_recognize_text));
        mSharedPreferences = getSharedPreferences(VoiceSettingsActivity.PREFER_NAME, Activity.MODE_PRIVATE);

        // 初始化识别无UI识别对象，使用SpeechRecognizer对象，可根据回调消息自定义界面；
        mRecognize = SpeechRecognizer.createRecognizer(this, mInitListener);
        // 初始化听写Dialog，如果只使用有UI听写功能，无需创建SpeechRecognizer
        // 使用UI听写功能，请将assets下文件拷贝到项目中
        mRecognizeDialog = new RecognizerDialog(this, mInitListener);

//        //        /设置视频背景的代码代码
        startvideo(R.raw.kaiji);
        MediaPlayer play = MediaPlayer.create(LoginActivity.this,
                R.raw.kjyin);
        play.setVolume(0.1f, 0.1f);//声道
        play.start();
//        给MediaPlayer对象加上播放完毕的监听：

        play.setOnCompletionListener( new MediaPlayer.OnCompletionListener() {

            @Override
            public void onCompletion(MediaPlayer mp) {
                Log.d("tag", "播放完毕");
                //根据需要添加自己的代码。。。
                anInt=1;
                speak();

            }

        });



//        findViewById(R.id.xf_recognize_start).setOnClickListener(this);
//        findViewById(R.id.xf_recognize_stop).setOnClickListener(this);
//        findViewById(R.id.xf_recognize_cancel).setOnClickListener(this);
//        findViewById(R.id.xf_recognize_stream).setOnClickListener(this);
//        findViewById(R.id.xf_recognize_setting).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(LoginActivity.this, VoiceSettingsActivity.class);
//                intent.putExtra("type", VoiceSettingsActivity.XF_RECOGNIZE);
//                startActivity(intent);
//            }
//        });


    }

     public void startvideo(int video){
         //        /设置视频背景的代码代码
         final VideoView videoview=(VideoView)findViewById(R.id.videoview);
         final String videopath = Uri.parse("android.resource://"+getPackageName()+"/"+video).toString();
         videoview.setVideoPath(videopath);
         videoview.start();
         videoview.setOnPreparedListener(new MediaPlayer.OnPreparedListener(){
             @Override
             public void onPrepared(MediaPlayer mediaPlayer) {
                 mediaPlayer.start();
                 mediaPlayer.setLooping(true);
             }
         });
         videoview.setOnCompletionListener(new MediaPlayer.OnCompletionListener(){
             @Override
             public void onCompletion(MediaPlayer mediaPlayer) {
                 videoview.setVideoPath(videopath);
                 videoview.start();
             }
         });
     }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 退出时释放连接
        mRecognize.cancel();
        mRecognize.destroy();
    }

    @Override
    public void onClick(View v) {
//
    }

    //初始化监听器
    private InitListener mInitListener = new InitListener() {
        @Override
        public void onInit(int code) {
            Log.d(TAG, "SpeechRecognizer init() code = " + code);
            if (code != ErrorCode.SUCCESS) {
                showTip("初始化失败，错误码：" + code);
            }
        }
    };

    //听写监听器
    private RecognizerListener mRecognizeListener = new RecognizerListener() {

        @Override
        public void onBeginOfSpeech() {
            // 此回调表示：sdk内部录音机已经准备好了，用户可以开始语音输入
            showTip("开始说话");
        }

        @Override
        public void onError(SpeechError error) {
            // 错误码：10118(您没有说话)，可能是录音机权限被禁，需要提示用户打开应用的录音权限。
            // 如果使用本地功能（语记）需要提示用户开启语记的录音权限。
            speak();

        }

        @Override
        public void onEndOfSpeech() {
            // 此回调表示：检测到了语音的尾端点，已经进入识别过程，不再接受语音输入
            showTip("结束说话");
        }

        @Override
        public void onResult(RecognizerResult results, boolean isLast) {
            Log.d(TAG, results.getResultString());
            printResult(results);

            if (isLast) {
                // TODO 最后的结果

            }
        }

        @Override
        public void onVolumeChanged(int volume, byte[] data) {
//            showTip("当前正在说话，音量大小：" + volume);
            Log.d(TAG, "返回音频数据："+data.length);
        }

        @Override
        public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {
            // 以下代码用于获取与云端的会话id，当业务出错时将会话id提供给技术支持人员，可用于查询会话日志，定位出错原因
            // 若使用本地能力，会话id为null
            //	if (SpeechEvent.EVENT_SESSION_ID == eventType) {
            //		String sid = obj.getString(SpeechEvent.KEY_EVENT_SESSION_ID);
            //		Log.d(TAG, "session id =" + sid);
            //	}
        }
    };

    private void printResult(RecognizerResult results) {
        String text = JsonParser.parseIatResult(results.getResultString());
        //防止重复监听，即说话一次，产生两次结果
        mRecognize.stopListening();
        if(TextUtils.isEmpty(text)){
            return;
        }
        String sn = null;
        try {
            JSONObject resultJson = new JSONObject(results.getResultString());
            sn = resultJson.optString("sn");
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }
        mRecognizeResults.put(sn, text);
        StringBuffer resultBuffer = new StringBuffer();
        for (String key : mRecognizeResults.keySet()) {
            resultBuffer.append(mRecognizeResults.get(key));
        }
        mResultText.setText(resultBuffer.toString());

//        mResultText.setSelection(mResultText.length());
//        输入密码时为1
        if (anInt==1){
        if (null!=mResultText.getText()){
//        if(mResultText.getText().toString().length()==Password.length()){
                if (mResultText.getText().toString().equals(Password)){
//                    密码正确选择模式为2
                    anInt=2;
                    JudgePassword(mResultText,R.raw.msxz);
                }else {
//                    Toast.makeText(this, "来了", Toast.LENGTH_SHORT).show();
                    JudgePassword(mResultText,R.raw.mmcw);
                }
//        }else if(mResultText.getText().length()==Pattern.length()){
//
//        }
        }else {
            speak();
        }
    }else if( anInt==2){
            if (null!=mResultText.getText()){
//            手机模式
            if (mResultText.getText().toString().equals(Patterns)){
//                ss

            }else if(mResultText.getText().toString().equals(Patternss)){
                //真人模式
                startvideo(R.raw.bqxzs);
                MediaPlayer play = MediaPlayer.create(LoginActivity.this,
                        R.raw.bqxz);
                play.setVolume(0.1f, 0.1f);//声道
                play.start();
//        给MediaPlayer对象加上播放完毕的监听：

                play.setOnCompletionListener( new MediaPlayer.OnCompletionListener() {

                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        Log.d("tag", "播放完毕");
                        //根据需要添加自己的代码。。。
                        anInt=3;
                        speak();

                    }

                });

            }else {
                MediaPlayer play = MediaPlayer.create(LoginActivity.this,
                        R.raw.mscw);
                play.setVolume(0.1f, 0.1f);//声道
                play.start();
//        给MediaPlayer对象加上播放完毕的监听：

                play.setOnCompletionListener( new MediaPlayer.OnCompletionListener() {

                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        Log.d("tag", "播放完毕");
                        //根据需要添加自己的代码。。。
                        speak();

                    }

                });
            }
            }else {
                speak();
            }
        }else if(anInt==3){
//      选择表情结果
            if (null!=mResultText.getText()){
               if (mResultText.getText().toString().equals("傻")){

               }else if (mResultText.getText().toString().equals("愁")){

               }else if (mResultText.getText().toString().equals("坏")){
                   Toast.makeText(this, "选择了坏", Toast.LENGTH_SHORT).show();
               }else if (mResultText.getText().toString().equals("酷")){

               }else if (mResultText.getText().toString().equals("喜")){

               }else if (mResultText.getText().toString().equals("羞")){

               }else if (mResultText.getText().toString().equals("乐")){

               }else if (mResultText.getText().toString().equals("哀")){

               }else if (mResultText.getText().toString().equals("怒")){

               }else if (mResultText.getText().toString().equals("乖")){

               }else {
                   MediaPlayer play = MediaPlayer.create(LoginActivity.this,
                           R.raw.srcw);
                   play.setVolume(0.1f, 0.1f);//声道
                   play.start();
//        给MediaPlayer对象加上播放完毕的监听：

                   play.setOnCompletionListener( new MediaPlayer.OnCompletionListener() {

                       @Override
                       public void onCompletion(MediaPlayer mp) {
                           Log.d("tag", "播放完毕");
                           //根据需要添加自己的代码。。。
                           anInt=3;
                           speak();

                       }

                   });
               }
            }else {
                speak();
            }
        }
    }
    //听写UI监听器
    private RecognizerDialogListener mRecognizeDialogListener = new RecognizerDialogListener() {
        public void onResult(RecognizerResult results, boolean isLast) {
            printResult(results);
        }

        //识别回调错误
        public void onError(SpeechError error) {
            // showTip(error.getPlainDescription(true));
//            if(error.getErrorCode() == 10118){
//                MediaPlayer play = MediaPlayer.create(LoginActivity.this,
//                        R.raw.kjyin);
//                play.setVolume(0.1f, 0.1f);//声道
//                play.start();
////        给MediaPlayer对象加上播放完毕的监听：
//
//                play.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
//
//                    @Override
//                    public void onCompletion(MediaPlayer mp) {
//                        Log.d("tag", "播放完毕");
                        // 显示听写对话框

                        mRecognizeDialog.setListener(mRecognizeDialogListener);
                        mRecognizeDialog.show();
//                    }
//                });
//
////                TODO
//            }

        }
    };

    private void showTip(final String str) {
        Toast.makeText(this, str, Toast.LENGTH_LONG).show();
    }

    //参数设置
    public void resetParam() {
        // 清空参数
        mRecognize.setParameter(SpeechConstant.PARAMS, null);
        // 设置听写引擎。TYPE_LOCAL表示本地，TYPE_CLOUD表示云端，TYPE_MIX 表示混合
        mRecognize.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD);
        // 设置返回结果格式
        mRecognize.setParameter(SpeechConstant.RESULT_TYPE, "json");

        String lag = mSharedPreferences.getString("recognize_language_preference", "mandarin");
        if (lag.equals("en_us")) {  // 设置语言
            mRecognize.setParameter(SpeechConstant.LANGUAGE, "en_us");
        } else {
            mRecognize.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
            // 设置语言区域
            mRecognize.setParameter(SpeechConstant.ACCENT, lag);
        }

        // 设置语音前端点:静音超时时间，即用户多长时间不说话则当做超时处理
        mRecognize.setParameter(SpeechConstant.VAD_BOS, mSharedPreferences.getString("recognize_vadbos_preference", "9000"));//（单位：MS）
        // 设置语音后端点:后端点静音检测时间，即用户停止说话多长时间内即认为不再输入， 自动停止录音
        mRecognize.setParameter(SpeechConstant.VAD_EOS, mSharedPreferences.getString("recognize_vadeos_preference", "1000"));
        // 设置标点符号,设置为"0"返回结果无标点,设置为"1"返回结果有标点
        mRecognize.setParameter(SpeechConstant.ASR_PTT, mSharedPreferences.getString("recognize_punc_preference", "0"));
        // 设置音频保存路径，保存音频格式支持pcm、wav，设置路径为sd卡请注意WRITE_EXTERNAL_STORAGE权限
        // 注：AUDIO_FORMAT参数语记需要更新版本才能生效
        mRecognize.setParameter(SpeechConstant.AUDIO_FORMAT, "wav");
        mRecognize.setParameter(SpeechConstant.ASR_AUDIO_PATH, Environment.getExternalStorageDirectory()+"/msc/recognize.wav");
    }

    // 定义录音的动态权限
    private void soundPermissions() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{
                    android.Manifest.permission.RECORD_AUDIO}, 1);
        }
    }
    /**
     * 重写onRequestPermissionsResult方法
     * 获取动态权限请求的结果,再开启录音
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

        } else {
            Toast.makeText(this, "拒绝权限无法正常使用", Toast.LENGTH_SHORT).show();

        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
//    判断密码
    public void JudgePassword(TextView editText,int raw){

     String password=editText.getText().toString();
        if (password.equals("猪")){
            MediaPlayer plays = MediaPlayer.create(LoginActivity.this,
                    raw);
            plays.setVolume(0.1f, 0.1f);//声道
            plays.start();
//        给MediaPlayer对象加上播放完毕的监听：

            plays.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                @Override
                public void onCompletion(MediaPlayer mp) {
                    Log.d("tag", "播放完毕");
                    //根据需要添加自己的代码。。。
                    speak();

////                    ccccccccccccc
//                    Intent intent=new Intent();
//                    intent.setClass(LoginActivity.this, MainActivity.class);
//                    startActivity(intent);
//                    finish();
                }

            });
        }else {
            MediaPlayer plays = MediaPlayer.create(LoginActivity.this,
                    raw);
            plays.setVolume(0.1f, 0.1f);//声道
            plays.start();
//        给MediaPlayer对象加上播放完毕的监听：

            plays.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                @Override
                public void onCompletion(MediaPlayer mp) {
                    Log.d("tag", "播放完毕");
                    //根据需要添加自己的代码。。。
                    speak();

////                    ccccccccccccc
//                    Intent intent=new Intent();
//                    intent.setClass(LoginActivity.this, MainActivity.class);
//                    startActivity(intent);
//                    finish();
                }

            });
        }
    }
public void speak(){
    int ret = 0; // 函数调用返回值
    mResultText.setText(null);// 清空显示内容
//                    Toast.makeText(LoginActivity.this, mResultText.getText().toString(), Toast.LENGTH_SHORT).show();

    mRecognizeResults.clear();
    // 设置参数
    resetParam();
//    boolean isShowDialog = mSharedPreferences.getBoolean("show_dialogs", false);
    boolean isShowDialog = mSharedPreferences.getBoolean("show_dialog", true);
    if (isShowDialog) {
        // 显示听写对话框
        mRecognizeDialog.setCanceledOnTouchOutside(false);// 设置点击屏幕Dialog不消失
        mRecognizeDialog.setListener(mRecognizeDialogListener);
        mRecognizeDialog.show();
//                    showTip("请开始说话………");
    } else {
        // 不显示听写对话框
        ret = mRecognize.startListening(mRecognizeListener);
        if (ret != ErrorCode.SUCCESS) {
//                        showTip("听写失败,错误码：" + ret);
            // 显示听写对话框
//            mRecognizeDialog.setCanceledOnTouchOutside(false);// 设置点击屏幕Dialog不消失
            mRecognizeDialog.setListener(mRecognizeDialogListener);
            mRecognizeDialog.show();
        } else {
//                        showTip("请开始说话…");
        }
    }

}


}





