package com.yorhp.commonlibrary.util.color;

//颜色相似
public interface LikeColor {
    boolean isLike(int color1, int color2, double aberration);
}
