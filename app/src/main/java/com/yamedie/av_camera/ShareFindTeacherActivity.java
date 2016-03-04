package com.yamedie.av_camera;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;

import com.linj.FileOperateUtil;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.yamedie.common.CommonDefine;
import com.yamedie.utils.CommonUtils;
import com.yamedie.utils.FileUtil;
import com.yamedie.utils.HttpHandler;
import com.yamedie.utils.ImageUtil;
import com.yamedie.utils.Logger;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;

public class ShareFindTeacherActivity extends BaseActivity {
    private final String INTENT_ACTION = "android.intent.action.SEND";
    private Bitmap mBitmap;
    private ImageView mIvShowPhoto;
    private boolean mFlagIsConnecting;
    private AlertDialog mConnectingDialog;
    private String mPhotoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_find_teacher);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new ClickFindTeacher());
        Intent intent = getIntent();
        Logger.i("ddddddddddd" + intent.getAction());
        mIvShowPhoto = ((ImageView) findViewById(R.id.show_photo));
        if (intent.getAction() != null && INTENT_ACTION.equals(intent.getAction())) {
            handleSendImage(intent);
        }
    }

    /**
     * 接收外部intent传来的image
     *
     * @param intent intent,来自其他应用的图片信息
     */
    private void handleSendImage(Intent intent) {
        Uri originalUri = intent.getParcelableExtra(Intent.EXTRA_STREAM);
        if (originalUri != null) {
            if (originalUri.toString().startsWith("file:///")) {
                mPhotoPath = originalUri.getPath();
                mBitmap = BitmapFactory.decodeFile(mPhotoPath);
                mIvShowPhoto.setImageBitmap(mBitmap);
            } else {
                ContentResolver resolver = getContentResolver();
                try {
                    mBitmap = MediaStore.Images.Media.getBitmap(resolver, originalUri);
                    mIvShowPhoto.setImageBitmap(mBitmap);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        mPhotoPath = FileUtil.getPathFromUri(getApplication(), originalUri);
                    } else {
                        //显得到bitmap图片这里开始的第二部分，获取图片的路径：
                        String[] proj = {MediaStore.Images.Media.DATA};
                        //好像是android多媒体数据库的封装接口，具体的看Android文档
                        Cursor cursor = managedQuery(originalUri, proj, null, null, null);
                        //按我个人理解 这个是获得用户选择的图片的索引值
                        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                        //将光标移至开头 ，这个很重要，不小心很容易引起越界
                        cursor.moveToFirst();
                        Logger.i(1, "cusor index:" + column_index);
                        Logger.i(1, "cusor get String:" + cursor.getString(0));
                        //最后根据索引值获取图片路径
                        mPhotoPath = cursor.getString(column_index);
                        cursor.close();
                    }
                } catch (IOException e) {
                    Logger.e("解析照片失败!!");
                    e.printStackTrace();
                }

            }
        }
    }

    class ClickFindTeacher implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            createConnectingDialog();
            CommonUtils.disableViewForSeconds(v, 666);
            //获取当前时间戳,用于统计找老师用时
            Date date = new Date();
            long longAgo = date.getTime();
            mobClickAgentGo(CommonDefine.UM_FIND_TEACHER_ON_SHARE);
            upLoadAndCheckFace(longAgo);
        }
    }

    private void upLoadAndCheckFace(final long longAgo) {
        RequestParams params = new RequestParams();
        long time = CommonUtils.timeSpendCheck();
        Bitmap bitmap = mBitmap;
        if (mBitmap.getHeight() >= 1000) {
            bitmap = ImageUtil.scalePicByMaxSide(mBitmap, 960);
        }
        byte[] bytes = ImageUtil.compressBitmapInOrderSize(bitmap, 100);//将图片压缩到100K以内
        Logger.i("上传图片处理耗时:" + CommonUtils.timeSpendCheck(time));
        Logger.i(1, "上传的图片宽高:" + bitmap.getWidth() + "_" + bitmap.getHeight());
        Logger.i(1, "上传的图片大小:" + bytes.length / 1024 + "kb");
        String tempPath = FileOperateUtil.getTempFolderPath(ShareFindTeacherActivity.this, "ac-temp");
        tempPath += "temp.jpg";
        Logger.i(1, "temp path:", tempPath);
        File file = FileUtil.bytes2File(tempPath, bytes);
        try {
            params.put("pic", file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        String token = CommonUtils.getTime(false);
        token = CommonUtils.stringToMD5("temp.jpg" + token);
        HttpHandler.postImg(params, CommonDefine.URL_UPLOAD_2, token, new JsonHttpResponseHandler() {
            @Override
            public void onFinish() {
                super.onFinish();
                mConnectingDialog.dismiss();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Logger.i(1, "response" + response.toString());
                if (!mFlagIsConnecting) {
                    return;
                }
                if (statusCode == 200) {
                    try {
                        int status = response.getInt("status");
                        String info;
                        switch (status) {
                            case 0:
                                JSONArray girls = response.getJSONArray("av_girls");
                                JSONObject girlJb = girls.getJSONObject(0);
                                String url = girlJb.getString("url");
                                String name = girlJb.getString("name");
                                double similar = girlJb.getDouble("similarity");
                                goShowTeacher(url, name, similar, longAgo);
                                break;
                            case -1:
                                toastGo("呃,发生了未知错误...");
                                break;
                            case 1201:
                                info = response.getString("info");
                                toastGo("没有识别出人脸,请重新拍一张吧~");
                                break;
                            case 1202:
                                info = response.getString("info");
                                toastGo("此人骨骼惊奇,没能帮你找到相似的老师呢");
                                break;
                            case 4001:
                                toastGo("你选的图片太大啦,人家装不下了呢");
                                break;
                            case 4002:
                                toastGo("这种文件格式人家放不进去呢,选个别的吧");
                                break;
                            case 4003:
                                toastGo("非法请求?(警惕脸)");
                                break;
                            case 4004:
                                toastGo("图片上传失败,请重新试一下吧");
                                break;
                            case 4005:
                                toastGo("文件太多啦,人家都装满了呢");
                                break;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    toastGo("网络出现问题!");
                }
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                toastGo("网络连接错误,请检查一下网络哈");
                Logger.e("网络请求失败-1");
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                toastGo("网络连接错误,请检查一下网络哈");
                Logger.e("网络请求失败-2");
                mConnectingDialog.dismiss();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                toastGo("网络连接错误,请检查一下网络哈");
                Logger.e("网络请求失败-3");
                Logger.e(throwable.toString());
            }
        });
    }

    /**
     * 跳转到showTeacher页面
     *
     * @param imgUrl     图片url
     * @param name       名字
     * @param similarity 相似度
     * @param longAgo    请求时的毫秒数
     */
    private void goShowTeacher(String imgUrl, String name, double similarity, long longAgo) {
        BigDecimal bigDecimal = new BigDecimal((float) similarity);
        float newSimilarity = bigDecimal.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
        Intent intent = new Intent();
        intent.setClass(ShareFindTeacherActivity.this, ShowTeacherActivity.class);
        intent.putExtra(CommonDefine.TAG_IMAGE_PATH, mPhotoPath);
        intent.putExtra(CommonDefine.TAG_IMAGE_URL, imgUrl);
        intent.putExtra(CommonDefine.TAG_TEACHER_NAME, name);
        intent.putExtra(CommonDefine.TAG_SIMILAR, String.valueOf(newSimilarity));
        intent.putExtra(CommonDefine.TAG_TIME, longAgo);
        startActivity(intent);
    }

    /**
     * 创建连接对话框
     */
    private void createConnectingDialog() {
        mFlagIsConnecting = true;
        final View connectingDialogView = View.inflate(ShareFindTeacherActivity.this, R.layout.dialog_connecting, null);
        android.app.AlertDialog.Builder ab = new android.app.AlertDialog.Builder(ShareFindTeacherActivity.this);
        ab.setView(connectingDialogView);
        ab.setCancelable(false);
        ab.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    mConnectingDialog.dismiss();
                    mFlagIsConnecting = false;
                    return true;
                }
                return false;
            }
        });
        mConnectingDialog = ab.create();
        mConnectingDialog.show();
    }
}
