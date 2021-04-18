package com.bjpowernode.pay.controller;


import com.bjpowernode.pay.service.AlipayService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;

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
    public void alipayNotify(HttpServletResponse response) throws IOException {
        System.out.println("====alipayNotify===");
        response.getWriter().print("success");
    }


}
