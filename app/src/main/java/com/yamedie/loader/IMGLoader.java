package com.yamedie.loader;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.util.LruCache;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Li DaChang on 16/2/28.
 */

public class IMGLoader {
    private LruCache<String, Bitmap> lruCache;
    private final static int MAX_POOL = 10;
    private ExecutorService thread_pools;

    public IMGLoader() {
        int memory = (int) Runtime.getRuntime().maxMemory();
        lruCache = new LruCache<String, Bitmap>((memory) / 6) {
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getByteCount();
            }
        };
    }

    public Bitmap load(final String imgUrl, final ImageView imageView, final LoadCallBack loadCallBack) {
        if (imgUrl == null) {
            return null;
        }
        if (imgUrl.equals("")) {
            return null;
        }
        final Bitmap bit = lruCache.get(imgUrl);
        if (bit != null) {
            return bit;
        }
        if (thread_pools == null) {
            thread_pools = Executors.newFixedThreadPool(MAX_POOL);
        }
        final Handler hand = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (msg.what == 111) {
                    Bitmap bitmap = (Bitmap) msg.obj;
                    lruCache.put(imgUrl, bit);
                    loadCallBack.imgCallBack(bit, imageView);
                }
            }
        };
        Thread thread = new Thread() {
            public void run() {
                try {
                    URL url = new URL(imgUrl);
                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                    httpURLConnection.setConnectTimeout(60 * 1000);
                    httpURLConnection.setReadTimeout(60 * 1000);
                    httpURLConnection.setDoInput(true);
                    httpURLConnection.connect();
                    if (httpURLConnection.getResponseCode() == 200) {
                        InputStream in = httpURLConnection.getInputStream();
                        BitmapFactory.Options op = new BitmapFactory.Options();
                        Message msg = new Message();
                        msg.what = 111;
                        msg.obj = bit;
                        hand.sendMessage(msg);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        };
        thread_pools.execute(thread);
        return null;
    }

    public interface LoadCallBack {
        void imgCallBack(Bitmap bitmap, ImageView imageView);
    }
}
