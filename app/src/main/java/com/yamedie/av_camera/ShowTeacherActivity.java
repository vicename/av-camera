package com.yamedie.av_camera;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;
import com.linj.FileOperateUtil;
import com.linj.imageloader.DisplayImageOptions;
import com.linj.imageloader.DownloadImgUtils;
import com.linj.imageloader.ImageLoader;
import com.linj.imageloader.displayer.RoundedBitmapDisplayer;
import com.squareup.picasso.Picasso;
import com.yamedie.common.CommonDefine;
import com.yamedie.loader.IMGLoader;
import com.yamedie.utils.ImageUtil;
import com.yamedie.utils.Logger;

import java.io.File;

public class ShowTeacherActivity extends BaseActivity {
    private ImageView mIvShowTeacher;
    private ImageView mIvShowMyGirl;
    private String mImgPath;
    private String mImgUrl;
    private String mName;
    private String mSimilarity;
    private TextView mTvName;
    private String mSavingPath;
    private String mSavingFileName;
    private int thumbnailSize;//缩略图尺寸
    private IMGLoader load;
    private DisplayImageOptions mOptions;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_teacher);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(mName);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initData();
        initView();
    }

    private void initData() {
        mImgUrl = getIntent().getStringExtra(CommonDefine.TAG_IMAGE_URL);
        mName = getIntent().getStringExtra(CommonDefine.TAG_TEACHER_NAME);
        mSimilarity = getIntent().getStringExtra(CommonDefine.TAG_SIMILAR);
        mImgPath = getIntent().getStringExtra(CommonDefine.TAG_IMAGE_PATH);
        String folder = FileOperateUtil.getFolderPath(ShowTeacherActivity.this, FileOperateUtil.TYPE_IMAGE, "test");
        mSavingFileName = FileOperateUtil.createFileNmae(".jpg");
        mSavingPath = folder + File.separator + mSavingFileName;
        Logger.i("saving path:" + mSavingPath);
        thumbnailSize = getResources().getDimensionPixelOffset(R.dimen.thumbnail_size);

    }

    private void initView() {
        mIvShowTeacher = ((ImageView) findViewById(R.id.iv_teacher));
        registerForContextMenu(mIvShowTeacher);//注册上下文菜单(长按呼出)
        mIvShowMyGirl = ((ImageView) findViewById(R.id.iv_my_girl));
        mTvName = ((TextView) findViewById(R.id.tv_name));
        mTvName.setText(mName + "-" + mSimilarity + getString(R.string.similarity_post));
        Bitmap myGirlThumbnail = ImageUtil.getImageThumbnail(mImgPath, thumbnailSize, thumbnailSize);//获取被拍摄女孩的缩略图
        mIvShowMyGirl.setImageBitmap(myGirlThumbnail);
//        mImgUrl = CommonDefine.URL_IMAGE_LOAD_TEST;
        Picasso.with(this).load(mImgUrl).placeholder(R.drawable.splash).error(R.drawable.ic_error).into(mIvShowTeacher);
//        loadImageByVoley();
    }

    private void loadImageByVoley() {
        RequestQueue mQueue = Volley.newRequestQueue(ShowTeacherActivity.this);
        ImageRequest imageRequest = new ImageRequest(mImgUrl, new Response.Listener<Bitmap>() {
            @Override
            public void onResponse(Bitmap bitmap) {
                mIvShowTeacher.setImageBitmap(bitmap);
            }
        }, 0, 0, Bitmap.Config.RGB_565, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                toastGo("aaaaaaaaa");
            }
        });
        mQueue.add(imageRequest);
    }

    private void loadImageByLinjUtil() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final Bitmap bitmap = DownloadImgUtils.downloadImgByUrl(mImgUrl);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mIvShowTeacher.setImageBitmap(bitmap);
                    }
                });
            }
        }).start();
    }


    /**
     * 将ImageView的图片保存
     *
     * @param imageView ImageView
     */
    private void savePicFromImageView(ImageView imageView) {
        imageView.setDrawingCacheEnabled(true);
        imageView.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(imageView.getDrawingCache());
        imageView.setDrawingCacheEnabled(false);
        Logger.i(1, "bitmap size:" + bitmap.getByteCount());
        String folder = FileOperateUtil.getFolderPath(this, FileOperateUtil.TYPE_IMAGE, "test");
        String imgName = FileOperateUtil.createFileNmae(".jpg");
        String path = folder + File.separator + mName + imgName;
        Logger.i(1, "path:" + path);
        boolean isOk = ImageUtil.savePic(getApplication(), bitmap, path, imgName);
        if (isOk) {
            toastGo("保存图片成功!");
        } else {
            toastGo("保存图片失败!");
        }
    }

    /**
     * 上下文菜单创建回调
     *
     * @param menu     菜单
     * @param v        view
     * @param menuInfo 菜单信息
     */
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(0, Menu.FIRST, 0, "保存");
        menu.add(0, 2, 0, "保存二者");
    }

    /**
     * 上下文菜单点击相应
     *
     * @param item 菜单id
     * @return 是否被选择
     */
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 1:
                boolean isOk = ImageUtil.savePicFromImageView(ShowTeacherActivity.this, mIvShowTeacher, mSavingPath, mSavingFileName);
                if (isOk) {
                    toastGo("保存图片成功!");
                } else {
                    toastGo("保存图片失败!");
                }
                break;
            case 2:
                Bitmap bitmap = ImageUtil.getBitmapFromView(mIvShowTeacher);//获取老师的bitmap(显示尺寸,而不是原图尺寸,毕竟原图大小不确定)
                bitmap = ImageUtil.compositeBitmapWithView(bitmap, mTvName);//进行合成
                boolean isSavingOk = ImageUtil.savePic(ShowTeacherActivity.this, bitmap, mSavingPath, mSavingFileName);
                if (isSavingOk) {
                    toastGo("保存图片成功!");
                } else {
                    toastGo("保存图片失败!");
                }
                break;
            default:
                break;
        }
        return super.onContextItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_show_teacher, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * 隐藏三个小点
     *
     * @param menu 菜单对象
     * @return 菜单
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.action_settings);
        item.setVisible(false);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            this.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
