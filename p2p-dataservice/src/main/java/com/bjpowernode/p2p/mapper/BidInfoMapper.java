package com.bjpowernode.p2p.mapper;

import com.bjpowernode.p2p.model.BidInfo;

import java.math.BigDecimal;

public interface BidInfoMapper {
    //计算累计投资金额
    BigDecimal selectSumBidMoney();
}