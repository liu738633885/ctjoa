package com.ctj.oa.work.approval;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

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
import com.ctj.oa.utils.imageloader.ImageLoader;
import com.ctj.oa.work.ChooseUserActivity;
import com.lewis.utils.DateUtils;
import com.orhanobut.logger.Logger;

import org.greenrobot.eventbus.Subscribe;

import java.util.List;

public class ApprovalDetailActivity extends BaseActivity implements View.OnClickListener {
    private String TAG = ApprovalDetailActivity.this.getClass().getSimpleName();
    private int id;
    private int mode;//1 apply  2approval
    public static final int MODE_APPLY = 1;
    public static final int MODE_APPROVAL = 2;
    public static final int MODE_CT = 3;
    private RecyclerView rv, rv0, rv1;
    private Button btn1, btn2, btn3;
    private TextView tv1, tv2, tv3;
    private View footerView, footerView2, headView;
    private TextView tv_ct, tv_nickname, tv_creat_time;
    private ImageView imv;
    private Approve approve;
    private int type = -1;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_approval_detail;
    }

    public static void goTo(Context context, int id, int mode) {
        Intent intent = new Intent(context, ApprovalDetailActivity.class);
        intent.putExtra("id", id);
        intent.putExtra("mode", mode);
        context.startActivity(intent);
    }

    public static void goTo(Context context, int id, int mode, int type) {
        Intent intent = new Intent(context, ApprovalDetailActivity.class);
        intent.putExtra("id", id);
        intent.putExtra("mode", mode);
        intent.putExtra("type", type);
        context.startActivity(intent);
    }

    protected void handleIntent(Intent intent) {
        id = intent.getIntExtra("id", 0);
        mode = intent.getIntExtra("mode", 0);
        type = intent.getIntExtra("type", -1);
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        btn1 = (Button) findViewById(R.id.btn1);
        btn2 = (Button) findViewById(R.id.btn2);
        btn3 = (Button) findViewById(R.id.btn3);
        btn1.setOnClickListener(this);
        btn2.setOnClickListener(this);
        btn3.setOnClickListener(this);
        rv = (RecyclerView) findViewById(R.id.rv);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(adapter);
        headView = LayoutInflater.from(this).inflate(R.layout.headview_approval_detail, (ViewGroup) rv.getParent(), false);
        tv_nickname = (TextView) headView.findViewById(R.id.tv1);
        tv_creat_time = (TextView) headView.findViewById(R.id.tv2);
        tv_creat_time = (TextView) headView.findViewById(R.id.tv2);
        imv = (ImageView) headView.findViewById(R.id.imv);
        adapter.addHeaderView(headView);
        footerView = LayoutInflater.from(this).inflate(R.layout.footerview_approval_detail, (ViewGroup) rv.getParent(), false);
        footerView2 = LayoutInflater.from(this).inflate(R.layout.footerview_approval_detail2, (ViewGroup) rv.getParent(), false);
        adapter.addFooterView(footerView);
        adapter.addFooterView(footerView2);
        tv1 = (TextView) footerView.findViewById(R.id.tv1);
        tv2 = (TextView) footerView.findViewById(R.id.tv2);
        tv3 = (TextView) footerView.findViewById(R.id.tv3);
        rv0 = (RecyclerView) footerView2.findViewById(R.id.rv0);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        rv0.setLayoutManager(linearLayoutManager);
        rv0.setAdapter(userAdapter);
        tv_ct = (TextView) footerView2.findViewById(R.id.tv_ct);
        rv1 = (RecyclerView) footerView.findViewById(R.id.rv1);
        rv1.setLayoutManager(new LinearLayoutManager(this));
        rv1.setAdapter(dataAdapter);
        getDetail();
    }

    private void getDetail() {
        String url = "";
        if (mode == MODE_APPLY) {
            url = Constants.GET_APPLY_INFO;
        } else if (mode == MODE_APPROVAL) {
            url = Constants.GET_CHECK_APPROVE_DETAIL;
        } else if (mode == MODE_CT) {
            url = Constants.CT_INFO;
        }
        NetBaseRequest request = RequsetFactory.creatBaseRequest(url);
        request.add("id", id);
        if (type != -1) {
            request.add("form", "message");
            request.add("type", type);
        }
        CallServer.getRequestInstance().add(this, 0x01, request, new HttpListenerCallback() {
            @Override
            public void onSucceed(int what, NetBaseBean netBaseBean) {
                if (netBaseBean.isSuccess()) {
                    approve = netBaseBean.parseObject(Approve.class);
                    updateUI();
                }
            }

            @Override
            public void onFailed(int what, String url, Object tag, Exception exception, int responseCode, long networkMillis) {

            }
        }, true, true, "");
    }

    private void updateUI() {
        try {
            if (mode == MODE_APPLY) {
                if (approve.getRevoke_state() == 1 && approve.getApplay_state() == 0) {
                    btn1.setVisibility(View.VISIBLE);
                } else {
                    btn1.setVisibility(View.GONE);
                }
                btn1.setText("撤回");
                btn2.setVisibility(View.GONE);
                btn3.setVisibility(View.VISIBLE);
            } else if (mode == MODE_APPROVAL) {
                btn1.setText("同意");
                btn2.setText("不同意");
                btn1.setVisibility(approve.getApprove_state() == 0 ? View.VISIBLE : View.GONE);
                btn2.setVisibility(approve.getApprove_state() == 0 ? View.VISIBLE : View.GONE);
                btn3.setVisibility(View.VISIBLE);
            } else if (mode == MODE_CT) {
                ((View) btn1.getParent()).setVisibility(View.GONE);
            }
            adapter.setNewData(approve.getApprove_list());
            tv1.setText(DateUtils.tenLongToString(approve.getAdd_time(), DateUtils.DB_DATA_FORMAT2));
            tv2.setText(approve.getDept_name());
            tv3.setText(approve.getClass_name() + " - " + approve.getApprove_title());
            tv_creat_time.setText(DateUtils.tenLongToString(approve.getAdd_time(), DateUtils.DB_DATA_FORMAT2));
            if(approve.getApply_user_info()!=null){
                ImageLoader.loadHeadImage(this, approve.getApply_user_info().getPortrait(), imv, -1);
                tv_nickname.setText(approve.getApply_user_info().getNickname());
            }
            updateCCUI();
            updateFieldUI();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateFieldUI() {
        if (approve != null && approve.getField_ext() != null) {
            dataAdapter.setNewData(approve.getField_ext());
        }
    }

    private void updateCCUI() {
        if (approve != null && approve.getCc_user_list() != null && approve.getCc_user_list().size() > 0) {
            footerView2.setVisibility(View.VISIBLE);
            tv_ct.setText("抄送人 (" + approve.getCc_user_list().size() + ")");
            userAdapter.setNewData(approve.getCc_user_list());
        } else {
            footerView2.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        if (approve != null && approve.getId() != 0) {
            switch (v.getId()) {
                case R.id.btn1:
                    if (mode == MODE_APPLY) {
                        //撤回
                        send_REVOKE_APPROVE();
                    } else {
                        //同意
                        CheckApproveActivity.goTo(this, approve);
                    }
                    break;
                case R.id.btn2:
                    //拒绝
                    NotCheckApproveActivity.goTo(this, approve);
                    break;
                case R.id.btn3:
                    //抄送
                    ChooseUserActivity.goTo(bContext, approve.getCc_user_list(), TAG);
                    break;
            }
        } else {
            Toast("没有获取到数据");
        }
    }


    private BaseQuickAdapter<Approve, BaseViewHolder> adapter = new BaseQuickAdapter<Approve, BaseViewHolder>(R.layout.item_approval_user) {
        @Override
        protected void convert(BaseViewHolder helper, Approve item) {
            int p = helper.getAdapterPosition() - getHeaderLayoutCount();
            helper.setVisible(R.id.line1, p != 0);
            helper.setVisible(R.id.line2, p != getData().size() - 1);
            helper.setText(R.id.tv1, item.getNickname());
            ImageView imv = helper.getView(R.id.imv);
            ImageLoader.loadHeadImage(bContext, item.getPortrait(), imv, 2);
            ImageView imv0 = helper.getView(R.id.imv0);
            TextView tv2 = helper.getView(R.id.tv2);
            //switch (mode == MODE_APPLY ? item.getApplay_state() : item.getApprove_state()) {
            switch (item.getApprove_state()) {
                case 0:
                    tv2.setTextColor(ContextCompat.getColor(mContext, R.color.super_orange));
                    helper.setText(R.id.tv2, "审批中");
                    imv0.setBackgroundResource(R.drawable.ic_more_round);
                    break;
                case 1:
                    tv2.setTextColor(ContextCompat.getColor(mContext, R.color.super_green));
                    helper.setText(R.id.tv2, "同意");
                    imv0.setBackgroundResource(R.drawable.ic_ok_round);
                    break;
                case 2:
                    tv2.setTextColor(ContextCompat.getColor(mContext, R.color.super_red));
                    helper.setText(R.id.tv2, "不同意");
                    imv0.setBackgroundResource(R.drawable.ic_close);
                    break;
                case 3:
                    tv2.setTextColor(ContextCompat.getColor(mContext, R.color.gray01));
                    helper.setText(R.id.tv2, "已撤回");
                    imv0.setBackgroundResource(R.drawable.ic_close_gray);
                    break;
            }
        }

    };
    private BaseQuickAdapter<UserInfo, BaseViewHolder> userAdapter = new BaseQuickAdapter<UserInfo, BaseViewHolder>(R.layout.item_user) {
        @Override
        protected void convert(BaseViewHolder helper, UserInfo item) {
            helper.setText(R.id.tv, item.getNickname());
            if (TextUtils.isEmpty(item.getPortrait())) {
                helper.setVisible(R.id.imv, false);
                helper.setVisible(R.id.tv0, true);
                try {
                    helper.setText(R.id.tv0, item.getNickname().substring(0, 1));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                ImageLoader.loadHeadImage(bContext, item.getPortrait(), ((ImageView) helper.getView(R.id.imv)), 2);
                helper.setVisible(R.id.imv, true);
                helper.setVisible(R.id.tv0, false);
            }
        }

    };
    private BaseQuickAdapter<Approve.Field, BaseViewHolder> dataAdapter = new BaseQuickAdapter<Approve.Field, BaseViewHolder>(R.layout.item_approval_detail) {
        @Override
        protected void convert(BaseViewHolder helper, Approve.Field item) {
            helper.setText(R.id.tv1, item.title_field);
            helper.setText(R.id.tv2, item.value_field);
        }

    };

    private void send_CCapprove(String ids) {
        NetBaseRequest request = RequsetFactory.creatBaseRequest(Constants.CC_APPROVE);
        request.add("user_id_list", ids);
        if (mode == MODE_APPLY) {
            request.add("id", id);
        } else {
            request.add("id", approve.getApprove_id());
        }
        CallServer.getRequestInstance().add(this, 0x02, request, new HttpListenerCallback() {
            @Override
            public void onSucceed(int what, NetBaseBean netBaseBean) {
                if (netBaseBean.isSuccess()) {
                    getDetail();
                }
                Toast(netBaseBean.getMessage());
            }

            @Override
            public void onFailed(int what, String url, Object tag, Exception exception, int responseCode, long networkMillis) {

            }
        }, true, true);

    }

    /**
     * 撤回
     */
    private void send_REVOKE_APPROVE() {
        NetBaseRequest request = RequsetFactory.creatBaseRequest(Constants.REVOKE_APPROVE);
        request.add("id", id);
        CallServer.getRequestInstance().add(this, 0x02, request, new HttpListenerCallback() {
            @Override
            public void onSucceed(int what, NetBaseBean netBaseBean) {
                if (netBaseBean.isSuccess()) {
                    getDetail();
                }
                Toast(netBaseBean.getMessage());
            }

            @Override
            public void onFailed(int what, String url, Object tag, Exception exception, int responseCode, long networkMillis) {

            }
        }, true, true);

    }

    @Subscribe
    public void onEventMainThread(EventRefresh e) {
        if (e.getWhere().equals(TAG)) {
            if (e.getData() != null) {
                List<UserInfo> users = (List<UserInfo>) e.getData();
                String ids = "";
                for (UserInfo info : users) {
                    ids = ids + info.getId() + ",";
                }
                if (ids.endsWith(",")) {
                    ids = ids.substring(0, ids.length() - 1);
                }
                send_CCapprove(ids);
                Logger.d(ids + "");
            }
            if (e.getAction().equals(EventRefresh.ACTION_REFRESH)) {
                getDetail();
            }
        }
    }
}
