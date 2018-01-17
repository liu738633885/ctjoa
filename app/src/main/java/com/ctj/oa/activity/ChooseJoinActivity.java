package com.ctj.oa.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RadioButton;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.ctj.oa.Constants;
import com.ctj.oa.R;
import com.ctj.oa.model.UserInfo;
import com.ctj.oa.model.event.EventRefresh;
import com.ctj.oa.model.netmodel.NetBaseBean;
import com.ctj.oa.model.work.company.Company;
import com.ctj.oa.model.work.company.CompanyList;
import com.ctj.oa.net.CallServer;
import com.ctj.oa.net.HttpListenerCallback;
import com.ctj.oa.net.NetBaseRequest;
import com.ctj.oa.net.RequsetFactory;
import com.ctj.oa.utils.manager.UserManager;
import com.ctj.oa.widgets.TitleBar;
import com.lewis.utils.T;
import com.orhanobut.logger.Logger;

import org.greenrobot.eventbus.EventBus;

public class ChooseJoinActivity extends BaseActivity {
    private TitleBar titleBar;
    private BaseQuickAdapter<Company, BaseViewHolder> adapter;
    private int company_id = -1;
    private RecyclerView rv;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_choose_join;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        titleBar = findViewById(R.id.titleBar);
        titleBar.getRightGroup().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Logger.d("选中的 id：" + company_id);
                if (company_id == -1) {
                    T.showShort(bContext, "请选择一家公司");
                } else {
                    selected_company();
                }
            }
        });
        initAdapter();
        rv = findViewById(R.id.rv);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(adapter);
        get_my_company_list();
    }


    private void initAdapter() {
        adapter = new BaseQuickAdapter<Company, BaseViewHolder>(R.layout.item_company) {
            @Override
            protected void convert(BaseViewHolder helper, final Company item) {
                final RadioButton cb = helper.getView(R.id.cb);
                if (item.getCompany_id()==company_id) {
                    cb.setChecked(true);
                }else {
                    cb.setChecked(false);
                }
                helper.setText(R.id.tv, item.getCompany_name());
                helper.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        cb.setChecked(true);
                        company_id = item.getCompany_id();
                        adapter.notifyDataSetChanged();
                    }
                });
            }
        };
    }

    private void get_my_company_list() {
        NetBaseRequest request = RequsetFactory.creatBaseRequest(Constants.GET_MY_COMPANY_LIST);
        CallServer.getRequestInstance().add(bContext, 0x01, request, new HttpListenerCallback() {
            @Override
            public void onSucceed(int what, NetBaseBean netBaseBean) {
                if (netBaseBean.isSuccess()) {
                    CompanyList companyList = netBaseBean.parseObject(CompanyList.class);
                    if (companyList.company_list.size() > 0) {
                        for (Company c:companyList.company_list){
                            if(c.selected==1){
                                company_id=c.getCompany_id();
                                break;
                            }
                        }
                        adapter.setNewData(companyList.company_list);
                    }
                }
            }

            @Override
            public void onFailed(int what, String url, Object tag, Exception exception, int responseCode, long networkMillis) {

            }
        }, true, true);
    }

    private void selected_company() {
        NetBaseRequest request = RequsetFactory.creatBaseRequest(Constants.SELECTED_COMPANY);
        request.remove("company_id");
        request.add("company_id", company_id);
        CallServer.getRequestInstance().add(bContext, 0x01, request, new HttpListenerCallback() {
            @Override
            public void onSucceed(int what, NetBaseBean netBaseBean) {
                if (netBaseBean.isSuccess()) {
                    //选择成功请求详情接口
                    getUserInfo();
                }
            }

            @Override
            public void onFailed(int what, String url, Object tag, Exception exception, int responseCode, long networkMillis) {

            }
        }, true, true, "");

    }

    private void getUserInfo() {
        NetBaseRequest request = RequsetFactory.creatBaseRequest(Constants.GET_USER_INFO);
        request.add("get_user_id", UserManager.getId());
        CallServer.getRequestInstance().add(this, 0x02, request, new HttpListenerCallback() {
            @Override
            public void onSucceed(int what, NetBaseBean netBaseBean) {
                if (netBaseBean.isSuccess()) {
                    UserInfo userInfo = netBaseBean.parseObject(UserInfo.class);
                    boolean isSave = UserManager.saveUserInfo(userInfo);
                    if (!isSave) {
                        UserManager.saveCompanyId(company_id);
                    }
                    Intent intent = new Intent(bContext, MainActivity.class);
                    startActivity(intent);
                    EventBus.getDefault().post(new EventRefresh(LoginChooseActivity.class.getSimpleName()));
                    finish();
                }
            }

            @Override
            public void onFailed(int what, String url, Object tag, Exception exception, int responseCode, long networkMillis) {
            }
        }, true, true);
    }

}