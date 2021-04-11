package com.bjpowernode.p2p.mapper;

import com.bjpowernode.p2p.model.User;

public interface UserMapper {
    //计算注册总用户数量
    int selectCountUsers();
}