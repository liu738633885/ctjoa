package com.ctj.oa.model.work.log;

import com.ctj.oa.model.work.approve.Approve;

import java.util.List;

/**
 * Created by lewis on 2017/5/22.
 */

public class LogList {
    private int pageno;
    private List<Log> list;

    public int getPageno() {
        return pageno;
    }

    public void setPageno(int pageno) {
        this.pageno = pageno;
    }

    public List<Log> getList() {
        return list;
    }

    public void setList(List<Log> list) {
        this.list = list;
    }
    
}
