package com.bjpowernode.p2p.service.impl;

import com.bjpowernode.common.PhoneFormatCheckUtils;
import com.bjpowernode.contans.P2PRedis;
import com.bjpowernode.p2p.mapper.FinanceAccountMapper;
import com.bjpowernode.p2p.mapper.UserMapper;
import com.bjpowernode.p2p.model.FinanceAccount;
import com.bjpowernode.p2p.model.User;
import com.bjpowernode.p2p.service.UserService;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@DubboService(interfaceClass = UserService.class, version = "1.0")
public class UserServiceImpl implements UserService {

    @Resource
    private UserMapper userMapper;
    @Resource
    private RedisTemplate redisTemplate;
    @Resource
    private FinanceAccountMapper financeAccountMapper;

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

    @Transactional( propagation = Propagation.REQUIRED)
    @Override
    public User registerUser(String phone, String password) {
        User user = null;
        if( phone != null && password !=null ){
            //查询手机号是否注册过， 没注册过，执行下面的注册
            User dbUser = userMapper.selectByPhone( phone);
            if( dbUser == null ){
                //创建u_user记录
                user  = new User();
                user.setPhone(phone);
                user.setLoginPassword(password);
                user.setAddTime(new Date());
                int rows = userMapper.insertUserForId(user);

                if( rows < 1){
                    user = null;
                    throw new RuntimeException("手机号:"+phone+",注册user失败");
                }

                //添加u_finance_account
                FinanceAccount account = new FinanceAccount();
                account.setUid(user.getId()); //主键值
                account.setAvailableMoney( new BigDecimal("888"));
                rows  = financeAccountMapper.insertSelective(account);
                if( rows < 1){
                    user = null;
                    throw new RuntimeException("手机号:"+phone+",注册资金账号失败");
                }
            }
        }
        return user;
    }

    @Override
    public User realNameModifyUser(String phone, String name, String idCard) {
        User user  = userMapper.selectByPhone(phone);
        if( user != null ){
            user.setName(name);
            user.setIdCard(idCard);
            int  rows  = userMapper.updateUserByRealName(phone,name,idCard);
            if( rows < 1){
                user = null; //没更新成功
            }
        }
        return user;
    }

    @Override
    public User userlogin(String phone, String password) {
        User user = null;
        if (PhoneFormatCheckUtils.isPhoneLegal(phone) && password != null){
            user = userMapper.selectLoginUser(phone,password);
            if (user != null){
                user.setLastLoginTime(new Date());
                userMapper.updateByPrimaryKeySelective(user);
            }
        }
        return user;
    }
}
