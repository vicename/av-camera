package com.linj.imageloader;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.Toast;

import com.linj.FileOperateUtil;

import java.io.ByteArrayOutputStream;
import java.io.File;

/**
 * 拍照返回的byte数据处理类
 * Created by Li Dachang on 16/2/24.
 * ..-..---.-.--..---.-...-..-....-.
 */
public class ImageDataHandler {
    private String mImagePath;
    private File mImageFile;
    private Context mContext;
    private String mImageFolder;//大图存放路径
    private int maxSize = 1000;//压缩后的图片最大值 单位KB

    private final String ROOT_FOLDER_PHOTO = "photo";

    public ImageDataHandler(Context context) {
        mContext = context;
        mImageFolder = FileOperateUtil.getFolderPathDC(ROOT_FOLDER_PHOTO);
        File folder = new File(mImageFolder);
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
            //产生新的文件名
            String imgName = FileOperateUtil.createFileName(".jpg");
            mImagePath = mImageFolder + File.separator + imgName;
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
}
