package com.linj.imageloader;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.linj.FileOperateUtil;
import com.linj.utils.Logger;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;

/**
 * 拍照返回的byte数据处理类
 * Created by Li Dachang on 16/2/24.
 * ..-..---.-.--..---.-...-..-....-.
 */
public class ImageDataHandler {
    private String mImagePath;
    private File mImageFile;
    private String mThumbPath;
    private String mSavePath;
    private Context mContext;
    private String mThumbnailFolder;//缩略图存放路径
    private String mImageFolder;//大图存放路径
    private int maxSize = 200;//压缩后的图片最大值 单位KB

    public ImageDataHandler(Context context) {
        mContext = context;
        mImageFolder = FileOperateUtil.getFolderPath(context, FileOperateUtil.TYPE_IMAGE, mSavePath);
        mThumbnailFolder = FileOperateUtil.getFolderPath(context, FileOperateUtil.TYPE_THUMBNAIL, mSavePath);
        Log.i("---", "folder path:" + mThumbnailFolder);
        File folder = new File(mImageFolder);
        if (!folder.exists()) {
            folder.mkdirs();
        }
        folder = new File(mThumbnailFolder);
        if (!folder.exists()) {
            folder.mkdirs();
        }
    }

    public String getImagePath() {
        return mImagePath;
    }

    public File getmImageFile() {
        return mImageFile;
    }

    public void setMaxSize(int maxSize) {
        this.maxSize = maxSize;
    }

    /**
     * 保存图片
     *
     * @param data 相机返回的文件流
     * @return 解析流生成的缩略图
     */
    public Bitmap save(byte[] data) {
        if (data != null) {
            //解析生成相机返回的图片
            Bitmap ImageBitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
            //获取加水印的图片
            ImageBitmap = getBitmapWithWaterMark(ImageBitmap, null);
            //生成缩略图
            Bitmap thumbnailBitmap = ThumbnailUtils.extractThumbnail(ImageBitmap, 213, 213);
            //产生新的文件名
            String imgName = FileOperateUtil.createFileNmae(".jpg");
            mImagePath = mImageFolder + File.separator + imgName;
            mThumbPath = mThumbnailFolder + File.separator + imgName;
            ByteArrayOutputStream bos = ImageUtil.compressByFileSize(ImageBitmap, maxSize);//压缩方法
            boolean isImageSavedOk=ImageUtil.savePic(mContext,bos.toByteArray(), mImagePath,imgName);
            if (!isImageSavedOk) {
                Toast.makeText(mContext,"照片保存出现问题,图片保存失败!",Toast.LENGTH_SHORT).show();
                return null;
            }
            return ImageBitmap;
        } else {
            Toast.makeText(mContext, "拍照失败，请重试", Toast.LENGTH_SHORT).show();
        }
        return null;
    }

    /**
     * 为图片添加水印
     *
     * @param bitmap             bitmap
     * @param watermarkImageView 水印view
     * @return 处理后的图片
     */
    public Bitmap getBitmapWithWaterMark(Bitmap bitmap, ImageView watermarkImageView) {
        // TODO Auto-generated method stub
        if (watermarkImageView == null || !(watermarkImageView.getVisibility() == View.VISIBLE)) {
            return bitmap;
        }
        Drawable mark = watermarkImageView.getDrawable();
        Bitmap wBitmap = ImageUtil.drawableToBitmap(mark);
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        int ww = wBitmap.getWidth();
        int wh = wBitmap.getHeight();
        Bitmap newb = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(newb);
        //draw src into
        canvas.drawBitmap(bitmap, 0, 0, null);//在 0，0坐标开始画入src
        canvas.drawBitmap(wBitmap, w - ww + 5, h - wh + 5, null);//在src的右下角画入水印
        //save all clip
        canvas.save(Canvas.ALL_SAVE_FLAG);//保存
        canvas.restore();//存储
        bitmap.recycle();
        bitmap = null;
        wBitmap.recycle();
        wBitmap = null;
        return newb;
    }

    private File saveFile(String path) {
        File file = new File(path);

        return file;
    }
}
