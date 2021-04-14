package com.bjpowernode.web.controller;

import com.bjpowernode.common.IDCardUtil;
import com.bjpowernode.common.PhoneFormatCheckUtils;
import com.bjpowernode.contans.P2PConstants;
import com.bjpowernode.p2p.model.FinanceAccount;
import com.bjpowernode.p2p.model.User;
import com.bjpowernode.p2p.service.FinanceAccountService;
import com.bjpowernode.p2p.service.UserService;
import com.bjpowernode.vo.RegisterUser;
import com.bjpowernode.vo.ResultObject;
import com.bjpowernode.web.service.IDCard;
import com.bjpowernode.web.service.Massage106;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Controller
public class UserController {

    @DubboReference(interfaceClass = UserService.class, version = "1.0")
    private UserService userService;
    @Resource
    private Massage106 massage106;
    @Resource
    private IDCard idCardCheck;
    @DubboReference(interfaceClass = FinanceAccountService.class,version = "1.0")
    private FinanceAccountService financeAccountService;

    //进入注册界面
    @GetMapping("/loan/page/register")
    public String login() {
        return "register";
    }

    //实名认证
    @GetMapping("/loan/page/realName")
    public String realNamePage(Model model) {
        return "realName";
    }

    //判断手机号是否注册过
    @GetMapping("/loan/register/phone")
    @ResponseBody
    public ResultObject hasedRegisterByPhone(@RequestParam("phone") String phone) {
        ResultObject ro = ResultObject.buildError("请检查手机号，或更换其他号码");
        if (phone != null && PhoneFormatCheckUtils.isPhoneLegal(phone)) {
            //调用dataservice服务
            User user = userService.queryUserByPhone(phone);
            if (user == null) {
                ro = ResultObject.buildOK("可以注册");
            }
        }
        return ro;
    }

    //发送短信验证码
    @PostMapping("/loan/sendCode")
    @ResponseBody
    public ResultObject sendSmsCode(@RequestParam("phone") String phone) {
        ResultObject ro = ResultObject.buildError("请检查手机号，或更换其他号码");
        if (phone == null && PhoneFormatCheckUtils.isPhoneLegal(phone)) {
            ro.setMsg("必须输入有效的手机号");
        } else {
            //给手机号发送验证码，调用京东万象的106短信接口
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
                ro = ResultObject.buildOK("短信验证码已经发送，请注意查看");
            }
        }
        return ro;
    }

    //注册
    @PostMapping("/loan/register")
    @ResponseBody
    public ResultObject userRegister(RegisterUser registerUser, HttpSession session) {
        ResultObject resultObject = ResultObject.buildError("注册失败");
        if (registerUser.getPhone() == null || registerUser.getAbc() == null || registerUser.getCode() == null) {
            resultObject.setMsg("参数错误");
        } else if (!PhoneFormatCheckUtils.isPhoneLegal(registerUser.getPhone())) {
            resultObject.setMsg("手机号格式错误");
        } else if (!massage106.checkRedisCode(registerUser.getPhone(), registerUser.getCode())) {
            resultObject.setMsg("验证码错误");
        } else {
            User user = userService.registerUser(registerUser.getPhone(), registerUser.getAbc());
            if (user != null) {
                session.setAttribute(P2PConstants.APP_SESSION_USER, user);
                resultObject.setCode(10000);
                resultObject.setMsg("注册成功");
            }
        }

        return resultObject;
    }

    //实名认证
    @PostMapping("/loan/realName")
    @ResponseBody
    public ResultObject userRealName(String name, @RequestParam("idcard") String idCard,HttpSession session) {
        ResultObject ro = ResultObject.buildError("请求失败，稍后重试");
        if (name == null || idCard == null || IDCardUtil.isIDCard(idCard)) {
            ro.setMsg("参数错误");
        }else {

            try {
                if (idCardCheck.check(idCard, name)) {
                    User sessionUser = (User) session.getAttribute(P2PConstants.APP_SESSION_USER);
                    User modifyUser = userService.realNameModifyUser(sessionUser.getPhone(),name,idCard);
                    if (modifyUser != null){
                        ro.setCode(10000);
                        ro.setMsg("成功");
                        session.setAttribute(P2PConstants.APP_SESSION_USER,modifyUser);
                    }
                }
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (InvalidKeyException e) {
                e.printStackTrace();
            }

        }



        return ro;

    }

    //用户中心
    @GetMapping("/loan/page/myCenter")
    public String myCenterPage(Model model,HttpSession session){

        User sessionUser = (User) session.getAttribute(P2PConstants.APP_SESSION_USER);

        //查询金额
        BigDecimal avaiableMoney = new BigDecimal("0");
        FinanceAccount account= financeAccountService.queryAccount(sessionUser.getId());
        if( account != null ){
            avaiableMoney = account.getAvailableMoney();
        }
        model.addAttribute("money",avaiableMoney);
        return "myCenter";
    }


}
