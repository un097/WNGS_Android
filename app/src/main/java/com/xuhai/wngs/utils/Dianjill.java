package com.xuhai.wngs.utils;

/**
 * Created by Administrator on 2014/10/15.
 */
public class Dianjill {
    private static long lastClickTime;

    public static boolean isFastDoubleClick() {
        long time = System.currentTimeMillis();
        if (time - lastClickTime < 3000) {
            return true;
        }
        lastClickTime = time;
        return false;
    }
}