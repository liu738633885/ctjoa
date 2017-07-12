package com.ctj.oa.enterprise;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.ctj.oa.Constants;
import com.ctj.oa.R;
import com.ctj.oa.activity.BaseActivity;
import com.ctj.oa.model.netmodel.NetBaseBean;
import com.ctj.oa.model.work.CircleClass;
import com.ctj.oa.model.work.company.Company;
import com.ctj.oa.net.CallServer;
import com.ctj.oa.net.HttpListenerCallback;
import com.ctj.oa.net.NetBaseRequest;
import com.ctj.oa.net.RequsetFactory;
import com.ctj.oa.utils.BitmapUtils;
import com.ctj.oa.utils.UpLoadManager;
import com.ctj.oa.utils.imageloader.ImageLoader;
import com.soundcloud.android.crop.Crop;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import me.iwf.photopicker.PhotoPicker;

public class JoinEntranceActivity extends BaseActivity {
    private Button btn;
    private ImageView imv;
    private String logo;
    private int cid;
    private EditText edt_username, edt_phone, edt_http, edt_service;
    private Spinner spinner;
    private List<CircleClass> list_class = new ArrayList<>();
    private ImageView imv1;
    private TextView tv1, tv2;
    private LinearLayout ll;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_entrance;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        btn = (Button) findViewById(R.id.btn);
        imv = (ImageView) findViewById(R.id.imv);
        imv1 = (ImageView) findViewById(R.id.imv1);
        tv1 = (TextView) findViewById(R.id.tv1);
        tv2 = (TextView) findViewById(R.id.tv2);
        ll = (LinearLayout) findViewById(R.id.ll);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                applyAddCircle();
            }
        });
        imv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PhotoPicker.builder()
                        .setPhotoCount(1)
                        .setShowCamera(true)
                        .setShowGif(false)
                        .setPreviewEnabled(true)
                        .start(JoinEntranceActivity.this, PhotoPicker.REQUEST_CODE);
            }
        });
        edt_username = (EditText) findViewById(R.id.edt_username);
        edt_phone = (EditText) findViewById(R.id.edt_phone);
        edt_http = (EditText) findViewById(R.id.edt_http);
        edt_service = (EditText) findViewById(R.id.edt_service);
        spinner = (Spinner) findViewById(R.id.spinner);
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
        getCircleInfo();
    }

    private void getCircleInfo() {
        NetBaseRequest request = RequsetFactory.creatBaseRequest(Constants.GET_CIRCLE_INFO);
        CallServer.getRequestInstance().add(this, 0x01, request, new HttpListenerCallback() {
            @Override
            public void onSucceed(int what, NetBaseBean netBaseBean) {
                if (netBaseBean.isSuccess()) {
                    Company company = (Company) netBaseBean.parseObject(Company.class);
                    switch (company.getCheck_state()) {
                        case 0:
                            ll.setVisibility(View.GONE);
                            btn.setEnabled(true);
                            break;
                        case 1:
                            ll.setVisibility(View.VISIBLE);
                            imv1.setImageResource(R.mipmap.check1);
                            tv1.setText("审核中");
                            tv1.setTextColor(0xff3686e8);
                            tv2.setText("你提交的" + company.getCompany_contact_name() + "外联申请" + "审核中");
                            btn.setEnabled(false);
                            break;
                        case 2:
                            ll.setVisibility(View.VISIBLE);
                            imv1.setImageResource(R.mipmap.check2);
                            tv1.setText("审核成功");
                            tv1.setTextColor(0xff23de27);
                            tv2.setText("你提交的" + company.getCompany_contact_name() + "外联申请" + "审核成功");
                            btn.setEnabled(false);
                            break;
                        case 3:
                            ll.setVisibility(View.VISIBLE);
                            imv1.setImageResource(R.mipmap.check3);
                            tv1.setText("未通过审核");
                            tv1.setTextColor(0xfff13539);
                            tv2.setText("你提交的" + company.getCompany_contact_name() + "外联申请" + "没有通过审核");
                            btn.setEnabled(false);
                            break;
                    }
                }
            }

            @Override
            public void onFailed(int what, String url, Object tag, Exception exception, int responseCode, long networkMillis) {

            }
        }, false, true, "");
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
                        ImageLoader.loadHeadImage(bContext, path_message, imv, -1);
                        logo = path_message;
                    }
                }
            }, 500, 500);
        }

    }

    private void beginCrop(Uri source) {
        Uri destination = Uri.fromFile(new File(BitmapUtils.getSaveRealPath(), System.currentTimeMillis() + "cropped.jpg"));
        Crop.of(source, destination).asSquare().start(this);
    }

    private void applyAddCircle() {
        NetBaseRequest request = RequsetFactory.creatBaseRequest(Constants.APPLY_ADD_CIRCLE);
        if (cid == 0) {
            Toast("请选择板块");
            return;
        }
        request.add("cid", cid);
        if (TextUtils.isEmpty(logo)) {
            Toast("请上传logo");
            return;
        }
        request.add("company_logo", logo);
        if (TextUtils.isEmpty(edt_username.getText().toString())) {
            Toast("请填写联系人");
            return;
        }
        request.add("company_contact_name", edt_username.getText().toString());
        if (TextUtils.isEmpty(edt_phone.getText().toString())) {
            Toast("请填写联系电话");
            return;
        }
        request.add("company_contact_phone", edt_phone.getText().toString());
        if (TextUtils.isEmpty(edt_http.getText().toString())) {
            Toast("请填写网址");
            return;
        }
        request.add("company_url", edt_http.getText().toString());
        if (TextUtils.isEmpty(edt_service.getText().toString())) {
            Toast("请填写产品服务");
            return;
        }
        request.add("company_service", edt_service.getText().toString());
        CallServer.getRequestInstance().add(this, 0x01, request, new HttpListenerCallback() {
            @Override
            public void onSucceed(int what, NetBaseBean netBaseBean) {
                Toast(netBaseBean.getMessage());
                if (netBaseBean.isSuccess()) {
                    getCircleInfo();
                }
            }

            @Override
            public void onFailed(int what, String url, Object tag, Exception exception, int responseCode, long networkMillis) {

            }
        }, true, true);
    }
}
