package com.bjpowernode.p2p.service.impl;

import com.bjpowernode.contans.P2PRedis;
import com.bjpowernode.p2p.mapper.UserMapper;
import com.bjpowernode.p2p.model.User;
import com.bjpowernode.p2p.service.UserService;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

@DubboService(interfaceClass = UserService.class, version = "1.0")
public class UserServiceImpl implements UserService {

    @Resource
    private UserMapper userMapper;
    @Resource
    private RedisTemplate redisTemplate;

    //查询所有用户数量
    @Override
    public int countRegisterUser() {
        ValueOperations vo = redisTemplate.opsForValue();
        Integer userNums = (Integer) vo.get(P2PRedis.USER_REGISTER_COUNT);
        if (userNums == null) {
            synchronized (this) {
                userNums = (Integer) vo.get(P2PRedis.USER_REGISTER_COUNT);
                userNums = userMapper.selectCountUsers();
                vo.set(P2PRedis.USER_REGISTER_COUNT, userNums, 1, TimeUnit.HOURS);
            }
        }


        return userNums;
    }

    @Override
    public User queryUserByPhone(String phone) {
        return userMapper.selectByPhone(phone);
    }
}
