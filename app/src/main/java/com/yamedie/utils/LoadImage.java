package com.yamedie.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * Created by Li Dachang on 16/1/26.
 * ..-..---.-.--..---.-...-..-....-.
 */
public class LoadImage {
    /**
     * 从url地址获取图片并以bitmap形式传回来
     * @param imgUrl utldivi
     * @return bitmap
     */
    public static Bitmap getBitmapFromUrl(String imgUrl) {
        URL url;
        Bitmap bitmap = null;
        try {
            Logger.i("---加载图片开始");
            url = new URL(imgUrl);
            InputStream is = url.openConnection().getInputStream();
            BufferedInputStream bis = new BufferedInputStream(is);
            //BitmapFactory.decodeStream(bis)方法在获取bmp图片时可能会无效
            // bitmap = BitmapFactory.decodeStream(bis);
            byte[] b = getBytes(is);
            bitmap = BitmapFactory.decodeByteArray(b, 0, b.length);
            bis.close();
            Logger.i("---加载图片成功");
        } catch (IOException e) {
            Logger.i("---加载图片失败!");
            e.printStackTrace();
        }
        return bitmap;
    }

    /**
     * 将InputStream对象转换为Byte[]
     * @param is InputStream对象
     * @return 字节
     * @throws IOException
     */
    public static byte[] getBytes(InputStream is) throws IOException {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] b = new byte[1024];
        int len;
        while ((len = is.read(b, 0, 1024)) != -1) {
            baos.write(b, 0, len);
            baos.flush();
        }
        byte[] bytes = baos.toByteArray();
        return bytes;
    }
}
