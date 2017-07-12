package com.ctj.oa.model.work.company;

/**
 * Created by lewis on 2017/6/1.
 */

public class Company {
    private int company_id;
    private int user_id;
    private int cid;
    private int check_state;//0未提交审核，1为审核中，2为通过，3为不通过
    private int prov_id;
    private int city_id;
    private int is_follow;
    private int company_dynamic_num;
    private int follow_num;
    private String company_name;
    private String company_url;
    private String company_logo;
    private String company_service;
    private String company_contact_name;
    private String company_contact_phone;
    private String prov_name;
    private String city_name;

    public int getCompany_id() {
        return company_id;
    }

    public void setCompany_id(int company_id) {
        this.company_id = company_id;
    }

    public int getProv_id() {
        return prov_id;
    }

    public void setProv_id(int prov_id) {
        this.prov_id = prov_id;
    }

    public int getCity_id() {
        return city_id;
    }

    public void setCity_id(int city_id) {
        this.city_id = city_id;
    }

    public int getIs_follow() {
        return is_follow;
    }

    public void setIs_follow(int is_follow) {
        this.is_follow = is_follow;
    }

    public String getCompany_name() {
        return company_name;
    }

    public void setCompany_name(String company_name) {
        this.company_name = company_name;
    }

    public String getCompany_logo() {
        return company_logo;
    }

    public void setCompany_logo(String company_logo) {
        this.company_logo = company_logo;
    }

    public String getCompany_service() {
        return company_service;
    }

    public void setCompany_service(String company_service) {
        this.company_service = company_service;
    }

    public String getProv_name() {
        return prov_name;
    }

    public void setProv_name(String prov_name) {
        this.prov_name = prov_name;
    }

    public String getCity_name() {
        return city_name;
    }

    public void setCity_name(String city_name) {
        this.city_name = city_name;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public int getCid() {
        return cid;
    }

    public void setCid(int cid) {
        this.cid = cid;
    }

    public int getCheck_state() {
        return check_state;
    }

    public void setCheck_state(int check_state) {
        this.check_state = check_state;
    }

    public String getCompany_url() {
        return company_url;
    }

    public void setCompany_url(String company_url) {
        this.company_url = company_url;
    }

    public String getCompany_contact_name() {
        return company_contact_name;
    }

    public void setCompany_contact_name(String company_contact_name) {
        this.company_contact_name = company_contact_name;
    }

    public String getCompany_contact_phone() {
        return company_contact_phone;
    }

    public void setCompany_contact_phone(String company_contact_phone) {
        this.company_contact_phone = company_contact_phone;
    }

    public int getCompany_dynamic_num() {
        return company_dynamic_num;
    }

    public void setCompany_dynamic_num(int company_dynamic_num) {
        this.company_dynamic_num = company_dynamic_num;
    }

    public int getFollow_num() {
        return follow_num;
    }

    public void setFollow_num(int follow_num) {
        this.follow_num = follow_num;
    }
}
