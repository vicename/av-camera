package com.yamedie.av_camera;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.linj.FileOperateUtil;
import com.linj.imageloader.ImageUtil;
import com.squareup.picasso.Picasso;
import com.yamedie.common.CommonDefine;
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
    }

    private void initView() {
        mIvShowTeacher = ((ImageView) findViewById(R.id.iv_teacher));
        registerForContextMenu(mIvShowTeacher);
        mIvShowMyGirl = ((ImageView) findViewById(R.id.iv_my_girl));
        mTvName = ((TextView) findViewById(R.id.tv_name));
        mTvName.setText(mName + "-" + mSimilarity + getString(R.string.similarity_post));
        Picasso.with(this).load(mImgUrl).into(mIvShowTeacher);
        Bitmap myGirlBitmap = BitmapFactory.decodeFile(mImgPath);
        mIvShowMyGirl.setImageBitmap(myGirlBitmap);
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
                savePicFromImageView(mIvShowTeacher);
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
