package com.ctj.oa.model.work.notice;

import com.ctj.oa.model.work.log.Log;

import java.util.List;

/**
 * Created by lewis on 2017/5/22.
 */

public class NoticeList {
    private int pageno;
    private List<Notice> list;

    public int getPageno() {
        return pageno;
    }

    public void setPageno(int pageno) {
        this.pageno = pageno;
    }

    public List<Notice> getList() {
        return list;
    }

    public void setList(List<Notice> list) {
        this.list = list;
    }
    
}
