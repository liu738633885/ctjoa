package com.ctj.oa.model;


import com.ctj.oa.model.UserInfo;

/**
 * Created by lewis on 16/6/22.
 */
public class Login {
    private String token;
    private UserInfo user_info;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public UserInfo getUser_info() {
        return user_info;
    }

    public void setUser_info(UserInfo user_info) {
        this.user_info = user_info;
    }
}
