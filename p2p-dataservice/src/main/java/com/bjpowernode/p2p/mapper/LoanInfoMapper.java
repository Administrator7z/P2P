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
    //按主键查询商品
    LoanInfo selectByPrimaryKey(Integer id);
    //投资更新剩余可投资金额
    int updateLeftMoneyByInvest(Integer loanId, BigDecimal money);
    //满标，更新此产品的满标时间和 status
    int updateByPrimaryKeySelective(LoanInfo queryInfo);
    /**
     * 获取满标的产品
     */
    List<LoanInfo> selectManBiaoList();
}