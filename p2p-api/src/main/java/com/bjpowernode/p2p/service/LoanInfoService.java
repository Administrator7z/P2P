package com.bjpowernode.p2p.service;

import com.bjpowernode.p2p.model.LoanInfo;

import java.math.BigDecimal;
import java.util.List;

public interface LoanInfoService {

    //统计年利率
    BigDecimal avgHistoryRate();
    //查询产品
    List<LoanInfo> queryPageByType(Integer type,Integer pageNo,Integer pageSize);
    //查询某个产品数量
    int queryRecordsByType(Integer type);
    //按id查询单个产品
    LoanInfo queryByLoanId(Integer loanId);
}
