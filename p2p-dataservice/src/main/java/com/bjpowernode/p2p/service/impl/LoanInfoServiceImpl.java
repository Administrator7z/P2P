package com.bjpowernode.p2p.service.impl;

import com.bjpowernode.common.AppUtil;
import com.bjpowernode.p2p.mapper.LoanInfoMapper;
import com.bjpowernode.p2p.model.LoanInfo;
import com.bjpowernode.p2p.service.LoanInfoService;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@DubboService(interfaceClass = LoanInfoService.class,version = "1.0")
public class LoanInfoServiceImpl implements LoanInfoService {

    @Resource
    private LoanInfoMapper loanInfoMapper;

    @Override
    public BigDecimal avgHistoryRate() {
        return loanInfoMapper.selectAvgHistoryRate();
    }
    //分页 展示数据
    @Override
    public List<LoanInfo> queryPageByType(Integer type, Integer pageNo, Integer pageSize) {
        List<LoanInfo> loanInfoList = new ArrayList<>();
        if (AppUtil.validProductType(type)){
            pageNo = AppUtil.defaultPageNo(pageNo);
            pageSize = AppUtil.defaultPageSize(pageSize);
            int offset = ( pageNo - 1 ) * pageSize;
            loanInfoList = loanInfoMapper.selectPageByType(type,offset,pageSize);
        }
        return loanInfoList;
    }

    @Override
    public int queryRecordsByType(Integer type) {
        return loanInfoMapper.selectCountRecordsByType(type);
    }

}
