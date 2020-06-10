package com.usth.mblog.common.lang;

import lombok.Data;

import java.io.Serializable;

/**
 * AJAX请求返回结果
 */
@Data
public class Result implements Serializable {

    private int status; //状态：0 成功 -1 失败
    private String msg; //成功/错误信息
    private Object data; //返回数据
    private String action; //跳转链接

    public static Result success() {
        return Result.success("操作成功",null);
    }


    public static Result success(Object data) {
        return Result.success("操作成功",data);
    }

    public static Result success(String msg,Object data) {
        Result result = new Result();
        result.status = 0;
        result.msg = msg;
        result.data = data;
        return result;
    }

    public static Result failed(String msg) {
        Result result = new Result();
        result.status = -1;
        result.msg = msg;
        result.data = null;
        return result;
    }

    public Result action(String action) {
        this.action = action;
        return this;
    }
}
