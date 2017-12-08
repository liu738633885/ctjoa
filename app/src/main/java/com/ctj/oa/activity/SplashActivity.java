package com.ctj.oa.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.widget.ImageView;
import android.widget.TextView;

import com.ctj.oa.Constants;
import com.ctj.oa.MainApplication;
import com.ctj.oa.R;
import com.ctj.oa.model.netmodel.NetBaseBean;
import com.ctj.oa.net.CallServer;
import com.ctj.oa.net.HttpListenerCallback;
import com.ctj.oa.net.NetBaseRequest;
import com.ctj.oa.net.RequsetFactory;
import com.ctj.oa.utils.imageloader.ImageLoader;
import com.ctj.oa.utils.manager.UserManager;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chatuidemo.DemoHelper;
import com.hyphenate.chatuidemo.ui.VideoCallActivity;
import com.hyphenate.chatuidemo.ui.VoiceCallActivity;
import com.hyphenate.util.EasyUtils;
import com.lewis.utils.SystemUtils;
import com.lewis.utils.T;


public class SplashActivity extends BaseActivity {
    private TextView tv_info;
    private Handler handler = new Handler();
    private static final int sleepTime = 2000;
    private ImageView imv_splash;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_splash;
    }


    @Override
    protected void initView(Bundle savedInstanceState) {
        CheckPermission();
        DemoHelper.getInstance().initHandler(this.getMainLooper());
        tv_info = (TextView) findViewById(R.id.tv_info);
        imv_splash = (ImageView) findViewById(R.id.imv_splash);
        ImageLoader.loadSplashImage(this, Constants.APP_PATH_ROOT + "/splash.jpg", imv_splash);
        tv_info.setText("版本:" + SystemUtils.getAppVersionName(this) + (MainApplication.getInstance().getResources().getInteger(R.integer.HTTP_CONFIG) == 2 ? "测试服务器" : ""));
        new Thread(new Runnable() {
            public void run() {
                if (DemoHelper.getInstance().isLoggedIn()) {
                    // auto login mode, make sure all group and conversation is loaed before enter the main screen
                    long start = System.currentTimeMillis();
                    EMClient.getInstance().chatManager().loadAllConversations();
                    EMClient.getInstance().groupManager().loadAllGroups();
                    long costTime = System.currentTimeMillis() - start;
                    //wait
                    if (sleepTime - costTime > 0) {
                        try {
                            Thread.sleep(sleepTime - costTime);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    String topActivityName = EasyUtils.getTopActivityName(EMClient.getInstance().getContext());
                    if (topActivityName != null && (topActivityName.equals(VideoCallActivity.class.getName()) || topActivityName.equals(VoiceCallActivity.class.getName()))) {
                        // nop
                        // avoid main screen overlap Calling Activity
                    } else {
                        //enter main screen
                        startActivity(new Intent(SplashActivity.this, MainActivity.class));
                    }
                    finish();
                } else {
                    try {
                        Thread.sleep(sleepTime);
                    } catch (InterruptedException e) {
                    }
                    startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                    finish();
                }
            }
        }).start();


    }

    private void CheckPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && android.os.Build.VERSION.RELEASE.equals("6.0.0")) {
            if (!Settings.System.canWrite(this)) {
                T.showLong(this, "请在该设置页面勾选");
                Uri selfPackageUri = Uri.parse("package:"
                        + getPackageName());
                Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS,
                        selfPackageUri);
                startActivity(intent);
            }
        }
    }

    private void openMainActivity() {
        if (UserManager.isLogin()) {
            NetBaseRequest getUserRequest = RequsetFactory.creatNoUidRequest(Constants.GET_USER_INFO);
            getUserRequest.add("uid", UserManager.getId());
            CallServer.getRequestInstance().addWithLogin(this, 0x01, getUserRequest, new HttpListenerCallback() {
                @Override
                public void onSucceed(int what, NetBaseBean netBaseBean) {
                    if (netBaseBean.getStatus() == 502) {
                        UserManager.logout();
                    } else {
                        EMClient.getInstance().groupManager().loadAllGroups();
                        EMClient.getInstance().chatManager().loadAllConversations();
                    }
                    openMainActivityWait(1000);
                }

                @Override
                public void onFailed(int what, String url, Object tag, Exception exception, int responseCode, long networkMillis) {
                    openMainActivityWait(800);
                }
            }, true, false);
        } else {
            openMainActivityWait(1500);
            EMClient.getInstance().groupManager().loadAllGroups();
            EMClient.getInstance().chatManager().loadAllConversations();
        }

    }

    private void openMainActivityWait(int millis) {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
            }
        }, millis);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
            }
        }, millis + 1000);
    }
}
