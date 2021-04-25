package com.bjpowernode.web.config;

import com.bjpowernode.interceptor.LoginInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebGlobalConfig implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        String addPath [] = {"/loan/**"};
        String exludePath [] = {
                "/loan/loan","/loan/loanInfo",
                "/loan/login","/loan/logout",
                "/loan/page/register","/loan/register",
                "/loan/register/phone","/loan/sendCode",
                "/loan/page/login"
        };
        registry.addInterceptor( new LoginInterceptor())
                .addPathPatterns(addPath)
                .excludePathPatterns(exludePath);

    }
}
