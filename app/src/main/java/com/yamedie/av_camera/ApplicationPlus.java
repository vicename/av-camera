package com.yamedie.av_camera;

import android.app.Application;

import com.yamedie.utils.Logger;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Created by Li Dachang on 16/1/25.
 * ..-..---.-.--..---.-...-..-....-.
 */
public class ApplicationPlus extends Application {
    private static ApplicationPlus mInstance;
    private ExecutorService mThreadPool;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        init();
    }

    public static ApplicationPlus getInstance() {
        return mInstance;
    }

    private void init() {
        mThreadPool = Executors.newCachedThreadPool();
    }

    public Future submitTask(Runnable task) {
        Logger.i("------applicationplus");
        return mThreadPool.submit(task);
    }
}
