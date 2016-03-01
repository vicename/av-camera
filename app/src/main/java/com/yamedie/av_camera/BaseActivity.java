package com.yamedie.av_camera;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.umeng.analytics.MobclickAgent;
import com.yamedie.common.CommonDefine;
import com.yamedie.utils.CommonUtils;
import com.yamedie.utils.Logger;

import java.util.Map;
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

    /**
     * 友盟打点-计数事件
     *
     * @param tag 标识
     */
    public void mobClickAgentGo(String tag) {
//        if (!CommonDefine.IS_DEBUG)
        MobclickAgent.onEvent(mContext, tag);
//        MobclickAgent.onEventValue(mContext, tag, null, 1);
    }

    public void mobClickAgentGo(String tag, Map<String, String> map) {
//        if (!CommonDefine.IS_DEBUG)
//        MobclickAgent.onEvent(mContext, tag, map);
        MobclickAgent.onEventValue(mContext, tag, map, 1);
    }

    public void toastGo(final String s) {
        if (!CommonUtils.isInMainThread()) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(BaseActivity.this, s, Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(BaseActivity.this, s, Toast.LENGTH_SHORT).show();
        }
    }

    protected Future submitTask(Runnable task) {
        return ApplicationPlus.getInstance().submitTask(task);
    }
}
