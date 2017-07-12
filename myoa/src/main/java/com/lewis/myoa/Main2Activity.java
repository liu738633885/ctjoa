package com.lewis.myoa;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class Main2Activity extends AppCompatActivity {
    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        webView = (WebView) findViewById(R.id.webView);
        webView.setWebViewClient(new MyWebViewClient());// 设置在当前webview里面跳转
        webView.loadUrl("http://192.168.2.222/generic/web/viewer.html");
        WebSettings webSetting = webView.getSettings();
        webSetting.setJavaScriptEnabled(true);// 支持js
        webSetting.setJavaScriptCanOpenWindowsAutomatically(true);
    }

    public class MyWebViewClient extends WebViewClient {
        private final static String TAG = "MyWebViewClient";

        public MyWebViewClient() {
        }

        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (url.contains("tel:")) {
                Uri uri = Uri.parse(url);
                Intent intent = new Intent(Intent.ACTION_DIAL, uri);
                view.getContext().startActivity(intent);
                return true;
            } else {
                view.loadUrl(url); // 在当前的webview中跳转到新的url
                Log.i(TAG, "loadUrl = " + url);
                if (url.equals("http://m.ainana.com/")) {
                    finish();
                }
                return true;
            }
        }

        public void onPageFinished(WebView view, String url) {
            // finish之后加载图片
            if (!view.getSettings().getLoadsImagesAutomatically()) {
                view.getSettings().setLoadsImagesAutomatically(true);
            }

        }

    }
}
