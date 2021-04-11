package com.bjpowernode.p2p.mapper;



import com.bjpowernode.p2p.model.LoanInfo;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;

public interface LoanInfoMapper {
    //统计年利率
    BigDecimal selectAvgHistoryRate();
    //显示各个产品
    List<LoanInfo> selectPageByType(Integer type,Integer offset,Integer rows);
    //查类型产品的总记录数
    int selectCountRecordsByType(Integer type);
}