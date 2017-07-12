package com.ctj.oa.seal;

import android.os.Bundle;

import com.ctj.oa.R;
import com.ctj.oa.activity.BaseActivity;

public class SealActivity extends BaseActivity {
    /*private PDFView pdfView;
    private TitleBar titleBar;
    private int nowPageNum;
    private Button btn;
    private BottomDialog bottomDialog;
    private LinePathView lineView;
    private LoadingDialog loadingDialog;
*/
    @Override
    protected int getContentViewId() {
        return R.layout.activity_seal;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
       /* pdfView = (PDFView) findViewById(R.id.pdfView);
        titleBar = (TitleBar) findViewById(R.id.titleBar);
        btn = (Button) findViewById(R.id.btn);
        bottomDialog = new BottomDialog(this);
        loadingDialog = new LoadingDialog(this);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLinePathView();
            }
        });
        getPdf();*/
    }

   /* private void getPdf() {
        DownloadRequest mDownloadRequest = NoHttp.createDownloadRequest("http://192.168.1.103/Reader2.pdf", Environment.getExternalStorageDirectory() + "", "Reader3.pdf", true, true);
        CallServer.getDownloadInstance().add(0x01, mDownloadRequest, new DownloadListener() {
            @Override
            public void onDownloadError(int what, Exception exception) {

            }

            @Override
            public void onStart(int what, boolean isResume, long rangeSize, Headers responseHeaders, long allCount) {
                titleBar.getRight_tv().setText("0/100");
                loadingDialog.show();
            }

            @Override
            public void onProgress(int what, int progress, long fileCount) {
                titleBar.getRight_tv().setText(progress + "/100");
            }

            @Override
            public void onFinish(int what, String filePath) {
                loadingDialog.dismiss();
                bottomDialog.dismiss();
                titleBar.getRight_tv().setText("1");
                pdfView.fromFile(new File(filePath))
                        .defaultPage(nowPageNum)
                        .onPageChange(new OnPageChangeListener() {
                            @Override
                            public void onPageChanged(int page, int pageCount) {
                                nowPageNum = page;
                                titleBar.getRight_tv().setText(nowPageNum + 1 + "/" + pageCount);
                            }
                        })
                        .enableAnnotationRendering(true)
                        .load();
            }

            @Override
            public void onCancel(int what) {

            }
        });
    }

    private void showLinePathView() {
        bottomDialog.setContentView(R.layout.dialog_line_path);
        lineView = (LinePathView) bottomDialog.findViewById(lineView);
        bottomDialog.findViewById(R.id.save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    lineView.save("/sdcard/myoa.png", true, 10);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                UpLoadManager.uploadImg("/sdcard/myoa.png", new UpCompleteListener() {
                    @Override
                    public void onComplete(boolean isSuccess, String result) {
                        ImageBean upYun = JSON.parseObject(result, ImageBean.class);
                        Logger.e("http://ainana.b0.upaiyun.com" + upYun.getUrl());
                        getPdf();
                    }
                });
            }
        });
        bottomDialog.show();

    }
*/
}
