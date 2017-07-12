package com.ctj.oa.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.ctj.oa.Constants;
import com.ctj.oa.R;
import com.ctj.oa.mine.ForgetPasswordActivity;
import com.ctj.oa.model.Login;
import com.ctj.oa.model.event.EventRefresh;
import com.ctj.oa.model.netmodel.NetBaseBean;
import com.ctj.oa.net.CallServer;
import com.ctj.oa.net.HttpListenerCallback;
import com.ctj.oa.net.NetBaseRequest;
import com.lewis.utils.MD5Util;
import com.ctj.oa.utils.manager.UserManager;
import com.ctj.oa.widgets.dialog.LoadingDialog;
import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chatuidemo.DemoHelper;
import com.hyphenate.chatuidemo.db.DemoDBManager;
import com.lewis.utils.EditTextUitls;
import com.lewis.utils.T;

import org.greenrobot.eventbus.Subscribe;


/**
 * Created by lewis on 16/6/22.
 */
public class LoginActivity extends BaseActivity implements View.OnClickListener, HttpListenerCallback {
    @Override
    protected int getContentViewId() {
        return R.layout.activity_login;
    }

    private EditText edt_username, edt_password;
    private Button btn_login, btn_register, btn_forget_password;
    private LoadingDialog waitDialog;
    private ImageButton btn_delete;
    private static final String TAG = "LoginActivity";

    @Override
    protected void initView(Bundle savedInstanceState) {
        DemoHelper.getInstance().logout(true, null);
        waitDialog = new LoadingDialog(this);
        waitDialog.setCancelable(false);
        waitDialog.setCanceledOnTouchOutside(false);
        edt_username = (EditText) findViewById(R.id.edt_username);
        edt_password = (EditText) findViewById(R.id.edt_password);
        btn_login = (Button) findViewById(R.id.btn_login);
        btn_forget_password = (Button) findViewById(R.id.btn_forget_password);
        btn_register = (Button) findViewById(R.id.btn_register);
        btn_delete = (ImageButton) findViewById(R.id.btn_delete);
        EditTextUitls.bindCleanToView(edt_username, btn_delete);
        btn_login.setOnClickListener(this);
        btn_register.setOnClickListener(this);
        btn_forget_password.setOnClickListener(this);
        if (DemoHelper.getInstance().getCurrentUsernName() != null) {
            edt_username.setText(DemoHelper.getInstance().getCurrentUsernName());
        }
        edt_password.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE || ((event.getKeyCode() == KeyEvent.KEYCODE_ENTER) && (event.getAction() == KeyEvent.ACTION_DOWN))) {
                    login();
                    return true;
                } else {
                    return false;
                }
            }
        });
        edt_username.addTextChangedListener(textWatcher);
        edt_password.addTextChangedListener(textWatcher);
    }

    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            setBtnLogin();
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };


    private void setBtnLogin() {
        if (edt_username.getText().length() > 0 && edt_password.getText().length() >= 8 && edt_password.getText().length() <= 20) {
            btn_login.setEnabled(true);
        } else {
            btn_login.setEnabled(false);
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_login:
                login();
                break;
            case R.id.btn_register:
                startActivity(new Intent(this, RegisterActivity.class));
                break;
            case R.id.btn_forget_password:
                startActivity(new Intent(this, ForgetPasswordActivity.class));
                break;
            default:
        }
    }

    @Override
    public void onSucceed(int what, NetBaseBean netBaseBean) {
        if (netBaseBean.isSuccess()) {
            final Login login = netBaseBean.parseObject(Login.class);
            if (UserManager.saveLoginInfo(login)) {
                loginHuanXin();
            }


        } else {
            T.showShort(LoginActivity.this, netBaseBean.getMessage());
            waitDialog.dismiss();
        }
    }


    @Override
    public void onFailed(int what, String url, Object tag, Exception exception, int responseCode, long networkMillis) {
        waitDialog.dismiss();
    }

    private void login() {
        if (TextUtils.isEmpty(edt_username.getText().toString())) {
            T.showShort(this, "用户名或手机号不能为空!");
            return;
        }
        if (TextUtils.isEmpty(edt_username.getText().toString())) {
            T.showShort(this, "密码不能为空!");
            return;
        }
        if (edt_username.getText().length() < 8 || edt_username.getText().length() > 20) {
            T.showShort(this, "密码长度必须为8~20之间!");
            return;
        }
        NetBaseRequest loginRequest = new NetBaseRequest(Constants.LOGIN);
        loginRequest.add("phone", edt_username.getText().toString());
        loginRequest.add("password", MD5Util.MD5String(edt_password.getText().toString()));
        waitDialog.show();
        CallServer.getRequestInstance().add(this, 0x01, loginRequest, this, false, false);
    }

    /**
     * login
     */
    public void loginHuanXin() {
        String currentUsername = edt_username.getText().toString().trim();

        // After logout，the DemoDB may still be accessed due to async callback, so the DemoDB will be re-opened again.
        // close it before login to make sure DemoDB not overlap
        DemoDBManager.getInstance().closeDB();

        // reset current user name before login
        DemoHelper.getInstance().setCurrentUserName(currentUsername);

        // call login method
        Log.d(TAG, "EMClient.getInstance().login");
        EMClient.getInstance().login(UserManager.getId() + "", "123456", new EMCallBack() {

            @Override
            public void onSuccess() {
                Log.d(TAG, "login: onSuccess");


                // ** manually load all local groups and conversation
                EMClient.getInstance().groupManager().loadAllGroups();
                EMClient.getInstance().chatManager().loadAllConversations();

                // update current user's display name for APNs
                boolean updatenick = EMClient.getInstance().pushManager().updatePushNickname(UserManager.getNickname());
                if (!updatenick) {
                    Log.e("LoginActivity", "update current user nick fail");
                }

                if (!LoginActivity.this.isFinishing() && waitDialog.isShowing()) {
                    waitDialog.dismiss();
                }
                // get user's info (this should be get from App's server or 3rd party service)
                DemoHelper.getInstance().getUserProfileManager().asyncGetCurrentUserInfo();

                Intent intent = new Intent(LoginActivity.this,
                        MainActivity.class);
                startActivity(intent);

                finish();
            }

            @Override
            public void onProgress(int progress, String status) {
                Log.d(TAG, "login: onProgress");
            }

            @Override
            public void onError(final int code, final String message) {
                Log.d(TAG, "login: onError: " + code);
                runOnUiThread(new Runnable() {
                    public void run() {
                        waitDialog.dismiss();
                        showWrongDialog("环信登录失败   详情:" + message);
                        Toast.makeText(getApplicationContext(), getString(R.string.Login_failed) + message,
                                Toast.LENGTH_SHORT).show();
                        //测试
                        Intent intent = new Intent(LoginActivity.this,
                                MainActivity.class);
                        startActivity(intent);

                        finish();
                    }
                });
            }
        });
    }

    @Subscribe
    public void onEventMainThread(EventRefresh b) {
        if (b.getAction().equals(EventRefresh.ACTION_REGISTER) && b.getData() != null) {
            String[] strs = (String[]) b.getData();
            edt_username.setText(strs[0]);
            edt_password.setText(strs[1]);
            btn_login.performClick();
        }
    }

}
