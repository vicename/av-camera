package com.yamedie.av_camera;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.yamedie.common.CommonDefine;

public class ShowTeacherActivity extends BassActivity {
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
        mIvShowMyGirl = ((ImageView) findViewById(R.id.iv_my_girl));
        mTvName = ((TextView) findViewById(R.id.tv_name));
        mTvName.setText(mName + "-" + mSimilarity + getString(R.string.similarity_post));
        Picasso.with(this).load(mImgUrl).into(mIvShowTeacher);
        Bitmap myGirlBitmap = BitmapFactory.decodeFile(mImgPath);
        mIvShowMyGirl.setImageBitmap(myGirlBitmap);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_show_teacher,menu);
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * 隐藏三个小点
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
