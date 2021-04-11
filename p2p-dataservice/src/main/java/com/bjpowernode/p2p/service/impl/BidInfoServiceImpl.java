package com.bjpowernode.p2p.service.impl;

import com.bjpowernode.p2p.mapper.BidInfoMapper;
import com.bjpowernode.p2p.service.BidInfoService;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;
import java.math.BigDecimal;

@DubboService(interfaceClass = BidInfoService.class,version = "1.0")
public class BidInfoServiceImpl implements BidInfoService{

    @Resource
    private BidInfoMapper bidInfoMapper;
    @Override
    public BigDecimal sumHistoryBidMoney() {
        return bidInfoMapper.selectSumBidMoney();
    }
}
