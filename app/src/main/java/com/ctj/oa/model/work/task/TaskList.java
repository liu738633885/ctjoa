package com.ctj.oa.model.work.task;

import com.ctj.oa.model.work.memo.Memo;

import java.util.List;

/**
 * Created by lewis on 2017/5/22.
 */

public class TaskList {
    private int pageno;
    private List<Task> list;

    public int getPageno() {
        return pageno;
    }

    public void setPageno(int pageno) {
        this.pageno = pageno;
    }

    public List<Task> getList() {
        return list;
    }

    public void setList(List<Task> list) {
        this.list = list;
    }
    
}
