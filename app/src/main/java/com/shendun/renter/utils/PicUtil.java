package com.shendun.renter.utils;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.util.Base64;

import org.opencv.core.Rect;

import java.io.ByteArrayOutputStream;
import java.io.IOException;


public class PicUtil {

    /**
     * 重设捕获区域的大小
     *
     * @param faceArray 人像绝对坐标
     * @param picWidth  原图像宽度
     * @param picHeight 原图像高度
     */
    public static void resizeFace(Rect faceArray, int picWidth, int picHeight) {
        int FaceX = faceArray.x;
        int FaceY = faceArray.y;
        int FaceWidth = faceArray.width;
        int FaceHeight = faceArray.height;

        double parawidth = 1.2;
        double paraheight = 1.4;
        int xcenter = FaceX + FaceWidth / 2;
        int ycenter = FaceY + FaceHeight / 2;
        FaceX = xcenter - (int) (FaceWidth * parawidth / 2);
        FaceY = ycenter - (int) (FaceHeight * paraheight / 1.8); //非对称
        if (FaceX < 0) FaceX = 0;
        if (FaceY < 0) FaceY = 0;
        FaceWidth = (int) (FaceWidth * parawidth);
        FaceHeight = (int) (FaceHeight * paraheight);
        if (FaceX + FaceWidth > picWidth) {
            FaceWidth = picWidth - FaceX - 2;
        }
        if (FaceY + FaceHeight > picHeight) {
            FaceHeight = picHeight - FaceY - 2;
        }
        faceArray.x = FaceX;
        faceArray.y = FaceY;
        faceArray.width = FaceWidth;
        faceArray.height = FaceHeight;
    }

    /**
     *  bitmap转byte[]
     */
    public static byte[] bitmapToBytes(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        return baos.toByteArray();
    }

    /**
     * bitmap转Base64
     * @param bitmap
     * @return
     */
    public static String bitmapToBase64(Bitmap bitmap) {

        // 要返回的字符串
        String reslut = null;
        ByteArrayOutputStream baos = null;

        try {
            if (bitmap != null) {
                baos = new ByteArrayOutputStream();
                // 压缩只对保存有效果bitmap还是原来的大小
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                baos.flush();
                baos.close();
                // 转换为字节数组
                byte[] byteArray = baos.toByteArray();
                // 转换为字符串
                reslut = Base64.encodeToString(byteArray, Base64.NO_WRAP);
            } else {
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (baos != null) {
                    baos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return reslut;
    }


    /**
     * drawable转bitmap
     * @param drawable
     * @return
     */
    public static Bitmap drawableToBitmap(Drawable drawable) {
        // 取 drawable 的长宽
        int w = drawable.getIntrinsicWidth();
        int h = drawable.getIntrinsicHeight();

        // 取 drawable 的颜色格式
        Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                : Bitmap.Config.RGB_565;
        // 建立对应 bitmap
        Bitmap bitmap = Bitmap.createBitmap(w, h, config);
        // 建立对应 bitmap 的画布
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, w, h);
        // 把 drawable 内容画到画布中
        drawable.draw(canvas);
        return bitmap;
    }
}
