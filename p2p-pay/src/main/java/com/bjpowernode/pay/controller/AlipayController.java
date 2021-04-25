package com.bjpowernode.pay.controller;


import com.bjpowernode.pay.service.AlipayService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@Controller
public class AlipayController {


    @Resource
    private AlipayService alipayService;



    @GetMapping("/alipay/page")
    @ResponseBody
    public void receiveWebPayRequest(Integer uid, BigDecimal money, HttpServletResponse response) throws IOException {
        String form = alipayService.alipayTradePagePay(uid,money.toString());
        response.setHeader("content-type", "text/html");
        PrintWriter writer = response.getWriter();
        writer.print(form);
        writer.flush();
        writer.close();
    }

    //支付宝的异步通知地址：notifyUrl
    @PostMapping("/alipay/notify")
    public void alipayNotify(HttpServletRequest request, HttpServletResponse response) throws IOException {
        System.out.println("====alipayNotify===");

        //收到支付宝发送过来的参数
        Map<String,String> params = new HashMap<String,String>();
        Map<String,String[]> requestParams = request.getParameterMap();
        for (Iterator<String> iter = requestParams.keySet().iterator(); iter.hasNext();) {
            String name = (String) iter.next();
            String[] values = (String[]) requestParams.get(name);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i]
                        : valueStr + values[i] + ",";
            }
            //乱码解决，这段代码在出现乱码时使用
            valueStr = new String(valueStr.getBytes("ISO-8859-1"), "utf-8");
            params.put(name, valueStr);
        }

        try{
            // 处理业务逻辑 ， 写到service
            alipayService.alipayNotifyService(params);
        } finally {
            //输出success给支付宝
            PrintWriter out = response.getWriter();
            out.println("success");
            out.flush();
            out.close();
        }
    }

    //创建一个查询的入口：定时任务
    @GetMapping("/alipay/query")
    @ResponseBody
    public String alipayQuery(){
        alipayService.alipayQueryService();
        return "ok-接受了查询的请求";
    }


}
