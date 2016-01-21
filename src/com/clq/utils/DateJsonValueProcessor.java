package com.clq.utils;


/*import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

import net.sf.json.JsonConfig;
import net.sf.json.processors.JsonValueProcessor;

*//**
 * json格式化日期的类
 * @author chenliqiang	
 * @update date 2015-07-16
 *
 *//*
public class DateJsonValueProcessor implements JsonValueProcessor {  
    private String format = "yyyy-MM-dd HH:mm:ss EE";  
  
    public DateJsonValueProcessor() {  
    }  
  
    public DateJsonValueProcessor(String format) {  
        this.format = format;  
    }  
  
    
  //处理数组的值
    public Object processArrayValue(Object value, JsonConfig jsonConfig) {
        return this.process(value);
    }
 
    //处理对象的值
    public Object processObjectValue(String key, Object value, JsonConfig jsonConfig) {
        return this.process(value);
    }
     
    //处理Date类型返回的Json数值
    private Object process(Object value) {
    	 SimpleDateFormat sdf = new SimpleDateFormat(format);  
        if (value == null) {
            return "";
        } else if (value instanceof Timestamp)
            return sdf.format((Timestamp) value);
        else if (value instanceof Date)
            return sdf.format((Date) value);
        else {
            return value.toString();
        }
    }
  
    public String getFormat() {  
        return format;  
    }  
  
    public void setFormat(String format) {  
        this.format = format;  
    }  
  
}  
*/