package com.ctj.oa;

import android.content.Context;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;

import com.hyphenate.chatuidemo.DemoHelper;
import com.pgyersdk.crash.PgyCrashManager;
import com.yanzhenjie.nohttp.OkHttpNetworkExecutor;
import com.yolanda.nohttp.Logger;
import com.yolanda.nohttp.NoHttp;
import com.yolanda.nohttp.cache.DBCacheStore;


/**
 * Created by lewis on 16/6/16.
 */
public class MainApplication extends MultiDexApplication {
    private static MainApplication instance;
    public static String currentUserNick = "";

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        NoHttp.initialize(this, new NoHttp.Config()
                // 设置全局连接超时时间，单位毫秒，默认10s。
                .setConnectTimeout(30 * 1000)
                // 设置全局服务器响应超时时间，单位毫秒，默认10s。
                .setReadTimeout(30 * 1000)
                // 配置缓存，默认保存数据库DBCacheStore，保存到SD卡使用DiskCacheStore。
                .setCacheStore(
                        new DBCacheStore(this).setEnable(true) // 如果不使用缓存，设置false禁用。
                ).setNetworkExecutor(new OkHttpNetworkExecutor()));
        Logger.setTag("NoHttpSample");
        Logger.setDebug(BuildConfig.DEBUG);// 开始NoHttp的调试模式, 这样就能看到请求过程和日志
        // 环信
        DemoHelper.getInstance().init(this);
        //bug跟踪
        if (!BuildConfig.DEBUG) {
            PgyCrashManager.register(this);
        }
    }

    public static MainApplication getInstance() {
        return instance;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}
