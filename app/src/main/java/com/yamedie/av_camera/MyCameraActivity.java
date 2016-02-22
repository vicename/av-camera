package com.yamedie.av_camera;

import android.graphics.Bitmap;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.linj.camera.view.CameraContainer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.yamedie.surfaceView.MySurfaceView;
import com.yamedie.utils.Logger;

public class MyCameraActivity extends BassActivity implements CameraContainer.TakePictureListener{
    private Button btn_camera_capture = null;
    private Button btn_camera_cancel = null;
    private Button btn_camera_ok = null;

    private Camera camera = null;
    private MySurfaceView mySurfaceView = null;

    private byte[] buffer = null;

    private final int TYPE_FILE_IMAGE = 1;
    private final int TYPE_FILE_VEDIO = 2;
    private Camera.PictureCallback pictureCallback = new Camera.PictureCallback() {

        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            if (data == null) {
                Logger.i("MyPicture", "picture taken data: null");
            } else {
                Logger.i("MyPicture", "picture taken data: " + data.length);
            }

            buffer = new byte[data.length];
            buffer = data.clone();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_camera);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        initView();
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();

        camera.release();
        camera = null;
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        if (camera == null) {
            camera = getCameraInstance();
        }
        //必须放在onResume中，不然会出现Home键之后，再回到该APP，黑屏
        mySurfaceView = new MySurfaceView(getApplicationContext(), camera);
        FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
        preview.addView(mySurfaceView);
    }

    private void initView() {
        btn_camera_capture = (Button) findViewById(R.id.camera_capture);
        btn_camera_ok = (Button) findViewById(R.id.camera_ok);
        btn_camera_cancel = (Button) findViewById(R.id.camera_cancel);
        btn_camera_capture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                camera.takePicture(null, null, pictureCallback);

                btn_camera_capture.setVisibility(View.INVISIBLE);
                btn_camera_ok.setVisibility(View.VISIBLE);
                btn_camera_cancel.setVisibility(View.VISIBLE);
            }
        });
        btn_camera_ok.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //保存图片
                saveImageToFile();

                camera.startPreview();
                btn_camera_capture.setVisibility(View.VISIBLE);
                btn_camera_ok.setVisibility(View.INVISIBLE);
                btn_camera_cancel.setVisibility(View.INVISIBLE);
            }
        });
        btn_camera_cancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                camera.startPreview();
                btn_camera_capture.setVisibility(View.VISIBLE);
                btn_camera_ok.setVisibility(View.INVISIBLE);
                btn_camera_cancel.setVisibility(View.INVISIBLE);
            }
        });
    }

    /**
     * 得到一相机对象
     * @return 相机对象
     */
    private Camera getCameraInstance() {
        Camera camera = null;
        try {
            camera = camera.open();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return camera;
    }

    //-----------------------保存图片---------------------------------------

    /**
     * 保存图片
     */
    private void saveImageToFile() {
        File file = getOutFile(TYPE_FILE_IMAGE);
        if (file == null) {
            Toast.makeText(getApplicationContext(), "文件创建失败,请检查SD卡读写权限", Toast.LENGTH_SHORT).show();
            return;
        }
        Logger.i("MyPicture", "自定义相机图片路径:" + file.getPath());
        Toast.makeText(getApplicationContext(), "图片保存路径：" + file.getPath(), Toast.LENGTH_SHORT).show();
        if (buffer == null) {
            Logger.i("MyPicture", "自定义相机Buffer: null");
        } else {
            try {
                FileOutputStream fos = new FileOutputStream(file);
                fos.write(buffer);
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //-----------------------生成Uri---------------------------------------
    /**
     * 得到输出文件的URI
     * @param fileType 文件类型
     * @return 文件URI
     */
    private Uri getOutFileUri(int fileType) {
        return Uri.fromFile(getOutFile(fileType));
    }

    /**
     * 生成输出文件
     * @param fileType 文件类型
     * @return 输出文件
     */
    private File getOutFile(int fileType) {

        String storageState = Environment.getExternalStorageState();
        if (Environment.MEDIA_REMOVED.equals(storageState)) {
            Toast.makeText(getApplicationContext(), "oh,no, SD卡不存在", Toast.LENGTH_SHORT).show();
            return null;
        }

        File mediaStorageDir = new File(Environment
                .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                , "MyPictures");
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Logger.i("MyPictures", "创建图片存储路径目录失败");
                Logger.i("MyPictures", "mediaStorageDir : " + mediaStorageDir.getPath());
                return null;
            }
        }

        File file = new File(getFilePath(mediaStorageDir, fileType));

        return file;
    }


    /**
     * 生成输出文件路径
     * @param mediaStorageDir
     * @param fileType
     * @return
     */
    private String getFilePath(File mediaStorageDir, int fileType) {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
                .format(new Date());
        String filePath = mediaStorageDir.getPath() + File.separator;
        if (fileType == TYPE_FILE_IMAGE) {
            filePath += ("IMG_" + timeStamp + ".jpg");
        } else if (fileType == TYPE_FILE_VEDIO) {
            filePath += ("VIDEO_" + timeStamp + ".mp4");
        } else {
            return null;
        }
        return filePath;
    }

    @Override
    public void onTakePictureEnd(Bitmap bm) {

    }

    @Override
    public void onAnimtionEnd(Bitmap bm, boolean isVideo) {

    }

    @Override
    public void onSavePictureEnd(String path) {

    }
}
