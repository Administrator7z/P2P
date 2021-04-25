package com.bjpowernode.p2p.service.impl;

import com.bjpowernode.contans.P2PConstants;
import com.bjpowernode.p2p.mapper.BidInfoMapper;
import com.bjpowernode.p2p.mapper.FinanceAccountMapper;
import com.bjpowernode.p2p.mapper.IncomeRecordMapper;
import com.bjpowernode.p2p.mapper.LoanInfoMapper;
import com.bjpowernode.p2p.model.BidInfo;
import com.bjpowernode.p2p.model.FinanceAccount;
import com.bjpowernode.p2p.model.IncomeRecord;
import com.bjpowernode.p2p.model.LoanInfo;
import com.bjpowernode.p2p.service.IncomeService;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

//处理收益
@DubboService(interfaceClass = IncomeService.class,version = "1.0")
public class IncomeServiceImpl implements IncomeService {

    @Resource
    private LoanInfoMapper loanInfoMapper;

    @Resource
    private BidInfoMapper bidInfoMapper;

    @Resource
    private IncomeRecordMapper incomeRecordMapper;

    @Resource
    private FinanceAccountMapper financeAccountMapper;

    //生成收益计划
    @Transactional
    @Override
    public synchronized void generateIncomePlan() {

        // 1. 查询满标的产品
        List<LoanInfo> loanInfoList = loanInfoMapper.selectManBiaoList();
        // 2. 获取每个产品的对笔投资记录
        for(LoanInfo loanInfo : loanInfoList){
            Integer productType = loanInfo.getProductType();
            //根据产品id，从投资表，获取投资记录
            List<BidInfo> bidInfoList = bidInfoMapper.selectAllByLoanId(loanInfo.getId());
            //对每笔投资记录，进行收益的计算
            //循环投资记录列表
            BigDecimal income = new BigDecimal("0");
            Date incomeDate = null; //产品到期时间
            for(BidInfo bidInfo:bidInfoList){

                //计算收益 收益= 投资金额 * 周期 * 利率
                //BigDecimal rate= loanInfo.getRate().divide( new BigDecimal("100"), BigDecimal.ROUND_HALF_UP);
                if( productType == P2PConstants.PRODUCT_TYPE_XINSHOUBAO_0){
                    // 新手宝，天为单位
                    //income = bidInfo.getBidMoney().multiply( new BigDecimal( loanInfo.getCycle())).multiply(rate)
                    //        .divide( new BigDecimal(365),BigDecimal.ROUND_HALF_UP);
                    Double dblIncome = ( bidInfo.getBidMoney().doubleValue()  * loanInfo.getCycle()
                                       * loanInfo.getRate().doubleValue() / 100)  / 365;
                    income = new BigDecimal(dblIncome);

                    //从满标时间开始 + 周期
                    incomeDate = DateUtils.addDays( loanInfo.getProductFullTime(),loanInfo.getCycle());
                } else {
                    //其他月为单位的
                    Double dblIncome =     bidInfo.getBidMoney().doubleValue()  *
                                           ( loanInfo.getCycle() / Double.parseDouble("12")) *
                                           loanInfo.getRate().doubleValue() / 100  ;
                    income = new BigDecimal(dblIncome);

                    incomeDate = DateUtils.addMonths( loanInfo.getProductFullTime(),loanInfo.getCycle());
                }

                //存到收益b_income_record
                IncomeRecord ir  = new IncomeRecord();
                ir.setBidId(bidInfo.getId());
                ir.setBidMoney(bidInfo.getBidMoney());
                ir.setIncomeDate(incomeDate);
                ir.setIncomeMoney(income);
                ir.setIncomeStatus(P2PConstants.INCOME_STATUS_NONE_BACK);
                ir.setLoanId(loanInfo.getId());
                ir.setUid(bidInfo.getUid());
                int rows  = incomeRecordMapper.insertSelective(ir);
                if( rows < 1 ){
                    throw new RuntimeException("收益计划-添加收益记录失败");
                }

            }
            // 投资记录循环完成
            //更新产品的状态值是 2
            loanInfo.setProductStatus(P2PConstants.PRODUCT_STATUS_MANBIAOINCOME_2);
            int rows = loanInfoMapper.updateByPrimaryKeySelective(loanInfo);
            if(rows < 1){
                throw new RuntimeException("收益计划-更新产品状态为2失败");
            }
        }
    }

    //收益返还
    @Transactional
    @Override
    public synchronized void genernateIncomeBack() {
        int rows = 0;
        //1. 获取到期的收益记录(status = 0 )
        List<IncomeRecord> incomeRecordList = incomeRecordMapper.selectExpireIncome();
        //循环记录
        for(IncomeRecord rr: incomeRecordList){
            //2.更新资金账号
            FinanceAccount financeAccount = financeAccountMapper.selectAccountForUpdate(rr.getUid());
            if( financeAccount != null){
                rows =  financeAccountMapper.updateByIncomeBack(rr.getUid(),rr.getBidMoney(),rr.getIncomeMoney());
                if( rows < 1){
                    throw new RuntimeException("收益返还-更新金额失败");
                }
                //3.更新此income_record的status = 1
                rr.setIncomeStatus(P2PConstants.INCOME_STATUS_HASD_BACK);
                rows = incomeRecordMapper.updateByPrimaryKeySelective(rr);
                if( rows < 1){
                    throw new RuntimeException("收益返还-更新收益的状态为已返还失败");
                }
            }
        }
    }
}
