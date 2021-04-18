package com.bjpowernode.vo;

import java.io.Serializable;

public class InvestTopBean implements Serializable {
    private static final long serialVersionUID = 5503411582791558917L;
    private String phone;
    private Double money;

    public InvestTopBean() {
    }

    public InvestTopBean(String phone, Double money) {
        this.phone = phone;
        this.money = money;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Double getMoney() {
        return money;
    }

    public void setMoney(Double money) {
        this.money = money;
    }

    @Override
    public String toString() {
        return "InvestTopBean{" +
                "phone='" + phone + '\'' +
                ", money=" + money +
                '}';
    }
}
