package com.ctj.oa.mine;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.ctj.oa.Constants;
import com.ctj.oa.R;
import com.ctj.oa.activity.BaseActivity;
import com.ctj.oa.model.netmodel.NetBaseBean;
import com.ctj.oa.net.CallServer;
import com.ctj.oa.net.HttpListenerCallback;
import com.ctj.oa.net.NetBaseRequest;
import com.ctj.oa.net.RequsetFactory;
import com.ctj.oa.widgets.TitleBar;

public class FeedbackActivity extends BaseActivity {
    private TitleBar titleBar;
    private EditText edt;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_feedback;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        titleBar = (TitleBar) findViewById(R.id.titleBar);
        titleBar.getRightGroup().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                feedBack();
            }
        });
        edt = (EditText) findViewById(R.id.edt);
    }

    private void feedBack() {
        NetBaseRequest request = RequsetFactory.creatBaseRequest(Constants.USER_FEEDBACK);
        if (TextUtils.isEmpty(edt.getText().toString())) {
            Toast("请输入反馈内容");
            return;
        }
        request.add("feedback_content", edt.getText().toString());
        request.add("platform", 1);
        CallServer.getRequestInstance().add(this, 0x01, request, new HttpListenerCallback() {
            @Override
            public void onSucceed(int what, NetBaseBean netBaseBean) {
                Toast(netBaseBean.getMessage());
                if (netBaseBean.isSuccess()) {
                    finish();
                }
            }

            @Override
            public void onFailed(int what, String url, Object tag, Exception exception, int responseCode, long networkMillis) {

            }
        }, true, true);
    }
}
