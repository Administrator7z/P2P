package com.bjpowernode.web.controller;

import com.bjpowernode.common.AppUtil;
import com.bjpowernode.p2p.model.LoanInfo;
import com.bjpowernode.p2p.service.LoanInfoService;
import com.bjpowernode.vo.PageInfo;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

@Controller
public class LoanInfoController {

    @DubboReference(interfaceClass = LoanInfoService.class ,version = "1.0")
    private LoanInfoService loanInfoService;



    @GetMapping("/loan/loan")
    public String queryPageLoanInfo(Integer type, @RequestParam(value = "pageNo",required = false,defaultValue = "1") Integer pageNo, Model model){
        PageInfo pageInfo = new PageInfo();
        List<LoanInfo> loanInfoList = new ArrayList<>();
        if (AppUtil.validProductType(type)){
            int records = loanInfoService.queryRecordsByType(type);
            if (records > 0 ){
                loanInfoList = loanInfoService.queryPageByType(type,pageNo,9);
            }

            pageInfo.setPageNo(pageNo);
            pageInfo.setTotalRecords(records);
        }else {
            return "404";
        }
        model.addAttribute("type",type);
        model.addAttribute("infoList",loanInfoList);
        model.addAttribute("pageInfo",pageInfo);
        return "loan";
    }

    //单个产品的详情
    @GetMapping("/loan/loanInfo")
    public String loanInfo(@RequestParam("loanId") Integer loanId){
        return "loanInfo";
    }


}
