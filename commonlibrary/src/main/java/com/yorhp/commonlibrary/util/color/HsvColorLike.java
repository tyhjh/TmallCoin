package com.yorhp.commonlibrary.util.color;

import android.graphics.Color;

public class HsvColorLike implements LikeColor {

    @Override
    public boolean isLike(int color1, int color2, double aberration) {
        if (hsvAberration(color1, color2) <= aberration) {
            return true;
        }
        return false;
    }


    /**
     * HSV颜色空间计算颜色距离
     */
    public static double hsvAberration(int color1, int color2) {
        float[] tempHSV1 = new float[3];
        Color.colorToHSV(color1, tempHSV1);
        float[] tempHSV2 = new float[3];
        Color.colorToHSV(color2, tempHSV2);
        HSV hsv1 = new HSV();
        hsv1.H = tempHSV1[0];
        hsv1.S = tempHSV1[1];
        hsv1.V = tempHSV1[2];
        HSV hsv2 = new HSV();
        hsv2.H = tempHSV2[0];
        hsv2.S = tempHSV2[1];
        hsv2.V = tempHSV2[2];
        return HSV.distanceOf(hsv1, hsv2);
    }



    public static class HSV {
        public float H;
        public float S;
        public float V;

        //self-defined
        private static final double R = 100;
        private static final double angle = 30;
        private static final double h = R * Math.cos(angle / 180 * Math.PI);
        private static final double r = R * Math.sin(angle / 180 * Math.PI);


        /**
         * HSV颜色空间计算颜色距离
         * HSV是个六棱锥模型,这个模型中颜色的参数分别是：色调（H），饱和度（S），明度（V）
         *
         * @param hsv1
         * @param hsv2
         * @return
         */
        public static double distanceOf(HSV hsv1, HSV hsv2) {
            double x1 = r * hsv1.V * hsv1.S * Math.cos(hsv1.H / 180 * Math.PI);
            double y1 = r * hsv1.V * hsv1.S * Math.sin(hsv1.H / 180 * Math.PI);
            double z1 = h * (1 - hsv1.V);
            double x2 = r * hsv2.V * hsv2.S * Math.cos(hsv2.H / 180 * Math.PI);
            double y2 = r * hsv2.V * hsv2.S * Math.sin(hsv2.H / 180 * Math.PI);
            double z2 = h * (1 - hsv2.V);
            double dx = x1 - x2;
            double dy = y1 - y2;
            double dz = z1 - z2;
            return Math.sqrt(dx * dx + dy * dy + dz * dz);
        }
    }

}
