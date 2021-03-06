package com.bjpowernode.web.controller;

import com.bjpowernode.common.IDCardUtil;
import com.bjpowernode.common.PhoneFormatCheckUtils;
import com.bjpowernode.contans.P2PConstants;
import com.bjpowernode.p2p.model.FinanceAccount;
import com.bjpowernode.p2p.model.RechargeRecord;
import com.bjpowernode.p2p.model.User;
import com.bjpowernode.p2p.model.ext.BidLoanInfo;
import com.bjpowernode.p2p.service.BidInfoService;
import com.bjpowernode.p2p.service.FinanceAccountService;
import com.bjpowernode.p2p.service.RechargeService;
import com.bjpowernode.p2p.service.UserService;
import com.bjpowernode.vo.RegisterUser;
import com.bjpowernode.vo.ResultObject;
import com.bjpowernode.web.service.IDCard;
import com.bjpowernode.web.service.Massage106;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.crypto.interfaces.PBEKey;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.bind.annotation.XmlInlineBinaryData;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

@Controller
public class UserController {

    @DubboReference(interfaceClass = UserService.class, version = "1.0")
    private UserService userService;
    @Resource
    private Massage106 massage106;
    @Resource
    private IDCard idCardCheck;
    @DubboReference(interfaceClass = FinanceAccountService.class, version = "1.0")
    private FinanceAccountService financeAccountService;
    @DubboReference(interfaceClass = BidInfoService.class, version = "1.0")
    private BidInfoService bidInfoService;
    @DubboReference(interfaceClass = RechargeService.class, version = "1.0")
    private RechargeService rechargeService;

    //??????????????????
    private Logger logger = LoggerFactory.getLogger(IndexController.class);

    //??????????????????
    @GetMapping("/loan/page/register")
    public String login() {
        return "register";
    }

    //????????????
    @GetMapping("/loan/page/realName")
    public String realNamePage(Model model) {
        return "realName";
    }

    //??????????????????????????????
    @GetMapping("/loan/register/phone")
    @ResponseBody
    public ResultObject hasedRegisterByPhone(@RequestParam("phone") String phone) {
        ResultObject ro = ResultObject.buildError("??????????????????????????????????????????");
        if (phone != null && PhoneFormatCheckUtils.isPhoneLegal(phone)) {
            //??????dataservice??????
            User user = userService.queryUserByPhone(phone);
            if (user == null) {
                ro = ResultObject.buildOK("????????????");
            }
        }

        return ro;
    }

    //?????????????????????
    @PostMapping("/loan/sendCode")
    @ResponseBody
    public ResultObject sendSmsCode(@RequestParam("phone") String phone) {
        ResultObject ro = ResultObject.buildError("??????????????????????????????????????????");
        if (phone == null && PhoneFormatCheckUtils.isPhoneLegal(phone)) {
            ro.setMsg("??????????????????????????????");
        } else {
            //???????????????????????????????????????????????????106????????????
            boolean result = false;
            try {
                result = massage106.invokeSmsApi(phone);
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (InvalidKeyException e) {
                e.printStackTrace();
            }
            if (result) {
                ro = ResultObject.buildOK("?????????????????????????????????????????????");
            }
        }
        return ro;
    }

    //??????
    @PostMapping("/loan/register")
    @ResponseBody
    public ResultObject userRegister(RegisterUser registerUser, HttpSession session) {
        ResultObject resultObject = ResultObject.buildError("????????????");
        if (registerUser.getPhone() == null || registerUser.getAbc() == null || registerUser.getCode() == null) {
            resultObject.setMsg("????????????");
        } else if (!PhoneFormatCheckUtils.isPhoneLegal(registerUser.getPhone())) {
            resultObject.setMsg("?????????????????????");
        } else if (!massage106.checkRedisCode(registerUser.getPhone(), registerUser.getCode())) {
            resultObject.setMsg("???????????????");
        } else {
            User user = userService.registerUser(registerUser.getPhone(), registerUser.getAbc());
            if (user != null) {
                session.setAttribute(P2PConstants.APP_SESSION_USER, user);
                resultObject.setCode(10000);
                resultObject.setMsg("????????????");
            }
        }

        return resultObject;
    }

    //????????????
    @PostMapping("/loan/realName")
    @ResponseBody
    public ResultObject userRealName(String name, @RequestParam("idcard") String idCard, HttpSession session) throws NoSuchAlgorithmException, InvalidKeyException, UnsupportedEncodingException {
        ResultObject ro = ResultObject.buildError("???????????????????????????");
        if (name == null || idCard == null || IDCardUtil.isIDCard(idCard)) {
            ro.setMsg("????????????");
        } else if
        (idCardCheck.check(idCard, name)) {
            User sessionUser = (User) session.getAttribute(P2PConstants.APP_SESSION_USER);
            User modifyUser = userService.realNameModifyUser(sessionUser.getPhone(), name, idCard);
            if (modifyUser != null) {
                ro = ResultObject.buildOK("??????");
                session.setAttribute(P2PConstants.APP_SESSION_USER, modifyUser);
            }

        }


        return ro;

    }

    //????????????
    @GetMapping("/loan/page/myCenter")
    public String myCenterPage(Model model, HttpSession session) {

        User sessionUser = (User) session.getAttribute(P2PConstants.APP_SESSION_USER);

        //????????????
        BigDecimal avaiableMoney = new BigDecimal("0");
        FinanceAccount account = financeAccountService.queryAccount(sessionUser.getId());
        if (account != null) {
            avaiableMoney = account.getAvailableMoney();
        }
        //?????????????????????
        List<BidLoanInfo> bidInfoList = bidInfoService.queryPageBidInfo(sessionUser.getId(), 1, 5);
        model.addAttribute("bidInfoList", bidInfoList);

        //?????????????????????
        List<RechargeRecord> rechargeRecordList = rechargeService.queryPageRechargeInfo(sessionUser.getId(), 1, 5);
        model.addAttribute("rechargeList", rechargeRecordList);


        model.addAttribute("money", avaiableMoney);
        return "myCenter";
    }

    //????????????
    //????????????
    @GetMapping("/loan/page/login")
    public String loginPage(String returnUrl, Model model) {
        System.out.println("returnUrl=" + returnUrl);
        model.addAttribute("returnUrl", returnUrl);
        return "login";
    }

    //??????
    @PostMapping("/loan/login")
    @ResponseBody
    public ResultObject userLogin(String phone, @RequestParam("abc") String password, HttpSession session) {
        ResultObject ro = ResultObject.buildError("????????????");
        if (StringUtils.isAnyEmpty(phone, password)) {
            ro.setMsg("??????????????????");
        } else if (!PhoneFormatCheckUtils.isPhoneLegal(phone)) {
            ro.setMsg("?????????????????????");
        } else {
            User user = userService.userlogin(phone, password);
            if (user != null) {
                ro = ResultObject.buildOK("????????????");
                session.setAttribute(P2PConstants.APP_SESSION_USER, user);
            }
        }

        return ro;
    }

    //??????
    @GetMapping("/loan/logout")
    public String userLogout(HttpSession session) {
        //session??????
        session.invalidate();
        //??????????????????
        return "redirect:/index";
    }

    //??????account??????
    @GetMapping("/loan/account")
    @ResponseBody
    public ResultObject queryAccount(HttpSession session) {
        ResultObject ro = ResultObject.buildError("????????????");

        User user = (User) session.getAttribute(P2PConstants.APP_SESSION_USER);
        FinanceAccount account = financeAccountService.queryAccount(user.getId());
        if (account != null) {
            ro = ResultObject.buildOK("????????????");
            ro.setData(account.getAvailableMoney());
        }
        return ro;
    }


}
