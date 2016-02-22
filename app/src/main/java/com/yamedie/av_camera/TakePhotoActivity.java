package com.yamedie.av_camera;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.linj.FileOperateUtil;
import com.linj.album.view.FilterImageView;
import com.linj.camera.view.CameraContainer;
import com.linj.camera.view.CameraView;

import java.io.File;
import java.util.List;

import com.yamedie.common.CommonDefine;
import com.yamedie.utils.Logger;

public class TakePhotoActivity extends BassActivity implements View.OnClickListener, CameraContainer.TakePictureListener {
    public final static String TAG = "TakePhotoActivity";
    private boolean mIsRecordMode = false;
    private String mSaveRoot;
    private CameraContainer mContainer;
    private FilterImageView mThumbView;
    private ImageButton mCameraShutterButton;
    private ImageButton mRecordShutterButton;
    private ImageView mFlashView;
    private ImageButton mSwitchModeButton;
    private ImageView mSwitchCameraView;
    private ImageView mSettingView;
    private ImageView mVideoIconView;
    private View mHeaderBar;
    private boolean isRecording = false;
    private String mImagePath;
    private Bitmap mBitmap;
    private final int TAG_CHOOSE_IMG = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_take_photo);
        init();
    }

    private void init() {
        //文件存储路径
        mSaveRoot = CommonDefine.PIC_SAVE_PATH;
        mHeaderBar = findViewById(R.id.camera_header_bar);
        mContainer = (CameraContainer) findViewById(R.id.container);
        mThumbView = (FilterImageView) findViewById(R.id.btn_thumbnail);
        mVideoIconView = (ImageView) findViewById(R.id.videoicon);
        mCameraShutterButton = (ImageButton) findViewById(R.id.btn_shutter_camera);
        mRecordShutterButton = (ImageButton) findViewById(R.id.btn_shutter_record);
        mSwitchCameraView = (ImageView) findViewById(R.id.btn_switch_camera);
        mFlashView = (ImageView) findViewById(R.id.btn_flash_mode);

        mSwitchModeButton = (ImageButton) findViewById(R.id.btn_switch_mode);//切换模式
        mSettingView = (ImageView) findViewById(R.id.btn_other_setting);
        mSettingView.setVisibility(View.GONE);

        mThumbView.setOnClickListener(this);
        mCameraShutterButton.setOnClickListener(this);
        mRecordShutterButton.setOnClickListener(this);
        mFlashView.setOnClickListener(this);
        mSwitchModeButton.setOnClickListener(this);
        mSwitchModeButton.setVisibility(View.GONE);//毙掉这个按钮
        mSwitchCameraView.setOnClickListener(this);
        mSettingView.setOnClickListener(this);
        mContainer.setRootPath(mSaveRoot);
        setFlashMode();
        initThumbnail();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            Logger.i("---", "ActivityResult resultCode error2");
            return;
        }
//        if (requestCode == TAG_CHOOSE_IMG) {
//            Logger.i("------------chooooooo");
//            Intent intent = new Intent(TakePhotoActivity.this, ShowIMGActivity.class);
//            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//            try {
//                ContentResolver resolver = getContentResolver();
//                Uri originalUri = data.getData();
//                Bitmap bitmap = MediaStore.Images.Media.getBitmap(resolver, originalUri);
//                byte[] bytes = FileUtil.Bitmap2Bytes(bitmap);
//                intent.putExtra("in_camera", bytes);
//                intent.setAction("choose_img");
//                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                setResult(0, intent);
//                startActivity(intent);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            finish();
//        }

    }

    /**
     * 初始化缩略图
     */
    private void initThumbnail() {
        String thumbFolder = FileOperateUtil.getFolderPath(this, FileOperateUtil.TYPE_THUMBNAIL, mSaveRoot);
        List<File> files = FileOperateUtil.listFiles(thumbFolder, ".jpg");
        if (files != null && files.size() > 0) {
            Bitmap thumbBitmap = BitmapFactory.decodeFile(files.get(0).getAbsolutePath());
            if (thumbBitmap != null) {
                mThumbView.setImageBitmap(thumbBitmap);
                //视频缩略图显示播放图案
                if (files.get(0).getAbsolutePath().contains("video")) {
                    mVideoIconView.setVisibility(View.VISIBLE);
                } else {
                    mVideoIconView.setVisibility(View.GONE);
                }
            }
        } else {
            mThumbView.setImageBitmap(null);
            mVideoIconView.setVisibility(View.VISIBLE);
        }

    }

    /**
     * 设置闪光灯关闭
     */
    private void setFlashMode() {
        mContainer.setFlashMode(CameraView.FlashMode.OFF);
        mFlashView.setImageResource(R.drawable.btn_flash_off);
    }

    private void stopRecord() {
        mContainer.stopRecord(this);
        isRecording = false;
        mRecordShutterButton.setBackgroundResource(R.drawable.btn_shutter_record);
    }

    @Override
    public void onClick(View v) {
// TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.btn_shutter_camera:
                mCameraShutterButton.setClickable(false);
                mContainer.takePicture(this);
                break;
            case R.id.btn_thumbnail:
//                startActivity(new Intent(this,AlbumAty.class));
                Intent intent = new Intent();
                intent.putExtra("bbbb", true);
                setResult(RESULT_OK, intent);
                finish();
//                startActivity(new Intent(this,ShowIMGActivity.class));
                break;
            case R.id.btn_flash_mode:
                if (mContainer.getFlashMode() == CameraView.FlashMode.ON) {
                    mContainer.setFlashMode(CameraView.FlashMode.OFF);
                    mFlashView.setImageResource(R.drawable.btn_flash_off);
                } else if (mContainer.getFlashMode() == CameraView.FlashMode.OFF) {
                    mContainer.setFlashMode(CameraView.FlashMode.AUTO);
                    mFlashView.setImageResource(R.drawable.btn_flash_auto);
                } else if (mContainer.getFlashMode() == CameraView.FlashMode.AUTO) {
                    mContainer.setFlashMode(CameraView.FlashMode.TORCH);
                    mFlashView.setImageResource(R.drawable.btn_flash_torch);
                } else if (mContainer.getFlashMode() == CameraView.FlashMode.TORCH) {
                    mContainer.setFlashMode(CameraView.FlashMode.ON);
                    mFlashView.setImageResource(R.drawable.btn_flash_on);
                }
                break;
            case R.id.btn_switch_mode:
                if (mIsRecordMode) {
                    mSwitchModeButton.setImageResource(R.drawable.ic_switch_camera);
                    mCameraShutterButton.setVisibility(View.VISIBLE);
                    mRecordShutterButton.setVisibility(View.GONE);
                    //拍照模式下显示顶部菜单
                    mHeaderBar.setVisibility(View.VISIBLE);
                    mIsRecordMode = false;
                    mContainer.switchMode(0);
                    stopRecord();
                } else {
                    mSwitchModeButton.setImageResource(R.drawable.ic_switch_video);
                    mCameraShutterButton.setVisibility(View.GONE);
                    mRecordShutterButton.setVisibility(View.VISIBLE);
                    //录像模式下隐藏顶部菜单
                    mHeaderBar.setVisibility(View.GONE);
                    mIsRecordMode = true;
                    mContainer.switchMode(5);
                }
                break;
            case R.id.btn_shutter_record:
                if (!isRecording) {
                    isRecording = mContainer.startRecord();
                    if (isRecording) {
                        mRecordShutterButton.setBackgroundResource(R.drawable.btn_shutter_recording);
                    }
                } else {
                    stopRecord();
                }
                break;
            case R.id.btn_switch_camera:
                mContainer.switchCamera();
                break;
            case R.id.btn_other_setting:
//                mContainer.setWaterMark();
                break;
            default:
                break;
        }
    }

    private void backToLast() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (mImagePath != null) {
                    Intent intent = new Intent();
                    intent.putExtra("aaaa", mImagePath);
                    setResult(RESULT_OK, intent);
                    finish();
                }
            }
        });
    }

    @Override
    public void onTakePictureEnd(Bitmap bm) {
        mCameraShutterButton.setClickable(true);
        mBitmap = bm;
        Logger.i("---", "bm3 height" + mBitmap.getHeight());
//        Logger.i("---","mSavePath",path);
    }

    @Override
    public void onAnimtionEnd(Bitmap bm, boolean isVideo) {
        if (bm != null) {
            //生成缩略图
            Bitmap thumbnail = ThumbnailUtils.extractThumbnail(bm, 213, 213);
            mThumbView.setImageBitmap(thumbnail);
            if (isVideo)
                mVideoIconView.setVisibility(View.VISIBLE);
            else {
                mVideoIconView.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onSavePictureEnd(String path) {
        Logger.i("---", "save path", path);
        mImagePath = path;
        if (mImagePath != null) {
            Intent intent = new Intent();
            intent.putExtra(CommonDefine.TAG_IMAGE_PATH, mImagePath);
//            setResult(RESULT_OK, intent);
//            finish();
            intent.setClass(TakePhotoActivity.this,ShowIMGActivity.class);
            startActivity(intent);
//            finish();
        }
//        backToLast();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
