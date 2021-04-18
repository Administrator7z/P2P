package com.bjpowernode.p2p.service;

import com.bjpowernode.p2p.model.ext.BidLoanInfo;
import com.bjpowernode.p2p.model.ext.BidUserInfo;

import java.math.BigDecimal;
import java.util.List;

public interface BidInfoService {
    //总累计投资
    BigDecimal sumHistoryBidMoney();
    //某个产品的最近投资记录
    List<BidUserInfo> queryBidInfoByLoanId(Integer loanId);
    /**
     * 某个用户的分页投资记录
     */
    List<BidLoanInfo> queryPageBidInfo(Integer uid, Integer pageNo, Integer pageSize);


    /**
     * 投资
     */
    boolean invest(Integer loanId,Integer uid,BigDecimal money);
}
