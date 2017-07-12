package com.ctj.oa.model.work.notice;

/**
 * Created by lewis on 2017/6/5.
 */

public class Notice {
    private String notice_class_name;
    private String notice_title;
    private String notice_content;
    private int class_id;
    private int id;
    private long send_time;

    public String getNotice_class_name() {
        return notice_class_name;
    }

    public void setNotice_class_name(String notice_class_name) {
        this.notice_class_name = notice_class_name;
    }

    public String getNotice_title() {
        return notice_title;
    }

    public void setNotice_title(String notice_title) {
        this.notice_title = notice_title;
    }

    public String getNotice_content() {
        return notice_content;
    }

    public void setNotice_content(String notice_content) {
        this.notice_content = notice_content;
    }

    public int getClass_id() {
        return class_id;
    }

    public void setClass_id(int class_id) {
        this.class_id = class_id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getSend_time() {
        return send_time;
    }

    public void setSend_time(long send_time) {
        this.send_time = send_time;
    }
}
