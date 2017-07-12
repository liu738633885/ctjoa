package com.ctj.oa.model.attend;

/**
 * Created by lewis on 2017/5/15.
 */

public class SignRecord {
    private String address;
    private String check_address;
    private Long add_time;
    private Long check_time;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCheck_address() {
        return check_address;
    }

    public void setCheck_address(String check_address) {
        this.check_address = check_address;
    }

    public Long getAdd_time() {
        return add_time;
    }

    public void setAdd_time(Long add_time) {
        this.add_time = add_time;
    }

    public Long getCheck_time() {
        return check_time;
    }

    public void setCheck_time(Long check_time) {
        this.check_time = check_time;
    }
}
