package com.ctj.oa.enterprise.business;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.ctj.oa.Constants;
import com.ctj.oa.R;
import com.ctj.oa.activity.BaseActivity;
import com.ctj.oa.model.netmodel.NetBaseBean;
import com.ctj.oa.model.work.company.Company;
import com.ctj.oa.model.work.company.CompanyList;
import com.ctj.oa.net.CallServer;
import com.ctj.oa.net.HttpListenerCallback;
import com.ctj.oa.net.NetBaseRequest;
import com.ctj.oa.net.RequsetFactory;
import com.ctj.oa.utils.MyViewUtils;
import com.ctj.oa.utils.imageloader.ImageLoader;
import com.ctj.oa.widgets.TitleBar;
import com.lewis.utils.CommonUtils;
import com.lewis.utils.SearchBarUtils;
import com.lewis.widgets.LewisSwipeRefreshLayout;

import java.util.List;


public class BusinessRegisterActivity extends BaseActivity implements LewisSwipeRefreshLayout.OnRefreshListener, BaseQuickAdapter.RequestLoadMoreListener {
    private LewisSwipeRefreshLayout swl;
    private RecyclerView rv;
    private EditText searchView;
    private int pagerNum;
    private String keyword;
    private int cid;
    private String title;
    private TitleBar titleBar;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_business_register;
    }

    public static void goTo(Context context, int cid, String title) {
        Intent intent = new Intent(context, BusinessRegisterActivity.class);
        intent.putExtra("cid", cid);
        intent.putExtra("title", title);
        context.startActivity(intent);
    }

    protected void handleIntent(Intent intent) {
        cid = intent.getIntExtra("cid", 0);
        title = intent.getStringExtra("title");
    }


    @Override
    protected void initView(Bundle savedInstanceState) {
        titleBar = (TitleBar) findViewById(R.id.titleBar);
        if (!TextUtils.isEmpty(title)) {
            titleBar.setCenterText(title);
        }
        swl = (LewisSwipeRefreshLayout) findViewById(R.id.swl);
        swl.setOnRefreshListener(this);
        rv = (RecyclerView) findViewById(R.id.rv);
        rv.setLayoutManager(new LinearLayoutManager(this));
        adapter.setOnLoadMoreListener(this, rv);
        adapter.setEmptyView(R.layout.empty_view);
        rv.setAdapter(adapter);
        rv.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                CommonUtils.hideSoftInput(BusinessRegisterActivity.this, v);
                return false;
            }
        });
        searchView = SearchBarUtils.init(this, new SearchBarUtils.OnSearchListener() {
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
        swl.setRefreshing(true);
        onRefresh();
    }

    private void getCompanyList(int num) {
        NetBaseRequest request = RequsetFactory.creatBaseRequest(Constants.GET_COMPANY_LIST);
        if (cid != 0) {
            request.add("cid", cid);
        }
        if (!TextUtils.isEmpty(keyword)) {
            request.add("keyword", keyword);
        }
        request.add("pageno", num);
        CallServer.getRequestInstance().add(this, num, request, new HttpListenerCallback() {
            @Override
            public void onSucceed(int what, NetBaseBean netBaseBean) {
                if (netBaseBean.isSuccess()) {
                    CompanyList companyList = netBaseBean.parseObject(CompanyList.class);
                    pagerNum = companyList.getPageno();
                    List<Company> list = companyList.getList();
                    MyViewUtils.bindListWithNum(pagerNum, what, adapter, list);
                } else {
                    Toast(netBaseBean.getMessage());
                }

            }

            @Override
            public void onFailed(int what, String url, Object tag, Exception exception, int responseCode, long networkMillis) {

            }
        }, swl);
    }


    private BaseQuickAdapter<Company, BaseViewHolder> adapter = new BaseQuickAdapter<Company, BaseViewHolder>(R.layout.item_bussiness_register) {
        @Override
        protected void convert(final BaseViewHolder helper, final Company item) {
            helper.setText(R.id.tv1, item.getCompany_name());
            helper.setText(R.id.tv2, item.getProv_name() + "  " + item.getCity_name());
            helper.setText(R.id.tv3, item.getCompany_service());
            ImageLoader.loadHeadImage(bContext, item.getCompany_logo(), (ImageView) helper.getView(R.id.imv), -1);
            Button btn = helper.getView(R.id.btn);
            btn.setText(item.getIs_follow() == 1 ? "已关注" : "关注");
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    follow(helper.getAdapterPosition());
                }
            });
            helper.getConvertView().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    BusinessRegisterDetailActivity.goTo(BusinessRegisterActivity.this, item.getCompany_id());
                }
            });
        }

        public void follow(final int position) {
            NetBaseRequest request = RequsetFactory.creatBaseRequest(Constants.COMPANY_FOLLOW);
            request.add("company_id", getData().get(position).getCompany_id());
            CallServer.getRequestInstance().add(bContext, 0x03, request, new HttpListenerCallback() {
                @Override
                public void onSucceed(int what, NetBaseBean netBaseBean) {
                    if (netBaseBean.isSuccess()) {
                        if (getData().get(position).getIs_follow() == 1) {
                            getData().get(position).setIs_follow(0);
                        } else {
                            getData().get(position).setIs_follow(1);
                        }
                        notifyItemChanged(position);
                    }
                }

                @Override
                public void onFailed(int what, String url, Object tag, Exception exception, int responseCode, long networkMillis) {

                }
            }, true, true, "");
        }
    };

    @Override
    public void onRefresh() {
        getCompanyList(0);
    }


    @Override
    public void onLoadMoreRequested() {
        getCompanyList(pagerNum);
    }


}
