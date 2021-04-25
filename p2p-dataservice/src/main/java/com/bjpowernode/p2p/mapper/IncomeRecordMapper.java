package com.bjpowernode.p2p.mapper;

import com.bjpowernode.p2p.model.FinanceAccount;
import com.bjpowernode.p2p.model.IncomeRecord;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface IncomeRecordMapper {

    //根据uid查询Account
    FinanceAccount selectByUid(@Param("uid") Integer uid);

    // 获取到期的收益记录
    List<IncomeRecord> selectExpireIncome();

    int insertSelective(IncomeRecord record);

    int updateByPrimaryKeySelective(IncomeRecord record);

}