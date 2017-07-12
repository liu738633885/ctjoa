package com.lewis.utils;

/**
 * Created by lewis on 2017/4/6.
 */

public class Utils {
    private static long lastClickTime;

    public synchronized static boolean isFastClick() {
        long time = System.currentTimeMillis();
        if (time - lastClickTime < 300) {
            return true;
        }
        lastClickTime = time;
        return false;
    }
}
