package com.clq.core;

import java.io.Serializable;

import javax.servlet.http.HttpServletRequest;


import com.alibaba.fastjson.JSONObject;
import com.clq.utils.Env;
import com.clq.utils.EnvUtils;

/**
 * result handler. 请求处理类，返回统一格式的结果，并解析名称一致的参数
 * instance: <code>ResultHandler.extract(req);</code>
 * @author chenliqiang
 * @create date 2015-10-10
 */
public class ResultHandler<T> implements Serializable{
    
    private static final long serialVersionUID = 1L;
    
    private static final int GET = 1;
    private static final int POST = 2;
    
    private static final int DEFAULT_PAGENO = 1;
    private static final int DEFAULT_PAGESIZE = 20;
    private static final int DEFAULT_LIMIT = 20;
    

    /**
     * 结果处理器，根据结果枚举类型传入处理策略
     */
    private Result result = Result.SUCCESS;
    
    private T data = null;
    
    private String msg;
    
    private int pageNo;
    
    private int pageSize;
    
    private int limit;
    
    private int total;
    
    private int method;  // 请求类型，1 GET， 2 POST  
    
    private boolean pageable = false;
    
    private ResultHandler(int pageNo, int pageSize, int limit, int method, boolean pageable){
        this.pageNo = pageNo;
        this.pageSize = pageSize;
        this.limit = limit;
        this.method = method;
        this.pageable = pageable;
    }
    
    /**
     * This method is ugry, does too much things with reflection.
     * 传入request 和 V 类型的结果集，输出封装的ResultHandler，其中data转换为T类型
     * @param req
     * @param pageable 是否需要返回分页格式
     * @param data
     * @return ResultHandler<T,V>
     * @author chenliqiang
     * @update date 2015-12-5
     */
    public static <T> ResultHandler<T> extract(HttpServletRequest req, Env env, boolean pageable){
    	int pageNo = env.paramInt("pageNo", 0);
    	int pageSize = env.paramInt("pageSize", 0);
    	int limit = env.paramInt("limit", 0);
    	int method = GET;
       
        if("post".equals(req.getMethod().toLowerCase())){
            method = POST;
        }
        if(pageSize < 0 || pageSize > 100){
            pageSize = 100;
        }
        if(limit < 0 || limit > 100){
            limit = 100;
        }
        
        return new ResultHandler<T>(pageNo, pageSize, limit, method, pageable);
    } 
    
    /**
     * 调用具体的枚举结果处理返回结果
     */
    public String toStringResult(){
        return result.toStringResult(this);
    }
    
    @Override
    public String toString(){
        return this.toStringResult();
    }
    
    public JSONObject toJsonResult(){
        return result.toJsonResult(this);
    }
    
    public void setResult(Result result) {
        this.result = result;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    /**
     * 默认值1
     * @return int
     * @author chenliqiang
     * @update date 2015-10-10
     */
    public int getPageNo() {
        return pageNo == 0 ? DEFAULT_PAGENO : pageNo;
    }

    /**
     * 默认值20,假如是不分页查询，且有查询限制条数，则取查询限制条数作为查询限制
     * @return int
     * @author chenliqiang
     * @update date 2015-10-10
     */
    public int getPageSize() {
    	if(limit > 0 && !isPageable()) {
    		return limit;
    	} 
        return pageSize == 0 ? DEFAULT_PAGESIZE : pageSize;
    }

    /**
     * 默认值20
     * @return int
     * @author chenliqiang
     * @update date 2015-12-05
     */
    public int getLimit() {
        return limit == 0 ? DEFAULT_LIMIT : limit;
    }
    
    /**
     * 获取未加工处理过的 pageNo
     * @return int
     * @author chenliqiang
     * @update date 2015-10-10
     */
    int getNativePageNo(){
        return pageNo;
    }
    
    /**
     * 获取未加工处理过的 pageSize
     * @return int
     * @author chenliqiang
     * @update date 2015-10-10
     */
    int getNativePageSize(){
        return pageSize;
    }
    
    /**
     * 获取未加工处理过的 limit
     * @return int
     * @author chenliqiang
     * @update date 2015-10-10
     */
    int getNativeLimit(){
        return limit;
    }
    
    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }
    
    /**
     * 限制为post提交，如果非post提交，则设置错误信息到结果集
     * @return boolean
     * @author chenliqiang
     * @update date 2015-10-10
     */
    public boolean isPost(){
        if(this.method == GET){
            this.msg = "提交方式有误";
            this.result = Result.ERROR;
            return false;
        }
        return true;
    }
    
    /**
     * 限制为get提交，如果非get提交，则设置错误信息到结果集
     * @return boolean
     * @author chenliqiang
     * @update date 2015-10-10
     */
    public boolean isGET(){
        if(this.method == POST){
            this.msg = "提交方式有误";
            this.result = Result.ERROR;
            return false;
        }
        return true;
    }
    
    
    public boolean isPageable() {
        return pageable;
    }

    public static void main(String[] args){
        /*// test code here
        HttpServletRequest req = null;
        ResultHandler<JSONObject> handler = ResultHandler.extract(req, true);
        handler.setResult(Result.SUCCESS);
        handler.setData(new JSONObject());
        handler.setTotal(10);
        handler.toString();*/
    }
    
}
