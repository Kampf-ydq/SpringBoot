package com.ditecting.entity;

/**
 * 自定义后端数据返回模板
 */
public class HoneyResult {
    //响应业务状态
    private Integer code;

    //响应消息
    private String msg;

    //响应的数据
    private Object data;

    public static HoneyResult build(Integer code, String msg, Object data){
        return new HoneyResult(code, msg, data);
    }

    public static HoneyResult ok(Object data){
        return new HoneyResult(data);
    }

    public static HoneyResult ok(){
        return new HoneyResult(null);
    }

    public HoneyResult(Integer code, String msg, Object data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public HoneyResult(Object data){
        this.code = 200;
        this.msg = "OK";
        this.data = data;
    }
}
