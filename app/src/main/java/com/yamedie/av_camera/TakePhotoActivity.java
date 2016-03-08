package com.yamedie.av_camera;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.linj.FileOperateUtil;
import com.linj.album.view.FilterImageView;
import com.linj.camera.view.CameraContainer;
import com.linj.camera.view.CameraView;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import com.yamedie.common.CommonDefine;
import com.yamedie.utils.FileUtil;
import com.yamedie.utils.ImageUtil;
import com.yamedie.utils.Logger;

public class TakePhotoActivity extends BaseActivity implements View.OnClickListener, CameraContainer.TakePictureListener {
    public final static String TAG = "TakePhotoActivity";
    private final String IMAGE_TYPE = "image/*";
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
    private ImageView mIvPotoLibrary;
    private View mHeaderBar;
    private boolean isRecording = false;
    private String mImagePath;
    private final int TAG_CHOOSE_IMG = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_take_photo);
        init();
        initUpdate();
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

        mIvPotoLibrary = ((ImageView) findViewById(R.id.iv_pic_library));
        mIvPotoLibrary.setOnClickListener(this);

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
            Logger.e("ActivityResult resultCode error2");
            return;
        }
        ContentResolver resolver = getContentResolver();
        //根据tag判断回调类型
        if (requestCode == TAG_CHOOSE_IMG) {//选取图片回调
            //获得图片的uri
            Uri originalUri = data.getData();
            String path = null;
            //有的时候uri获取过来是file路径,所以要进行区分,否则会空指针
            Logger.i("uri String :" + originalUri.toString());
            if (originalUri.toString().startsWith("file:///")) {
                if (ImageUtil.isPicture(originalUri.toString())) {
                    path = originalUri.getPath();
                } else {
                    toastGo("您选取的不是图片!");
                }
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    path = FileUtil.getPathFromUri(getApplication(), originalUri);
                } else {
                    //显得到bitmap图片这里开始的第二部分，获取图片的路径：
                    String[] proj = {MediaStore.Images.Media.DATA};
                    //好像是android多媒体数据库的封装接口，具体的看Android文档
                    Cursor cursor = getContentResolver().query(originalUri, proj, null, null, null);
                    //按我个人理解 这个是获得用户选择的图片的索引值
                    cursor.moveToFirst();
                    int column_index = cursor.getColumnIndex(proj[0]);
                    //将光标移至开头 ，这个很重要，不小心很容易引起越界
                    //最后根据索引值获取图片路径
                    path = cursor.getString(column_index);
                    Logger.i(1, "cursor index:" + column_index);
                    Logger.i(1, "cursor get String:" + cursor.getString(0));
                    cursor.close();
                }
                Logger.i("选取图片path:" + path);
            }
            //获取到图片路径后即跳转到展示图片页面
            if (!TextUtils.isEmpty(path)) {
                Intent intent = new Intent(getApplication(), ShowIMGActivity.class);
                intent.setAction(CommonDefine.ACTION_CHOOSE_PIC_TO_SHOW_IMG);
                intent.putExtra(CommonDefine.TAG_IMAGE_PATH, path);
                startActivity(intent);
            } else {
                toastGo("选取图片失败!");
            }
            mobClickAgentGo(CommonDefine.UM_TAKE_PHOTO_CHOOSE_PIC);
        }
    }

    /**
     * 初始化自动更新方法,利用反射判断百度 友盟 360的自动更新是否存在,是的话则使用其自动更新
     * 此方法需要在build文件中进行配置,即根据不同渠道配置不同的依赖
     */
    private void initUpdate() {
        try {//友盟
            Class classUmeng = Class.forName("com.umeng.update.UmengUpdateAgent");
            Logger.i("umeng update exist");
            Method updateMethodUmeng = classUmeng.getDeclaredMethod("update", Context.class);
            Object classObj = classUmeng.newInstance();
            Logger.i("获取到方法:" + classObj.toString());
            updateMethodUmeng.invoke(classObj, TakePhotoActivity.this);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        try {//360
            Class classW360 = Class.forName("com.qihoo.appstore.updatelib.UpdateManager");
            Logger.i("360 update exist");
            Method updateMethodW360 = classW360.getDeclaredMethod("checkUpdate", Context.class);
            Object classObj = classW360.newInstance();
            Logger.i("获取到方法:" + classObj.toString());
            updateMethodW360.invoke(classObj, TakePhotoActivity.this);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

        try {//百度
            Class classBD = Class.forName("com.baidu.autoupdatesdk.BDAutoUpdateSDK");
            Logger.i("baidu update exist");
            Method updateMethodW360 = classBD.getDeclaredMethod("silenceUpdateAction", Context.class);
            Object classObj = classBD.newInstance();
            Logger.i("获取到方法:" + classObj.toString());
            updateMethodW360.invoke(classObj, TakePhotoActivity.this);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    /**
     * 选择图片
     */
    private void choosePic() {
        Intent getAlbum = new Intent(Intent.ACTION_GET_CONTENT);
        getAlbum.setType(IMAGE_TYPE);
        startActivityForResult(getAlbum, TAG_CHOOSE_IMG);
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
                mobClickAgentGo(CommonDefine.UM_TAKE_PHOTO_TAKE_PHOTO);
                break;
            case R.id.iv_pic_library:
                choosePic();
                break;
            case R.id.btn_thumbnail:
                choosePic();
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
    }

    @Override
    public void onAnimationEnd(Bitmap bm, boolean isVideo) {
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
            intent.setAction(CommonDefine.ACTION_TAKE_PHOTO_TO_SHOW_IMG);
            intent.setClass(TakePhotoActivity.this, ShowIMGActivity.class);
            startActivity(intent);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
