package com.yamedie.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

/**
 * Created by Li Dachang on 16/2/26.
 * ..-..---.-.--..---.-...-..-....-.
 */
public class CustomView extends RelativeLayout {

    private final static String MATERIALDESIGNXML = "http://schemas.android.com/apk/res-auto";
    private final static String ANDROIDXML = "http://schemas.android.com/apk/res/android";

    private final int disabledBackgroundColor = Color.parseColor("#536dfe");
    protected int beforeBackground;

    // Indicate if user touched this view the last time
    public boolean isLastTouch = false;

    public CustomView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        if (enabled)
            setBackgroundColor(beforeBackground);
        else
            setBackgroundColor(disabledBackgroundColor);
        invalidate();
    }

    boolean animation = false;

    @Override
    protected void onAnimationStart() {
        super.onAnimationStart();
        animation = true;
    }

    @Override
    protected void onAnimationEnd() {
        super.onAnimationEnd();
        animation = false;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (animation)
            invalidate();
    }
}
