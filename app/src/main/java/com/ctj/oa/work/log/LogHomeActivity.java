package com.ctj.oa.work.log;

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
import com.ctj.oa.model.work.log.Log;
import com.ctj.oa.net.CallServer;
import com.ctj.oa.net.HttpListenerCallback;
import com.ctj.oa.net.NetBaseRequest;
import com.ctj.oa.net.RequsetFactory;
import com.ctj.oa.work.approval.AddApprovalActivity;

import java.util.List;

public class LogHomeActivity extends BaseActivity {
    private RecyclerView rv;
    private View headView;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_log_home;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        rv = (RecyclerView) findViewById(R.id.rv);
        rv.setLayoutManager(new GridLayoutManager(this, 3));
        rv.setAdapter(adapter);
        headView = LayoutInflater.from(this).inflate(R.layout.headview_log_home, rv, false);
        adapter.addHeaderView(headView);
        headView.findViewById(R.id.rl_all).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogListActivity.goTo(bContext, 2);
            }
        });
        headView.findViewById(R.id.rl_mine).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogListActivity.goTo(bContext, 1);
            }
        });
        headView.findViewById(R.id.rl_share).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogListActivity.goTo(bContext, 3);
            }
        });
        getLogTemplate();
    }

    private void getLogTemplate() {
        NetBaseRequest request = RequsetFactory.creatBaseRequest(Constants.GET_LOG_TEMPLATE);
        CallServer.getRequestInstance().add(this, 0x01, request, new HttpListenerCallback() {
            @Override
            public void onSucceed(int what, NetBaseBean netBaseBean) {
                if (netBaseBean.isSuccess()) {
                    List<Log> list = netBaseBean.parseList(Log.class);
                    adapter.setNewData(list);
                }

            }

            @Override
            public void onFailed(int what, String url, Object tag, Exception exception, int responseCode, long networkMillis) {

            }
        }, true, true, "");
    }

    private BaseQuickAdapter<Log, BaseViewHolder> adapter = new BaseQuickAdapter<Log, BaseViewHolder>(R.layout.item_log_home) {
        @Override
        protected void convert(BaseViewHolder helper, final Log item) {
            TextView tv = helper.getView(R.id.tv);
            tv.setText(item.getName().substring(0, 1));
            GradientDrawable myGrad = (GradientDrawable) tv.getBackground().mutate();
            myGrad.setColor(Color.parseColor(item.getBg_color()));
            helper.setText(R.id.tv2, item.getName());
            helper.getConvertView().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AddLogActivity.goTo(bContext, item.getId(),item.getName());
                }
            });
        }
    };
}
