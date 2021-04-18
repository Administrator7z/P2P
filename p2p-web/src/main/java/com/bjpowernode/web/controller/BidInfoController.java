package com.bjpowernode.web.controller;

import com.bjpowernode.contans.P2PConstants;
import com.bjpowernode.contans.P2PRedis;
import com.bjpowernode.p2p.model.User;
import com.bjpowernode.p2p.service.BidInfoService;
import com.bjpowernode.vo.ResultObject;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.math.BigDecimal;

@Controller
public class BidInfoController {


    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @DubboReference(interfaceClass = BidInfoService.class,version = "1.0")
    private BidInfoService bidInfoService;

    @PostMapping("/loan/invest")
    @ResponseBody
    public ResultObject investPage(Integer loanId, BigDecimal money, HttpSession session){

        ResultObject ro  = ResultObject.buildError("请求失败");
        if( loanId == null || loanId < 1  || money == null || money.intValue() <100){
            ro.setMsg("请求的数据不正确");
        } else {
            //投资
            User user  = (User) session.getAttribute(P2PConstants.APP_SESSION_USER);
            boolean result = bidInfoService.invest(loanId,user.getId(),money);
            if( result ) {
                ro = ResultObject.buildOK("投资成功");
                //更新投资排行榜
                ZSetOperations<String, String> zset = stringRedisTemplate.opsForZSet();

                //value 是手机号， score：投资金额
                zset.incrementScore(P2PRedis.LAON_INVEST_TOP,user.getPhone(),money.doubleValue());

            }
        }
        return ro;

    }
}
