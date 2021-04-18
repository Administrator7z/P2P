package com.bjpowernode.p2p.model.ext;

import com.bjpowernode.p2p.model.BidInfo;

import java.io.Serializable;

public class BidLoanInfo extends BidInfo implements Serializable {
    private static final long serialVersionUID = -4081933738775575599L;

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
