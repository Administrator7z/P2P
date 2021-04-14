package com.bjpowernode.p2p.mapper;

import com.bjpowernode.p2p.model.FinanceAccount;
import org.apache.ibatis.annotations.Param;

public interface FinanceAccountMapper {
        //注册送888
        int insertSelective(FinanceAccount record);
}