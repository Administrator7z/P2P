package com.bjpowernode.p2p.service.impl;

import com.bjpowernode.p2p.mapper.FinanceAccountMapper;
import com.bjpowernode.p2p.model.FinanceAccount;
import com.bjpowernode.p2p.service.FinanceAccountService;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;

@DubboService(interfaceClass = FinanceAccountService.class,version = "1.0")
public class FinanceAccountServiceImpl implements FinanceAccountService{


    @Resource
    private FinanceAccountMapper financeAccountMapper;


    @Override
    public FinanceAccount queryAccount(Integer uid) {
        FinanceAccount account = null;
        if( uid != null && uid.intValue() > 0 ){
            account =  financeAccountMapper.selectByUid(uid);
        }
        return account;
    }
}
