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
import com.lewis.utils.MD5Util;
import com.ctj.oa.widgets.TitleBar;

public class EditPasswordActivity extends BaseActivity {
    private TitleBar titleBar;
    private EditText edt_old_password, edt_new_password, edt_new_password2;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_edit_password;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        titleBar = (TitleBar) findViewById(R.id.titleBar);
        titleBar.getRightGroup().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editPassword();
            }
        });
        edt_old_password = (EditText) findViewById(R.id.edt_old_password);
        edt_new_password = (EditText) findViewById(R.id.edt_new_password);
        edt_new_password2 = (EditText) findViewById(R.id.edt_new_password2);
    }

    private void editPassword() {
        NetBaseRequest request = RequsetFactory.creatBaseRequest(Constants.EDIT_PASSWORD);
        if (TextUtils.isEmpty(edt_old_password.getText().toString())) {
            Toast(" 请先输入旧密码");
            return;
        }
        if (edt_new_password.getText().toString().length() < 8) {
            Toast("新密码最少8位");
            return;
        }

        if (!edt_new_password2.getText().toString().equals(edt_new_password.getText().toString())) {
            Toast("密码不一致");
            return;
        }
        request.add("old_password", MD5Util.MD5String(edt_old_password.getText().toString()));
        request.add("new_password", MD5Util.MD5String(edt_new_password.getText().toString()));
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
