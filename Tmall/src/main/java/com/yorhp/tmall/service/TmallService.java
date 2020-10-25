package com.yorhp.tmall.service;

import android.accessibilityservice.AccessibilityService;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.os.SystemClock;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;
import android.widget.ImageView;

import com.display.loglibrary.LogUtil;
import com.yorhp.commonlibrary.app.BaseAccessbilityService;
import com.yorhp.commonlibrary.util.ScreenUtil;
import com.yorhp.commonlibrary.util.threadpool.AppExecutors;
import com.yorhp.recordlibrary.ScreenShotUtil;
import com.yorhp.tmall.R;
import com.yorhp.wordfindlibrary.impl.OcrResult;
import com.yorhp.wordfindlibrary.impl.WordsFindManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import toast.ToastUtil;

import static android.view.WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR;

/**
 * @author tyhj
 * @date 2020/10/22
 * @Description: 双十一领喵币辅助
 */

public class TmallService extends BaseAccessbilityService {

    /**
     * 开始识别的Y轴比例
     */
    private static final float START_X_SCALE = 0.67F;


    /**
     * 开始识别的X轴比例
     */
    private static final float START_Y_SCALE = 0F;


    /**
     * 已完成的坐标
     */
    private List<Rect> rectListFinished = new ArrayList<>();

    /**
     * 可以点击的按钮，去逛逛目前容易识别出错
     */
    private static final List<String> btnTexts = Arrays.asList(new String[]{"去浏览", "去逛逛", "去搜索", "去观看"});


    /**
     * 去逛逛，容易识别出错的字
     */
    private static final List<String> btnOtherTexts = Arrays.asList(new String[]{"去迎证", "去海迎", "去进诞", "去证斑", "去迎迎"});


    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
    }

    @Override
    public void onInterrupt() {

    }


    private int startX, startY;

    /**
     * 开始进行领喵币操作
     */
    private void start() {
        AppExecutors.getInstance().cpuThread().execute(() -> {
            while (true) {
                if (start) {
                    //获取屏幕截屏
                    Bitmap bitmap = ScreenShotUtil.getInstance().getScreenShot();
                    int height = bitmap.getHeight();
                    int width = bitmap.getWidth();
                    startX = (int) (START_X_SCALE * width);
                    startY = (int) (START_Y_SCALE * height);
                    //裁剪出识别区域，只识别按钮
                    Bitmap wordsBitmap = Bitmap.createBitmap(bitmap, startX, startY, (int) ((1 - START_X_SCALE) * width), (int) ((1 - START_Y_SCALE) * height));
                    //获取文字所在的区域
                    List<OcrResult> rectList = WordsFindManager.getInstance().runModel(wordsBitmap);
                    rectListFinished.clear();
                    //遍历文字找到按钮进行点击
                    boolean isFindTxt = findBtn(rectList);
                    if (!isFindTxt) {
                        startX = 0;
                        startY = 0;
                        //还是没有可以点击的文字，识别全图
                        //获取文字所在的区域
                        rectList = WordsFindManager.getInstance().runModel(bitmap);
                        boolean notFinished = findBtn(rectList);
                        //没了
                        if (!notFinished) {
                            ToastUtil.showShort("我累了，你自己点吧");
                            start = false;
                        }
                    }
                }
                //等待个2秒，页面刷新
                SystemClock.sleep(2000);
            }
        });
    }


    /**
     * 遍历文字找到按钮进行点击
     *
     * @param rectList
     */
    private boolean findBtn(List<OcrResult> rectList) {
        if (rectList == null) {
            return false;
        }
        //识别到去浏览的按钮
        for (OcrResult result : rectList) {
            String txt = result.getTxt();
            if (txt.equals("已完成")) {
                //添加到已完成列表
                rectListFinished.add(result.getRect());
            }
            LogUtil.i("识别到的文字为：" + txt);
            if (btnTexts.contains(txt) || btnOtherTexts.contains(txt)) {
                //获取喵币
                getCatCoin(startX, startY, result);
                return true;
            }
            //逛一逛容易识别出错的地方
            if (txt.contains("去") && txt.contains("迎")) {
                //获取喵币
                if (notFinished(result)) {
                    //获取喵币
                    getCatCoin(startX, startY, result);
                    return true;
                }
            }

            if (txt.contains("一") && (txt.contains("进") || txt.contains("海") || txt.contains("诞") || txt.contains("迎"))) {
                if (notFinished(result)) {
                    //获取喵币
                    getCatCoin(startX, startY, result);
                    return true;
                }
            }

            //其他的处理
            if (txt.contains("浏览") || txt.contains("逛一逛")) {
                if (notFinished(result)) {
                    //获取喵币
                    getCatCoin(startX, startY, result);
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 是否未完成任务
     *
     * @return
     */
    private boolean notFinished(OcrResult result) {
        for (Rect rect : rectListFinished) {
            int value = Math.abs(rect.top - result.getRect().top);
            if (value < 70) {
                return false;
            }
        }
        return true;
    }


    /**
     * 获取喵币
     *
     * @param startX
     * @param startY
     * @param result
     */
    private void getCatCoin(int startX, int startY, OcrResult result) {
        Rect rect = result.getRect();
        //点击去浏览
        clickOnScreen(rect.left + startX, rect.top + startY, 10, null);
        //等待页面加载3秒
        SystemClock.sleep(3 * 1000);
        //划一下
        performScrollDownward((int) (ScreenUtil.SCREEN_HEIGHT*0.7), ScreenUtil.SCREEN_HEIGHT / 2, null);
        //浏览17秒
        SystemClock.sleep(20 * 1000);
        //返回
        performBackClick();
    }


    private volatile boolean start;
    private WindowManager.LayoutParams params;
    private WindowManager windowManager;
    private ImageView btnView;

    /**
     * 创建悬浮窗
     */
    private void createWindowView() {
        btnView = new ImageView(getApplicationContext());
        btnView.setImageResource(R.drawable.ic_star);
        windowManager = (WindowManager) getApplicationContext()
                .getSystemService(Context.WINDOW_SERVICE);
        params = new WindowManager.LayoutParams();
        // 设置Window Type
        params.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        // 设置悬浮框不可触摸
        params.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | FLAG_LAYOUT_INSET_DECOR;
        // 悬浮窗不可触摸，不接受任何事件,同时不影响后面的事件响应
        params.format = PixelFormat.RGBA_8888;
        // 设置悬浮框的宽高
        params.width = 150;
        params.height = 150;
        params.gravity = Gravity.TOP;
        params.x = 300;
        params.y = 200;


        btnView.setOnTouchListener(new View.OnTouchListener() {

            //保存悬浮框最后位置的变量
            int lastX, lastY;
            int paramX, paramY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        lastX = (int) event.getRawX();
                        lastY = (int) event.getRawY();
                        paramX = params.x;
                        paramY = params.y;
                        break;
                    case MotionEvent.ACTION_MOVE:
                        int dx = (int) event.getRawX() - lastX;
                        int dy = (int) event.getRawY() - lastY;
                        params.x = paramX + dx;
                        params.y = paramY + dy;
                        // 更新悬浮窗位置
                        windowManager.updateViewLayout(btnView, params);
                        break;
                }
                return false;
            }
        });

        btnView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (start) {
                    //暂停
                    start = false;
                    ToastUtil.showShort("pause");
                } else {
                    //开始
                    start = true;
                    LogUtil.i("start is click");
                    ToastUtil.showShort("have fun");
                }
            }
        });
        windowManager.addView(btnView, params);
    }


    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        //初始化文字识别框架
        WordsFindManager.getInstance().init(getApplicationContext());
        //开始运行
        start();
        //显示悬浮窗
        createWindowView();
        //打开淘宝APP
        Intent intent = getPackageManager().getLaunchIntentForPackage("com.taobao.taobao");
        if (intent != null) {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
        ToastUtil.showLong("请先打开领喵币中心，然后点击屏幕右上方的五角星");

    }

    /**
     * 模拟界面向下滑操作
     */
    public void performScrollDownward(int start, int distance, AccessibilityService.GestureResultCallback callback) {
        Path path = new Path();
        path.moveTo(ScreenUtil.SCREEN_WIDTH / 2, start);
        path.lineTo(ScreenUtil.SCREEN_WIDTH / 2, start - distance);
        gestureOnScreen(path, 0, 100, callback);
    }

}
