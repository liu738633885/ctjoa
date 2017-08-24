package com.ctj.oa.mine;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.cache.DiskCache;
import com.ctj.oa.Constants;
import com.ctj.oa.R;
import com.ctj.oa.activity.BaseActivity;
import com.ctj.oa.activity.LoginActivity;
import com.ctj.oa.activity.WebViewActivity;
import com.ctj.oa.utils.BitmapUtils;
import com.ctj.oa.utils.SPUtils;
import com.ctj.oa.utils.manager.SettingsManager;
import com.hyphenate.EMCallBack;
import com.hyphenate.chatuidemo.DemoHelper;
import com.lewis.utils.FileUtil;

import java.io.File;

/**
 * Created by lewis on 2017/4/25.
 */

public class SettingActivity extends BaseActivity implements View.OnClickListener {
    private LinearLayout ll_edit_password, ll_clean, ll_reset, ll_about;
    private TextView tvCacheSize;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    showCacheSize();
                    Toast("清除缓存成功");

            }
            super.handleMessage(msg);
        }
    };

    @Override
    protected int getContentViewId() {
        return R.layout.activity_setting;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        ll_edit_password = (LinearLayout) findViewById(R.id.ll_edit_password);
        ll_clean = (LinearLayout) findViewById(R.id.ll_clean);
        ll_reset = (LinearLayout) findViewById(R.id.ll_reset);
        ll_about = (LinearLayout) findViewById(R.id.ll_about);
        tvCacheSize = (TextView) findViewById(R.id.tvCacheSize);
        ll_edit_password.setOnClickListener(this);
        ll_clean.setOnClickListener(this);
        ll_reset.setOnClickListener(this);
        ll_about.setOnClickListener(this);
        showCacheSize();
    }

    private void showCacheSize() {
        double size = FileUtil.getFolderSize(new File(getCacheDir(), BitmapUtils.SAVE_IMG));
        size += FileUtil.getFolderSize(new File(getCacheDir(), DiskCache.Factory.DEFAULT_DISK_CACHE_DIR));
        tvCacheSize.setText(FileUtil.getFormatSize(size));
    }


    public void logout(View view) {
        final ProgressDialog pd = new ProgressDialog(this);
        String st = getResources().getString(R.string.Are_logged_out);
        pd.setMessage(st);
        pd.setCanceledOnTouchOutside(false);
        pd.show();
        DemoHelper.getInstance().logout(false, new EMCallBack() {

            @Override
            public void onSuccess() {
                runOnUiThread(new Runnable() {
                    public void run() {
                        pd.dismiss();
                        // show login screen
                        finish();
                        startActivity(new Intent(SettingActivity.this, LoginActivity.class));
                    }
                });
            }

            @Override
            public void onProgress(int progress, String status) {

            }

            @Override
            public void onError(int code, String message) {
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        pd.dismiss();
                        Toast.makeText(SettingActivity.this, "unbind devicetokens failed", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_edit_password:
                startActivity(new Intent(this, EditPasswordActivity.class));
                break;
            case R.id.ll_clean:
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Glide.get(getApplicationContext()).clearDiskCache();
                        FileUtil.deleteFolderFile(getCacheDir() + BitmapUtils.SAVE_IMG, false);
                        handler.sendEmptyMessage(1);
                    }
                }).start();
                SPUtils.getHuanxinUserInstance().clear();

                break;
            case R.id.ll_reset:
                SettingsManager.clean();
                Toast("重置设置成功");
                break;
            case R.id.ll_about:
                WebViewActivity.goTo(this, Constants.WEB_ABOUT, "关于");
                break;
        }
    }
}
