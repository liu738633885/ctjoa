package com.ctj.oa.work.approval;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.ctj.oa.Constants;
import com.ctj.oa.R;
import com.ctj.oa.activity.BaseActivity;
import com.ctj.oa.model.netmodel.NetBaseBean;
import com.ctj.oa.model.work.approve.Approve;
import com.ctj.oa.model.work.approve.ApproveCount;
import com.ctj.oa.net.CallServer;
import com.ctj.oa.net.HttpListenerCallback;
import com.ctj.oa.net.NetBaseRequest;
import com.ctj.oa.net.RequsetFactory;
import com.lewis.utils.T;

import java.util.List;

public class ApprovalHomeActivity extends BaseActivity {
    private RecyclerView rv;
    private View headView;
    private TextView count1, count2;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_approval_home;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        rv = (RecyclerView) findViewById(R.id.rv);
        rv.setLayoutManager(new GridLayoutManager(this, 3));
        rv.setAdapter(adapter);
        headView = LayoutInflater.from(this).inflate(R.layout.headview_approval_home, rv, false);
        adapter.addHeaderView(headView);
        //adapter.setNewData(makeDate());
        headView.findViewById(R.id.rl_apply).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ApprovalHomeActivity.this, ApplyListActivity.class));
            }
        });
        headView.findViewById(R.id.rl_approval).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ApprovalListActivity.goTo(bContext, Constants.MY_APPROVE_LIST);
            }
        });
        headView.findViewById(R.id.rl_cc_approval).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ApprovalListActivity.goTo(bContext, Constants.CC_APPROVE_LIST);
            }
        });
        count1 = (TextView) headView.findViewById(R.id.count1);
        count2 = (TextView) headView.findViewById(R.id.count2);
        getApproveTemplate();
    }

    private void getApproveTemplate() {
        NetBaseRequest request = RequsetFactory.creatBaseRequest(Constants.GET_APPROVE_TEMPLATE);
        CallServer.getRequestInstance().add(this, 0x01, request, new HttpListenerCallback() {
            @Override
            public void onSucceed(int what, NetBaseBean netBaseBean) {
                if (netBaseBean.isSuccess()) {
                    List<Approve> list = netBaseBean.parseList(Approve.class);
                    adapter.setNewData(list);

                } else {
                    T.showShort(ApprovalHomeActivity.this, netBaseBean.getMessage());
                }
            }

            @Override
            public void onFailed(int what, String url, Object tag, Exception exception, int responseCode, long networkMillis) {

            }
        }, true, true);
    }

    private BaseQuickAdapter<Approve, BaseViewHolder> adapter = new BaseQuickAdapter<Approve, BaseViewHolder>(R.layout.item_log_home) {
        @Override
        protected void convert(BaseViewHolder helper, final Approve item) {
            TextView tv = helper.getView(R.id.tv);
            tv.setText(item.getApprove_title().substring(0, 1));
            GradientDrawable myGrad = (GradientDrawable) tv.getBackground().mutate();
            myGrad.setColor(Color.parseColor(item.getBg_color()));
            helper.setText(R.id.tv2, item.getApprove_title());
            helper.getConvertView().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AddApprovalActivity.goTo(ApprovalHomeActivity.this, item.getId());
                }
            });
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        getCount();
    }

    private void getCount() {
        NetBaseRequest request = RequsetFactory.creatBaseRequest(Constants.GET_APPLY_APPROVE_COUNT);
        CallServer.getRequestInstance().add(this, 0x01, request, new HttpListenerCallback() {
            @Override
            public void onSucceed(int what, NetBaseBean netBaseBean) {
                ApproveCount count = netBaseBean.parseObject(ApproveCount.class);
                if (count.getApply_count() > 0) {
                    count1.setText(count.getApply_count() + "");
                    count1.setVisibility(View.VISIBLE);
                } else {
                    count1.setVisibility(View.INVISIBLE);
                }
                if (count.getApprove_count() > 0) {
                    count2.setText(count.getApprove_count() + "");
                    count2.setVisibility(View.VISIBLE);
                } else {
                    count2.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onFailed(int what, String url, Object tag, Exception exception, int responseCode, long networkMillis) {

            }
        }, true, false);
    }

  /*  private List<MineView> makeDate() {
        List<MineView> mineViews = new ArrayList<>();
        mineViews.add(new MineView(0xff70a6fe, "采", "采购申请"));
        mineViews.add(new MineView(0xffff8458, "用", "用印申请"));
        mineViews.add(new MineView(0xfffcbc05, "月", "开票申请"));
        mineViews.add(new MineView(0xff25c0b2, "付", "付款申请"));
        mineViews.add(new MineView(0xff7787df, "报", "报销申请"));
        mineViews.add(new MineView(0xfffcbc05, "加", "加班申请"));
        mineViews.add(new MineView(0xffcf7fde, "出", "出差申请"));
        mineViews.add(new MineView(0xff3db7f6, "外", "外出申请"));
        return mineViews;
    }*/
}
