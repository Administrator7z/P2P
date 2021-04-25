package com.bjpowernode.p2p.service;

import com.bjpowernode.p2p.model.RechargeRecord;

import java.util.List;

//充值的
public interface RechargeService {

    //用户充值记录
    List<RechargeRecord> queryPageRechargeInfo(Integer uid, Integer pageNo, Integer pageSize);
    //创建充值记录
    int addRechargeRecord(RechargeRecord rr);
    /**
     * @param outTradeNo   商家订单号
     * @param totalAmount  支付宝发送过来的支付金额
     * @param tradeStatus  支付状态
     * @param appId        app_id
     * @return
     */
    //支付宝的notify
    boolean aliayNotifyByRecharge(String outTradeNo,
                                  String totalAmount,
                                  String tradeStatus
    );


    /**
     * 商家调用支付宝的查询接口，根据查询充值结果，处理充值记录
     * @param outTradeNo
     * @param totalAmount
     * @param tradeStatus
     * @return
     */
    boolean  alipayQueryByRecharge(String outTradeNo,
                                   String totalAmount,
                                   String tradeStatus);
}

