package com.bjpowernode.p2p.mapper;

import com.bjpowernode.p2p.model.User;
import org.apache.ibatis.annotations.Param;

public interface UserMapper {
    //计算注册总用户数量
    int selectCountUsers();
    //使用手机号作为条件查询用户
    User selectByPhone(String phone);
    //添加user记录，同时获取创建的记录的主键id
    int insertUserForId(User user);
    //实名
    int updateUserByRealName(String phone, String name, String idCard);
}