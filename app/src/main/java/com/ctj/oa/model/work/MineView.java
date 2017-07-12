package com.ctj.oa.model.work;


/**
 * Created by lewis on 16/7/18.
 */
public class MineView {
    private int id;
    private String tittle;
    private String info;
    private Class<?> activity;

    public MineView(int id, String tittle) {
        this.id = id;
        this.tittle = tittle;
    }

    public MineView(int id, String tittle, Class activity) {
        this.id = id;
        this.tittle = tittle;
        this.activity = activity;
    }

    public Class<?> getActivity() {
        return activity;
    }

    public void setActivity(Class<?> activity) {
        this.activity = activity;
    }

    public MineView(int id, String tittle, String info) {
        this.id = id;
        this.tittle = tittle;
        this.info = info;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTittle() {
        return tittle;
    }

    public void setTittle(String tittle) {
        this.tittle = tittle;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }
}
