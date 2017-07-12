package com.ctj.oa.model.work.log;

import java.util.List;

/**
 * Created by lewis on 2017/6/4.
 */

public class LogDetail {
    private List<Item> content;
    private int id;
    private int comment_count;
    private String title;
    private String bg_color;
    private String nickname;
    private long add_time;

    public class Item {
        public String title_field;
        public String value_field;
    }

    public List<Item> getContent() {
        return content;
    }

    public void setContent(List<Item> content) {
        this.content = content;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public long getAdd_time() {
        return add_time;
    }

    public void setAdd_time(long add_time) {
        this.add_time = add_time;
    }

    public int getComment_count() {
        return comment_count;
    }

    public void setComment_count(int comment_count) {
        this.comment_count = comment_count;
    }

    public String getBg_color() {
        return bg_color;
    }

    public void setBg_color(String bg_color) {
        this.bg_color = bg_color;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
}
