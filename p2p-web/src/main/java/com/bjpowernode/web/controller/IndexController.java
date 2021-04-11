package com.bjpowernode.web.controller;

import com.bjpowernode.p2p.model.LoanInfo;
import com.bjpowernode.p2p.service.BidInfoService;
import com.bjpowernode.p2p.service.LoanInfoService;
import com.bjpowernode.p2p.service.UserService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.math.BigDecimal;
import java.util.List;

@Controller
public class IndexController {

    @DubboReference(interfaceClass = LoanInfoService.class,version = "1.0")
    private LoanInfoService loanInfoService;
    @DubboReference(interfaceClass = UserService.class,version = "1.0")
    private UserService userService;
    @DubboReference(interfaceClass = BidInfoService.class,version = "1.0")
    private BidInfoService bidInfoService;


    @GetMapping("/index")
    public String index(Model model) {
        //历史年化利率
        BigDecimal avgHistoryRate = loanInfoService.avgHistoryRate();
        //统计用户总量
        int countRegisterUsers = userService.countRegisterUser();
        //统计总投资量
        BigDecimal sumHistoryBidMoney = bidInfoService.sumHistoryBidMoney();
        //查询新手宝type=0
        List<LoanInfo> xinShouBaoList = loanInfoService.queryPageByType(0,0,1);
        //查询优选的type=1
        List<LoanInfo> youXuanList = loanInfoService.queryPageByType(1,0,4);
        //查询散标的type=2
        List<LoanInfo> sanBiaoList = loanInfoService.queryPageByType(2,0,8);

        model.addAttribute("avgHistoryRate",avgHistoryRate);
        model.addAttribute("countRegisterUsers",countRegisterUsers);
        model.addAttribute("sumHistoryBidMoney",sumHistoryBidMoney);
        model.addAttribute("xinShouBaoList",xinShouBaoList);
        model.addAttribute("youXuanList",youXuanList);
        model.addAttribute("sanBiaoList",sanBiaoList);
        return "index";
    }
}
