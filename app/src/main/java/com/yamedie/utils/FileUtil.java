package com.yamedie.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Li Dachang on 16/1/25.
 * ..-..---.-.--..---.-...-..-....-.
 * 一些文件处理相关的工具类
 *
 */
public class FileUtil {
    public FileUtil() {
        // TODO Auto-generated constructor stub
    }

    /**
     * InputStream to byte
     * @param inStream 输入流
     * @return byte
     * @throws Exception
     */
    public byte[] readInputStream(InputStream inStream) throws Exception {
        byte[] buffer = new byte[1024];
        int len = -1;
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();

        while ((len = inStream.read(buffer)) != -1) {
            outStream.write(buffer, 0, len);
        }

        byte[] data = outStream.toByteArray();
        outStream.close();
        inStream.close();

        return data;
    }

    /**
     * Byte to bitmap
     * @param bytes byte
     * @param opts
     * @return bitmap
     */
    public Bitmap getBitmapFromBytes(byte[] bytes, BitmapFactory.Options opts) {
        if (bytes != null){
            if (opts != null){
                return BitmapFactory.decodeByteArray(bytes, 0, bytes.length,opts);
            }
            else{
                return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            }
        }

        return null;
    }

    /**
     * bitmap to bytes
     * @param bm
     * @return
     */
    public static byte[] Bitmap2Bytes(Bitmap bm) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }

    /**
     * file to bytes
     * @param file file
     * @return byteArray
     */
    public static byte[] getBytesFromFile(File file) {
        if (file == null) {
            return null;
        }
        try {
            FileInputStream stream = new FileInputStream(file);
            ByteArrayOutputStream out = new ByteArrayOutputStream(100000);
            byte[] b = new byte[100000];
            int n;
            while ((n = stream.read(b)) != -1)
                out.write(b, 0, n);
            stream.close();
            out.close();
            return out.toByteArray();
        } catch (IOException e) {

        }
        return null;
    }
}
