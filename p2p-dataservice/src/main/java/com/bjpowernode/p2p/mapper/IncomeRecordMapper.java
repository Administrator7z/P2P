package com.bjpowernode.p2p.mapper;

import com.bjpowernode.p2p.model.FinanceAccount;
import com.bjpowernode.p2p.model.IncomeRecord;
import org.apache.ibatis.annotations.Param;

public interface IncomeRecordMapper {

    //根据uid查询Account
    FinanceAccount selectByUid(@Param("uid") Integer uid);

}