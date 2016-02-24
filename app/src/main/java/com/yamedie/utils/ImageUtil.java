package com.yamedie.utils;

import android.graphics.Bitmap;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;

import android.annotation.SuppressLint;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;

/**
 * Created by Li Dachang on 16/2/17.
 * ..-..---.-.--..---.-...-..-....-.
 */

@SuppressLint("NewApi")
public class ImageUtil {

    /**
     * Drawable转换成Bitmap
     *
     * @param drawable
     * @return
     */
    public static Bitmap drawable2Bitmap(Drawable drawable) {
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    /**
     * BitmapDrawable转换成Bitmap
     *
     * @param drawable
     * @return
     */
    public static Bitmap drawable2Bitmap(BitmapDrawable drawable) {
        return drawable.getBitmap();
    }

    /**
     * Bitmap 转换成 Drawable
     *
     * @param bitmap
     * @return
     */
    public static Drawable bitmap2Drawable(Bitmap bitmap) {
        return new BitmapDrawable(bitmap);
    }

    /**
     * Bitmap转换成字节数组byte[]
     *
     * @param bmp
     * @return
     */
    public static byte[] bitmap2Byte(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }

    /**
     * 字节数组byte[]转换成Bitmap
     *
     * @param buffer
     * @return
     */
    public static Bitmap byte2Bitmap(byte[] buffer) {
        return BitmapFactory.decodeByteArray(buffer, 0, buffer.length);
    }

    /**
     * 缩放本地图片
     *
     * @param file   本地图片路径
     * @param width  宽
     * @param height 高
     * @return
     */
    public static Bitmap getBreviaryBitmapByFilepath(File file, int width, int height) {
        Bitmap bitmap = null;

        if (null != file && file.exists()) {
            BitmapFactory.Options options = null;
            if (width > 0 && height > 0) {
                options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                bitmap = BitmapFactory.decodeFile(file.getPath(), options);
                final int minSideLength = Math.min(width, height);
                options.inSampleSize = computeSampleSize(options, minSideLength, width * height);
                options.inJustDecodeBounds = false;
                options.inInputShareable = true;
            }
            try {
                bitmap = BitmapFactory.decodeFile(file.getPath(), options);
                return bitmap;
            } catch (OutOfMemoryError e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 估算图片大小
     * @param options options
     * @param minSideLength 短边长度
     * @param maxNumOfPixels 最大像素数
     * @return 图片大小
     */
    private static int computeSampleSize(BitmapFactory.Options options, int minSideLength, int maxNumOfPixels) {
        int initialSize = computeInitialSampleSize(options, minSideLength, maxNumOfPixels);
        int roundedSize;
        if (initialSize <= 8) {
            roundedSize = 1;
            while (roundedSize < initialSize) {
                roundedSize <<= 1;
            }
        } else {
            roundedSize = (initialSize + 7) / 8 * 8;
        }
        return roundedSize;
    }

    private static int computeInitialSampleSize(BitmapFactory.Options options,int minSideLength, int maxNumOfPixels) {
        double w = options.outWidth;
        double h = options.outHeight;

        int lowerBound = (maxNumOfPixels == -1) ? 1 : (int) Math.ceil(Math
                .sqrt(w * h / maxNumOfPixels));

        int upperBound = (minSideLength == -1) ? 128 : (int) Math.min(
                Math.floor(w / minSideLength), Math.floor(h / minSideLength));

        if (upperBound < lowerBound) {
            return lowerBound;
        }

        if ((maxNumOfPixels == -1) && (minSideLength == -1)) {
            return 1;
        } else if (minSideLength == -1) {
            return lowerBound;
        } else {
            return upperBound;
        }
    }

    /**
     * 获取图片缩略图
     * @param imagePath 路径
     * @param width 宽
     * @param height 高
     * @return 缩略图bitmap
     */
    public static Bitmap getImageThumbnail(String imagePath, int width,int height) {
        Bitmap bitmap = null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        bitmap = BitmapFactory.decodeFile(imagePath, options);
        options.inJustDecodeBounds = false;
        int h = options.outHeight;
        int w = options.outWidth;
        int beWidth = w / width;
        int beHeight = h / height;
        int be = 1;
        if (beWidth < beHeight) {
            be = beWidth;
        } else {
            be = beHeight;
        }
        if (be <= 0) {
            be = 1;
        }
        options.inSampleSize = be;
        bitmap = BitmapFactory.decodeFile(imagePath, options);
        bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height,
                ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
        return bitmap;
    }

    /**
     * 获取视频缩略图
     * @param videoPath 视频路径
     * @param width 宽
     * @param height 高
     * @param kind 类型
     * @return 缩略图bitmap
     */
    public static Bitmap getVideoThumbnail(String videoPath, int width,
                                           int height, int kind) {
        Bitmap bitmap = null;
        bitmap = ThumbnailUtils.createVideoThumbnail(videoPath, kind);
        System.out.println("w" + bitmap.getWidth());
        System.out.println("h" + bitmap.getHeight());
        bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height,
                ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
        return bitmap;
    }

    /**
     * 根据宽高压缩图片
     * @param image 图片bitmap
     * @param size 压缩后宽度
     * @return 压缩后bitmap
     */
    public static Bitmap compressBitmap(Bitmap image, float size) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        if (baos.toByteArray().length / 1024 > 1024) {
            baos.reset();
            image.compress(Bitmap.CompressFormat.JPEG, 50, baos);
            Logger.i("--------1");
        }
        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        newOpts.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeStream(bais, null, newOpts);
        newOpts.inJustDecodeBounds = false;
        int outWidth = newOpts.outWidth;
        int outHeight = newOpts.outHeight;
        Logger.i("---", "w:" + outWidth + ",h:" + outHeight);
        int be = 1;
        if (outWidth > outHeight && outWidth > size) {
            be = (int) (newOpts.outWidth / size);
        } else if (outWidth < outHeight && outHeight > size) {
            be = (int) (newOpts.outHeight / size);
        }
        if (be <= 0)
            be = 1;
        newOpts.inSampleSize = be;
        Logger.i("------","sampleSize:"+be);
        bais = new ByteArrayInputStream(baos.toByteArray());
        bitmap = BitmapFactory.decodeStream(bais, null, newOpts);
        return compressBiamapSample(bitmap);
    }

    /**
     * 压缩图片
     * @param image
     * @return
     */
    public static Bitmap compressBiamapSample(Bitmap image) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        int options = 100;
        while (baos.toByteArray().length / 1024 > 100) {
            baos.reset();//
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);
            options -= 10;
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);
        return bitmap;
    }

    /**
     * 得到 800x480的图片
     *
     * @param srcPath 图片路径
     * @return 图片bitmap
     */
    public static Bitmap getimage(String srcPath) {
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        newOpts.inJustDecodeBounds = true;

        newOpts.inJustDecodeBounds = false;
        int w = newOpts.outWidth;
        int h = newOpts.outHeight;
        float hh = 800f;
        float ww = 480f;
        int be = 1;
        if (w > h && w > ww) {
            be = (int) (newOpts.outWidth / ww);
        } else if (w < h && h > hh) {
            be = (int) (newOpts.outHeight / hh);
        }
        if (be <= 0)
            be = 1;
        newOpts.inSampleSize = be;
        Bitmap bitmap = BitmapFactory.decodeFile(srcPath, newOpts);
        return compressBiamapSample(bitmap);
    }

    /**
     * 根据路径判断是否为图片
     * @param path 路径
     * @return 是否是图片
     */
    public static boolean isPicture(String path) {
        path = path.toLowerCase();
        return path.endsWith(".jpg") || path.endsWith(".png");
    }

    /**
     * 放大图片
     *
     * @param bitmap
     * @param big
     * @return
     */
    public static Bitmap big(Bitmap bitmap, float big) {
        Matrix matrix = new Matrix();
        matrix.postScale(big, big); // 长和宽放大缩小的比例
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }


    /**
     * 缩小图片
     *
     * @param bitmap
     * @return
     */
    public static Bitmap small(Bitmap bitmap) {
        Matrix matrix = new Matrix();
        matrix.postScale(0.8f, 0.8f); // 长和宽放大缩小的比例
        Bitmap resizeBmp = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
                bitmap.getHeight(), matrix, true);
        return resizeBmp;
    }
}
