package com.linj.imageloader;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;

import com.linj.utils.Logger;

/**
 * 根据zhy的工具类进行修改
 * http://blog.csdn.net/lmj623565791/article/details/41874561
 *
 * @author DC
 */
public class ImageUtil {
    /**
     * 根据需求的宽和高以及图片实际的宽和高计算SampleSize
     *
     * @param options
     * @param reqWidth
     * @param reqHeight
     * @return
     */
    public static int caculateInSampleSize(Options options, int reqWidth, int reqHeight) {
        int width = options.outWidth;
        int height = options.outHeight;
        Log.i("---", "图片宽高:" + width + "," + height);
        int inSampleSize = 1;

        if (width > reqWidth || height > reqHeight) {
            int widthRadio = Math.round(width * 1.0f / reqWidth);
            int heightRadio = Math.round(height * 1.0f / reqHeight);

            inSampleSize = Math.max(widthRadio, heightRadio);
        }
        return inSampleSize;
    }

    /**
     * 根据ImageView获适当的压缩的宽和高
     *
     * @param imageView
     * @return
     */
    public static ImageSize getImageViewSize(ImageView imageView) {
        ImageSize imageSize = new ImageSize();
        DisplayMetrics displayMetrics = imageView.getContext().getResources().getDisplayMetrics();
        LayoutParams lp = imageView.getLayoutParams();

        int width = imageView.getWidth();// 获取imageview的实际宽度
        if (width <= 0) {
            width = lp.width;// 获取imageview在layout中声明的宽度
        }
        if (width <= 0) {
            //width = imageView.getMaxWidth();// 检查最大值
            width = getImageViewFieldValue(imageView, "mMaxWidth");
        }
        if (width <= 0) {
            width = displayMetrics.widthPixels;
        }

        int height = imageView.getHeight();// 获取imageview的实际高度
        if (height <= 0) {
            height = lp.height;// 获取imageview在layout中声明的宽度
        }
        if (height <= 0) {
            height = getImageViewFieldValue(imageView, "mMaxHeight");// 检查最大值
        }
        if (height <= 0) {
            height = displayMetrics.heightPixels;
        }
        imageSize.width = width;
        imageSize.height = height;

        return imageSize;
    }

    public static class ImageSize {
        int width;
        int height;
    }

    /**
     * 通过反射获取imageview的某个属性值
     *
     * @param object
     * @param fieldName
     * @return
     */
    private static int getImageViewFieldValue(Object object, String fieldName) {
        int value = 0;
        try {
            Field field = ImageView.class.getDeclaredField(fieldName);
            field.setAccessible(true);
            int fieldValue = field.getInt(object);
            if (fieldValue > 0 && fieldValue < Integer.MAX_VALUE) {
                value = fieldValue;
            }
        } catch (Exception e) {
        }
        return value;

    }


    /**
     * Bitmap转换成字节数组byte[]
     *
     * @param bmp
     * @return
     */
    public static byte[] bitmap2Byte(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
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

        int lowerBound = (maxNumOfPixels == -1) ? 1 : (int) Math.ceil(Math.sqrt(w * h / maxNumOfPixels));

        int upperBound = (minSideLength == -1) ? 128 : (int) Math.min(Math.floor(w / minSideLength), Math.floor(h / minSideLength));

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
     * 根据指定文件大小压缩图片
     *
     * @param bitmap  bitmap
     * @param maxSize 文件大小
     * @return 文件输出流
     */
    public static ByteArrayOutputStream compressByFileSize(Bitmap bitmap, int maxSize) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 99;
        while (baos.toByteArray().length / 1024 > maxSize) { // 循环判断如果压缩后图片是否大于100kb,大于继续压缩
            options -= 10;// 每次都减少10
            //压缩比小于0，不再压缩
            if (options < 0) {
                break;
            }
            Logger.i(1, "压缩中,照片大小", baos.toByteArray().length / 1024 + "KB-----ImageUtil");
            baos.reset();// 重置baos即清空baos
            bitmap.compress(Bitmap.CompressFormat.JPEG, options, baos);// 这里压缩options%，把压缩后的数据存放到baos中
        }
        return baos;
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
     * 保存图片,同时将其加入到系统图库
     *
     * @param context   上下文
     * @param bytes     字节数组
     * @param path      存储路径
     * @param imageName 图片名称(用于插入到系统图库)
     * @return 是否成功
     */
    public static boolean savePic(Context context, byte[] bytes, String path, String imageName) {
        boolean isOk;
        File file = new File(path);
//        if (file.exists()) {
//            file.delete();
//        }
        //如果文件所在的父文件夹不存在,则创建父文件夹
        if (!file.getParentFile().exists()) {
            if (!file.getParentFile().mkdirs()) {
                return false;
            }
        }
        try {
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(bytes);
            fos.flush();
            fos.close();
            //把文件插入到系统图库
//            MediaStore.Images.Media.insertImage(context.getContentResolver(), file.getAbsolutePath(), imageName, null);
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
     * 将字节转换为文件
     *
     * @param path  路径
     * @param bytes 字节数组
     * @return 文件
     */
    public static File getFileFromByte(String path, byte[] bytes) {
        File file = new File(path);
        FileOutputStream fos = null;
        if (!file.getParentFile().exists()) {
            if (!file.getParentFile().mkdirs()) {
                return null;
            }
        }
        try {
            fos = new FileOutputStream(file);
            fos.write(bytes);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }


}
