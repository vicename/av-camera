package com.yamedie.av_camera;

import android.content.Intent;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.yamedie.utils.CommonUtils;
import com.yamedie.utils.Logger;

public class MainActivity extends BaseActivity {
    private final int REQUEST_CAPTURE_IMAGE = 10086;
    private Button btnTakeCamera;
    private Bitmap photoBit;
    private ImageView ivShowPhoto;
    private final int TYPE_FILE_IMAGE = 1;
    private final int TYPE_FILE_VEDIO = 2;
    private Uri imageUri;
    private AudioManager mAudioManager;
    private Button btnGetIMG;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        initView();
    }

    private void initView() {
        btnTakeCamera = (Button) findViewById(R.id.btn_take_camera);
        btnTakeCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setSilence();
//                takeCamera();
//                takeMyCamera();
                takeOtherCamera();
            }
        });
        btnGetIMG = (Button) findViewById(R.id.btn_getIMG);
        btnGetIMG.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, ShowIMGActivity.class));
            }
        });
    }

    /**
     * 设置静音
     */
    public void setSilence() {
        mAudioManager = (AudioManager) getSystemService(MainActivity.AUDIO_SERVICE);
        int ringerMode = mAudioManager.getRingerMode();//当前声音模式
        Logger.i("---","ringerMode",ringerMode);
        mAudioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
        int ringerMode2 = mAudioManager.getRingerMode();//当前声音模式
        Logger.i("---","ringerMode2",ringerMode2);
    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if (resultCode == RESULT_OK) {
//            if (requestCode == REQUEST_CAPTURE_IMAGE) {
//                Bundle extras = data.getExtras();
//                ivShowPhoto.setImageURI(data.getData());
//
////                photoBit = (Bitmap) extras.get("data");
////                Logger.i("---","data",photoBit.toString());
////                ivShowPhoto.setImageBitmap(photoBit);
//            } else {
//                toastGo("啊啊");
//            }
//        }
//    }

    //得到输出文件的URI
    private Uri getOutFileUri(int fileType) {
        return Uri.fromFile(getOutFile(fileType));
    }

    //生成输出文件
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

    //生成输出文件路径
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

    public void takeCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        imageUri = getOutFileUri(TYPE_FILE_IMAGE);//得到一个File Uri
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, REQUEST_CAPTURE_IMAGE);
        }
    }

    /**
     * 调用自定义的相机拍照
     */
    public void takeMyCamera() {
        if (CommonUtils.checkCameraHardWare(MainActivity.this)) {
            Intent intent = new Intent(MainActivity.this, MyCameraActivity.class);
            startActivity(intent);
        }
    }

    public void takeOtherCamera(){
        if (CommonUtils.checkCameraHardWare(MainActivity.this)) {
            Intent intent = new Intent(MainActivity.this, TakePhotoActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
