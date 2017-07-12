package com.ctj.oa.model.work.log;

import java.util.List;

/**
 * Created by lewis on 2017/5/22.
 */

public class LogCommentList {
    private int pageno;
    private List<LogComment> list;

    public int getPageno() {
        return pageno;
    }

    public void setPageno(int pageno) {
        this.pageno = pageno;
    }

    public List<LogComment> getList() {
        return list;
    }

    public void setList(List<LogComment> list) {
        this.list = list;
    }
    
}
