package com.bjpowernode.p2p.mapper;

import com.bjpowernode.p2p.model.User;
import org.apache.ibatis.annotations.Param;

public interface UserMapper {
    //计算注册总用户数量
    int selectCountUsers();
    //使用手机号作为条件查询用户
    User selectByPhone(String phone);
}