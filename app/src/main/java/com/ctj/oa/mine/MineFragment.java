package com.ctj.oa.mine;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ctj.oa.Constants;
import com.ctj.oa.R;
import com.ctj.oa.activity.ChooseJoinActivity;
import com.ctj.oa.activity.WebViewActivity;
import com.ctj.oa.chat.organization.OrganizationActivity;
import com.ctj.oa.fragment.BaseFragment;
import com.ctj.oa.model.UserInfo;
import com.ctj.oa.model.netmodel.NetBaseBean;
import com.ctj.oa.net.CallServer;
import com.ctj.oa.net.HttpListenerCallback;
import com.ctj.oa.net.NetBaseRequest;
import com.ctj.oa.net.RequsetFactory;
import com.ctj.oa.utils.imageloader.ImageLoader;
import com.ctj.oa.utils.manager.UserManager;
import com.ctj.oa.widgets.WaveHelper;
import com.lewis.widgets.LewisSwipeRefreshLayout;
import com.lewis.widgets.WaveView;

/**
 * Created by lewis on 2017/4/24.
 */

public class MineFragment extends BaseFragment implements View.OnClickListener, LewisSwipeRefreshLayout.OnRefreshListener {
    @Override
    protected int getLayoutId() {
        return R.layout.fragment_mine;
    }

    private LewisSwipeRefreshLayout swl;
    private WaveView waveView;
    private WaveHelper mWaveHelper;
    private LinearLayout ll_setting;
    private View parentView;
    private ImageView imv_avatar;
    private TextView name;
    private LinearLayout ll_admin, ll_create_company, ll_feedback, ll_help, ll_share, ll_qiuzhi, ll_qiehuan;
    private int backmode;

    @Override
    protected void initView(View view, Bundle savedInstanceState) {
        parentView = view;
        ll_setting = view.findViewById(R.id.ll_setting);
        ll_setting.setOnClickListener(this);
        ll_help = view.findViewById(R.id.ll_help);
        ll_share = view.findViewById(R.id.ll_share);
        ll_help.setOnClickListener(this);
        ll_share.setOnClickListener(this);
        initWaveView();
        imv_avatar = view.findViewById(R.id.imv_avatar);
        imv_avatar.setOnClickListener(this);
        name = view.findViewById(R.id.name);
        swl = view.findViewById(R.id.swl);
        swl.setOnRefreshListener(this);
        ll_admin = view.findViewById(R.id.ll_admin);
        ll_create_company = view.findViewById(R.id.ll_create_company);
        ll_feedback = view.findViewById(R.id.ll_feedback);
        ll_admin.setOnClickListener(this);
        ll_create_company.setOnClickListener(this);
        ll_feedback.setOnClickListener(this);
        ll_qiuzhi = view.findViewById(R.id.ll_qiuzhi);
        ll_qiuzhi.setOnClickListener(this);
        ll_qiehuan = view.findViewById(R.id.ll_qiehuan);
        ll_qiehuan.setOnClickListener(this);
        updateUI();
    }

    private void updateUI() {
        name.setText(UserManager.getNickname());
        ll_admin.setVisibility(UserManager.isAdmin() ? View.VISIBLE : View.GONE);
        ll_create_company.setVisibility(UserManager.getCompanyId() == 0 ? View.VISIBLE : View.GONE);
        ImageLoader.loadHeadImage(getActivity(), UserManager.getPortrait(), imv_avatar, 0);
        swl.setRefreshing(false);
    }

    private void initWaveView() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (waveView == null) {
                    waveView = (WaveView) parentView.findViewById(R.id.waveView);
                    waveView.setShapeType(WaveView.ShapeType.SQUARE);
                    waveView.setWaveColor(Color.parseColor("#18FFFFFF"),
                            Color.parseColor("#13FFFFFF"));
                }
                if (mWaveHelper == null) {
                    mWaveHelper = new WaveHelper(waveView);
                    mWaveHelper.start();
                }
            }
        }, 300);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mWaveHelper != null) {
            mWaveHelper.cancel();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mWaveHelper != null) {
            mWaveHelper.start();
        }
        switch (backmode) {
            case 1:
                getUserInfo();
                break;
            case 2:
                break;
        }
        backmode = 0;
    }

    private void getUserInfo() {
        NetBaseRequest request = RequsetFactory.creatBaseRequest(Constants.GET_USER_INFO);
        request.add("get_user_id", UserManager.getId());
        CallServer.getRequestInstance().add(getActivity(), 0x01, request, new HttpListenerCallback() {
            @Override
            public void onSucceed(int what, NetBaseBean netBaseBean) {
                if (netBaseBean.isSuccess()) {
                    UserInfo userInfo = netBaseBean.parseObject(UserInfo.class);
                    UserManager.saveUserInfo(userInfo);
                    updateUI();
                }
            }

            @Override
            public void onFailed(int what, String url, Object tag, Exception exception, int responseCode, long networkMillis) {
            }
        }, swl);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_setting:
                startActivity(new Intent(getContext(), SettingActivity.class));
                break;
            case R.id.imv_avatar:
                UserProfileActivity.goTo(getActivity(), UserManager.getId());
                backmode = 1;
                break;
            case R.id.ll_feedback:
                startActivity(new Intent(getActivity(), FeedbackActivity.class));
                break;
            case R.id.ll_create_company:
                WebViewActivity.goTo(getActivity(), Constants.WEB_CREATE_COMPANY + "?user_id=" + UserManager.getId() + "&token=" + UserManager.getToken(), "创建公司");
                backmode = 1;
                break;
            case R.id.ll_admin:
                startActivity(new Intent(getActivity(), OrganizationActivity.class));
                break;
            case R.id.ll_qiuzhi:
                WebViewActivity.goTo(getActivity(), Constants.WEB_JOBS_WANTED_INDEX + "?user_id=" + UserManager.getId() + "&token=" + UserManager.getToken(), WebViewActivity.HIDE_TITLE);
                break;
            case R.id.ll_help:
                WebViewActivity.goTo(getActivity(), Constants.WEB_HELP, "帮助中心");
                break;
            case R.id.ll_share:
                WebViewActivity.goToShare(getActivity(), Constants.WEB_SHARE, "推荐朋友");
                break;
            case R.id.ll_qiehuan:
                startActivity(new Intent(getActivity(), ChooseJoinActivity.class));
                break;
        }
    }

    @Override
    public void onRefresh() {
        updateUI();
    }
}
