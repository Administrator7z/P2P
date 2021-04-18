package com.bjpowernode.web.controller;

import com.bjpowernode.common.AppUtil;
import com.bjpowernode.contans.P2PConstants;
import com.bjpowernode.contans.P2PRedis;
import com.bjpowernode.p2p.model.FinanceAccount;
import com.bjpowernode.p2p.model.LoanInfo;
import com.bjpowernode.p2p.model.User;
import com.bjpowernode.p2p.model.ext.BidUserInfo;
import com.bjpowernode.p2p.service.BidInfoService;
import com.bjpowernode.p2p.service.FinanceAccountService;
import com.bjpowernode.p2p.service.LoanInfoService;
import com.bjpowernode.vo.InvestTopBean;
import com.bjpowernode.vo.PageInfo;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

@Controller
public class LoanInfoController {

    @DubboReference(interfaceClass = LoanInfoService.class ,version = "1.0")
    private LoanInfoService loanInfoService;
    @DubboReference(interfaceClass = BidInfoService.class,version = "1.0")
    private BidInfoService bidInfoService;
    @DubboReference(interfaceClass = FinanceAccountService.class, version = "1.0")
    private FinanceAccountService financeAccountService;
    @Resource
    private StringRedisTemplate stringRedisTemplate;


    //分页查看产品
    @GetMapping("/loan/loan")
    public String queryPageLoanInfo(Integer type, @RequestParam(value = "pageNo",required = false,defaultValue = "1") Integer pageNo, Model model){
        PageInfo pageInfo = new PageInfo();
        List<LoanInfo> loanInfoList = new ArrayList<>();
        List<InvestTopBean> list = new ArrayList<>();
        if (AppUtil.validProductType(type)){
            int records = loanInfoService.queryRecordsByType(type);
            if (records > 0 ){
                loanInfoList = loanInfoService.queryPageByType(type,pageNo, P2PConstants.PAGE_SIZE_DEFAULT);
            }

            //@TODO 投资排行榜

            ZSetOperations<String, String> zset = stringRedisTemplate.opsForZSet();
            Set<ZSetOperations.TypedTuple<String>> typedTuples = zset.reverseRangeWithScores(P2PRedis.LAON_INVEST_TOP, 0, 5);
            Iterator<ZSetOperations.TypedTuple<String>> iterator = typedTuples.iterator();
            while( iterator.hasNext() ){
                ZSetOperations.TypedTuple<String> next = iterator.next();
                InvestTopBean bean = new InvestTopBean(next.getValue(),next.getScore());
                list.add(bean);
            }

            pageInfo.setPageNo(pageNo);
            pageInfo.setTotalRecords(records);
        }
        model.addAttribute("investTopList",list);
        model.addAttribute("type",type);
        model.addAttribute("infoList",loanInfoList);
        model.addAttribute("pageInfo",pageInfo);
        return "loan";
    }

    //单个产品的详情
    @GetMapping("/loan/loanInfo")
    public String loanInfo(Integer loanId, Model model, HttpSession session){
        LoanInfo loanInfo = null;
        List<BidUserInfo> bidUserInfoList= new ArrayList<>();
        if(loanId != null){
            //产品信息
            loanInfo = loanInfoService.queryByLoanId(loanId);
            //产品的最近3条投资记录
            bidUserInfoList  =  bidInfoService.queryBidInfoByLoanId(loanId);
        }
        //查询金额
        User user = (User) session.getAttribute(P2PConstants.APP_SESSION_USER);
        if( user != null){
            FinanceAccount account = financeAccountService.queryAccount(user.getId());
            if( account != null){
                model.addAttribute("accountMoney",account.getAvailableMoney());
            }
        }
        model.addAttribute("loanInfo",loanInfo);
        model.addAttribute("bidUserInfoList",bidUserInfoList);
        return "loanInfo";
    }


}
