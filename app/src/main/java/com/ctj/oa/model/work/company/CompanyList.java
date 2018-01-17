package com.ctj.oa.model.work.company;

import java.util.List;

/**
 * Created by lewis on 2017/6/2.
 */

public class CompanyList {
    private List<Company> list;
    public List<Company> company_list;
    private int pageno;

    public List<Company> getList() {
        return list;
    }

    public void setList(List<Company> list) {
        this.list = list;
    }

    public int getPageno() {
        return pageno;
    }

    public void setPageno(int pageno) {
        this.pageno = pageno;
    }
}
