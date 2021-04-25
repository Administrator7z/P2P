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

    //充值更新金额
    int updateByRecharge(@Param("uid") Integer uid, @Param("rechargeMoney") BigDecimal rechargeMoney);

    //收益返还，更新金额
    int updateByIncomeBack(@Param("uid") Integer uid,
                           @Param("bidMoney") BigDecimal bidMoney,
                           @Param("incomeMoney") BigDecimal incomeMoney);

}