package com.lisdoo.jstock.controller.utils;

import com.lisdoo.jstock.service.exchange.exception.JsException;

public class ResultUtil {

    /**成功且带数据**/
    public static Result success(Object object){
        Result result = new Result();
        result.setCode(ResultEnum.SUCCESS.getCode());
        result.setMsg(ResultEnum.SUCCESS.getMsg());
        result.setData(object);
        return result;
    }
    /**成功但不带数据**/
    public static Result success(){

        return success(null);
    }
    /**失败**/
    public static Result error(Integer code,String msg){
        Result result = new Result();
        result.setCode(code);
        result.setMsg(msg);
        return result;
    }

    public static Result error(JsException e) {
        Result result = new Result();
        result.setCode(e.getCode());
        result.setMsg(e.toString());
        return result;
    }
}
