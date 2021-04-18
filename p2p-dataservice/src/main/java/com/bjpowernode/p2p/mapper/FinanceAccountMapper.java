package com.bjpowernode.p2p.mapper;

import com.bjpowernode.p2p.model.FinanceAccount;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;

public interface FinanceAccountMapper {
        //注册送888
        int insertSelective(FinanceAccount record);
    //查询余额
    FinanceAccount selectByUid(Integer uid);
    //查询余额 同时上锁
    FinanceAccount selectAccountForUpdate(Integer uid);
    //用户购买产品，扣除金额
    int updateByInvest(Integer uid, BigDecimal money);
}