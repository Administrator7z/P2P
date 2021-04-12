package com.bjpowernode.p2p.service.impl;

import com.bjpowernode.contans.P2PRedis;
import com.bjpowernode.p2p.mapper.BidInfoMapper;
import com.bjpowernode.p2p.model.ext.BidUserInfo;
import com.bjpowernode.p2p.service.BidInfoService;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@DubboService(interfaceClass = BidInfoService.class, version = "1.0")
public class BidInfoServiceImpl implements BidInfoService {

    @Resource
    private BidInfoMapper bidInfoMapper;
    @Resource
    private RedisTemplate redisTemplate;

    //计算总投资金额
    @Override
    public BigDecimal sumHistoryBidMoney() {
        ValueOperations vo = redisTemplate.opsForValue();
        BigDecimal sumBidMoney = (BigDecimal) vo.get(P2PRedis.BID_HISTORY_SUM);
        if (sumBidMoney == null){
            synchronized (this) {
                sumBidMoney = (BigDecimal) vo.get(P2PRedis.BID_HISTORY_SUM);
                if (sumBidMoney == null){
                    sumBidMoney = bidInfoMapper.selectSumBidMoney();
                    vo.set(P2PRedis.BID_HISTORY_SUM,sumBidMoney,1, TimeUnit.HOURS);
                }
            }
        }
        return sumBidMoney;
    }

    //查询某个产品的最近投资
    @Override
    public List<BidUserInfo> queryBidInfoByLoanId(Integer loanId) {
        List<BidUserInfo> infoList = new ArrayList<>();
        if (loanId != null || loanId.intValue() > 0) {
            infoList = bidInfoMapper.selectBidByLoanId(loanId);
        }
        return infoList;
    }
}
