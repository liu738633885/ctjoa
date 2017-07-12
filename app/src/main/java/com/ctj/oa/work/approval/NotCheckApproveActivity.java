package com.ctj.oa.work.approval;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.ctj.oa.Constants;
import com.ctj.oa.R;
import com.ctj.oa.activity.BaseActivity;
import com.ctj.oa.model.UserInfo;
import com.ctj.oa.model.event.EventRefresh;
import com.ctj.oa.model.netmodel.NetBaseBean;
import com.ctj.oa.model.work.approve.Approve;
import com.ctj.oa.net.CallServer;
import com.ctj.oa.net.HttpListenerCallback;
import com.ctj.oa.net.NetBaseRequest;
import com.ctj.oa.net.RequsetFactory;
import com.ctj.oa.widgets.TitleBar;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

public class NotCheckApproveActivity extends BaseActivity {

    private Approve approve;
    private TitleBar titleBar;
    private RecyclerView rv;
    private EditText footerView;
    private List<UserInfo> list;
    private int approve_node;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_not_check_approve;
    }

    public static void goTo(Context context, Approve approve) {
        Intent intent = new Intent(context, NotCheckApproveActivity.class);
        intent.putExtra("approve", approve);
        context.startActivity(intent);
    }

    protected void handleIntent(Intent intent) {
        approve = (Approve) intent.getSerializableExtra("approve");
    }


    @Override
    protected void initView(Bundle savedInstanceState) {
        titleBar = (TitleBar) findViewById(R.id.titleBar);
        titleBar.getRightGroup().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                check();
            }
        });
        rv = (RecyclerView) findViewById(R.id.rv);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(adapter);
        footerView = (EditText) LayoutInflater.from(this).inflate(R.layout.footerview_approval_check, (ViewGroup) rv.getParent(), false);
        adapter.addFooterView(footerView);

        list = new ArrayList<>();
        UserInfo userInfo = new UserInfo();
        userInfo.setNickname("返回申请人");
        userInfo.setId(0);
        list.add(userInfo);
        if (approve.getApprove_node() > 1) {
            UserInfo userInfo2 = new UserInfo();
            userInfo2.setNickname("返回上一级");
            userInfo2.setId(approve.getApprove_node());
            list.add(userInfo2);
        }
        adapter.setNewData(list);
    }

    private void check() {
        NetBaseRequest request = RequsetFactory.creatBaseRequest(Constants.CHECK_APPLY_APPROVE);
        request.add("id", approve.getId());
        request.add("approve_state", 2);
        if (approve_node < 0) {
            Toast("至少选择一项");
            return;
        }
        request.add("approve_node", approve_node);
        request.add("approve_desc", footerView.getText().toString());
        CallServer.getRequestInstance().add(this, 0x01, request, new HttpListenerCallback() {
            @Override
            public void onSucceed(int what, NetBaseBean netBaseBean) {
                if (netBaseBean.isSuccess()) {
                    finish();
                    EventBus.getDefault().post(new EventRefresh(EventRefresh.ACTION_REFRESH, ApprovalDetailActivity.class.getSimpleName()));
                }
            }

            @Override
            public void onFailed(int what, String url, Object tag, Exception exception, int responseCode, long networkMillis) {

            }
        }, true, true, "");

    }

    private BaseQuickAdapter<UserInfo, BaseViewHolder> adapter = new BaseQuickAdapter<UserInfo, BaseViewHolder>(R.layout.item_organization_member_simple_check) {
        @Override
        protected void convert(BaseViewHolder helper, final UserInfo item) {
            helper.setText(R.id.tv1, item.getNickname());
            final CheckBox cb = helper.getView(R.id.cb);
            cb.setVisibility(item.getId() == -1 ? View.INVISIBLE : View.VISIBLE);
            cb.setChecked(item.getId() == approve_node);
            item.setCheck(item.getId() == approve_node);
            helper.getConvertView().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!item.isCheck()) {
                        item.setCheck(true);
                        approve_node = item.getId();
                        notifyDataSetChanged();
                    }
                }
            });

        }
    };

}

