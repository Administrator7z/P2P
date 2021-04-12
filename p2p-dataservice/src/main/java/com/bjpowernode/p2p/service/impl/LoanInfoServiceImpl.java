package com.bjpowernode.p2p.service.impl;

import com.bjpowernode.common.AppUtil;
import com.bjpowernode.contans.P2PRedis;
import com.bjpowernode.p2p.mapper.LoanInfoMapper;
import com.bjpowernode.p2p.model.LoanInfo;
import com.bjpowernode.p2p.service.LoanInfoService;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@DubboService(interfaceClass = LoanInfoService.class, version = "1.0")
public class LoanInfoServiceImpl implements LoanInfoService {

    @Resource
    private LoanInfoMapper loanInfoMapper;
    @Resource
    private RedisTemplate redisTemplate;


    //历史年化收益率
    @Override
    public BigDecimal avgHistoryRate() {
        ValueOperations vo = redisTemplate.opsForValue();
        BigDecimal avgHistoryRate = (BigDecimal) vo.get(P2PRedis.LOAN_HISTORY_RATE);
        if (avgHistoryRate == null) {
            synchronized (this) {
                avgHistoryRate = (BigDecimal) vo.get(P2PRedis.LOAN_HISTORY_RATE);
                if (avgHistoryRate == null) {
                    avgHistoryRate = loanInfoMapper.selectAvgHistoryRate();
                    vo.set(P2PRedis.LOAN_HISTORY_RATE, avgHistoryRate, 1, TimeUnit.HOURS);
                }
            }
        }
        return avgHistoryRate;
    }

    //分页 展示数据
    @Override
    public List<LoanInfo> queryPageByType(Integer type, Integer pageNo, Integer pageSize) {
        List<LoanInfo> loanInfoList = new ArrayList<>();
        if (AppUtil.validProductType(type)) {
            pageNo = AppUtil.defaultPageNo(pageNo);
            pageSize = AppUtil.defaultPageSize(pageSize);
            int offset = (pageNo - 1) * pageSize;
            loanInfoList = loanInfoMapper.selectPageByType(type, offset, pageSize);
        }

        return loanInfoList;
    }

    @Override
    public int queryRecordsByType(Integer type) {
        return loanInfoMapper.selectCountRecordsByType(type);
    }

    //按主键查询产品
    @Override
    public LoanInfo queryByLoanId(Integer loanId) {
        LoanInfo loanInfo = null;
        if (loanId != null || loanId.intValue() > 0) {
            loanInfo = loanInfoMapper.selectByPrimaryKey(loanId);
        }
        return loanInfo;
    }

}
