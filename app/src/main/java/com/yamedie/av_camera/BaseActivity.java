package com.yamedie.av_camera;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.umeng.analytics.MobclickAgent;
import com.yamedie.utils.Logger;

import java.util.concurrent.Future;

/**
 * Created by Li Dachang on 16/1/25.
 * ..-..---.-.--..---.-...-..-....-.
 */
public class BaseActivity extends AppCompatActivity {
    private Context mContext;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        mContext = this;
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
//        submitTask(new Runnable() {
//            @Override
//            public void run() {
//                Logger.i("-------submite task");
//            }
//        });
        new Thread(new Runnable() {
            @Override
            public void run() {
                MobclickAgent.onResume(mContext);

            }
        }).start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        new Thread(new Runnable() {
            @Override
            public void run() {
                MobclickAgent.onPause(mContext);

            }
        }).start();
    }

    public void toastGo(final String s) {
        Toast.makeText(BaseActivity.this, s, Toast.LENGTH_SHORT).show();
    }

    protected Future submitTask(Runnable task) {
        return ApplicationPlus.getInstance().submitTask(task);
    }
}
