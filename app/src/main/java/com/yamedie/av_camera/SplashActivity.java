package com.yamedie.av_camera;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.WindowManager;

import com.yamedie.common.CommonDefine;
import com.yamedie.utils.SharedPreferencesUtils;

public class SplashActivity extends AppCompatActivity {
    private SharedPreferencesUtils sPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);
        sPref = new SharedPreferencesUtils(this);
        final boolean isFirst=sPref.getPrefBoolean(CommonDefine.IS_FIRST, true);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (isFirst) {
                    Intent intent = new Intent(SplashActivity.this, GuidActivity.class);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(SplashActivity.this, TakePhotoActivity.class);
                    startActivity(intent);
                }
                finish();
            }
        }).start();
    }
}
