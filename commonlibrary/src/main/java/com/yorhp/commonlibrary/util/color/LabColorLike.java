package com.yorhp.commonlibrary.util.color;

import android.graphics.Color;

public class LabColorLike implements LikeColor {

    @Override
    public boolean isLike(int color1, int color2, double aberration) {
        if (labAberration(color1, color2) <= aberration) {
            return true;
        }
        return false;
    }


    /**
     * LAB颜色空间计算色差，基于人眼对颜色的感知，
     * 可以表示人眼所能感受到的所有颜色。
     * L表示明度，A表示红绿色差，B表示蓝黄色差
     */
    public static int labAberration(int color1, int color2) {
        int r1 = Color.red(color1); // 取高两位
        int g1 = Color.green(color1);// 取中两位
        int b1 = Color.blue(color1);// 取低两位
        int r2 = Color.red(color2); // 取高两位
        int g2 = Color.green(color2);// 取中两位
        int b2 = Color.blue(color2);// 取低两位

        int rmean = (r1 + r2) / 2;
        int r = r1 - r2;
        int g = g1 - g2;
        int b = b1 - b2;
        return (int) Math.sqrt((2 + rmean / 256) * (Math.pow(r, 2)) + 4 * (Math.pow(g, 2)) + (2 + (255 - rmean) / 256) * (Math.pow(b, 2)));
    }

}
