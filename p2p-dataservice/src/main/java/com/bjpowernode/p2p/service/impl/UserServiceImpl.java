package com.bjpowernode.p2p.service.impl;

import com.bjpowernode.p2p.mapper.UserMapper;
import com.bjpowernode.p2p.service.UserService;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;

@DubboService(interfaceClass = UserService.class,version = "1.0")
public class UserServiceImpl implements UserService {

    @Resource
    private UserMapper userMapper;

    @Override
    public int countRegisterUser() {
        return userMapper.selectCountUsers();
    }
}
