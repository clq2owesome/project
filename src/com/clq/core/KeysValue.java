package com.clq.core;

import java.lang.reflect.Method;
import java.util.Map;

public class KeysValue {
	private Method[] setters;
	private Method[] getters;
	private String[] fieldNames;
	private String[] columnNames;
	private String[] labelNames;
	private Class[] types;//各个field的类型
	private Class keyType;//主键类型，联合主键类型为自定义的Mid
	
	public KeysValue(Method[] setters, Method[] getters, String[] fieldNames, 
			String[] columnNames, String[] labelNames, Class[] types) {
		
		this.setters = setters;
		this.getters = getters;
		this.fieldNames = fieldNames;
		this.columnNames = columnNames;
		this.labelNames = labelNames;
		this.types = types;
		this.keyType = Mid.class;
	}
	
	public Object[] get(Object obj) {
		Object[] object = new Object[getters.length];
		
        try {
        	for(int i=0; i<getters.length; i++) {
        		object[i] = getters[i].invoke(obj);//获取主键的值，getXXX()
        	}
        	
            return object;
            
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
	
	public void set(Object obj, Map<String, Object> map) {
		try {
			for(int i=0; i<fieldNames.length; i++) {
				Object value = map.get(fieldNames);
				if(null != value) setters[i].invoke(obj, value);//设置主键的值，setXXX()
			}
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
	}

	
	
	public Method[] getSetters() {
		return setters;
	}

	public Method[] getGetters() {
		return getters;
	}

	public String[] getFieldNames() {
		return fieldNames;
	}

	public String[] getColumnNames() {
		return columnNames;
	}

	public String[] getLabelNames() {
		return labelNames;
	}

	public Class[] getTypes() {
		return types;
	}

	public Class getKeyType() {
		return keyType;
	}

}
