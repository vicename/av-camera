package com.yamedie.av_camera;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;

import com.yamedie.utils.LoadImage;

public class GirlInfoActivity extends AppCompatActivity {

    private String mName;
    private Bitmap mBitmap;
    private String mImgUrl;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_girl_info);
        mName = getIntent().getStringExtra("girl_name");
//        byte[] bytes = getIntent().get("url");
        mImgUrl = getIntent().getStringExtra("url");
//        if (bytes.length > 0) {
//            mBitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
//        }
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(mName);
        setSupportActionBar(toolbar);
        init();
    }

    private void init() {
        final ImageView ivPic = (ImageView) findViewById(R.id.iv_pic);
        if (mImgUrl != null) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    final Bitmap bitmap = LoadImage.getBitmapFromUrl(mImgUrl);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ivPic.setImageBitmap(bitmap);
                        }
                    });
                }
            }).start();

        }
    }

}
