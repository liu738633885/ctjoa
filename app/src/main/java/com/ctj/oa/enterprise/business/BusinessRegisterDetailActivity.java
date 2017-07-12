package com.ctj.oa.enterprise.business;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.ctj.oa.Constants;
import com.ctj.oa.R;
import com.ctj.oa.activity.BaseActivity;
import com.ctj.oa.model.netmodel.NetBaseBean;
import com.ctj.oa.model.work.company.Company;
import com.ctj.oa.net.CallServer;
import com.ctj.oa.net.HttpListenerCallback;
import com.ctj.oa.net.NetBaseRequest;
import com.ctj.oa.net.RequsetFactory;
import com.ctj.oa.utils.imageloader.ImageLoader;
import com.hyphenate.chatuidemo.ui.ChatActivity;

public class BusinessRegisterDetailActivity extends BaseActivity {

    private int id;
    private ImageView imv;
    private TextView tv1, tv2, tv3, tv4, tv5, tv6, tv7;
    private Button btn1, btn2;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_business_register_detail;
    }

    public static void goTo(Context context, int businessId) {
        Intent intent = new Intent(context, BusinessRegisterDetailActivity.class);
        intent.putExtra("id", businessId);
        context.startActivity(intent);
    }

    //获取Intent
    protected void handleIntent(Intent intent) {
        id = intent.getIntExtra("id", 0);
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        imv = (ImageView) findViewById(R.id.imv);
        tv1 = (TextView) findViewById(R.id.tv1);
        tv2 = (TextView) findViewById(R.id.tv2);
        tv3 = (TextView) findViewById(R.id.tv3);
        tv4 = (TextView) findViewById(R.id.tv4);
        tv5 = (TextView) findViewById(R.id.tv5);
        tv6 = (TextView) findViewById(R.id.tv6);
        tv7 = (TextView) findViewById(R.id.tv7);
        btn1 = (Button) findViewById(R.id.btn1);
        btn2 = (Button) findViewById(R.id.btn2);
        get_company_detail();
    }

    private void get_company_detail() {
        NetBaseRequest request = RequsetFactory.creatBaseRequest(Constants.GET_COMPANY_DETAIL);
        request.add("company_id", id);
        CallServer.getRequestInstance().add(this, 0x01, request, new HttpListenerCallback() {
            @Override
            public void onSucceed(int what, NetBaseBean netBaseBean) {
                if (netBaseBean.isSuccess()) {
                    final Company company = netBaseBean.parseObject(Company.class);
                    try {
                        ImageLoader.loadHeadImage(bContext, company.getCompany_logo(), imv, 2);
                        tv1.setText(company.getCompany_name());
                        tv2.setText("企业动态  " + company.getCompany_dynamic_num());
                        tv3.setText("关注者  " + company.getFollow_num());
                        tv4.setText(company.getCompany_contact_name());
                        tv5.setText(company.getCompany_contact_phone());
                        tv6.setText(company.getCompany_url());
                        tv7.setText(company.getCompany_service());
                        if (company.getIs_follow() == 1) {
                            btn1.setText("取消关注");
                        } else {
                            btn1.setText("关注");
                        }
                        btn1.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                follow();
                            }
                        });
                        btn2.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                startActivity(new Intent(bContext, ChatActivity.class).putExtra("userId", company.getUser_id() + ""));
                            }
                        });

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailed(int what, String url, Object tag, Exception exception, int responseCode, long networkMillis) {

            }
        }, true, true, "");
    }


    public void follow() {
        NetBaseRequest request = RequsetFactory.creatBaseRequest(Constants.COMPANY_FOLLOW);
        request.add("company_id", id);
        CallServer.getRequestInstance().add(bContext, 0x03, request, new HttpListenerCallback() {
            @Override
            public void onSucceed(int what, NetBaseBean netBaseBean) {
                if (netBaseBean.isSuccess()) {
                    get_company_detail();
                }
            }

            @Override
            public void onFailed(int what, String url, Object tag, Exception exception, int responseCode, long networkMillis) {

            }
        }, true, true, "");
    }
}
