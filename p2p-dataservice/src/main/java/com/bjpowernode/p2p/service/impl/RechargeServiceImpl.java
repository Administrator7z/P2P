package com.bjpowernode.p2p.service.impl;

import com.bjpowernode.common.AppUtil;
import com.bjpowernode.p2p.mapper.RechargeRecordMapper;
import com.bjpowernode.p2p.model.RechargeRecord;
import com.bjpowernode.p2p.service.RechargeService;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@DubboService(interfaceClass = RechargeService.class,version = "1.0")
public class RechargeServiceImpl implements RechargeService {

    @Resource
    private RechargeRecordMapper rechargeRecordMapper;

    @Override
    public List<RechargeRecord> queryPageRechargeInfo(Integer uid, Integer pageNo, Integer pageSize) {
        List<RechargeRecord> list = new ArrayList<>();
        if( uid != null && uid > 0 ){
            pageNo = AppUtil.defaultPageNo(pageNo);
            pageSize = AppUtil.defaultPageSize(pageSize);

            int offSet = (pageNo -1 ) * pageSize;
            list = rechargeRecordMapper.selectPageByUid(uid,offSet,pageSize);
        }
        return list;
    }
    //添加充值记录
    @Override
    public int addRechargeRecord(RechargeRecord rr) {
        return rechargeRecordMapper.insertSelective(rr);
    }
}
