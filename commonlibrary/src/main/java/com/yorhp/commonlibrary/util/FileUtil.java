package com.yorhp.commonlibrary.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * 〈一句话功能简述〉
 * 〈功能详细描述〉
 *
 * @author hanpei
 * @version 1.0, 2019/9/7
 * @since 产品模块版本
 */
public class FileUtil {

    /**
     * 从assets目录下拷贝文件
     *
     * @param context            上下文
     * @param assetsFilePath     文件的路径名如：SBClock/0001cuteowl/cuteowl_dot.png
     * @param targetFileFullPath 目标文件路径如：/sdcard/SBClock/0001cuteowl/cuteowl_dot.png
     */
    public static void copyFileFromAssets(Context context, String assetsFilePath, String targetFileFullPath) {
        Log.d("Tag", "copyFileFromAssets ");
        InputStream assestsFileImputStream;
        try {
            assestsFileImputStream = context.getAssets().open(assetsFilePath);
            copyFile(assestsFileImputStream, targetFileFullPath);
        } catch (IOException e) {
            Log.d("Tag", "copyFileFromAssets " + "IOException-" + e.getMessage());
            e.printStackTrace();
        }
    }


    public static void drawSmallPoint(Bitmap bitmap, int x, int y, int color) {
        drawPoint(bitmap, x, y, 2, color);
    }

    /**
     * @param bitmap
     * @param fromY  开始的比例
     * @param y      长度所占比例
     * @return
     */
    public static Bitmap cropBitmapY(Bitmap bitmap, double fromY, double y) {
        return Bitmap.createBitmap(bitmap, 0, (int) (bitmap.getHeight() * fromY), bitmap.getWidth(), (int) (bitmap.getHeight() * y));
    }


    /**
     * @param bitmap
     * @param fromX  开始的比例
     * @param x      长度所占比例
     * @return
     */
    public static Bitmap cropBitmapX(Bitmap bitmap, double fromX, double x) {
        return Bitmap.createBitmap(bitmap,  (int) (bitmap.getWidth() * fromX),0,  (int) (bitmap.getWidth() * x),bitmap.getHeight());
    }




    private static void copyFile(InputStream in, String targetPath) {
        try {
            File file = new File(targetPath);
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            FileOutputStream fos = new FileOutputStream(file);
            byte[] buffer = new byte[1024];
            int byteCount = 0;
            while ((byteCount = in.read(buffer)) != -1) {
                // buffer字节
                fos.write(buffer, 0, byteCount);
            }
            fos.flush();// 刷新缓冲区
            in.close();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static File bitmapToPath(Bitmap bitmap, String filepath) {
        File file = new File(filepath);
        //3.保存Bitmap
        try {
            //文件

            if (file.exists()) {
                file.delete();
                file.createNewFile();
            }

            FileOutputStream fos = null;
            fos = new FileOutputStream(file);
            if (null != fos) {
                bitmap.compress(Bitmap.CompressFormat.PNG, 90, fos);
                fos.flush();
                fos.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return file;
    }


    public static void drawPoint(Bitmap bitmap, int x, int y, int size, int color) {
        Canvas canvas = new Canvas(bitmap);
        Paint paint2 = new Paint();
        paint2.setColor(color);
        paint2.setStyle(Paint.Style.FILL);
        canvas.drawRect(x - size, y - size, x + size, y + size, paint2);
    }


    public static void drawRect(Bitmap bitmap, Rect rect, int color) {
        Canvas canvas = new Canvas(bitmap);
        Paint paint2 = new Paint();
        paint2.setColor(color);
        paint2.setStyle(Paint.Style.FILL);
        canvas.drawRect(rect, paint2);
    }


    /**
     * 从assets目录中复制整个文件夹内容
     *
     * @param context Context 使用CopyFiles类的Activity
     * @param oldPath String  原文件路径  如：/aa
     * @param newPath String  复制后路径  如：xx:/bb/cc
     */
    public void copyFilesForAssets(Context context, String oldPath, String newPath) {
        try {
            //获取assets目录下的所有文件及目录名
            String fileNames[] = context.getAssets().list(oldPath);
            //如果是目录
            if (fileNames.length > 0) {
                File file = new File(newPath);
                file.mkdirs();//如果文件夹不存在，则创建
                for (String fileName : fileNames) {
                    copyFilesForAssets(context, oldPath + "/" + fileName, newPath + "/" + fileName);
                }
            } else {//如果是文件
                InputStream is = context.getAssets().open(oldPath);
                FileOutputStream fos = new FileOutputStream(new File(newPath));
                byte[] buffer = new byte[1024];
                int byteCount = 0;
                //循环从输入流读取 buffer字节
                while ((byteCount = is.read(buffer)) != -1) {
                    //将读取的输入流写入到输出流
                    fos.write(buffer, 0, byteCount);
                }
                fos.flush();//刷新缓冲区
                is.close();
                fos.close();
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


}
