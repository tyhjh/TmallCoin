package com.yorhp.commonlibrary.app;

import android.app.Application;
import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;

import com.display.loglibrary.LogUtil;
import com.yorhp.commonlibrary.util.FileUtil;
import com.yorhp.commonlibrary.util.ScreenUtil;
import com.yorhp.commonlibrary.util.SharedPreferencesUtil;
import com.yorhp.crashlibrary.CrashHander;
import com.yorhp.crashlibrary.saveErro.ISaveErro;
import com.yorhp.crashlibrary.saveErro.SaveErroToSDCard;
import com.yorhp.recordlibrary.ScreenShotUtil;

import java.io.File;

import toast.ToastUtil;

/**
 * @author Tyhj
 * @date 2020-01-31
 * @Description: java类作用描述
 */

public class BaseApplication extends Application {

    public static String rootDir, savePointDir, saveChessDir, gradeDir, crashDir;

    public static boolean isDebug = true;

    @Override
    public void onCreate() {
        super.onCreate();
        ToastUtil.init(this);
        SharedPreferencesUtil.init(this);
        ScreenUtil.getScreenSize(this);
        initDir();
        LogUtil.init(this);
        LogUtil.setLogFilepath("/sdcard/Log/nana");
        LogUtil.setPrinttFileLevel(Log.VERBOSE);
        LogUtil.i("init Application");
        CrashHander.getInstance().init(this, new ISaveErro() {
            @Override
            public void saveErroMsg(Throwable throwable) {
                new SaveErroToSDCard(crashDir).saveErroMsg(throwable);
                Bitmap bitmap = ScreenShotUtil.getInstance().getScreenShot();
                FileUtil.bitmapToPath(bitmap, BaseApplication.savePointDir + "crash" + System.currentTimeMillis() + ".png");
            }
        });
    }

    //文件夹初始化
    public void initDir() {
        rootDir = Environment.getExternalStorageDirectory() + "/AlwaysJump/";
        File f1 = new File(rootDir);
        if (!f1.exists()) {
            f1.mkdirs();
        }

        saveChessDir = rootDir + "chess/";
        File f5 = new File(saveChessDir);
        if (!f5.exists()) {
            f5.mkdirs();
        }

        savePointDir = rootDir + "check/";
        File f6 = new File(savePointDir);
        if (!f6.exists()) {
            f6.mkdirs();
        }

        gradeDir = rootDir + "grade/";
        File f7 = new File(gradeDir);
        if (!f7.exists()) {
            f7.mkdirs();
        }

        crashDir = rootDir + "crash/";
        File f8 = new File(crashDir);
        if (!f8.exists()) {
            f8.mkdirs();
        }

    }

}
