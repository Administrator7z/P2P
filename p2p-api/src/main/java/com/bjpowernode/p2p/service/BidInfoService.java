package com.bjpowernode.p2p.service;

import com.bjpowernode.p2p.model.ext.BidUserInfo;

import java.math.BigDecimal;
import java.util.List;

public interface BidInfoService {
    //总累计投资
    BigDecimal sumHistoryBidMoney();
    //某个产品的最近投资记录
    List<BidUserInfo> queryBidInfoByLoanId(Integer loanId);
}
