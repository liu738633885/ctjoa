package com.ctj.oa.work.approval;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.ctj.oa.Constants;
import com.ctj.oa.R;
import com.ctj.oa.activity.BaseActivity;
import com.ctj.oa.model.netmodel.NetBaseBean;
import com.ctj.oa.model.work.approve.Approve;
import com.ctj.oa.model.work.approve.ApproveList;
import com.ctj.oa.net.CallServer;
import com.ctj.oa.net.HttpListenerCallback;
import com.ctj.oa.net.NetBaseRequest;
import com.ctj.oa.net.RequsetFactory;
import com.ctj.oa.utils.MyViewUtils;
import com.ctj.oa.widgets.MyPopView;
import com.ctj.oa.widgets.TitleBar;
import com.lewis.utils.DateUtils;
import com.lewis.utils.SearchBarUtils;
import com.lewis.widgets.LewisSwipeRefreshLayout;

import java.util.ArrayList;
import java.util.List;

public class ApplyListActivity extends BaseActivity implements View.OnClickListener, LewisSwipeRefreshLayout.OnRefreshListener, BaseQuickAdapter.RequestLoadMoreListener {
    private Button btn_choose_1, btn_choose_2;
    private MyPopView myPopView1, myPopView2;
    private RecyclerView rv;
    private LewisSwipeRefreshLayout swl;
    private TitleBar titleBar;
    private int state = -1;
    private int class_id;
    private int pagerNum;
    private List<Approve> classList;
    private String keyword;
    private EditText editText;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_apply_list;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        titleBar = (TitleBar) findViewById(R.id.titleBar);
        titleBar.getRightGroup().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(bContext, ChooseApprovalActivity.class));
            }
        });
        btn_choose_1 = (Button) findViewById(R.id.btn_choose_1);
        btn_choose_1.setOnClickListener(this);
        btn_choose_2 = (Button) findViewById(R.id.btn_choose_2);
        btn_choose_2.setOnClickListener(this);
        myPopView1 = new MyPopView(this);
        myPopView1.setData(new String[]{"全部", "审批中", "同意", "不同意", "已撤回"});
        myPopView1.setListener(new MyPopView.OnItemClickListener() {
            @Override
            public void onItemClick(String item, int position) {
                if (item.equals("全部")) {
                    item = "状态";
                }
                btn_choose_1.setText(item);
                state = position - 1;
                getApplyList(0);
            }
        });
        myPopView2 = new MyPopView(this);
        myPopView2.setData(new String[]{"全部"});
        myPopView2.setListener(new MyPopView.OnItemClickListener() {
            @Override
            public void onItemClick(String item, int position) {
                if (item.equals("全部")) {
                    item = "类型";
                    class_id = -1;
                } else {
                    class_id = classList.get(position - 1).getClass_id();
                }
                btn_choose_2.setText(item);
                getApplyList(0);
            }
        });
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
        swl = (LewisSwipeRefreshLayout) findViewById(R.id.swl);
        swl.setOnRefreshListener(this);
        rv = (RecyclerView) findViewById(R.id.rv);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(adapter);
        adapter.setOnLoadMoreListener(this, rv);
        adapter.setEmptyView(R.layout.empty_view);
        getClassList();
        onRefresh();
    }

    private void getClassList() {
        NetBaseRequest request = RequsetFactory.creatBaseRequest(Constants.GET_APPROVE_CLASS_LIST);
        CallServer.getRequestInstance().add(this, 0x04, request, new HttpListenerCallback() {
            @Override
            public void onSucceed(int what, NetBaseBean netBaseBean) {
                if (netBaseBean.isSuccess()) {
                    classList = netBaseBean.parseList(Approve.class);
                    List<String> classNames = new ArrayList<String>();
                    classNames.add("全部");
                    for (Approve approve : classList) {
                        classNames.add(approve.getClass_name());
                    }
                    myPopView2.setData(classNames);
                }
            }

            @Override
            public void onFailed(int what, String url, Object tag, Exception exception, int responseCode, long networkMillis) {

            }
        }, true, false);
    }

    private void getApplyList(int num) {
        NetBaseRequest request = RequsetFactory.creatBaseRequest(Constants.GET_APPLY_LIST);
        request.add("state", state);
        request.add("class_id", class_id);
        request.add("pageno", num);
        if (!TextUtils.isEmpty(keyword)) {
            request.add("keyword", keyword);
        }
        CallServer.getRequestInstance().add(this, num, request, new HttpListenerCallback() {
            @Override
            public void onSucceed(int what, NetBaseBean netBaseBean) {
                if (netBaseBean.isSuccess()) {
                    ApproveList applyList = netBaseBean.parseObject(ApproveList.class);
                    pagerNum = applyList.getPageno();
                    List<Approve> list = applyList.getList();
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

    private BaseQuickAdapter<Approve, BaseViewHolder> adapter = new BaseQuickAdapter<Approve, BaseViewHolder>(R.layout.item_log) {
        @Override
        protected void convert(BaseViewHolder helper, final Approve item) {
            try {
                TextView tv = helper.getView(R.id.tv);
                tv.setText(item.getApprove_title().substring(0, 1));
                GradientDrawable myGrad = (GradientDrawable) tv.getBackground().mutate();
                myGrad.setColor(Color.parseColor(item.getBg_color()));
                helper.setText(R.id.tv2, item.getApprove_title());
                helper.setText(R.id.tv3, DateUtils.tenLongToString(item.getAdd_time(), DateUtils.DB_DATA_FORMAT2));
                TextView tv4 = helper.getView(R.id.tv4);
                switch (item.getApplay_state()) {
                    case 0:
                        tv4.setTextColor(ContextCompat.getColor(mContext, R.color.super_orange));
                        helper.setText(R.id.tv4, "审批中");
                        break;
                    case 1:
                        tv4.setTextColor(ContextCompat.getColor(mContext, R.color.super_green));
                        helper.setText(R.id.tv4, "同意");
                        break;
                    case 2:
                        tv4.setTextColor(ContextCompat.getColor(mContext, R.color.super_red));
                        helper.setText(R.id.tv4, "不同意");
                        break;
                    case 3:
                        tv4.setTextColor(ContextCompat.getColor(mContext, R.color.gray01));
                        helper.setText(R.id.tv4, "已撤回");
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            helper.getConvertView().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ApprovalDetailActivity.goTo(bContext, item.getId(), ApprovalDetailActivity.MODE_APPLY);
                }
            });
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_choose_1:
                myPopView1.showPopupWindow(v);
                break;
            case R.id.btn_choose_2:
                myPopView2.showPopupWindow(v);
                break;
            default:
        }
    }

    @Override
    public void onRefresh() {
        getApplyList(0);
    }

    @Override
    public void onLoadMoreRequested() {
        getApplyList(pagerNum);
    }

}
