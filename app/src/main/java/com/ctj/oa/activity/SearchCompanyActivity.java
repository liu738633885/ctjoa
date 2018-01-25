package com.ctj.oa.activity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.ctj.oa.Constants;
import com.ctj.oa.R;
import com.ctj.oa.model.netmodel.NetBaseBean;
import com.ctj.oa.model.work.company.Company;
import com.ctj.oa.net.CallServer;
import com.ctj.oa.net.HttpListenerCallback;
import com.ctj.oa.net.NetBaseRequest;
import com.ctj.oa.net.RequsetFactory;
import com.lewis.utils.SearchBarUtils;
import com.lewis.widgets.LewisSwipeRefreshLayout;

import java.util.List;

public class SearchCompanyActivity extends BaseActivity implements LewisSwipeRefreshLayout.OnRefreshListener {
    private RecyclerView rv;
    private LewisSwipeRefreshLayout swl;
    private String keyword;
    private EditText editText;
    private TextView cancel;
    private BaseQuickAdapter<Company, BaseViewHolder> adapter;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_search_company;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {

        editText = SearchBarUtils.init(this, new SearchBarUtils.OnSearchListener() {
            @Override
            public void onSearch(String string) {
                keyword = string;
                onRefresh();
            }

            @Override
            public void cleanSearch() {
                keyword = "";
                onRefresh();
            }
        });
        cancel = findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        swl = findViewById(R.id.swl);
        swl.setOnRefreshListener(this);
        rv = findViewById(R.id.rv);
        rv.setLayoutManager(new LinearLayoutManager(this));
        initAdpter();
        rv.setAdapter(adapter);

    }

    @Override
    public void onRefresh() {
        select_join_company_list();
    }

    private void initAdpter() {
        adapter = new BaseQuickAdapter<Company, BaseViewHolder>(R.layout.item_search_company) {
            @Override
            protected void convert(BaseViewHolder helper, final Company item) {
                helper.setText(R.id.tv, item.getCompany_name());
                Button btn = helper.getView(R.id.btn);
                btn.setVisibility(View.GONE);
                if (item.may_join == 1) {
                    btn.setVisibility(View.VISIBLE);
                } else {
                    btn.setVisibility(View.GONE);
                }
                helper.setVisible(R.id.tv_status, item.join_status != 5);
                switch (item.join_status) {
                    case 1:
                        helper.setText(R.id.tv_status, "待审核");
                        break;
                    case 2:
                        helper.setText(R.id.tv_status, "审核通过");
                        break;
                    case 3:
                        helper.setText(R.id.tv_status, "审核未通过");
                        break;
                    case 4:
                        helper.setText(R.id.tv_status, "失效");
                        break;
                    case 5:
                        break;
                    default:
                        helper.setText(R.id.tv_status, "");
                }
                btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        joinCompany(item.getCompany_id());
                    }
                });
            }
        };
    }

    private void joinCompany(int company_id) {
        NetBaseRequest request = RequsetFactory.creatBaseRequest(Constants.JOIN_COMPANY);
        request.remove("company_id");
        request.add("company_id", company_id);
        CallServer.getRequestInstance().add(this, 0x01, request, new HttpListenerCallback() {
            @Override
            public void onSucceed(int what, NetBaseBean netBaseBean) {
                Toast(netBaseBean.getMessage());
                if(netBaseBean.isSuccess()){
                    onRefresh();
                }
            }

            @Override
            public void onFailed(int what, String url, Object tag, Exception exception, int responseCode, long networkMillis) {

            }
        }, true, true, "");
    }

    private void select_join_company_list() {
        NetBaseRequest request = RequsetFactory.creatBaseRequest(Constants.SELECT_JOIN_COMPANY_LIST);
        request.add("keyword", keyword);
        CallServer.getRequestInstance().add(this, 0x01, request, new HttpListenerCallback() {
            @Override
            public void onSucceed(int what, NetBaseBean netBaseBean) {
                if (netBaseBean.isSuccess()) {
                    List<Company> list = netBaseBean.parseList(Company.class);
                    adapter.setNewData(list);
                }
            }

            @Override
            public void onFailed(int what, String url, Object tag, Exception exception, int responseCode, long networkMillis) {

            }
        }, swl, "");
    }
}
