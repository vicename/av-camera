package com.yamedie.av_camera;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.umeng.analytics.MobclickAgent;

import java.util.concurrent.Future;

/**
 * Created by Li Dachang on 16/1/25.
 * ..-..---.-.--..---.-...-..-....-.
 */
public class BassActivity extends AppCompatActivity {
    private Context mContext;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        mContext = this;
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        new Thread(new Runnable() {
            @Override
            public void run() {
                MobclickAgent.onResume(mContext);

            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        new Thread(new Runnable() {
            @Override
            public void run() {
                MobclickAgent.onPause(mContext);

            }
        });
    }

    public void toastGo(final String s) {
        Toast.makeText(BassActivity.this, s, Toast.LENGTH_SHORT).show();
    }

    protected Future submitTask(Runnable task) {
        return ApplicationPlus.getInstance().submitTask(task);
    }
}
