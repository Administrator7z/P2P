package com.bjpowernode.p2p.service.impl;

import com.bjpowernode.common.AppUtil;
import com.bjpowernode.contans.P2PConstants;
import com.bjpowernode.contans.P2PRedis;
import com.bjpowernode.p2p.mapper.BidInfoMapper;
import com.bjpowernode.p2p.mapper.FinanceAccountMapper;
import com.bjpowernode.p2p.mapper.LoanInfoMapper;
import com.bjpowernode.p2p.model.BidInfo;
import com.bjpowernode.p2p.model.FinanceAccount;
import com.bjpowernode.p2p.model.LoanInfo;
import com.bjpowernode.p2p.model.ext.BidLoanInfo;
import com.bjpowernode.p2p.model.ext.BidUserInfo;
import com.bjpowernode.p2p.service.BidInfoService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

@DubboService(interfaceClass = BidInfoService.class, version = "1.0")
public class BidInfoServiceImpl implements BidInfoService {

    @Resource
    private BidInfoMapper bidInfoMapper;
    @Resource
    private RedisTemplate redisTemplate;
    @Resource
    private FinanceAccountMapper financeAccountMapper;
    @Resource
    private LoanInfoMapper loanInfoMapper;

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
    /**
     * 某个用户的分页投资记录
     */
    @Override
    public List<BidLoanInfo> queryPageBidInfo(Integer uid,Integer pageNo,Integer pageSize) {
        List<BidLoanInfo> list = new ArrayList<>();
        if(uid != null && uid.intValue() > 0 ){
            pageNo = AppUtil.defaultPageNo(pageNo);
            pageSize = AppUtil.defaultPageSize(pageSize);

            int offSet = (pageNo -1 ) * pageSize;
            list = bidInfoMapper.selectBidLoanInfo(uid,offSet,pageSize);
        }

        return list;
    }
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean invest(Integer loanId, Integer uid, BigDecimal money) {
        boolean result = false;
        int rows  = 0;
        // 验证数据， money 和 account ， 产品的金额对比
        //1.查询uid的资金, 同时给数据上锁
        FinanceAccount account  = financeAccountMapper.selectAccountForUpdate(uid);
        if( account != null ){
            //2 。扣除金额
            rows  = financeAccountMapper.updateByInvest(uid,money);
            if( rows < 1 ){
                throw new RuntimeException("投资-扣除资金余额失败");
            }

            //3.操作产品
            LoanInfo loanInfo = loanInfoMapper.selectByPrimaryKey(loanId);
            if( loanInfo == null ){
                throw new RuntimeException("投资-产品不存在");
            } else {
                //判断money是否符合购买条件
                int intMoney = money.intValue();
                int min = loanInfo.getBidMinLimit().intValue();
                int max = loanInfo.getBidMaxLimit().intValue();
                int left = loanInfo.getLeftProductMoney().intValue();

                if( intMoney >= min && intMoney <=max && intMoney <=left){
                    //扣除剩余可投资金额
                    rows  = loanInfoMapper.updateLeftMoneyByInvest(loanId,money);
                    if( rows < 1){
                        throw new RuntimeException("投资-更新产品剩余可投资金额失败");
                    }

                    //4.创建投资记录
                    BidInfo bidInfo = new BidInfo();
                    bidInfo.setLoanId(loanId);
                    bidInfo.setUid(uid);
                    bidInfo.setBidMoney(money);
                    bidInfo.setBidStatus(1);
                    bidInfo.setBidTime( new Date());
                    rows  = bidInfoMapper.insertSelective(bidInfo);
                    if( rows < 1 ){
                        throw new RuntimeException("投资-创建投资记录失败");
                    }
                    result = true;

                    //5.查询此时产品的剩余可投资金额，是否够满标
                    LoanInfo queryInfo = loanInfoMapper.selectByPrimaryKey(loanId);
                    if( queryInfo.getLeftProductMoney().intValue() == 0 ){
                        //满标，更新此产品的满标时间和 status
                        queryInfo.setProductStatus(P2PConstants.PRODUCT_STATUS_MANBIAO_1);
                        queryInfo.setProductFullTime( new Date());
                        rows  = loanInfoMapper.updateByPrimaryKeySelective(queryInfo);
                        if( rows < 1){
                            result = false;
                            throw new RuntimeException("投资-更新产品状态失败");
                        }
                        result = true;
                    }
                } else {
                    throw new RuntimeException("投资-投资金额不正确");
                }
            }
        }
        return result;
    }

}
