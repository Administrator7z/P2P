package com.bjpowernode.p2p.mapper;

import com.bjpowernode.p2p.model.BidInfo;
import com.bjpowernode.p2p.model.ext.BidUserInfo;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;

public interface BidInfoMapper {
    //计算累计投资金额
    BigDecimal selectSumBidMoney();
    //某个产品最近的投资记录
    List<BidUserInfo> selectBidByLoanId(Integer loanId);
}