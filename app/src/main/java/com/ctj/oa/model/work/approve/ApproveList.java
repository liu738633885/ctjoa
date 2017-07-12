package com.ctj.oa.model.work.approve;

import java.util.List;

/**
 * Created by lewis on 2017/5/22.
 */

public class ApproveList {
    private int pageno;
    private List<Approve> list;

    public int getPageno() {
        return pageno;
    }

    public void setPageno(int pageno) {
        this.pageno = pageno;
    }

    public List<Approve> getList() {
        return list;
    }

    public void setList(List<Approve> list) {
        this.list = list;
    }
    
}
