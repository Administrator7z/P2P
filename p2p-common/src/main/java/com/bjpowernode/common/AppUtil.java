package com.bjpowernode.common;

import javax.xml.stream.FactoryConfigurationError;

public class AppUtil {
    //检查产品类型
    public static boolean validProductType(Integer type) {
        boolean valid = false;
        if (type != null) {
            if (type == 0 || type == 1 || type == 2) {
                valid = true;
            }
        }
        return valid;

    }
    //默认页数为1
    public static int defaultPageNo(Integer pageNo){
        if (pageNo == null || pageNo.intValue() <=0){
            pageNo = 1;
        }
        return pageNo;
    }
    //pageSize
    public static int  defaultPageSize(Integer pageSize){
        if (pageSize == null || pageSize.intValue() <=0){
            pageSize = 9;
        }
        return pageSize;
    }
}
