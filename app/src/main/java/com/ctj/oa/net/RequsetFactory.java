package com.ctj.oa.net;


import com.ctj.oa.utils.manager.UserManager;


/**
 * Created by lewis on 16/6/23.
 */
public class RequsetFactory {
    //目前所有的 url 都带 token
    //都带 city

    /**
     * @param url 默认的创建 NetBaseRequest
     *            验证登录
     * @return
     */
    public static NetBaseRequest creatBaseRequest(String url) {
        NetBaseRequest netBaseRequest = new NetBaseRequest(url);
        if (UserManager.isLogin()) {
            netBaseRequest.add("user_id", UserManager.getId());
            netBaseRequest.add("token", UserManager.getToken());
            netBaseRequest.add("company_id", UserManager.getCompanyId());
        }
        return netBaseRequest;
    }

    public static NetBaseRequest creatNoUidRequest(String url) {
        NetBaseRequest netBaseRequest = new NetBaseRequest(url);
        return netBaseRequest;
    }

}
