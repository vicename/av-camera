package com.yamedie.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;

import com.yamedie.av_camera.ShowIMGActivity;
import com.yamedie.av_camera.R;
import com.yamedie.av_camera.TakePhotoActivity;
import com.yamedie.common.CommonDefine;
import com.yamedie.utils.SharedPreferencesUtils;

/**
 * Created by Li Dachang on 16/1/27.
 * ..-..---.-.--..---.-...-..-....-.
 */
public class ViewPagerAdapter extends PagerAdapter {
    //界面列表
    private ArrayList<View> views;
    private LayoutInflater mInflater;
    private Context mContext;
    private boolean isFirst;

    public ViewPagerAdapter(Context context) {
        mContext = context;
        isFirst = true;
        mInflater = LayoutInflater.from(context);
        views = new ArrayList<>();
    }

    /**
     * 获得当前界面数
     */
    @Override
    public int getCount() {
        return 3;
    }

    /**
     * 判断是否由对象生成界面
     */
    @Override
    public boolean isViewFromObject(View arg0, Object arg1) {
        return (arg0 == arg1);
    }

    /**
     * 销毁position位置的界面
     */
    @Override
    public void destroyItem(View container, int position, Object object) {
        View temp = null;
        if (position < views.size()) {
            temp = views.get(position);
        }
        if (temp != null) {
            ((ViewPager) container).removeView(temp);
        }
    }

    /**
     * 初始化position位置的界面
     */
    @Override
    public Object instantiateItem(View container, int position) {
        View temp = null;
        if (position < views.size()) {
            temp = views.get(position);
        } else {
            switch (position) {
                case 0:
                    temp = mInflater.inflate(R.layout.content_guid_pager1, null);
                    break;
                case 1:
                    temp = mInflater.inflate(R.layout.content_guid_pager2, null);
                    break;
                case 2:
                    temp = mInflater.inflate(R.layout.content_guid_pager6, null);
                    Button guideGo = (Button) temp.findViewById(R.id.btn_go);
                    guideGo.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (isFirst) {
                                isFirst = false;
                                Intent intent;
                                intent = new Intent(mContext, TakePhotoActivity.class);
                                mContext.startActivity(intent);
                                SharedPreferencesUtils sPref = new SharedPreferencesUtils(mContext);
                                sPref.saveIn(CommonDefine.IS_FIRST,false);
                                        ((Activity) mContext).finish();
                            }
                        }
                    });
                    break;
            }
            views.add(temp);
        }
        ((ViewPager) container).addView(temp, 0);
        return temp;
    }
}
