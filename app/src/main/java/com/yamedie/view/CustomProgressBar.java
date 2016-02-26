package com.yamedie.view;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.util.AttributeSet;

import com.linj.imageloader.CommonUtil;
import com.yamedie.av_camera.R;

/**
 * Created by Li Dachang on 16/2/26.
 * ..-..---.-.--..---.-...-..-....-.
 */
public class CustomProgressBar extends CustomView {
    private final static String ANDROID_XML = "http://schemas.android.com/apk/res/android";

    public final static int STATE_SCALE_BIG = 0;
    public final static int STATE_ROTATE = 1;
    public final static int STATE_SCALE_SMALL = 2;
    public final static int STATE_CHECK = 3;
    public final static int STATE_WARING = 4;

    private int mState = STATE_SCALE_BIG;

    private static final int mPointSize = 3;

    private int mBackgroundColor;
    private float radius1 = 3;
    private float radius2 = 0;
    private int cont = 0;
    private int arcD = 1;
    private int arcO = 0;
    private float rotateAngle = 0;
    private int limit = 0;

    private boolean mIsWifiHasNet = false;

    private Paint mPressPaint;
    private Paint mTransparentPaint;
    private Paint mSecondPaint;
    private Paint mFourthPaint;

    private int mCurrentX;
    private int mCurrentY;
    private int mStartX;
    private int mStartY;
    private boolean mIsMove = true;

    public CustomProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        mBackgroundColor = getResources().getColor(R.color.colorPrimary);

        mPressPaint = new Paint();
        mPressPaint.setAntiAlias(true);
        mPressPaint.setColor(makePressColor());

        mTransparentPaint = new Paint();
        mTransparentPaint.setAntiAlias(true);
        mTransparentPaint.setColor(getResources().getColor(android.R.color.transparent));
        mTransparentPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));

        mSecondPaint = new Paint();
        mSecondPaint.setAntiAlias(true);
        mSecondPaint.setColor(mBackgroundColor);

        mFourthPaint = new Paint();
        mFourthPaint.setDither(true);
        mFourthPaint.setAntiAlias(true);
        mFourthPaint.setStrokeJoin(Paint.Join.ROUND);
        mFourthPaint.setStrokeCap(Paint.Cap.ROUND);
        mFourthPaint.setStrokeWidth(CommonUtil.dpToPx(3, getResources()));
        mFourthPaint.setStyle(Paint.Style.STROKE);
        mFourthPaint.setColor(mBackgroundColor);
        setAttributes(attrs);
    }

    // Set atributtes of XML to View
    protected void setAttributes(AttributeSet attrs) {
        setMinimumHeight(CommonUtil.dpToPx(32, getResources()));
        setMinimumWidth(CommonUtil.dpToPx(32, getResources()));
        //Set background Color
        // Color by resource
        int backgroundColor = attrs.getAttributeResourceValue(ANDROID_XML, "background", -1);
        if (backgroundColor != -1) {
            setBackgroundColor(getResources().getColor(backgroundColor));
        } else {
            // Color by hexadecimal
            int background = attrs.getAttributeIntValue(ANDROID_XML, "background", -1);
            if (background != -1)
                setBackgroundColor(background);
            else
                setBackgroundColor(Color.parseColor("#1E88E5"));
        }

        setMinimumHeight(CommonUtil.dpToPx(3, getResources()));
    }

    public int getState() {
        return mState;
    }

    /**
     * Make a dark color to ripple effect
     *
     * @return
     */
    protected int makePressColor() {
        int r = (this.mBackgroundColor >> 16) & 0xFF;
        int g = (this.mBackgroundColor >> 8) & 0xFF;
        int b = (this.mBackgroundColor >> 0) & 0xFF;
        return Color.argb(128, r, g, b);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        switch (mState) {
            case STATE_SCALE_BIG:
                drawFirstAnimation(canvas);
                break;
            case STATE_ROTATE:
                drawSecondAnimation(canvas);
                break;
            case STATE_SCALE_SMALL:
                drawThirdAnimation(canvas);
                break;
            case STATE_CHECK:
                drawFourthAnimation(canvas);
                break;
            case STATE_WARING:
                drawFifthAnimation(canvas);
                break;
        }
        if (!mIsClear) {
            invalidate();
        }
    }

    private boolean mIsClear = false;

    public void clear() {
        mIsClear = true;
    }

    public void setWifiOk() {
        if (mState != STATE_SCALE_SMALL && mState != STATE_CHECK) {
            resetValue();
            mIsWifiHasNet = true;
            mState = STATE_SCALE_SMALL;
        }
    }

    public void setWifiNoNet() {
        if (mState != STATE_SCALE_SMALL && mState != STATE_WARING) {
            resetValue();
            mIsWifiHasNet = false;
            mState = STATE_SCALE_SMALL;
        }
    }

    public void resetAllValue() {
        mState = STATE_SCALE_BIG;
        mIsClear = false;
        resetValue();
    }

    private void resetValue() {
        mCurrentX = 0;
        mCurrentY = 0;
        mStartX = 0;
        mStartY = 0;
        mIsMove = false;
    }

    /**
     * 初始化，并放大
     *
     * @param canvas 画布
     */
    private void drawFirstAnimation(Canvas canvas) {
        if (radius1 < getWidth() / 2) {
            radius1 = (radius1 >= getWidth() / 2) ? (float) getWidth() / 2 : radius1 + 1;
            canvas.drawCircle(getWidth() / 2, getHeight() / 2, radius1, mPressPaint);
        } else {
            Bitmap bitmap = Bitmap.createBitmap(canvas.getWidth(), canvas.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas temp = new Canvas(bitmap);
            temp.drawCircle(getWidth() / 2, getHeight() / 2, getHeight() / 2, mPressPaint);
            if (cont >= 50) {
                radius2 = (radius2 >= getWidth() / 2) ? (float) getWidth() / 2 : radius2 + 1;
            } else {
                radius2 = (radius2 >= getWidth() / 2 - CommonUtil.dpToPx(4, getResources())) ? (float) getWidth() / 2 - CommonUtil.dpToPx(4, getResources()) : radius2 + 1;
            }
            temp.drawCircle(getWidth() / 2, getHeight() / 2, radius2, mTransparentPaint);
            canvas.drawBitmap(bitmap, 0, 0, new Paint());
            if (radius2 >= getWidth() / 2 - CommonUtil.dpToPx(4, getResources()))
                cont++;
            if (radius2 >= getWidth() / 2)
                mState = STATE_ROTATE;
        }
    }

    /**
     * 旋转
     *
     * @param canvas 画布
     */
    private void drawSecondAnimation(Canvas canvas) {
        if (cont > 0) {
            if (arcO == limit)
                arcD += 6;
            if (arcD >= 290 || arcO > limit) {
                arcO += 6;
                arcD -= 6;
            }
            if (arcO > limit + 290) {
                limit = arcO;
                arcO = limit;
                arcD = 1;
            }
            rotateAngle += 4;
            canvas.rotate(rotateAngle, getWidth() / 2, getHeight() / 2);
            Bitmap bitmap = Bitmap.createBitmap(canvas.getWidth(), canvas.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas temp = new Canvas(bitmap);
            mSecondPaint.setColor(mBackgroundColor);
            temp.drawArc(new RectF(0, 0, getWidth(), getHeight()), arcO, arcD, true, mSecondPaint);
            temp.drawCircle(getWidth() / 2, getHeight() / 2, (getWidth() / 2) - CommonUtil.dpToPx(4, getResources()), mTransparentPaint);
            canvas.drawBitmap(bitmap, 0, 0, new Paint());
        }
    }

    /**
     * 缩小
     *
     * @param canvas 画布
     */
    private void drawThirdAnimation(Canvas canvas) {
        if (radius1 > CommonUtil.dpToPx(mPointSize, getResources())) {
            radius1 = (radius1 <= CommonUtil.dpToPx(3, getResources())) ? CommonUtil.dpToPx(mPointSize, getResources()) : radius1 - 1;
            canvas.drawCircle(getWidth() / 2, getHeight() / 2, radius1, mPressPaint);
        } else {
            if (mIsWifiHasNet) {
                mState = STATE_CHECK;
            } else {
                mState = STATE_WARING;
            }
        }
    }

    /**
     * 移动到左边并且打钩
     *
     * @param canvas 画布
     */
    private void drawFourthAnimation(Canvas canvas) {
        if (mCurrentX == 0) {
            mCurrentX = getWidth() / 2;
            mCurrentY = getHeight() / 2;
            mIsMove = true;
        }

        if (mIsMove) {
            mCurrentX--;
            canvas.drawPoint(mCurrentX, mCurrentY, mFourthPaint);
            if (mCurrentX < CommonUtil.dpToPx(3, getResources())) {
                mStartX = mCurrentX;
                mStartY = mCurrentY;
                mIsMove = false;
            }
        } else {
            if (mCurrentX < CommonUtil.dpToPx(7, getResources())) {
                mCurrentX++;
                mCurrentY++;
                canvas.drawLine(mStartX, mStartY, mCurrentX, mCurrentY, mFourthPaint);
            } else {
                canvas.drawLine(mStartX, mStartY, CommonUtil.dpToPx(7, getResources()), CommonUtil.dpToPx(17, getResources()), mFourthPaint);
                if (mCurrentY >= CommonUtil.dpToPx(5, getResources())) {
                    mCurrentX++;
                    mCurrentY--;
                }
                canvas.drawLine(CommonUtil.dpToPx(7, getResources()), CommonUtil.dpToPx(17, getResources()), mCurrentX, mCurrentY, mFourthPaint);
            }
        }
    }

    /**
     * 移动到上面并且画感叹号
     *
     * @param canvas 画布
     */
    private void drawFifthAnimation(Canvas canvas) {
        if (mCurrentX == 0) {
            mCurrentX = getWidth() / 2;
            mCurrentY = getHeight() / 2;
            mIsMove = true;
        }

        if (mIsMove) {
            mCurrentY = mCurrentY - 1;
            canvas.drawPoint(mCurrentX, mCurrentY, mFourthPaint);
            if (mCurrentY < CommonUtil.dpToPx(3, getResources())) {
                mStartX = mCurrentX;
                mStartY = mCurrentY;
                mIsMove = false;
            }
        } else {
            if (mCurrentY < CommonUtil.dpToPx(14, getResources())) {
                mCurrentY++;
                canvas.drawLine(mStartX, mStartY, mCurrentX, mCurrentY, mFourthPaint);
            } else {
                canvas.drawLine(mStartX, mStartY, mStartX, CommonUtil.dpToPx(13, getResources()), mFourthPaint);
                canvas.drawPoint(mStartX, CommonUtil.dpToPx(20, getResources()), mFourthPaint);
            }
        }
    }

    // Set color of background
    public void setBackgroundColor(int color) {
        super.setBackgroundColor(getResources().getColor(android.R.color.transparent));
        if (isEnabled()) {
            beforeBackground = mBackgroundColor;
        }
        this.mBackgroundColor = color;
        mPressPaint.setColor(makePressColor());
        mFourthPaint.setColor(mBackgroundColor);
    }

}