package com.bjpowernode.p2p.model.ext;

import com.bjpowernode.p2p.model.BidInfo;

import java.io.Serializable;

public class BidUserInfo extends BidInfo implements Serializable {
    private static final long serialVersionUID = 2637707700198020268L;
    private String phone;


    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
