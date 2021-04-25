package com.bjpowernode;

import com.bjpowernode.p2ptask.TaskManager;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;

//启动dubbo的服务
@EnableDubbo
//启动定时任务
@EnableScheduling
@SpringBootApplication
public class P2pTaskApplication {

	public static void main(String[] args) {

		ApplicationContext ctx = SpringApplication.run(P2pTaskApplication.class, args);
		TaskManager tm  = (TaskManager) ctx.getBean("taskManager");

		//tm.invokeDataServiceIncomePlan();
		//tm.invokeDataServiceIncomeBack();
		tm.invokePayAlipayQuery();
	}

}
