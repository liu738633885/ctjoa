package com.ctj.oa.model.work.task;

import com.ctj.oa.model.UserInfo;

import java.util.List;

/**
 * Created by lewis on 2017/6/28.
 */

public class Task {
    private int id;
    private int company_id;
    private int tight;
    private int create_user_id;
    private int principal_user_id;
    private String principal_user_name;
    private long end_time;
    private long add_time;
    private int status;
    private int task_item;
    private String update_time;
    private String participant_user_id;
    private String participant_user_name;
    private String create_user_name;
    private String desc;
    private String title;
    private String principal_user_nickname;
    private int is_receipt;
    private int is_discuss;
    private int is_over;
    private int is_confirm;
    private int is_cancel;
    private int is_recover;
    private List<UserInfo> may_discuss_list;

    public int getIs_receipt() {
        return is_receipt;
    }

    public void setIs_receipt(int is_receipt) {
        this.is_receipt = is_receipt;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCompany_id() {
        return company_id;
    }

    public void setCompany_id(int company_id) {
        this.company_id = company_id;
    }

    public int getTight() {
        return tight;
    }

    public void setTight(int tight) {
        this.tight = tight;
    }

    public int getCreate_user_id() {
        return create_user_id;
    }

    public void setCreate_user_id(int create_user_id) {
        this.create_user_id = create_user_id;
    }

    public int getPrincipal_user_id() {
        return principal_user_id;
    }

    public void setPrincipal_user_id(int principal_user_id) {
        this.principal_user_id = principal_user_id;
    }

    public long getEnd_time() {
        return end_time;
    }

    public void setEnd_time(long end_time) {
        this.end_time = end_time;
    }

    public long getAdd_time() {
        return add_time;
    }

    public void setAdd_time(long add_time) {
        this.add_time = add_time;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getUpdate_time() {
        return update_time;
    }

    public void setUpdate_time(String update_time) {
        this.update_time = update_time;
    }

    public String getParticipant_user_id() {
        return participant_user_id;
    }

    public void setParticipant_user_id(String participant_user_id) {
        this.participant_user_id = participant_user_id;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPrincipal_user_name() {
        return principal_user_name;
    }

    public void setPrincipal_user_name(String principal_user_name) {
        this.principal_user_name = principal_user_name;
    }

    public String getParticipant_user_name() {
        return participant_user_name;
    }

    public void setParticipant_user_name(String participant_user_name) {
        this.participant_user_name = participant_user_name;
    }

    public String getCreate_user_name() {
        return create_user_name;
    }

    public void setCreate_user_name(String create_user_name) {
        this.create_user_name = create_user_name;
    }

    public String getPrincipal_user_nickname() {
        return principal_user_nickname;
    }

    public void setPrincipal_user_nickname(String principal_user_nickname) {
        this.principal_user_nickname = principal_user_nickname;
    }

    public int getTask_item() {
        return task_item;
    }

    public void setTask_item(int task_item) {
        this.task_item = task_item;
    }

    public int getIs_discuss() {
        return is_discuss;
    }

    public void setIs_discuss(int is_discuss) {
        this.is_discuss = is_discuss;
    }

    public int getIs_over() {
        return is_over;
    }

    public void setIs_over(int is_over) {
        this.is_over = is_over;
    }

    public int getIs_confirm() {
        return is_confirm;
    }

    public void setIs_confirm(int is_confirm) {
        this.is_confirm = is_confirm;
    }

    public int getIs_cancel() {
        return is_cancel;
    }

    public void setIs_cancel(int is_cancel) {
        this.is_cancel = is_cancel;
    }

    public int getIs_recover() {
        return is_recover;
    }

    public void setIs_recover(int is_recover) {
        this.is_recover = is_recover;
    }

    public List<UserInfo> getMay_discuss_list() {
        return may_discuss_list;
    }

    public void setMay_discuss_list(List<UserInfo> may_discuss_list) {
        this.may_discuss_list = may_discuss_list;
    }
}
