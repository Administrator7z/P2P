package com.bjpowernode.contans;

public class P2PConstants {
    //定义产品类型
    //新手宝
    public static final Integer PRODUCT_TYPE_XINSHOUBAO_0 = 0;
    //优选
    public static final Integer PRODUCT_TYPE_YOUXUAN_1 = 1;
    //散标
    public static final Integer PRODUCT_TYPE_SANBIAO_2 = 2;


    //默认的分页大小
    public static final Integer PAGE_SIZE_DEFAULT = 9;
    //session中的user
    public static final String APP_SESSION_USER = "app_user";

    //产品的状态 未满标
    public static final Integer PRODUCT_STATUS_WEIMANBIAO_0 = 0;
    //满标
    public static final Integer PRODUCT_STATUS_MANBIAO_1 = 1;
    //满标生成收益计划
    public static final Integer PRODUCT_STATUS_MANBIAOINCOME_2 = 2;



    //充值中
    public static final Integer RECHARGE_PROCESSING = 0;

    //充值成功
    public static final Integer RECHARGE_SUCCESS = 1;

    //失败
    public static final Integer RECHARGE_FAIL = 2;

    // 收益表的状态
    //收益没有返还
    public static final Integer  INCOME_STATUS_NONE_BACK=0;
    //返还
    public static final Integer  INCOME_STATUS_HASD_BACK=1;


}
