package com.lewis.myoa;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.orhanobut.logger.Logger;
import com.upyun.library.listener.UpCompleteListener;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    private LinePathView lineView;
    private ImageView imv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        lineView = (LinePathView) findViewById(R.id.lineView);
        imv = (ImageView) findViewById(R.id.imv);
    }

    public void save(View view) {
        if (lineView.getTouched()) {
            try {
                lineView.save("/sdcard/myoa.png", true, 10);
                UpLoadManager.uploadImg("/sdcard/myoa.png", new UpCompleteListener() {
                    @Override
                    public void onComplete(boolean isSuccess, String result) {
                        UpYun upYun = JSON.parseObject(result, UpYun.class);
                        Logger.e("000===" + upYun.getUrl());
                        //Logger.e("000===" + result);

                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(this, "您没有签名~", Toast.LENGTH_SHORT).show();
        }
    }
}
