package com.bjpowernode.p2p.mapper;

import com.bjpowernode.p2p.model.BidInfo;
import com.bjpowernode.p2p.model.ext.BidLoanInfo;
import com.bjpowernode.p2p.model.ext.BidUserInfo;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;

public interface BidInfoMapper {
    //计算累计投资金额
    BigDecimal selectSumBidMoney();
    //某个产品最近的投资记录
    List<BidUserInfo> selectBidByLoanId(Integer loanId);
    /**
     * 某个用户的分页投资记录
     */
    List<BidLoanInfo> selectBidLoanInfo(Integer uid, int offSet, Integer pageSize);
    //创建投资记录
    int insertSelective(BidInfo bidInfo);
    /**
     * 某产品的所有投资记录
     * @param loanId
     * @return
     */
    List<BidInfo> selectAllByLoanId(@Param("loanId") Integer loanId);
}