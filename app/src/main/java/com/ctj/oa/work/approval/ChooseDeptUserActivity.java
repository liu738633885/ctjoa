package com.ctj.oa.work.approval;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.ctj.oa.Constants;
import com.ctj.oa.R;
import com.ctj.oa.activity.BaseActivity;
import com.ctj.oa.model.UserInfo;
import com.ctj.oa.model.event.EventRefresh;
import com.ctj.oa.model.netmodel.NetBaseBean;
import com.ctj.oa.net.CallServer;
import com.ctj.oa.net.HttpListenerCallback;
import com.ctj.oa.net.NetBaseRequest;
import com.ctj.oa.net.RequsetFactory;
import com.ctj.oa.utils.imageloader.ImageLoader;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

public class ChooseDeptUserActivity extends BaseActivity {
    private RecyclerView rv;
    private String id;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_choose_dept_user;
    }

    public static void goTo(Context context, String id, String where) {
        Intent intent = new Intent(context, ChooseDeptUserActivity.class);
        intent.putExtra("id", id);
        intent.putExtra("where", where);
        context.startActivity(intent);
    }

    protected void handleIntent(Intent intent) {
        id = intent.getStringExtra("id");
        where = intent.getStringExtra("where");
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        rv = (RecyclerView) findViewById(R.id.rv);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(adapter);
        adapter.setEmptyView(R.layout.empty_view);
        getDeptUser();
    }

    private void getDeptUser() {
        NetBaseRequest request = RequsetFactory.creatBaseRequest(Constants.GET_DEPT_USER);
        request.add("post_id", id);
        CallServer.getRequestInstance().add(this, 0x01, request, new HttpListenerCallback() {
            @Override
            public void onSucceed(int what, NetBaseBean netBaseBean) {
                if (netBaseBean.isSuccess()) {
                    List<UserInfo> list = netBaseBean.parseList(UserInfo.class);
                    adapter.setNewData(list);
                }
            }

            @Override
            public void onFailed(int what, String url, Object tag, Exception exception, int responseCode, long networkMillis) {

            }
        }, true, true, "");
    }

    private BaseQuickAdapter<UserInfo, BaseViewHolder> adapter = new BaseQuickAdapter<UserInfo, BaseViewHolder>(R.layout.item_organization_member_simple) {
        @Override
        protected void convert(BaseViewHolder helper, final UserInfo item) {
            ImageLoader.loadHeadImage(bContext, item.getPortrait(), (ImageView) helper.getView(R.id.imv), 2);
            helper.setText(R.id.tv1, item.getNickname());
            helper.getConvertView().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EventBus.getDefault().post(new EventRefresh(item, where));
                    finish();
                }
            });
        }
    };
}
