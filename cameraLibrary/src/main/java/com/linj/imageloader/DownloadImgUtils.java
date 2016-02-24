package com.linj.imageloader;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import com.linj.imageloader.ImageUtil.ImageSize;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.util.Log;
import android.widget.ImageView;

/**
 * http://blog.csdn.net/lmj623565791/article/details/41874561
 *
 * @author zhy
 */
public class DownloadImgUtils {

    /**
     * 根据url下载图片在指定的文件
     *
     * @param urlStr
     * @param file
     * @return
     */
    public static boolean downloadImgByUrl(String urlStr, File file) {
        FileOutputStream fos = null;
        InputStream is = null;
        try {
            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            is = conn.getInputStream();
            fos = new FileOutputStream(file);
            byte[] buf = new byte[512];
            int len = 0;
            while ((len = is.read(buf)) != -1) {
                fos.write(buf, 0, len);
            }
            fos.flush();
            return true;

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (is != null)
                    is.close();
            } catch (IOException e) {
            }

            try {
                if (fos != null)
                    fos.close();
            } catch (IOException e) {
            }
        }

        return false;

    }

    /**
     * 根据url下载图片在指定的文件
     *
     * @param urlStr    图片url地址
     * @param imageView 图片显示的view
     * @return bitmap
     */
    public static Bitmap downloadImgByUrl(String urlStr, ImageView imageView) {
        FileOutputStream fileOutputStream = null;
        InputStream is = null;
        try {
            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            is = new BufferedInputStream(conn.getInputStream());
//            is.mark(is.available());
            int connInt=conn.getHeaderFieldInt("Content-Length", 0);
            if (connInt ==0) {
                return null;
            }
            is.mark(connInt+10);
            Log.i("-----", "--is.available:" + connInt);
            Options opts = new Options();
            opts.inJustDecodeBounds = true;
            Bitmap bitmap = BitmapFactory.decodeStream(is, null, opts);

            //获取imageView想要显示的宽和高
            ImageSize imageViewSize = ImageUtil.getImageViewSize(imageView);
            opts.inSampleSize = ImageUtil.caculateInSampleSize(opts,
                    imageViewSize.width, imageViewSize.height);

            opts.inJustDecodeBounds = false;
            is.reset();
            bitmap = BitmapFactory.decodeStream(is, null, opts);

            conn.disconnect();
            return bitmap;

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (is != null)
                    is.close();
            } catch (IOException e) {
            }
            try {
                if (fileOutputStream != null)
                    fileOutputStream.close();
            } catch (IOException e) {
            }
        }

        return null;

    }

}
