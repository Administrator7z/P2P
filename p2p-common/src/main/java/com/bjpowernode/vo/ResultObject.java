package com.bjpowernode.vo;

import com.bjpowernode.contans.P2PRespCode;

public class ResultObject {
    // 请求的状态码  10000:成功， 20000：用户没有登录， 30000以后的是其他错误
    private Integer code;
    // 请求的状态码对应的描述
    private String msg;
    //应答数据
    private Object data;

    public ResultObject() {
    }

    public ResultObject(Integer code, String msg, Object data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public static ResultObject buildOK(String msg){
        ResultObject ro = new ResultObject();
        ro.setCode(P2PRespCode.SUCCESS);
        ro.setMsg(msg);
        ro.setData("");
        return ro;
    }
    public static ResultObject buildError(String msg){
        ResultObject ro = new ResultObject();
        ro.setCode(P2PRespCode.FAILURE);
        ro.setMsg(msg);
        ro.setData("");
        return ro;
    }


    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "ResultObject{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                ", data=" + data +
                '}';
    }
}
