package com.lewis.myoa;

import android.util.Log;

import com.orhanobut.logger.Logger;
import com.upyun.library.common.Params;
import com.upyun.library.common.UploadEngine;
import com.upyun.library.listener.UpCompleteListener;
import com.upyun.library.listener.UpProgressListener;
import com.upyun.library.utils.UpYunUtils;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by lewis on 16/6/24.
 */
public class UpLoadManager {

    /**
     * 压缩的最大大小(粗略)
     */
    final static int compressWidth = 1000;
    final static int compressHeight = 1000;

    private static final String TAG = "UpLoadManager";
    public static String KEY = "fC+LEr6dMS5/FTWpq1TrPIMdd9Q=";
    public static String SPACE = "ainana";
    public static String savePath = "/uploads/{year}{mon}{day}{hour}/{random32}{.suffix}";
    //String savePath = "/" + SystemUtils.getDeviceIMEI(MainApplication.getInstance()) + System.currentTimeMillis() + ".test";

    //操作员
    public static String OPERATER = "newandroid";
    //密码
    public static String PASSWORD = "ainana123456";

    private static String creatSavePath(String filePath, String topPath) {
        String suffix = filePath.substring(filePath.lastIndexOf("."));
        String savePath = "/" + System.currentTimeMillis();
        savePath += suffix;
        Logger.d("生成的网络 path:" + savePath);
        return savePath;
    }

    public static void uploadImg(String filePath, UpCompleteListener completeListener) {
        uploadImg(filePath, completeListener, "");
    }

    public static void uploadImg(String filePath, UpCompleteListener completeListener, String topPath) {
        File temp = new File(filePath);
        final Map<String, Object> paramsMap = new HashMap<>();
        //上传空间
        paramsMap.put(Params.BUCKET, SPACE);
        //保存路径，任选其中一个
        paramsMap.put(Params.SAVE_KEY, savePath);
        //可选参数（详情见api文档介绍）
        //paramsMap.put(Params.RETURN_URL, "httpbin.org/post");
        paramsMap.put("x-gmkerl-thumb", "/max/1000");
        //paramsMap.put("x-gmkerl-thumb", "/format/png");
        //paramsMap.put(Params.CONTENT_MD5, UpYunUtils.md5Hex(temp));
        //paramsMap.put(Params.RETURN_URL, "http://ainana.b0.upaiyun.com/");
        //进度回调，可为空
        UpProgressListener progressListener = new UpProgressListener() {
            @Override
            public void onRequestProgress(final long bytesWrite, final long contentLength) {
                Log.d(TAG, (100 * bytesWrite) / contentLength + "%");
            }
        };

        //结束回调，不可为空
        if (completeListener == null) {
            return;
        }

        //UploadManager.getInstance().formUpload(temp, paramsMap, KEY, completeListener, progressListener);
        //UploadManager.getInstance().formUpload(new File(filePath), paramsMap, signatureListener, completeListener, progressListener);
        //UploadManager.getInstance().blockUpload(temp, paramsMap, KEY, completeListener, progressListener);
        //UploadManager.getInstance().blockUpload(temp, paramsMap, signatureListener, completeListener, progressListener);
        UploadEngine.getInstance().formUpload(temp, paramsMap, OPERATER, UpYunUtils.md5(PASSWORD), completeListener, progressListener);
        //UploadEngine.getInstance().formUpload(temp, policy, OPERATER, signature, completeListener, progressListener);
    }


    public interface UpLoadListener {
        public void Success(List<String> urls);

        public void Failed(int whereFailed);
    }
}
