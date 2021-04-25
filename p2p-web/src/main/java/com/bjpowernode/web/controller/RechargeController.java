package com.bjpowernode.web.controller;

import com.bjpowernode.common.HttpClientUtils;
import com.bjpowernode.contans.P2PConstants;
import com.bjpowernode.contans.P2PRedis;
import com.bjpowernode.p2p.model.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Controller
public class RechargeController {

    @Value("${pay.service.alipay}")
    private String payServiceAlipayUrl;



    @GetMapping("/loan/page/toRecharge")
    public String toRechargePage(Model model){
        return "toRecharge";

    }


    @PostMapping("/loan/alipay/entry")
    public void alipayEntry(BigDecimal rechargeMoney, HttpSession session, HttpServletRequest request,
                            HttpServletResponse response ){

        if (rechargeMoney !=null && rechargeMoney.doubleValue() > 0) {
            User user = (User) session.getAttribute(P2PConstants.APP_SESSION_USER);
            Map<String,String> params = new HashMap<>();
            params.put("uid",String.valueOf(user.getId()));
            params.put("money",rechargeMoney.toString());
            try {
                String result  = HttpClientUtils.doGet(payServiceAlipayUrl,params);
                if("error".equals(result)){
                    //下单失败
                    request.getRequestDispatcher("/toRecharge.html").forward(request,response);
                } else {
                    PrintWriter out = response.getWriter();
                    out.println(result);
                    out.flush();
                    out.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }


        }

    }




}
