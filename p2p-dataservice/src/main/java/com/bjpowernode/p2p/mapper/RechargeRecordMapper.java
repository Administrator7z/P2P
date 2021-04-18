package com.bjpowernode.p2p.mapper;

import com.bjpowernode.p2p.model.RechargeRecord;

import java.util.List;

public interface RechargeRecordMapper {
    //分页查询充值记录
    List<RechargeRecord> selectPageByUid(Integer uid, int offSet, Integer pageSize);
    //添加充值记录
    int insertSelective(RechargeRecord rr);
}