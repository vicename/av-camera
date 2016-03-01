package com.yamedie.av_camera;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.faceplusplus.api.FaceDetecter;
import com.facepp.error.FaceppParseException;
import com.facepp.http.HttpRequests;
import com.facepp.http.PostParameters;
import com.linj.FileOperateUtil;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.yamedie.common.CommonDefine;
import com.yamedie.utils.CommonUtils;
import com.yamedie.utils.FileUtil;
import com.yamedie.utils.HttpHandler;
import com.yamedie.utils.ImageUtil;
import com.yamedie.utils.Logger;

public class ShowIMGActivity extends BaseActivity {
    private final String IMAGE_TYPE = "image/*";
    //这里的IMAGE_CODE是自己任意定义的
    private final int TAG_CHOOSE_IMG = 0;
    private final int TAG_TAKE_PHOTO = 11000;
    private Button mBtnAllPics;
    private Button mBtnTakePhoto;
    private Button mBtnChoosePic;
    private Button mBtnCheckFace;
    private ImageView mIvShowPhoto;
    private List<Uri> list;
    private Bitmap mBitmap;//图片对象
    private FaceDetecter detecter;
    private Handler detectHanler;
    private HandlerThread detectThread;
    private HttpRequests request;
    private String mPhotoPath;
    private TextView tvName;
    private TextView tvSimilar;
    private ImageView mIvGoRetakePhoto;
    private ImageView mIvGoPhotoLibrary;
    private AlertDialog mConnectingDialog;
    private boolean mFlagIsConnecting;
    private String mAction;
    private final String ACTION_TAKE_PHOTO = CommonDefine.ACTION_TAKE_PHOTO_TO_SHOW_IMG;
    private final String ACTION_CHOOSE_PIC = CommonDefine.ACTION_CHOOSE_PIC_TO_SHOW_IMG;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_img);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        initData();
        initView();
    }

    private void initData() {
        list = new ArrayList<>();
        detecter = new FaceDetecter();
        detecter.init(ShowIMGActivity.this, CommonDefine.API_KEY_VALUE);
        detectThread = new HandlerThread("detect");
        detectThread.start();
        detectHanler = new Handler(detectThread.getLooper());
        request = new HttpRequests(CommonDefine.API_KEY_VALUE, CommonDefine.API_SECRET_VALUE);
        Intent intent = getIntent();
        if (intent != null) {
            mPhotoPath = getIntent().getStringExtra(CommonDefine.TAG_IMAGE_PATH);//获取传过来的图片
            if (mPhotoPath != null) {
                mBitmap = BitmapFactory.decodeFile(mPhotoPath);//获取图片为bitmap
            }
            mAction = getIntent().getAction();
        }
        if (!TextUtils.isEmpty(mPhotoPath)) {
            Logger.i("---", mPhotoPath);
        }
    }

    private void initView() {
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new ClickCheckFaceServer());
        fab.setRippleColor(getResources().getColor(R.color.bright_foreground_inverse_material_light));
        mIvShowPhoto = (ImageView) findViewById(R.id.iv_show_photo);
        mIvShowPhoto.setImageBitmap(mBitmap);
        tvName = (TextView) findViewById(R.id.tv_name);
        tvSimilar = (TextView) findViewById(R.id.tv_similar);
        mBtnTakePhoto = (Button) findViewById(R.id.btn_take_photo);
        mBtnTakePhoto.setOnClickListener(new ClickTakePhoto());
        mBtnChoosePic = (Button) findViewById(R.id.btn_choose_pic);
        mBtnChoosePic.setOnClickListener(new ClickChoosePic());
        mBtnAllPics = (Button) findViewById(R.id.btn_all_pics);
        mBtnAllPics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getAllPic();
            }
        });
        mBtnCheckFace = (Button) findViewById(R.id.btn_check_face);
        mBtnCheckFace.setOnClickListener(new ClickCheckFaceServer());
        mIvGoRetakePhoto = ((ImageView) findViewById(R.id.iv_go_retake_photo));
        mIvGoRetakePhoto.setOnClickListener(new ClickTakePhoto());
        mIvGoPhotoLibrary = ((ImageView) findViewById(R.id.iv_go_photo_library));
        mIvGoPhotoLibrary.setOnClickListener(new ClickChoosePic());

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            Logger.i("---", "ActivityResult resultCode error");
            return;
        }
        //外界的程序访问ContentProvider所提供数据 可以通过ContentResolver接口
        ContentResolver resolver = getContentResolver();
        //根据tag判断回调类型
        if (requestCode == TAG_CHOOSE_IMG) {//选取图片回调
            try {
                //获得图片的uri
                Uri originalUri = data.getData();
                //有的时候uri获取过来是file路径,所以要进行区分,否则会空指针
                if (originalUri.toString().startsWith("file:///")) {
                    if (ImageUtil.isPicture(originalUri.toString())) {
                        mPhotoPath = originalUri.getPath();
                        mBitmap = BitmapFactory.decodeFile(mPhotoPath);
                        mIvShowPhoto.setImageBitmap(mBitmap);
                    } else {
                        toastGo("您选取的不是图片!");
                    }
                } else {
                    mBitmap = MediaStore.Images.Media.getBitmap(resolver, originalUri);
                    mBitmap = ImageUtil.compressBitmap(mBitmap, 720);
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
                    Logger.i("-----", "选取图片path:" + mPhotoPath);
                    mAction = ACTION_CHOOSE_PIC;
                    tvName.setVisibility(View.INVISIBLE);
                    tvSimilar.setVisibility(View.INVISIBLE);
                }

            } catch (IOException e) {
                Logger.i("---err", e.toString());
            }
        }
        //拍照回调
        if (requestCode == TAG_TAKE_PHOTO) {
            Bundle bundle = data.getExtras();
            boolean isStartSelected = bundle.getBoolean("bbbb");
            if (isStartSelected) {
                choosePic();
                return;
            }
            String imagePath = bundle.getString("aaaa");
            if (imagePath != null) {
                mPhotoPath = imagePath;
                File file = new File(imagePath);
                Uri uri = Uri.fromFile(file);
                Bitmap bm = BitmapFactory.decodeFile(imagePath);
//                bm=ImageUtil.compressBitmap(bm, 120);
                mBitmap = bm;
                mIvShowPhoto.setImageBitmap(bm);
                tvName.setVisibility(View.INVISIBLE);
                tvSimilar.setVisibility(View.INVISIBLE);
            }
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

    private class ClickTakePhoto implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            finish();
        }
    }

    private class ClickChoosePic implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            mobClickAgentGo(CommonDefine.UM_SHOW_IMG_CHOOSE_PIC);
            choosePic();
        }
    }

    private class ClickCheckFaceServer implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            createConnectingDialog();
            CommonUtils.disableViewForSeconds(v, 666);
            mobClickAgentGo(CommonDefine.UM_FIND_TEACHER_ON_CHOOSE_PIC);
            //获取当前时间戳,用于统计找老师用时
            Date date = new Date();
            long longAgo = date.getTime();
            Logger.i(1);
            //根据不同的action打不同的点
            if (!TextUtils.isEmpty(mAction)) {
                if (mAction.equals(ACTION_TAKE_PHOTO)) {
                    mobClickAgentGo(CommonDefine.UM_FIND_TEACHER_ON_TAKE_PHOTO);
                } else {
                    mobClickAgentGo(CommonDefine.UM_FIND_TEACHER_ON_CHOOSE_PIC);
                }
            } else {
                Logger.e("check face:got no action!!---dc");
            }
            Logger.i(2);
            upLoadAndCheckFace(longAgo);
        }
    }

    private class ClickCheckFace implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            FaceDetecter.Face[] faces = detecter.findFaces(mBitmap);
            Logger.i("---", "face[]", faces);
            detectHanler.post(new Runnable() {
                @Override
                public void run() {
                    FaceDetecter.Face[] faces = detecter.findFaces(mBitmap);
//                    Logger.i("---","faces size",faces.length);
                    if (faces == null) {
                        toastGo("没有人脸");
                        return;
                    }
                    try {
                        request.offlineDetect(detecter.getImageByteArray(), detecter.getResultJsonString(), new PostParameters());
                    } catch (FaceppParseException e) {
                        e.printStackTrace();
                    }
                    final Bitmap bit = getFaceInfoBitmap(faces, mBitmap);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mIvShowPhoto.setImageBitmap(bit);
                            System.gc();
                        }
                    });

                }
            });
        }
    }

    /**
     * 上传图片并寻找相似人脸
     *
     * @param longAgo 当前时间戳,用于统计耗时
     */
    private void upLoadAndCheckFace(final long longAgo) {
        RequestParams params = new RequestParams();
        Bitmap bitmap = mBitmap;
        if (mBitmap.getHeight() >= 1000) {
            bitmap = ImageUtil.scalePicByMaxSide(mBitmap, 960);
        }
        Logger.i(1, "上传的图片宽高:" + bitmap.getWidth() + "_" + bitmap.getHeight());
        byte[] bytes = FileUtil.Bitmap2Bytes(bitmap);
        Logger.i(1, "上传的图片大小:" + bytes.length);
        String tempPath = FileOperateUtil.getTempFolderPath(ShowIMGActivity.this, "av-camera");
        tempPath = CommonDefine.PIC_TEMP_PATH + "temp.jpg";
        Logger.i(1, "temp path:", tempPath);
        File file = FileUtil.bytes2File(tempPath, bytes);
        try {
            params.put("pic", file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
//        params.put("name", "hahahahaha");
        HttpHandler.postImg(params, CommonDefine.URL_UPLOAD_2, new JsonHttpResponseHandler() {
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
                        Logger.i(1, "status:" + status);
                        switch (status) {
                            case 0:
                                JSONArray girls = response.getJSONArray("av_girls");
                                JSONObject girlJb = girls.getJSONObject(0);
                                String url = girlJb.getString("url");
                                String name = girlJb.getString("name");
                                float similar = (float) girlJb.getDouble("similarity");
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
                toastGo("网络连接错误,请检-查一下网络哈");
                Logger.e("网络请求失败-2");
                mConnectingDialog.dismiss();
            }
        });

    }

    private void goShowTeacher(String imgUrl, String name, float similarity, long longAgo) {
        Intent intent = new Intent();
        intent.setClass(ShowIMGActivity.this, ShowTeacherActivity.class);
        intent.putExtra(CommonDefine.TAG_IMAGE_PATH, mPhotoPath);
        intent.putExtra(CommonDefine.TAG_IMAGE_URL, imgUrl);
        intent.putExtra(CommonDefine.TAG_TEACHER_NAME, name);
        intent.putExtra(CommonDefine.TAG_SIMILAR, String.valueOf(similarity));
        intent.putExtra(CommonDefine.TAG_TIME, longAgo);
        startActivity(intent);
    }

    /**
     * 创建连接对话框
     */
    private void createConnectingDialog() {
        mFlagIsConnecting = true;
        final View connectingDialogView = View.inflate(ShowIMGActivity.this, R.layout.dialog_connecting, null);
        android.app.AlertDialog.Builder ab = new android.app.AlertDialog.Builder(ShowIMGActivity.this);
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

    /**
     * 调用浏览器搜索老师
     *
     * @param value 老师名字
     */
    public void jumpBrowser(String value) {

        String url = "http://wap.baidu.com/s?word=" + value; // web address
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        startActivity(intent);
    }

    public static Bitmap getFaceInfoBitmap(FaceDetecter.Face[] faceinfos, Bitmap oribitmap) {
        Bitmap tmp;
        tmp = oribitmap.copy(Bitmap.Config.ARGB_8888, true);

        Canvas localCanvas = new Canvas(tmp);
        Paint localPaint = new Paint();
        localPaint.setColor(0xffff0000);
        localPaint.setStyle(Paint.Style.STROKE);
        for (FaceDetecter.Face localFaceInfo : faceinfos) {
            RectF rect = new RectF(oribitmap.getWidth() * localFaceInfo.left, oribitmap.getHeight()
                    * localFaceInfo.top, oribitmap.getWidth() * localFaceInfo.right,
                    oribitmap.getHeight()
                            * localFaceInfo.bottom);
            localCanvas.drawRect(rect, localPaint);
        }
        return tmp;
    }


    public void getAllPic() {
        String[] projection = {MediaStore.Images.Thumbnails._ID};
        Uri uri = null;
        Cursor cursor = managedQuery(
                MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI,
                projection, //List of columns to return ：想要他返回的列
                null, // Return all rows
                null,
                null);
        int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Thumbnails._ID);
        int i = 0;
        while (cursor.moveToNext() && i < cursor.getCount()) {
            //移到指定的位置，遍历数据库
            cursor.moveToPosition(i);
            uri = Uri.withAppendedPath(MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI, cursor.getInt(columnIndex) + "");
            Logger.i("---", "pic-uri:", uri);
            list.add(uri);
            i++;
        }
        cursor.close();//关闭数据库
    }
}
