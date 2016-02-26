package com.yamedie.utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import android.annotation.SuppressLint;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;

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
     * 将drawable转换为Bitmap
     *
     * @param drawable Drawable
     * @return bitmap
     */
    public static Bitmap drawableToBitmap(Drawable drawable) {
        Bitmap bitmap = Bitmap.createBitmap(
                drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(),
                drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                        : Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);
        return bitmap;
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
     * 估算图片大小
     *
     * @param options        options
     * @param minSideLength  短边长度
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

    private static int computeInitialSampleSize(BitmapFactory.Options options, int minSideLength, int maxNumOfPixels) {
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
     *
     * @param imagePath 路径
     * @param width     宽
     * @param height    高
     * @return 缩略图bitmap
     */
    public static Bitmap getImageThumbnail(String imagePath, int width, int height) {
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
     *
     * @param videoPath 视频路径
     * @param width     宽
     * @param height    高
     * @param kind      类型
     * @return 缩略图bitmap
     */
    public static Bitmap getVideoThumbnail(String videoPath, int width, int height, int kind) {
        Bitmap bitmap = null;
        bitmap = ThumbnailUtils.createVideoThumbnail(videoPath, kind);
        System.out.println("w" + bitmap.getWidth());
        System.out.println("h" + bitmap.getHeight());
        bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height,
                ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
        return bitmap;
    }

    /**
     * 保存图片,同时通知系统图库进行更新
     *
     * @param context   上下文
     * @param bitmap    bitmap
     * @param path      路径
     * @param imageName 图片名称,用于插入到系统图库
     * @return 是否成功
     */
    public static boolean savePic(Context context, Bitmap bitmap, String path, String imageName) {
        boolean isOk;
        File file = new File(path);
        //如果文件所在的父文件夹不存在,则创建父文件夹
        if (!file.getParentFile().exists()) {
            if (!file.getParentFile().mkdirs()) {
                return false;
            }
        }
        try {
            file.createNewFile();
            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, fos);
            fos.write(bitmap2Byte(bitmap));
            fos.flush();
            fos.close();
            //把文件插入到系统图库
            MediaStore.Images.Media.insertImage(context.getContentResolver(), file.getAbsolutePath(), imageName, null);
            //通知系统图库更新
            context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + path)));
            isOk = true;
        } catch (IOException e) {
            e.printStackTrace();
            isOk = false;
        }
        return isOk;
    }

    /**
     * 将ImageView的图片保存
     *
     * @param imageView ImageView
     */
    public static boolean savePicFromImageView(Context context, ImageView imageView, String path, String imgName) {
        Drawable mark = imageView.getDrawable();
        Bitmap bitmap = drawableToBitmap(mark);
        Logger.i(1, "bitmap size:" + bitmap.getByteCount());
        Logger.i(1, "path:" + path);
        return savePic(context, bitmap, path, imgName);
    }

    /**
     * 从ImageView中获取Bitmap
     * 注意:此方法获取的是图片实际显示的大小,同时也包括imageview的一些效果如背景/阴影等.如果要获取原图,则需要获取其drawable
     *
     * @param view view
     * @return bitmap
     */
    public static Bitmap viewToBitmap(View view) {
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(view.getDrawingCache());
        view.setDrawingCacheEnabled(false);
        return bitmap;
    }

    /**
     * 为Bitmap添加合成图片
     *
     * @param bitmap 原图bitmap
     * @param view   被合成的view
     * @return 处理后的图片
     */
    public static Bitmap composeBitmapWithView(Bitmap bitmap, View view) {
        // TODO Auto-generated method stub
        if (view == null || !(view.getVisibility() == View.VISIBLE)) {
            return bitmap;
        }
        Bitmap wBitmap = viewToBitmap(view);
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        int ww = wBitmap.getWidth();
        int wh = wBitmap.getHeight();
        Bitmap newBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(newBitmap);
        //draw src into
        canvas.drawBitmap(bitmap, 0, 0, null);//在 0，0坐标开始画入src
        canvas.drawBitmap(wBitmap, w - ww - 20, h - wh - 20, null);//在src的右下角画入水印
        //save all clip
        canvas.save(Canvas.ALL_SAVE_FLAG);//保存
        canvas.restore();//存储
        bitmap.recycle();
        bitmap = null;//回收
        wBitmap.recycle();
        wBitmap = null;//回收
        return newBitmap;
    }

    /**
     * 将两个Bitmap进行合成
     *
     * @param originBitmap 原始天皇巨星bitmap
     * @param bitmap       被合成的bitmap
     * @return 合成后的bitmap
     */
    public static Bitmap ComposeBitmaps(Bitmap originBitmap, Bitmap bitmap) {
        int originBitmapWidth = originBitmap.getWidth();
        int originBitmapHeight = originBitmap.getHeight();
        Bitmap composedBitmap = Bitmap.createBitmap(originBitmapWidth, originBitmapHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(composedBitmap);//新建一个基于处理后bitmap的画板
        canvas.drawBitmap(originBitmap, 0, 0, null);
        canvas.drawBitmap(bitmap, originBitmapWidth - bitmap.getWidth() - 20, originBitmapHeight - bitmap.getHeight() - 20, null);
        canvas.save(Canvas.ALL_SAVE_FLAG);//保存
        canvas.restore();//存储
        originBitmap.recycle();//回收
        bitmap.recycle();
        return composedBitmap;
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
     * 根据较长边改变图片尺寸
     *
     * @param bitmap bitmap
     * @param size   尺寸(像素)
     * @return bitmap
     */
    public static Bitmap scalePicByLongSize(Bitmap bitmap, int size) {
        //获取图片宽高
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        float scale;
        //选取较长边计算缩放比例
        if (width >= height) {
            scale = ((float) size) / width;
        } else {
            scale = ((float) size) / height;
        }
        //使用矩阵处理图片
        Matrix matrix = new Matrix();
        matrix.postScale(scale, scale);
        Logger.i("---", "old bitmap:" + width + "_" + height);
        //生成新的bitmap
        Bitmap newBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
        Logger.i("---", "new bitmap:" + newBitmap.getWidth() + "_" + newBitmap.getHeight());
        return newBitmap;
    }

    /**
     * 限定长边长度对半压缩图片
     *
     * @param image 图片bitmap
     * @param size  限定长度
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
        Logger.i("------", "sampleSize:" + be);
        bais = new ByteArrayInputStream(baos.toByteArray());
        bitmap = BitmapFactory.decodeStream(bais, null, newOpts);
        return compressBiamapSample(bitmap);
    }

    /**
     * 压缩图片至100k以内
     *
     * @param image bitmap
     * @return bitmap
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
     *
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
