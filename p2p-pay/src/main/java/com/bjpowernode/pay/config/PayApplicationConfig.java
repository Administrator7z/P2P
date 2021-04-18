package com.bjpowernode.pay.config;

import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

@Configuration
public class PayApplicationConfig {

    @Resource
    private AlipayConfig alipayConfig;

    //使用@Bean 注入AlipayClient
    @Bean
    public AlipayClient alipayClient(){
        //获得初始化的AlipayClient
        AlipayClient alipayClient = new DefaultAlipayClient(
                alipayConfig.getGatewayUrl(),
                alipayConfig.getApp_id(),
                alipayConfig.getMerchant_private_key(),
                "json",
                alipayConfig.getCharset(),
                alipayConfig.getAlipay_public_key(),
                alipayConfig.getSign_type());
        return alipayClient;
    }
}
