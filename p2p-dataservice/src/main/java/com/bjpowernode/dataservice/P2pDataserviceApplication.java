package com.bjpowernode.dataservice;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan(basePackages = "com.bjpowernode.p2p.mapper")
@EnableDubbo
@SpringBootApplication
public class P2pDataserviceApplication {

    public static void main(String[] args) {
        SpringApplication.run(P2pDataserviceApplication.class, args);
    }

}
