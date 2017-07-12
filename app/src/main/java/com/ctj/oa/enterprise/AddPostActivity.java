package com.ctj.oa.enterprise;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.ctj.oa.Constants;
import com.ctj.oa.R;
import com.ctj.oa.activity.BaseActivity;
import com.ctj.oa.activity.ImagePagerActivity;
import com.ctj.oa.model.netmodel.NetBaseBean;
import com.ctj.oa.model.work.CircleClass;
import com.ctj.oa.net.CallServer;
import com.ctj.oa.net.HttpListenerCallback;
import com.ctj.oa.net.NetBaseRequest;
import com.ctj.oa.net.RequsetFactory;
import com.ctj.oa.utils.UpLoadManager;
import com.ctj.oa.widgets.MultiImageView2;
import com.ctj.oa.widgets.TitleBar;

import java.util.ArrayList;
import java.util.List;

import me.iwf.photopicker.PhotoPicker;

public class AddPostActivity extends BaseActivity {
    private MultiImageView2 multiImageView;
    private ArrayList<String> imgList = new ArrayList<String>();
    private Spinner spinner;
    private EditText edt1, edt2;
    private List<CircleClass> list_class = new ArrayList<>();
    private int cid;
    private TitleBar titleBar;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_add_post;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        titleBar = (TitleBar) findViewById(R.id.titleBar);
        titleBar.getRightGroup().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                add_article();
            }
        });
        spinner = (Spinner) findViewById(R.id.spinner);
        edt1 = (EditText) findViewById(R.id.edt1);
        edt2 = (EditText) findViewById(R.id.edt2);
        multiImageView = (MultiImageView2) findViewById(R.id.multiImagView);
        multiImageView.setOnItemClickListener(new MultiImageView2.OnItemClickListener() {
            @Override
            public void onItemClick(View view, final int position) {
                ImagePagerActivity.ImageSize imageSize = new ImagePagerActivity.ImageSize(view.getMeasuredWidth(), view.getMeasuredHeight());
                ImagePagerActivity.startImagePagerActivity(AddPostActivity.this, imgList, position, imageSize);
            }

            @Override
            public void onLastItemClick(View view, int position) {
                if (imgList.size() > 9) {
                    Toast.makeText(AddPostActivity.this, "最多9张", Toast.LENGTH_SHORT).show();
                    return;
                }
                PhotoPicker.builder()
                        .setPhotoCount(9 - imgList.size())
                        .setShowCamera(true)
                        .setShowGif(false)
                        .setPreviewEnabled(true)
                        .start(AddPostActivity.this, PhotoPicker.REQUEST_CODE);


            }

            @Override
            public void onDeleteItemClick(View view, final int position) {
                new AlertDialog.Builder(AddPostActivity.this).setTitle("删除这张?").setPositiveButton("是", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        imgList.remove(position);
                        multiImageView.setList(imgList);
                    }
                }).setNegativeButton("否", null).show();
            }
        });
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                multiImageView.setList(new ArrayList<String>());
            }
        }, 300);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                try {
                    cid = list_class.get(position).getClass_id();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        getCircleClassList();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == PhotoPicker.REQUEST_CODE) {
            if (data != null) {
                ArrayList<String> photos =
                        data.getStringArrayListExtra(PhotoPicker.KEY_SELECTED_PHOTOS);

                UpLoadManager.uploadImgs(this, photos, new UpLoadManager.UpLoadListener() {
                    @Override
                    public void Success(List<String> urls) {
                        imgList.addAll(urls);
                        multiImageView.setList(imgList);
                    }

                    @Override
                    public void Failed(int whereFailed) {
                        Toast("第" + (whereFailed + 1) + "张上传失败");
                    }
                });
            }
        }
    }

    private void add_article() {
        NetBaseRequest request = RequsetFactory.creatBaseRequest(Constants.ADD_ARTICLE);
        if (cid == 0) {
            Toast("请选择板块");
            return;
        }
        request.add("class_id", cid);
        if (imgList != null && imgList.size() > 0) {
            String str = "";
            for (String s : imgList) {
                str = str + s + ",";
            }
            if (str.endsWith(",")) {
                str = str.substring(0, str.length() - 1);
            }
            request.add("img_list", str);
        } else {
            Toast("最少要有一张图片");
            return;
        }
        if (TextUtils.isEmpty(edt1.getText().toString())) {
            Toast("请输入标题");
            return;
        }
        request.add("article_title", edt1.getText().toString());
        if (TextUtils.isEmpty(edt2.getText().toString())) {
            Toast("请输入内容");
            return;
        }
        request.add("article_content", edt2.getText().toString());
        CallServer.getRequestInstance().add(this, 0x01, request, new HttpListenerCallback() {
            @Override
            public void onSucceed(int what, NetBaseBean netBaseBean) {
                Toast(netBaseBean.getMessage());
                if (netBaseBean.isSuccess()) {
                    finish();
                }
            }

            @Override
            public void onFailed(int what, String url, Object tag, Exception exception, int responseCode, long networkMillis) {

            }
        }, true, true);
    }

    private void getCircleClassList() {
        NetBaseRequest request = RequsetFactory.creatBaseRequest(Constants.GET_CIRCLE_CLASS_LIST);
        CallServer.getRequestInstance().add(this, 0x01, request, new HttpListenerCallback() {
            @Override
            public void onSucceed(int what, NetBaseBean netBaseBean) {
                if (netBaseBean.isSuccess()) {
                    list_class = netBaseBean.parseList(CircleClass.class);
                    List<String> list = new ArrayList<String>();
                    for (CircleClass c : list_class) {
                        list.add(c.getClass_name());
                    }
                    spinner.setAdapter(new ArrayAdapter<String>(bContext, android.R.layout.simple_spinner_item, list));
                }
            }

            @Override
            public void onFailed(int what, String url, Object tag, Exception exception, int responseCode, long networkMillis) {

            }
        }, false, true, "");
    }

}
