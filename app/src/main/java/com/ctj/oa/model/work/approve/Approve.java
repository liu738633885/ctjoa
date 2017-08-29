package com.ctj.oa.model.work.approve;

import android.text.TextUtils;

import com.ctj.oa.model.UserInfo;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Created by lewis on 2017/5/15.
 */

public class Approve implements Serializable {
    private int id;
    private int user_id;
    private int company_id;
    private String bg_color;
    private long add_time;
    private int process_type;//0为自由流程，1为固定流程
    //class
    private int class_id;
    private String class_name;
    //approve
    private int approve_id;
    private String approve_title;
    private int approve_state;
    private int approve_node;
    //applay
    private String approve_desc;
    private int applay_state;
    private int applay_user_id;
    private String nickname;
    private String portrait;

    //detail
    private int revoke_state; //1可撤回  0不可撤回
    private List<Approve> approve_list;
    private String dept_name;
    private List<UserInfo> cc_user_list;
    private List<Field> field_ext;
    private List<UserInfo> approve_user;
    private UserInfo apply_user_info;
    private int ct_id;

    public class Field implements Serializable{
        public String title_field;
        public String value_field;
    }



    /*==================*/

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public int getCompany_id() {
        return company_id;
    }

    public void setCompany_id(int company_id) {
        this.company_id = company_id;
    }

    public String getBg_color() {
        if (TextUtils.isEmpty(bg_color)) {
            bg_color = "#3685e8";
        }
        return bg_color;
    }

    public void setBg_color(String bg_color) {
        this.bg_color = bg_color;
    }

    public long getAdd_time() {
        return add_time;
    }

    public void setAdd_time(long add_time) {
        this.add_time = add_time;
    }

    public int getClass_id() {
        return class_id;
    }

    public void setClass_id(int class_id) {
        this.class_id = class_id;
    }

    public String getClass_name() {
        return class_name;
    }

    public void setClass_name(String class_name) {
        this.class_name = class_name;
    }

    public int getApprove_id() {
        return approve_id;
    }

    public void setApprove_id(int approve_id) {
        this.approve_id = approve_id;
    }

    public String getApprove_title() {
        return approve_title;
    }

    public void setApprove_title(String approve_title) {
        this.approve_title = approve_title;
    }

    public String getApprove_desc() {
        return approve_desc;
    }

    public void setApprove_desc(String approve_desc) {
        this.approve_desc = approve_desc;
    }

    public int getApplay_state() {
        return applay_state;
    }

    public void setApplay_state(int applay_state) {
        this.applay_state = applay_state;
    }

    public int getApplay_user_id() {
        return applay_user_id;
    }

    public void setApplay_user_id(int applay_user_id) {
        this.applay_user_id = applay_user_id;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getPortrait() {
        return portrait;
    }

    public void setPortrait(String portrait) {
        this.portrait = portrait;
    }

    public List<Approve> getApprove_list() {
        return approve_list;
    }

    public void setApprove_list(List<Approve> approve_list) {
        this.approve_list = approve_list;
    }

    public String getDept_name() {
        return dept_name;
    }

    public void setDept_name(String dept_name) {
        this.dept_name = dept_name;
    }

    public List<UserInfo> getCc_user_list() {
        return cc_user_list;
    }

    public void setCc_user_list(List<UserInfo> cc_user_list) {
        this.cc_user_list = cc_user_list;
    }

    public int getRevoke_state() {
        return revoke_state;
    }

    public void setRevoke_state(int revoke_state) {
        this.revoke_state = revoke_state;
    }

    public List<Field> getField_ext() {
        return field_ext;
    }

    public void setField_ext(List<Field> field_ext) {
        this.field_ext = field_ext;
    }

    public int getProcess_type() {
        return process_type;
    }

    public void setProcess_type(int process_type) {
        this.process_type = process_type;
    }

    public List<UserInfo> getApprove_user() {
        return approve_user;
    }

    public void setApprove_user(List<UserInfo> approve_user) {
        this.approve_user = approve_user;
    }

    public int getApprove_state() {
        return approve_state;
    }

    public void setApprove_state(int approve_state) {
        this.approve_state = approve_state;
    }

    public int getApprove_node() {
        return approve_node;
    }

    public void setApprove_node(int approve_node) {
        this.approve_node = approve_node;
    }

    public UserInfo getApply_user_info() {
        return apply_user_info;
    }

    public void setApply_user_info(UserInfo apply_user_info) {
        this.apply_user_info = apply_user_info;
    }

    public int getCt_id() {
        return ct_id;
    }

    public void setCt_id(int ct_id) {
        this.ct_id = ct_id;
    }
}
