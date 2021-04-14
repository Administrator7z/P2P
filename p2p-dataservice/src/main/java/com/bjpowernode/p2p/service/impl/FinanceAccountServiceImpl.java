package com.bjpowernode.p2p.service.impl;

import com.bjpowernode.p2p.model.FinanceAccount;
import com.bjpowernode.p2p.service.FinanceAccountService;
import org.apache.dubbo.config.annotation.DubboService;

@DubboService(interfaceClass = FinanceAccountService.class,version = "1.0")
public class FinanceAccountServiceImpl implements FinanceAccountService{




    @Override
    public FinanceAccount queryAccount(Integer uid) {
        return null;
    }
}
