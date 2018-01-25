package com.ctj.oa.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.ctj.oa.Constants;
import com.ctj.oa.R;
import com.ctj.oa.model.UserCompany;
import com.ctj.oa.model.event.EventRefresh;
import com.ctj.oa.model.netmodel.NetBaseBean;
import com.ctj.oa.net.CallServer;
import com.ctj.oa.net.HttpListenerCallback;
import com.ctj.oa.net.NetBaseRequest;
import com.ctj.oa.net.RequsetFactory;
import com.ctj.oa.utils.manager.UserManager;

import org.greenrobot.eventbus.Subscribe;

public class LoginChooseActivity extends BaseActivity {
    private TextView tv_create, tv_choose,tv_search;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_login_choose;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        tv_create = findViewById(R.id.tv_create);
        tv_search = findViewById(R.id.tv_search);
        tv_choose = findViewById(R.id.tv_choose);
        tv_create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WebViewActivity.goTo(bContext, Constants.WEB_CREATE_COMPANY + "?user_id=" + UserManager.getId() + "&token=" + UserManager.getToken(), "创建公司");
            }
        });
        tv_choose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(bContext, ChooseJoinActivity.class));
            }
        });
        tv_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(bContext, SearchCompanyActivity.class));
            }
        });
        my_create_company_exists();
    }

    private void my_create_company_exists() {
        NetBaseRequest request = RequsetFactory.creatBaseRequest(Constants.MY_CREATE_COMPANY_EXISTS);
        CallServer.getRequestInstance().add(this, 0x01, request, new HttpListenerCallback() {
            @Override
            public void onSucceed(int what, NetBaseBean netBaseBean) {
                if (netBaseBean.isSuccess()) {
                    UserCompany userCompany = netBaseBean.parseObject(UserCompany.class);
                    if (userCompany.exists == 0) {
                        tv_create.setVisibility(View.VISIBLE);
                    } else {
                        tv_create.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onFailed(int what, String url, Object tag, Exception exception, int responseCode, long networkMillis) {

            }
        }, true, true);
    }

    @Subscribe
    public void onEventMainThread(EventRefresh e) {
        if (!TextUtils.isEmpty(e.getAction()) && e.getAction().equals(LoginChooseActivity.class.getSimpleName())) {
            finish();
        }
    }
}
