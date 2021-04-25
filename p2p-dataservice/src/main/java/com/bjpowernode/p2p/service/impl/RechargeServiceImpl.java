package com.bjpowernode.p2p.service.impl;

import com.bjpowernode.common.AppUtil;
import com.bjpowernode.contans.P2PConstants;
import com.bjpowernode.contans.P2PRedis;
import com.bjpowernode.p2p.mapper.FinanceAccountMapper;
import com.bjpowernode.p2p.mapper.RechargeRecordMapper;
import com.bjpowernode.p2p.model.FinanceAccount;
import com.bjpowernode.p2p.model.RechargeRecord;
import com.bjpowernode.p2p.service.RechargeService;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@DubboService(interfaceClass = RechargeService.class,version = "1.0")
public class RechargeServiceImpl implements RechargeService {

    @Resource
    private RechargeRecordMapper rechargeRecordMapper;

    @Resource
    private FinanceAccountMapper financeAccountMapper;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public List<RechargeRecord> queryPageRechargeInfo(Integer uid, Integer pageNo, Integer pageSize) {
        List<RechargeRecord> list = new ArrayList<>();
        if( uid != null && uid > 0 ){
            pageNo = AppUtil.defaultPageNo(pageNo);
            pageSize = AppUtil.defaultPageSize(pageSize);

            int offSet = (pageNo -1 ) * pageSize;
            list = rechargeRecordMapper.selectPageByUid(uid,offSet,pageSize);
        }
        return list;
    }
    //添加充值记录
    @Override
    public int addRechargeRecord(RechargeRecord rr) {
        return rechargeRecordMapper.insertSelective(rr);
    }

    /**
     * 处理支付宝的notify通知
     * @param outTradeNo   商家订单号
     * @param totalAmount  支付宝发送过来的支付金额
     * @param tradeStatus  支付状态
     * @param appId        app_id
     * @return
     */

    /**
     * 1.判断订单是否是商家自己的
     * 2.判断支付金额和商家的是否一样
     * 3.判断支付的状态（成功，其他的）
     * 4.如果是成功的支付（ 1》更新资金账号，增加金额 2》更新此充值订单记录状态为充值成功）
     * 5.如果支付失败（更新此充值订单记录状态为充值失败）
     */
    @Transactional
    @Override
    public synchronized boolean aliayNotifyByRecharge(String outTradeNo,
                                                      String totalAmount,
                                                      String tradeStatus
    ) {

        boolean result = false;
        int rows = 0 ;
        //1.查询充值订单是否存在的
        RechargeRecord rr  = rechargeRecordMapper.selectByRechargeNo(outTradeNo);
        if( rr != null ){
            // 判断订单是否处理过
            if(P2PConstants.RECHARGE_PROCESSING == rr.getRechargeStatus()){
                //是处理中（status = 0）的状态， 才可以继续处理
                BigDecimal aliayTotalAmount = new BigDecimal(totalAmount);
                if( aliayTotalAmount.compareTo( rr.getRechargeMoney()) == 0){  //金额和商家的一样的

                    //判断 充值的状态
                    if("TRADE_SUCCESS".equals(tradeStatus)) { //成功
                        //1.更新 u_finance_account金额
                        FinanceAccount account = financeAccountMapper.selectAccountForUpdate(rr.getUid());
                        if( account != null){
                            rows = financeAccountMapper.updateByRecharge(rr.getUid(),rr.getRechargeMoney());
                            if(rows < 1){
                                throw new RuntimeException("支付宝异步通知-更新资金账号失败");
                            }

                            //2.更充值recharge_record他的状态是 1 成功
                            rr.setRechargeStatus(P2PConstants.RECHARGE_SUCCESS);
                            rows = rechargeRecordMapper.updateByPrimaryKeySelective(rr);
                            if( rows < 1 ){
                                throw new RuntimeException("支付宝异步通知-更新recharge表状态为1失败");
                            }
                            //充值成功
                            result = true;
                        }
                    } else{
                        //失败 更充值recharge_record他的状态是 2 失败
                        rr.setRechargeStatus(P2PConstants.RECHARGE_FAIL);
                        rows = rechargeRecordMapper.updateByPrimaryKeySelective(rr);
                        if( rows < 1 ){
                            throw new RuntimeException("支付宝异步通知-更新recharge表状态为2失败");
                        }
                    }
                    //删除 处理过的订单
                    stringRedisTemplate.opsForZSet().remove(P2PRedis.ALIPAY_TRANDE_LIST,outTradeNo);
                }
            }
        }
        return result;
    }

    /**
     *  商家调用支付宝的查询接口，根据查询充值结果，处理充值记录
     * @param outTradeNo
     * @param totalAmount
     * @param tradeStatus
     * @return
     */

    @Transactional
    @Override
    public synchronized boolean alipayQueryByRecharge(String outTradeNo,
                                                      String totalAmount,
                                                      String tradeStatus) {

        int rows = 0;
        boolean result = false;
        // 查询充值记录
        RechargeRecord rr  = rechargeRecordMapper.selectByRechargeNo(outTradeNo);
        if( rr  != null){
            // 判断充值记录的状态和金额
            if( P2PConstants.RECHARGE_PROCESSING == rr.getRechargeStatus()){

                if( "TRADE_SUCCESS".equals(tradeStatus)){
                    BigDecimal alipayTotalAmount = new BigDecimal(totalAmount);
                    if( alipayTotalAmount.compareTo(rr.getRechargeMoney()) == 0){   // 金额一致
                        //更新账号金额
                        rows = financeAccountMapper.updateByRecharge(rr.getUid(),rr.getRechargeMoney());
                        if( rows < 1){
                            throw new RuntimeException("支付宝查询-更新资金余额失败");
                        }

                        //更新充值记录的状态
                        rr.setRechargeStatus(P2PConstants.RECHARGE_SUCCESS);
                        rows = rechargeRecordMapper.updateByPrimaryKeySelective(rr);
                        if( rows < 1){
                            throw  new RuntimeException("支付宝查询-更新充值记录状态失败");
                        }
                        result = true;
                    }
                } else {
                    //充值失败
                    rr.setRechargeStatus(P2PConstants.RECHARGE_FAIL);
                    rows = rechargeRecordMapper.updateByPrimaryKeySelective(rr);
                    if( rows < 1){
                        throw new RuntimeException("支付宝查询-更新充值记录状态为2失败");
                    }
                }
                //删除redis中已经处理过的充值订单
                stringRedisTemplate.opsForZSet().remove(P2PRedis.ALIPAY_TRANDE_LIST,outTradeNo);
            }
        }
        return result;
    }
}
