package com.bjpowernode.p2p.service;

import com.bjpowernode.p2p.model.FinanceAccount;

import java.math.BigDecimal;

public interface FinanceAccountService {

    //查余额
    FinanceAccount queryAccount(Integer uid);
}
