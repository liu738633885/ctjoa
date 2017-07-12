package com.ctj.oa.utils;


import com.ctj.oa.Constants;

/**
 * Created by lewis on 16/7/28.
 * 处理一些常用的字符串
 */
public class StringUtils {

    /**
     * 把杂乱的图片字符串转化成相对路径
     *
     * @param path
     * @return
     */
    public static String getImgRelativePath(String path) {
        if (!path.startsWith(Constants.IMG_HEAD)) {
            return path;
        } else {
            String[] strings = path.split(Constants.IMG_HEAD);
            if (strings.length > 0) {
                return strings[(strings.length - 1)];
            } else {
                return path;
            }
        }

    }
}
