package com.aofei.tch.anagyre.other.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

/**
 * 图片压缩工具类，返回Bitmap
 * Created by Xiaokai on 2016/11/17
 */
public class BitmapUtils {
    /**
     * 压缩尺寸，返回正方形图片
     *
     * @param source
     * @return
     */
    public static Bitmap compressSize(Bitmap source, int targetSize) {
        if (source == null) {
            return null;
        }

        // 原始尺寸
        int sWidth = source.getWidth();
        int sHeight = source.getHeight();
        int sSize = Math.min(sWidth, sHeight);

        // 压缩比例
        int sampleSize = sSize / targetSize;

        // 裁剪图片为正方形，正中间部分
        Bitmap bitmap = Bitmap.createBitmap(source, (sWidth - sSize) / 2, (sHeight - sSize) / 2, sSize, sSize);

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = sampleSize;

        ByteArrayOutputStream os = new ByteArrayOutputStream();

        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);

        ByteArrayInputStream is = new ByteArrayInputStream(os.toByteArray());
        Bitmap target = BitmapFactory.decodeStream(is, null, options);

        source.recycle();

        return target;
    }

    /**
     * 压缩尺寸，返回长方形图片(按宽高比例压缩)
     *
     * @param source
     * @return
     */
    public static Bitmap compressSize(Bitmap source, int targetWidth, int targetHeight) {
        if (source == null) {
            return null;
        }
        // 原始尺寸
        int sWidth = source.getWidth();
        int sHeight = source.getHeight();

        // 压缩比例
        int sampleSize = sWidth / targetWidth;

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = sampleSize;

        ByteArrayOutputStream os = new ByteArrayOutputStream();

        source.compress(Bitmap.CompressFormat.JPEG, 100, os);

        ByteArrayInputStream is = new ByteArrayInputStream(os.toByteArray());
        Bitmap target = BitmapFactory.decodeStream(is, null, options);

        source.recycle();

        return target;
    }

    /**
     * 图片二次采样，质量每次压缩 5%，直到10KB大小
     *
     * @param source
     * @return
     */
    public static Bitmap compressQuality(Bitmap source) {
        if (source == null) {
            return null;
        }
        ByteArrayOutputStream os = new ByteArrayOutputStream();

        int quality = 100;
        do {
            // 每次压缩之前，要清空输出流
            os.reset();
            quality -= 5;
            if (quality < 0) {
                break;
            }
            source.compress(Bitmap.CompressFormat.JPEG, quality -= 1, os);
        }
        while (os.toByteArray().length > 1024 * 10); // 图片大于10KB就继续压缩

        ByteArrayInputStream is = new ByteArrayInputStream(os.toByteArray());

        Bitmap target = BitmapFactory.decodeStream(is, null, null);
        source.recycle();

        return target;
    }

    /**
     * 裁剪圆形图片，返回圆形图片
     *
     * @param source
     * @return
     */
    public static Bitmap createCircleBitmap(Bitmap source) {
        if (source == null) {
            return null;
        }
        // 原始尺寸大小，取最小值
        int size = Math.min(source.getWidth(), source.getHeight());

        // 裁剪起始坐标
        int x = (source.getWidth() - size) / 2;
        int y = (source.getHeight() - size) / 2;

        // 圆的半径
        int radius = size / 2;

        // 裁剪为正方形
        Bitmap bitmap = Bitmap.createBitmap(source, x, y, size, size);


        // 创建一张空白透明的图片
        Bitmap target = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);

        // 创建一张画布，和画笔
        Canvas canvas = new Canvas(target);
        Paint paint = new Paint();
        paint.setAntiAlias(true);

        canvas.drawCircle(size / 2, size / 2, radius, paint);

        // 设置两张图片重合时取交集
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        // 把剪完的图片画上去
        canvas.drawBitmap(bitmap, 0, 0, paint);

        // 回收原始图片
        source.recycle();

        return target;
    }
}
