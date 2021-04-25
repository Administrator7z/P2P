package com.bjpowernode.interceptor;

import com.bjpowernode.contans.P2PConstants;
import com.bjpowernode.p2p.model.User;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

//登录拦截器
public class LoginInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        // 获取Session中的用户信息
        User user  = (User) request.getSession().getAttribute(P2PConstants.APP_SESSION_USER);
        if( user == null ){
            //没有登录，让他登录
            response.sendRedirect( request.getContextPath() + "/loan/page/login");
            return false;
        }
        return true;
    }
}
