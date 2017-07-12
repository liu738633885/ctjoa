package com.hyphenate.chatuidemo.parse;

import android.content.Context;

import com.ctj.oa.Constants;
import com.ctj.oa.MainApplication;
import com.ctj.oa.model.UserInfo;
import com.ctj.oa.model.netmodel.NetBaseBean;
import com.ctj.oa.net.CallServer;
import com.ctj.oa.net.HttpListenerCallback;
import com.ctj.oa.net.NetBaseRequest;
import com.ctj.oa.net.RequsetFactory;
import com.ctj.oa.utils.manager.UserManager;
import com.hyphenate.EMValueCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chatuidemo.DemoHelper;
import com.hyphenate.easeui.domain.EaseUser;
import com.hyphenate.easeui.utils.EaseCommonUtils;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;

public class ParseManager {

    private static final String TAG = ParseManager.class.getSimpleName();


    private Context appContext;
    private static ParseManager instance = new ParseManager();


    private ParseManager() {
    }

    public static ParseManager getInstance() {
        return instance;
    }

    public void onInit(Context context) {
        this.appContext = context.getApplicationContext();

    }

    /**
     * 获取联系人
     *
     * @param usernames
     * @param callback
     */
    int i = 0;

    public void getContactInfos(final List<String> usernames, final EMValueCallBack<List<EaseUser>> callback) {
        Logger.e("xxxx");
        if (usernames.size() < 1) {
            return;
        }
        i = 0;
        final List<EaseUser> list = new ArrayList<>();
        EMValueCallBack<EaseUser> callBack = new EMValueCallBack<EaseUser>() {
            @Override
            public void onSuccess(EaseUser easeUser) {
                EaseCommonUtils.setUserInitialLetter(easeUser);
                list.add(easeUser);
                if (i >= usernames.size() - 1) {
                    callback.onSuccess(list);
                    return;
                }
                i++;
                asyncGetUserInfo(usernames.get(i), this);
            }

            @Override
            public void onError(int ii, String s) {
                callback.onSuccess(list);
            }
        };
        asyncGetUserInfo(usernames.get(i), callBack);
    }


    /**
     * 获取自己的用户
     *
     * @param callback
     */
    public void asyncGetCurrentUserInfo(final EMValueCallBack<EaseUser> callback) {
        //Logger.e("xxxx");
        final String username = EMClient.getInstance().getCurrentUser();
        asyncGetUserInfo(username, new EMValueCallBack<EaseUser>() {

            @Override
            public void onSuccess(EaseUser value) {
                callback.onSuccess(value);
            }

            @Override
            public void onError(int error, String errorMsg) {
                if (UserManager.isLogin()) {
                    EaseUser easeUser = new EaseUser(UserManager.getId());
                    easeUser.setAvatar(UserManager.getPortrait());
                    easeUser.setNickname(UserManager.getNickname());
                    callback.onSuccess(easeUser);
                } else {
                    callback.onError(error, errorMsg);
                }
            }
        });
    }

    /**
     * 获取单独的用户
     *
     * @param callback
     */
    public void asyncGetUserInfo(final String username, final EMValueCallBack<EaseUser> callback) {
        //Logger.e("xxxx");
        Logger.e("调用asyncGetUserInfo");
        NetBaseRequest netBaseRequest = RequsetFactory.creatBaseRequest(Constants.GET_USER_INFO);
        netBaseRequest.add("get_user_id", username);
        netBaseRequest.add("type", 1);
        if (username.equals(Constants.ADMIN_ID)) {
            EaseUser user = DemoHelper.getInstance().getContactList().get(username);
            if (user == null) {
                user = new EaseUser(username);
            }
            user.setNickname("消息通知");
            //user.setAvatar(info.getPortrait());
            callback.onSuccess(user);
            return;
        }
        CallServer.getRequestInstance().add(MainApplication.getInstance(), 0x01, netBaseRequest, new HttpListenerCallback() {
            @Override
            public void onSucceed(int what, NetBaseBean netBaseBean) {
                if (netBaseBean.isSuccess()) {
                    UserInfo info = netBaseBean.parseObject(UserInfo.class);
                    if (callback != null) {
                        EaseUser user = DemoHelper.getInstance().getContactList().get(username);
                        if (user == null) {
                            user = new EaseUser(username);
                        }
                        user.setNickname(info.getNickname());
                        user.setAvatar(info.getPortrait());
                        callback.onSuccess(user);
                        Logger.d("从网络获取成功" + info.toString());
                    }

                } else {
                    callback.onError(netBaseBean.getStatus(), netBaseBean.getMessage());
                }
            }

            @Override
            public void onFailed(int what, String url, Object tag, Exception exception, int responseCode, long networkMillis) {
                callback.onError(responseCode, exception.getMessage());
            }
        }, true, false);
    }
}
