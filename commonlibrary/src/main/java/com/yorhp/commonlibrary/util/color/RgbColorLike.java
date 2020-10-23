package com.yorhp.commonlibrary.util.color;

import android.graphics.Color;

import log.LogUtils;


public class RgbColorLike implements LikeColor {
    @Override
    public boolean isLike(int color1, int color2, double aberration) {
        return baseLike(color1, color2, aberration);
    }


    /**
     * @param color1 第一种颜色
     * @param color2 第二种颜色
     * @return
     */
    public static boolean baseLike(int color1, int color2, double aberration) {
        int red = Color.red(color1); // 取高两位
        int green = Color.green(color1);// 取中两位
        int blue = Color.blue(color1);// 取低两位
        int red2 = Color.red(color2); // 取高两位
        int green2 = Color.green(color2);// 取中两位
        int blue2 = Color.blue(color2);// 取低两位
        if (red == red2 && green == green2 && blue == blue2) {
            return true;
        }
        if ((Math.abs(red - red2) < aberration && Math.abs(green - green2) < aberration && Math.abs(blue - blue2) < aberration) &&
                (Math.abs(red - red2) + Math.abs(green - green2) + Math.abs(blue - blue2)) < aberration * 2.5) {
            return true;
        }
        return false;
    }


    /**
     * 简单的RGB颜色判断
     *
     * @return
     */
    public static void rgbAberration(int color1, int color2) {
        int red1 = Color.red(color1); // 取高两位
        int green1 = Color.green(color1);// 取中两位
        int blue1 = Color.blue(color1);// 取低两位
        int red2 = Color.red(color2); // 取高两位
        int green2 = Color.green(color2);// 取中两位
        int blue2 = Color.blue(color2);// 取低两位

        int red = Math.abs(red1 - red2);
        int green = Math.abs(green1 - green2);
        int blue = Math.abs(blue1 - blue2);
        LogUtils.e("RGB颜色判断：色差：" + red + "，" + green + "，" + blue + "，all：" + (red + green + blue));
    }

}
