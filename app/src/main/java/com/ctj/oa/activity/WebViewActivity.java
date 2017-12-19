package com.ctj.oa.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.ctj.oa.R;
import com.ctj.oa.utils.BitmapUtils;
import com.ctj.oa.utils.ShareUtils;
import com.ctj.oa.utils.UpLoadManager;
import com.ctj.oa.widgets.TitleBar;
import com.lewis.widgets.LewisSwipeRefreshLayout;
import com.orhanobut.logger.Logger;
import com.soundcloud.android.crop.Crop;

import java.io.File;
import java.util.ArrayList;

import me.iwf.photopicker.PhotoPicker;

public class WebViewActivity extends BaseActivity implements View.OnClickListener {
    private TitleBar titleBar;
    private LewisSwipeRefreshLayout swl;
    private WebView webView;
    private String TAG = WebViewActivity.class.getSimpleName();
    private String title, url;
    private boolean share;
    private Handler mHandler = new Handler();
    public static final String HIDE_TITLE = "hidetitle";

    //招聘
    private boolean showLoading = true;



    @Override
    protected int getContentViewId() {
        return R.layout.activity_web;
    }


    public static void goTo(Context context, String url, String title) {
        Intent intent = new Intent(context, WebViewActivity.class);
        intent.putExtra("title", title);
        intent.putExtra("url", url);
        context.startActivity(intent);
    }

    public static void goToShare(Context context, String url, String title) {
        Intent intent = new Intent(context, WebViewActivity.class);
        intent.putExtra("title", title);
        intent.putExtra("url", url);
        intent.putExtra("share", true);
        context.startActivity(intent);
    }

    //获取Intent
    protected void handleIntent(Intent intent) {
        title = intent.getStringExtra("title");
        url = intent.getStringExtra("url");
        share = intent.getBooleanExtra("share", false);
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        titleBar = findViewById(R.id.titleBar);
        titleBar.setLeftClike(new TitleBar.LeftClike() {
            @Override
            public void onClick(View view) {
                back();
            }
        });
        if (!TextUtils.isEmpty(title)) {
            if (title.equals(HIDE_TITLE)) {
                titleBar.setVisibility(View.GONE);
            } else {
                titleBar.setCenterText(title);
            }

        }
        if (share) {
            titleBar.setRight_text("分享");
            titleBar.getRightGroup().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ShareUtils.ShareWX(WebViewActivity.this, url, "仝君协仝", "仝君协仝-互联网工作平台，轻松实现自由工作！邀您使用：https://m.jingoal.com ", "", ShareUtils.SHARE_TO_SESSION);
                }
            });
        }
        webView = findViewById(R.id.webView);
        swl = findViewById(R.id.swl);
        swl.setEnabled(false);
        swl.setOnRefreshListener(new LewisSwipeRefreshLayout.OnRefreshListener() {
            public void onRefresh() {
                webView.reload();
            }
        });
        setWebView();
        //url = "http://tapi.wiseexpo.com/jobs.Recruit/index.html?token=123123131&user_id=77";
        if (url.contains("hidetitle")) {
            titleBar.setVisibility(View.GONE);
        }
        webView.loadUrl(url);
        Logger.d(url);
    }

    /**
     * 左上角返回键的点击事件
     */
    public void back() {
        //finish();
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            finish();
        }
    }


    /**
     * 设置webview
     */
    @SuppressLint({"NewApi", "SetJavaScriptEnabled"})
    private void setWebView() {
        WebSettings webSetting = webView.getSettings();
        // 告诉WebView先不要自动加载图片，等页面finish后再发起图片加载。
        if (Build.VERSION.SDK_INT >= 19) {
            webView.getSettings().setLoadsImagesAutomatically(true);
        } else {
            webView.getSettings().setLoadsImagesAutomatically(false);
        }
        // MyWebChromeClient webChromeClient = new MyWebChromeClient(webView,
        // MainActivity.this);
        webView.setWebChromeClient(defaultWebChromeClient);// 显示进度条等浏览器选项
        MyWebViewClient webViewClient = new MyWebViewClient();
        webView.setWebViewClient(webViewClient);// 设置在当前webview里面跳转
        webView.setLayerType(View.LAYER_TYPE_NONE, null);
        webSetting.setUseWideViewPort(true);// 可任意比例缩放
        webSetting.setJavaScriptEnabled(true);// 支持js
        webSetting.setJavaScriptCanOpenWindowsAutomatically(true);
        webSetting.setSupportZoom(false); // 支持缩放
        webSetting.setAllowFileAccess(true); // 设置可以访问文件
        webSetting.setLoadsImagesAutomatically(true); // 支持自动加载图片
        //webSetting.setAppCacheEnabled(false);
        webSetting.setDomStorageEnabled(true);// 开启 DOM storage 功能
        webSetting.setCacheMode(webSetting.LOAD_DEFAULT);// 设置缓存
        webView.setSaveEnabled(false);

        //https 问题
        webSetting.setJavaScriptEnabled(true);
        webSetting.setDomStorageEnabled(true);
        webSetting.setAllowFileAccess(true);
        webSetting.setAppCacheEnabled(true);
        webView.addJavascriptInterface(new MyJavaScriptInterface(), "xtoa");
       /* try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                webSetting.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }*/


    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            back();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    protected void onDestroy() {
        Log.i(TAG, "onDestroy");
        if (webView != null) {// 移除webview
            swl.removeView(webView);
            webView.removeAllViews();
            webView.destroy();
        }
        super.onDestroy();
    }

    private final WebChromeClient defaultWebChromeClient = new WebChromeClient() {
        public final static String W_TAG = "WebChromeClient";

        @Override
        public void onReceivedTitle(WebView view, String title) {
            super.onReceivedTitle(view, title);
            Log.i(W_TAG, "title = " + title);
            titleBar.setCenterText(title);
        }

        public void onProgressChanged(WebView view, int newProgress) {
            if (newProgress == 0 && showLoading) {
                swl.setRefreshing(true);
                Log.i(W_TAG, "onProgressChanged mypDialog.show() newProgress = " + newProgress);
            }
            if (newProgress == 100) {
                swl.setRefreshing(false);
                showLoading = true;
            }
            Log.i(W_TAG, "onProgressChanged newProgress = " + newProgress);
        }


    };

    public class MyWebViewClient extends WebViewClient {
        private final static String TAG = "MyWebViewClient";

        public MyWebViewClient() {
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (url.contains("tel:")) {
                Uri uri = Uri.parse(url);
                Intent intent = new Intent(Intent.ACTION_DIAL, uri);
                view.getContext().startActivity(intent);
                return true;
            } else {
                view.loadUrl(url); // 在当前的webview中跳转到新的url
                if (url.contains("hidetitle")) {
                    titleBar.setVisibility(View.GONE);
                } else {
                    titleBar.setVisibility(View.VISIBLE);
                }
                Log.i(TAG, "loadUrl = " + url);
                //swl.setEnabled(!url.startsWith("http://api.idocv.com"));
                return true;
            }
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            // finish之后加载图片
            if (!view.getSettings().getLoadsImagesAutomatically()) {
                view.getSettings().setLoadsImagesAutomatically(true);
            }
            swl.setRefreshing(false);
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            swl.setRefreshing(true);
        }

    }

    final class MyJavaScriptInterface {

        MyJavaScriptInterface() {
        }

        /**
         * This is not called on the UI thread. Post a runnable to invoke
         * loadUrl on the UI thread.
         */
        @JavascriptInterface
        public void uploadLogo() {
            mHandler.post(new Runnable() {
                public void run() {
                    //mWebView.loadUrl("javascript:wave()");
                    //Toast("order_no:");
                    //String url = "http://api.rockbrain.net/uploads/img/593761400d33020170607101320.jpg";
                    //webView.loadUrl("javascript:callUpload('" + url + "')");
                    //webView.loadUrl("javascript:alert(123)");
                    PhotoPicker.builder()
                            .setPhotoCount(1)
                            .setShowCamera(true)
                            .setShowGif(false)
                            .setPreviewEnabled(true)
                            .start(WebViewActivity.this, PhotoPicker.REQUEST_CODE);
                }
            });
        }
        @JavascriptInterface
        public void load(final String url) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    webView.loadUrl(url);
                }
            });
        }

        @JavascriptInterface
        public void back() {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    webView.goBack();
                }
            });
        }

        @JavascriptInterface
        public void loadNewTab(final String url) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    WebViewActivity.goTo(WebViewActivity.this, url, "");
                }
            });
        }

        @JavascriptInterface
        public void close() {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    finish();
                }
            });
        }

        @JavascriptInterface
        public void reload() {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    webView.reload();
                }
            });

        }

        @JavascriptInterface
        public void reload(int mode) {
            if (mode == 1) {
                showLoading = false;
            }
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    webView.reload();
                }
            });

        }

        @JavascriptInterface
        public void hideTitle(boolean hide) {
            if (hide) {
                titleBar.setVisibility(View.INVISIBLE);
            } else {
                titleBar.setVisibility(View.VISIBLE);
            }
        }


       /* @JavascriptInterface
        public void loadNewTab(String url, boolean finish) {
            WebViewActivity.goTo(WebViewActivity.this, url, "");
            if (finish) {
                finish();
            }
        }*/

        @JavascriptInterface
        public void showRecruitTitle(boolean show) {
            if (show) {

            } else {

            }
        }

    }
    @Override
    public void onClick(View view) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }
        if (requestCode == PhotoPicker.REQUEST_CODE) {
            if (data != null) {
                ArrayList<String> photos =
                        data.getStringArrayListExtra(PhotoPicker.KEY_SELECTED_PHOTOS);
                Uri sourceUri = Uri.fromFile(new File(photos.get(0)));
                beginCrop(sourceUri);

            }
        }
        if (requestCode == Crop.REQUEST_CROP) {
            UpLoadManager.uploadImg(this, Crop.getOutput(data).getPath(), new UpLoadManager.SingleUpLoadListener() {
                @Override
                public void onComplete(boolean isSuccess, String path_message) {
                    if (isSuccess) {
                        //String url = "http://api.rockbrain.net/uploads/img/5a26aa6fb44fe20171205221719.jpg";
                        webView.loadUrl("javascript:callUpload('" + path_message + "')");
                    }
                }
            }, 500, 500);
        }
    }

    private void beginCrop(Uri source) {
        Uri destination = Uri.fromFile(new File(BitmapUtils.getSaveRealPath(), System.currentTimeMillis() + "cropped.jpg"));
        Crop.of(source, destination).asSquare().start(this);
    }
}
