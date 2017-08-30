package com.ctj.oa.model.work.memo;

/**
 * Created by lewis on 2017/6/4.
 */

public class Memo {
    private int id;
    private int comment_count;
    private long add_time;
    private long is_tips;
    private String nickname;
    private String portrait;
    private String content;
    private int is_stop;//1时可关闭提醒


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getComment_count() {
        return comment_count;
    }

    public void setComment_count(int comment_count) {
        this.comment_count = comment_count;
    }

    public long getAdd_time() {
        return add_time;
    }

    public void setAdd_time(long add_time) {
        this.add_time = add_time;
    }

    public long getIs_tips() {
        return is_tips;
    }

    public void setIs_tips(long is_tips) {
        this.is_tips = is_tips;
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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getIs_stop() {
        return is_stop;
    }

    public void setIs_stop(int is_stop) {
        this.is_stop = is_stop;
    }
}
