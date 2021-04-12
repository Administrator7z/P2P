package com.bjpowernode.web.controller;

import com.bjpowernode.p2p.model.User;
import com.bjpowernode.p2p.service.UserService;
import com.bjpowernode.vo.ResultObject;
import com.bjpowernode.web.service.TXMassageService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class UserController {

    @DubboReference(interfaceClass = UserService.class, version = "1.0")
    private UserService userService;

    //进入注册界面
    @GetMapping("/loan/page/register")
    public String login(){
        return "register";
    }

    //判断手机号是否注册过
    @GetMapping("/loan/register/phone")
    @ResponseBody
    public ResultObject hasedRegisterByPhone(@RequestParam("phone") String phone){
        ResultObject ro  = ResultObject.buildError("请检查手机号，或更换其他号码");
        if( phone != null){
            //调用dataservice服务
            User user = userService.queryUserByPhone(phone);
            if( user == null){
                ro = ResultObject.buildOK("可以注册");
            }
        }
        return ro;
    }

    //发送短信验证码
    @PostMapping("/loan/sendCode")
    @ResponseBody
    public ResultObject sendSmsCode(@RequestParam("phone") String phone){
        ResultObject ro = ResultObject.buildError("请检查手机号，或更换其他号码");
        if( phone == null){
            ro.setMsg("必须输入有效的手机号");
        } else if( phone.length() != 11 ){
            ro.setMsg("必须输入有效的手机号");
        } else {
            //给手机号发送验证码，调用京东万象的106短信接口
            boolean result = TXMassageService.sendSmsCode(phone);
            if( result ){
                ro = ResultObject.buildOK("短信验证码已经发送，请注意查看");
            }
        }
        return ro;
    }
}
