package com.yamedie.av_camera;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
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
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.yamedie.common.CommonDefine;
import com.yamedie.loader.IMGLoader;
import com.yamedie.utils.CommonUtils;
import com.yamedie.utils.ImageUtil;
import com.yamedie.utils.Logger;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ShowTeacherActivity extends BaseActivity {
    private ImageView mIvShowTeacher;
    private ImageView mIvShowMyGirl;
    private String mImgPath;
    private String mImgUrl;
    private String mName;
    private String mSimilarity;
    private long mTimeWhenUpload;
    private TextView mTvName;
    private TextView mTvNameToComposed;
    private String mSavingPath;
    private String mSavingFileName;
    private int thumbnailSize;//缩略图尺寸
    private IMGLoader load;
    private DisplayImageOptions mOptions;
    private Target mTarget;


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

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Picasso.with(this).cancelRequest(mTarget);
    }

    private void initData() {
        mImgUrl = getIntent().getStringExtra(CommonDefine.TAG_IMAGE_URL);
        mImgUrl = CommonUtils.encodeUrl(mImgUrl);//转义带中文的url
        mName = getIntent().getStringExtra(CommonDefine.TAG_TEACHER_NAME);
        mSimilarity = getIntent().getStringExtra(CommonDefine.TAG_SIMILAR);
        mImgPath = getIntent().getStringExtra(CommonDefine.TAG_IMAGE_PATH);
        mTimeWhenUpload = getIntent().getLongExtra(CommonDefine.TAG_TIME, 0);
        String folder = FileOperateUtil.getFolderPath(ShowTeacherActivity.this, FileOperateUtil.TYPE_IMAGE, "test");
        mSavingFileName = FileOperateUtil.createFileNmae(".jpg");
        mSavingPath = folder + File.separator + mSavingFileName;
        Logger.i("saving path:" + mSavingPath);
        Logger.i(1, "url", mImgUrl, "name:", mName, "similarity", mSimilarity);
        thumbnailSize = getResources().getDimensionPixelOffset(R.dimen.thumbnail_size);

    }

    private void initView() {
        mIvShowTeacher = ((ImageView) findViewById(R.id.iv_teacher));
        registerForContextMenu(mIvShowTeacher);//注册上下文菜单(长按呼出)
        mIvShowMyGirl = ((ImageView) findViewById(R.id.iv_my_girl));
        mTvName = ((TextView) findViewById(R.id.tv_name));
        mTvNameToComposed = (TextView) findViewById(R.id.tv_name_to_composed);
        String baseInfo = mName + "-" + mSimilarity + getString(R.string.similarity_post);
        mTvName.setText(baseInfo);
        mTvNameToComposed.setText(baseInfo);
        Bitmap myGirlThumbnail = ImageUtil.getImageThumbnail(mImgPath, thumbnailSize, thumbnailSize);//获取被拍摄女孩的缩略图
        mIvShowMyGirl.setImageBitmap(myGirlThumbnail);
        //mImgUrl = CommonDefine.URL_IMAGE_LOAD_TEST;
        //Picasso.with(this).load(mImgUrl).placeholder(R.drawable.abc_dialog_material_background_light).error(R.drawable.ic_error).into(mIvShowTeacher);
        //loadImageByVoley();
        //loadImageByLinjUtil("http://203.100.82.13/冬月枫/a53febe2gc7e4cd6e43d6&690.jpg");
        initPicassoTarget();
        Picasso.with(this).load(mImgUrl).into(mTarget);
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
            }
        });
        mQueue.add(imageRequest);
    }

    private void loadImageByLinjUtil(final String url) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final Bitmap bitmap = DownloadImgUtils.downloadImgByUrl(url);
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
     * 初始化picasso图片加载回调
     */
    private void initPicassoTarget() {
        mTarget = new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom loadedFrom) {
                if (mTimeWhenUpload > 0) {
                    Date date = new Date();
                    long timeWhenLoadIMG = date.getTime();
                    float spendSeconds = (float) (timeWhenLoadIMG - mTimeWhenUpload);
                    Logger.i("time spend:" + spendSeconds);
                    mIvShowTeacher.setImageBitmap(bitmap);
                    Map<String, String> map = new HashMap<>();
                    map.put(CommonDefine.UM_FIND_TEACHER_TIME, String.valueOf(spendSeconds));
                    mobClickAgentGo(CommonDefine.UM_SHOW_TEACHER, map);
                } else {
                    Logger.e("ShowTeacherActivity没有获取到时间戳!");
                }
            }

            @Override
            public void onBitmapFailed(Drawable drawable) {
            }

            @Override
            public void onPrepareLoad(Drawable drawable) {

            }
        };
    }


    /**
     * 合成老师+文字+被拍摄女孩
     *
     * @param originalBitmap 老师的bitmap
     * @param text           文字view
     * @param imageView      被拍摄姑娘的view
     * @return 合成后bitmap
     */
    public Bitmap compositeBitmaps(Bitmap originalBitmap, View text, ImageView imageView) {
        // TODO Auto-generated method stub
        if (text == null || imageView == null) {
            return originalBitmap;
        }
        Bitmap textBitmap = ImageUtil.getBitmapFromView(text);//获取view的Bitmap
        Bitmap imageBitmap = ImageUtil.getBitmapFromView(imageView);
        int thumbnailMargin = getResources().getDimensionPixelOffset(R.dimen.thumbnail_margin);//获取缩略图的边距
        int oWidth = originalBitmap.getWidth();
        int oHeight = originalBitmap.getHeight();
        int imageWidth = imageBitmap.getWidth();
        int imageHeight = imageBitmap.getHeight();
        Bitmap newBitmap = Bitmap.createBitmap(oWidth, oHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(newBitmap);
        canvas.drawBitmap(originalBitmap, 0, 0, null);//在 0，0坐标开始画入src
        canvas.drawBitmap(textBitmap, 0, oHeight - textBitmap.getHeight(), null);
        canvas.drawBitmap(imageBitmap, oWidth - imageWidth - thumbnailMargin, oHeight - imageHeight - text.getHeight(), null);
        canvas.save(Canvas.ALL_SAVE_FLAG);//保存
        canvas.restore();//存储
        originalBitmap.recycle();
        textBitmap.recycle();
        return newBitmap;
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
                mobClickAgentGo(CommonDefine.UM_CLICK_SAVE_TEACHER);
                boolean isOk = ImageUtil.savePicFromImageView(ShowTeacherActivity.this, mIvShowTeacher, mSavingPath, mSavingFileName);
                if (isOk) {
                    toastGo("保存图片成功!");
                } else {
                    toastGo("保存图片失败!");
                }
                break;
            case 2:
                mobClickAgentGo(CommonDefine.UM_CLICK_SAVE_TEACHER_COMPOSED);
                Bitmap bitmap = ImageUtil.getBitmapFromView(mIvShowTeacher);//获取老师的bitmap(显示尺寸,而不是原图尺寸,毕竟原图大小不确定)
                bitmap = compositeBitmaps(bitmap, mTvNameToComposed, mIvShowMyGirl);
                boolean isSavingOk = ImageUtil.savePic(ShowTeacherActivity.this, bitmap, mSavingPath, mSavingFileName);
                if (isSavingOk) {
                    toastGo("保存图片成功!");
                    bitmap.recycle();
                } else {
                    toastGo("保存图片失败!");
                    bitmap.recycle();
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
