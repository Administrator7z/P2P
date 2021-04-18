package com.bjpowernode.pay.service;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.bjpowernode.contans.P2PConstants;
import com.bjpowernode.contans.P2PRedis;
import com.bjpowernode.p2p.model.RechargeRecord;
import com.bjpowernode.p2p.service.RechargeService;
import com.bjpowernode.pay.config.AlipayConfig;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;

@Service
public class AlipayService {

    @Resource
    private AlipayConfig alipayConfig;

    @Resource
    private AlipayClient alipayClient;

    @Resource
    private StringRedisTemplate stringRedisTemplate;


    @DubboReference(interfaceClass = RechargeService.class,version = "1.0")
    private RechargeService rechargeService;

    /**
     *
     * 支付宝的下单请求
     * @return form表单
     */
    public String alipayTradePagePay(Integer uid,String money){

        // 支付宝的返回结果
        String form = null;
        //记录充值信息
        RechargeRecord rr = new RechargeRecord();
        rr.setChannel("alipay"); //充值渠道，表示使用哪个支付
        rr.setRechargeDesc("购买理财支付");
        rr.setRechargeMoney(new BigDecimal(money));
        rr.setRechargeNo( outTradeNo() ); //商家生成的订单号
        rr.setRechargeStatus(P2PConstants.RECHARGE_PROCESSING);
        rr.setRechargeTime( new Date());
        rr.setUid(uid);
        int rows  = rechargeService.addRechargeRecord(rr);
        if( rows > 0 ){
            AlipayTradePagePayRequest alipayRequest = new AlipayTradePagePayRequest();
            alipayRequest.setReturnUrl(alipayConfig.getReturn_url());
            alipayRequest.setNotifyUrl(alipayConfig.getNotify_url());

            //商户订单号，商户网站订单系统中唯一订单号，必填
            String out_trade_no = rr.getRechargeNo();
            //付款金额，必填
            String total_amount = money;
            //订单名称，必填
            String subject = "bjpowernode";
            //商品描述，可空
            String body = "licai";

            alipayRequest.setBizContent("{\"out_trade_no\":\""+ out_trade_no +"\","
                    + "\"total_amount\":\""+ total_amount +"\","
                    + "\"subject\":\""+ subject +"\","
                    + "\"body\":\""+ body +"\","
                    + "\"product_code\":\"FAST_INSTANT_TRADE_PAY\"}");

            try {
                form = alipayClient.pageExecute(alipayRequest).getBody();
            } catch (AlipayApiException e) {
                form = null;
                e.printStackTrace();
            }
        }
        System.out.println("result是一个form表单="+form);
        return form;
    }

    //商家订单号 ： 时间序列+自增加值
    private String outTradeNo(){
        String outTradeNo = DateFormatUtils.format( new Date(),"yyyyMMddHHmmssSSS") +
                stringRedisTemplate.opsForValue().increment(P2PRedis.ALIPAY_OUT_TRAND_NO);

        return outTradeNo;
    }

}
