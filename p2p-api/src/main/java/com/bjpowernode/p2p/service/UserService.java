package com.bjpowernode.p2p.service;

import com.bjpowernode.p2p.model.User;

public interface UserService {
    //统计所有用户数量
    int countRegisterUser();
    //判断手机号是否注册过
    User queryUserByPhone(String phone);
}
