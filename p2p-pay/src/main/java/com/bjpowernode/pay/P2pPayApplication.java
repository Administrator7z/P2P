package com.bjpowernode.pay;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

//Dubbo的启用
@EnableDubbo
@SpringBootApplication
public class P2pPayApplication {

    public static void main(String[] args) {
        SpringApplication.run(P2pPayApplication.class, args);
    }

}
