package com.bjpowernode.pay.service;

import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.bjpowernode.contans.P2PConstants;
import com.bjpowernode.contans.P2PRedis;
import com.bjpowernode.p2p.model.RechargeRecord;
import com.bjpowernode.p2p.service.RechargeService;
import com.bjpowernode.pay.config.AlipayConfig;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.StringJoiner;

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
        String form = "error";
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
                //把订单号存入redis
                stringRedisTemplate.opsForZSet().add(P2PRedis.ALIPAY_TRANDE_LIST,out_trade_no,rr.getRechargeTime().getTime());
            } catch (AlipayApiException e) {
                form = "error";
                e.printStackTrace();
            }
        }
        System.out.println("result是一个form表单="+form);
        return form;
    }
    /***
     * 处理notify
     * @return
     */
    public void alipayNotifyService(Map<String,String> params){

        boolean result = false;
        //验证 签名值
        try {
            boolean signVerified = AlipaySignature.rsaCheckV1(params,
                    alipayConfig.getAlipay_public_key(),
                    alipayConfig.getCharset(),
                    alipayConfig.getSign_type()); //调用SDK验证签名

            if( signVerified ){ //验签成功： 说明这个数据是支付宝发送给商家的， 没有被改变过
                /**处理支付的业务逻辑
                 * 1.判断订单是否是商家自己的
                 * 2.判断支付金额和商家的是否一样
                 * 3.判断支付的状态（成功，其他的）
                 * 4.如果是成功的支付（ 1》更新资金账号，增加金额 2》更新此充值订单记录状态为充值成功）
                 * 5.如果支付失败（更新此充值订单记录状态为充值失败）
                 */

                //判断app_id
                String appId = params.get("app_id");
                String outTradeNo = params.get("out_trade_no");
                String tradeNo = params.get("trade_no");
                String totalAmount = params.get("total_amount");
                String tradeStatus = params.get("trade_status");
                String buyer_id = params.get("buyer_id");
                String seller_id = params.get("seller_id");
                StringJoiner join = new StringJoiner("#","{","}");
                join.add(appId).add(outTradeNo).add(tradeNo).add(totalAmount).add(tradeStatus)
                        .add(buyer_id).add(seller_id);
                System.out.println("======支付宝alipay-notify====="+join.toString());
                result  = rechargeService.aliayNotifyByRecharge(outTradeNo,totalAmount,tradeStatus);
            } else {
                // 验签失败
                result = false;
            }
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }

    }



    //商家订单号 ： 时间序列+自增加值
    private String outTradeNo(){
        String outTradeNo = DateFormatUtils.format( new Date(),"yyyyMMddHHmmssSSS") +
                stringRedisTemplate.opsForValue().increment(P2PRedis.ALIPAY_OUT_TRAND_NO);

        return outTradeNo;
    }

    /**
     * 查询接口
     */
    public void alipayQueryService() {

        //商户订单号，商户网站订单系统中唯一订单号

        //默认是score从小到大排序的
        Set<ZSetOperations.TypedTuple<String>> sets =
                stringRedisTemplate.opsForZSet().rangeWithScores(P2PRedis.ALIPAY_TRANDE_LIST, 0, -1);
        long before10MinuteTime = DateUtils.addMinutes(new Date(),-10).getTime();
        // lambda
        sets.stream().forEach( t->{
            System.out.println( t.getValue() + "====="+ new BigDecimal(t.getScore()).longValue());
            long rechargeTime = new BigDecimal(t.getScore()).longValue();
            //找的 充值是10分钟以上的
            if( rechargeTime < before10MinuteTime ){ //10分钟之前充值的
                invokeAlipayQueryApi(t.getValue());
            }
        });
    }

    private void invokeAlipayQueryApi(String out_trade_no){
        //设置请求参数
        AlipayTradeQueryRequest alipayRequest = new AlipayTradeQueryRequest();
        alipayRequest.setBizContent("{\"out_trade_no\":\""+ out_trade_no +"\"}");
        //请求
        String result = "";
        try {
            AlipayTradeQueryResponse response = alipayClient.execute(alipayRequest);
            if(response.isSuccess()){
                //处理业务逻辑
                result = response.getBody(); // json格式的数据
                JSONObject obj = JSONObject.parseObject(result).getJSONObject("alipay_trade_query_response");
                if( obj.getString("code").equals("10000")){
                    // 处理支付的结果
                    String outTradeNo = obj.getString("out_trade_no");
                    String totalAmount =obj.getString("total_amount");
                    String tradeStatus = obj.getString("trade_status");
                    //调用data-service的服务方法，处理结果
                    rechargeService.alipayQueryByRecharge(outTradeNo,totalAmount,tradeStatus);
                }
            }
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }
    }

}
