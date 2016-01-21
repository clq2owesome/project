package com.clq.core;

import java.lang.reflect.Method;

/**
 * 单一主键对象封装（包括get，set方法，主键字段名，主键数据库名，主键注释，主键类型）
 * @author chenliqiang
 * @update date 2015-09-23
 */
public class KeyValue {
	private Method setter;
	private Method getter;
	private String fieldName;
	private String columnName;
	private String labelName;
	private Class type;
	private Class keyType;//主键类型，单一主键类型与字段类型相同
	
	public KeyValue(Method setter, Method getter, String fieldName, 
			String columnName, String labelName, Class type) {
		
		this.setter = setter;
		this.getter = getter;
		this.fieldName = fieldName;
		this.columnName = columnName;
		this.labelName = labelName;
		this.type = type;
		this.keyType = type;
	}
	
	
	public Object get(Object obj) {
        try {
            return getter.invoke(obj);//获取主键的值，getXXX()
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
	
	public Object set(Object obj, Object value) {
		try {
            return setter.invoke(obj, value);//设置主键的值，setXXX(value)
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
	}
	
	
	public Method getSetter() {
		return setter;
	}
	public Method getGetter() {
		return getter;
	}
	public String getFieldName() {
		return fieldName;
	}
	public String getColumnName() {
		return columnName;
	}
	public String getLabelName() {
		return labelName;
	}
	public Class getType() {
		return type;
	}
	public Class getKeyType() {
		return keyType;
	}

}
