package com.bjpowernode.p2p.service;

import com.bjpowernode.p2p.model.RechargeRecord;

import java.util.List;

//充值的
public interface RechargeService {

    //用户充值记录
    List<RechargeRecord> queryPageRechargeInfo(Integer uid, Integer pageNo, Integer pageSize);
    //创建充值记录
    int addRechargeRecord(RechargeRecord rr);
}
