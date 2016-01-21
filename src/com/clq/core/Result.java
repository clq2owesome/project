package com.clq.core;

import org.apache.commons.lang.StringUtils;
import com.alibaba.fastjson.JSONObject;
import com.clq.utils.JsonEntityUtil;

/**
 * 结果处理的枚举，处理各种结果，统一的参数封装，确保接口参数格式的一致性
 * @author chenliqiang
 * @create date 2015-10-10
 */
public enum Result{
	
    SUCCESS {//成功
        <T>String toStringResult(ResultHandler<T> handler) {
            return JsonEntityUtil.object2String(this.toJsonResult(handler));
        }

        <T>JSONObject toJsonResult(ResultHandler<T> handler) {
            JSONObject result = fromHandler(handler);
            result.put("status", ResultStatus.SUCCESS.status());
            if(StringUtils.isEmpty(result.getString("msg")))
                result.put("msg", ResultStatus.SUCCESS.msg());
            return result;
        }
    },
    
    ERROR {//错误
        <T> String toStringResult(ResultHandler<T> handler){
            return JsonEntityUtil.object2String(this.toJsonResult(handler));
        }

        <T>JSONObject toJsonResult(ResultHandler<T> handler) {
            JSONObject result = new JSONObject();
            result.put("status", ResultStatus.ERROR.status());
            if(StringUtils.isEmpty(handler.getMsg()))
                result.put("msg", ResultStatus.ERROR.msg());
            else 
                result.put("msg", handler.getMsg());
            return result;
        }
    },
    
    NOTLOGIN {//未登录
        <T>String toStringResult(ResultHandler<T> handler){
            return JsonEntityUtil.object2String(this.toJsonResult(handler));
        }

        <T>JSONObject toJsonResult(ResultHandler<T> handler) {
            JSONObject result = new JSONObject();
            result.put("status", ResultStatus.NOTLOGIN.status());
            if(StringUtils.isEmpty(handler.getMsg()))
                result.put("msg",ResultStatus.NOTLOGIN.msg());
            else 
                result.put("msg", handler.getMsg());
            return result;
        }
    },
    
    PARAMERROR {//参数错误
        <T>String toStringResult(ResultHandler<T> handler){
            return JsonEntityUtil.object2String(this.toJsonResult(handler));
        }

        <T>JSONObject toJsonResult(ResultHandler<T> handler) {
            JSONObject result = new JSONObject();
            result.put("status", ResultStatus.PARAMERROR.status());
            if(StringUtils.isEmpty(handler.getMsg()))
                result.put("msg",ResultStatus.PARAMERROR.msg());
            else 
                result.put("msg", handler.getMsg());
            return result;
        }
    },
    
    TYPEERROR {//请求方式错误
        <T>String toStringResult(ResultHandler<T> handler){
            return JsonEntityUtil.object2String(this.toJsonResult(handler));
        }

        <T>JSONObject toJsonResult(ResultHandler<T> handler) {
            JSONObject result = new JSONObject();
            result.put("status", ResultStatus.TYPEERROR.status());
            if(StringUtils.isEmpty(handler.getMsg()))
                result.put("msg",ResultStatus.TYPEERROR.msg());
            else 
                result.put("msg", handler.getMsg());
            return result;
        }
    };
    
    <T>JSONObject fromHandler(ResultHandler<T> handler){
        JSONObject result = new JSONObject();
        int pageNo = handler.getPageNo();
        int pageSize = handler.getPageSize();
        int total = handler.getTotal();
        int limit = handler.getNativeLimit();
        int pageTotal = 0;
        
        if(pageSize > 0){
            pageTotal = (total % pageSize == 0 ? total/pageSize : total/pageSize+1);
        }
        if(limit > 0 && !handler.isPageable()){
            result.put("limit", limit);
        } else if(handler.isPageable()){
            result.put("pageNo", pageNo);
            result.put("pageSize", pageSize);
            result.put("pageTotal", pageTotal);
        }
        if(handler.getData() != null){
            result.put("data", handler.getData());
        }
        if(total > 0) {
        	result.put("total", total);
        }
        result.put("msg", handler.getMsg());
        
        return result;
    }
    
    abstract <T> String toStringResult(ResultHandler<T> handler);
    
    abstract <T> JSONObject toJsonResult(ResultHandler<T> handler);
}

enum ResultStatus {
    
    SUCCESS(1, "请求成功"),
    ERROR(-1, "请求失败"), 
    NOTLOGIN(-2, "请先登录再进行操作"),
    PARAMERROR(-3, "请求参数不合法"),
    TYPEERROR(-4, "请求方式不对");
    
    private int status;
    
    private String msg;
    
    ResultStatus(int status, String msg){
        this.status = status;
        this.msg = msg;
    }
    int status(){
        return this.status;
    }
    String msg(){
        return this.msg;
    }
}
