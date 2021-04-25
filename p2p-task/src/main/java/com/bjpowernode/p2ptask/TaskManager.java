package com.bjpowernode.p2ptask;

import com.bjpowernode.common.HttpClientUtils;
import com.bjpowernode.p2p.service.IncomeService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class TaskManager {


    @Value("${pay.alipay.query}")
    private String payServiceAlipayQueryURL;

    @DubboReference(interfaceClass = IncomeService.class,version = "1.0")
    private IncomeService incomeService;
    /**
     * 定义执行定时任务的方法：
     * 1.public 方法
     * 2.没有参数
     * 3.没有返还值 void
     */
    //@Scheduled(cron = "1 35 9 * * ?")
    public void testCron(){
        System.out.println("执行定时任务："+ new Date());
    }

    //每5秒钟执行一次任务
    //@Scheduled(cron = "*/5 * * * * ?")
    public void testCron2(){
        System.out.println("执行任务，"+ new Date());
    }

    //每10钟执行一次任务
    //@Scheduled(cron = "0 */10 * * * ?")
    public void testCron3(){
        System.out.println("执行任务，"+ new Date());
    }


    //调用收益计划生成
    @Scheduled(cron = "0 */20 * * * ?")
    public void invokeDataServiceIncomePlan(){
        System.out.println("开启执行收益计划的操作");
        incomeService.generateIncomePlan();
    }

    //调用收益返还
    @Scheduled(cron = "0 */30 * * * ?")
    public void invokeDataServiceIncomeBack(){
        System.out.println("开始执行收益返还的操作");
        incomeService.genernateIncomeBack();
    }

    //调用pay支付服务的，调单接口
    @Scheduled(cron = "0 */15 * * * ?")
    public void invokePayAlipayQuery(){
        try {
            HttpClientUtils.doGet(payServiceAlipayQueryURL);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
