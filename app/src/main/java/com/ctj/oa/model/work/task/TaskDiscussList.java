package com.ctj.oa.model.work.task;

import java.util.List;

/**
 * Created by lewis on 2017/5/22.
 */

public class TaskDiscussList {
    private int pageno;
    private List<TaskDiscuss> list;

    public int getPageno() {
        return pageno;
    }

    public void setPageno(int pageno) {
        this.pageno = pageno;
    }

    public List<TaskDiscuss> getList() {
        return list;
    }

    public void setList(List<TaskDiscuss> list) {
        this.list = list;
    }
    
}
