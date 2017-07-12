package com.ctj.oa.model.work.approve;

import android.text.TextUtils;

import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lewis on 2017/5/16.
 */

public class ApproveTemplate {
    private int id;
    private String approve_title;
    private String approve_desc;
    private int class_id;
    private String class_name;
    private List<Item> approve_ext;
    private int process_type;//0为自由流程，1为固定流程
    private ApproveUser approve_user;

    public class Item {
        public int type_field;
        public String title_field;
        public String[] tip_field;
        public int required_field;//是否必填 0不是必填  1是必填
        public String value;
        public int select_position = -1;
        public List<String> values = new ArrayList<>();
    }

    public class ApproveUser {
        public int type;//1为人员  2为职务
        public String inner;
        public String ct_inner;
        public String nickname;
    }

    public void makeData(Check check) {
        List<String> list = new ArrayList<>();
        for (Item item : approve_ext) {
            String x = "{\"" + item.title_field + "\":\"";
            if (item.type_field == 5) {
                String b = item.values.toString().replace("[", "").replace("]", "");
                x += b + "\"}";
                if (item.required_field == 1) {
                    if (item.values == null || item.values.size() == 0) {
                        check.Fail(item.title_field);
                        return;
                    }
                }
            } else if (item.type_field == 8) {
                String b = item.values.toString().replace("[", "").replace("]", "");
                x += b + "\"}";
                if (item.required_field == 1) {
                    if (item.values == null || item.values.size() != 2 || TextUtils.isEmpty(item.values.get(0)) || TextUtils.isEmpty(item.values.get(1))) {
                        check.Fail(item.title_field);
                        return;
                    }
                }
            } else {
                if (TextUtils.isEmpty(item.value)) {
                    x += "\"}";
                } else {
                    x += item.value + "\"}";
                }
                if (item.required_field == 1 && TextUtils.isEmpty(item.value)) {
                    check.Fail(item.title_field);
                    return;
                }
            }
            list.add(x);
        }
        check.Success(list.toString());
        Logger.e("hahah" + list.toString());
    }

    public interface Check {
        void Fail(String title);

        void Success(String date);
    }

    /*=====getsetter*/

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public List<Item> getApprove_ext() {
        return approve_ext;
    }

    public void setApprove_ext(List<Item> approve_ext) {
        this.approve_ext = approve_ext;
    }

    public int getProcess_type() {
        return process_type;
    }

    public void setProcess_type(int process_type) {
        this.process_type = process_type;
    }

    public ApproveUser getApprove_user() {
        return approve_user;
    }

    public void setApprove_user(ApproveUser approve_user) {
        this.approve_user = approve_user;
    }
}
