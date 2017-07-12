package com.ctj.oa.model.work.company;

import java.util.List;

/**
 * Created by lewis on 2017/6/2.
 */

public class ArticeList {
    private List<Artice> list;
    private int pageno;

    public List<Artice> getList() {
        return list;
    }

    public void setList(List<Artice> list) {
        this.list = list;
    }

    public int getPageno() {
        return pageno;
    }

    public void setPageno(int pageno) {
        this.pageno = pageno;
    }
}
