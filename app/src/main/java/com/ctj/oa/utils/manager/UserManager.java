package com.ctj.oa.utils.manager;

import android.content.Context;
import android.content.Intent;

import com.ctj.oa.activity.LoginActivity;
import com.ctj.oa.model.Login;
import com.ctj.oa.model.UserInfo;
import com.ctj.oa.model.event.EventRefresh;
import com.ctj.oa.utils.SPUtils;
import com.hyphenate.chat.EMClient;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by lewis on 16/6/22.
 */
public class UserManager {
    public static final String KEY_TOKEN = "token";
    public static final String KEY_ISLOGIN = "isLogin";
    public static final String KEY_ID = "id";
    public static final String KEY_NICKNAME = "nickname";
    public static final String KEY_SEX = "sex";
    public static final String KEY_PORTRAIT = "portrait";
    public static final String KEY_COMPANY_ID = "company_id";
    public static final String KEY_COMPANY_NAME = "company_name";
    public static final String KEY_POST_NAME = "post_name";
    public static final String KEY_IS_ADMIN = "is_admin";


    public static boolean isLogin() {
        return (boolean) SPUtils.getUserInstance().get(KEY_ISLOGIN, false);
    }

    public static boolean isLogin(Context context) {
        if (!isLogin()) {
            context.startActivity(new Intent(context, LoginActivity.class));
        }
        return isLogin();
    }

    public static boolean saveLoginInfo(Login login) {
        if (login == null) {
            return false;
        } else {
            SPUtils.getUserInstance().put(KEY_TOKEN, login.getToken());
            SPUtils.getUserInstance().put(KEY_ISLOGIN, true);
            return saveUserInfo(login.getUser_info());
        }
    }

    public static boolean saveUserInfo(UserInfo userInfo) {
        if (userInfo == null) {
            return false;
        } else {
            SPUtils.getUserInstance().put(KEY_ID, userInfo.getId());
            SPUtils.getUserInstance().put(KEY_NICKNAME, userInfo.getNickname());
            SPUtils.getUserInstance().put(KEY_SEX, userInfo.getSex());
            SPUtils.getUserInstance().put(KEY_PORTRAIT, userInfo.getPortrait());
            SPUtils.getUserInstance().put(KEY_COMPANY_ID, userInfo.getCompany_id());
            SPUtils.getUserInstance().put(KEY_COMPANY_NAME, userInfo.getCompany_name());
            SPUtils.getUserInstance().put(KEY_POST_NAME, userInfo.getPost_name());
            SPUtils.getUserInstance().put(KEY_IS_ADMIN, userInfo.getIs_admin());
            return true;
        }
    }


    public static String getToken() {
        return (String) SPUtils.getUserInstance().get(KEY_TOKEN, "");
    }

    public static String getToken(Context context) {
        if (isLogin(context)) {
            return getToken();
        } else {
            return "";
        }

    }

    public static String getId() {
        return (int) SPUtils.getUserInstance().get(KEY_ID, 0) + "";
    }

    public static String getPortrait() {
        return (String) SPUtils.getUserInstance().get(KEY_PORTRAIT, "");
    }

    public static String getNickname() {
        return (String) SPUtils.getUserInstance().get(KEY_NICKNAME, "");
    }

    public static String getPostName() {
        return (String) SPUtils.getUserInstance().get(KEY_POST_NAME, "");
    }

    public static String getCompanyName() {
        return (String) SPUtils.getUserInstance().get(KEY_COMPANY_NAME, "");
    }

    public static int getCompanyId() {
        return (int) SPUtils.getUserInstance().get(KEY_COMPANY_ID, 0);
    }

    public static boolean isAdmin() {
        int isadmin = (int) SPUtils.getUserInstance().get(KEY_IS_ADMIN, 0);
        return isadmin == 1;
    }


    public static void logout() {
        logoutOA();
        EMClient.getInstance().logout(true);
    }

    public static boolean logoutOA() {
        if (isLogin()) {
            SPUtils.getUserInstance().clear();
            EventBus.getDefault().post(new EventRefresh(EventRefresh.ACTION_LOGIN));
            return true;
        } else {
            return false;
        }
    }


}
