package com.clq.core;

import java.lang.reflect.Method;

/**
 * 字段的封装类，包含字段的get，set方法，字段名，依赖对象，列名
 * @author chenliqiang
 * @update date 2015-09-07
 */
public class ValueGetter {
    private Method method;
    private String fieldName;
    private String fieldPath;
    private String columnName;
    private Class referType;

    public ValueGetter(Method method, String fieldName) {
        this.method = method;
        this.fieldName = fieldName;
        this.columnName = fieldName;
    }

    public ValueGetter(Method method, String fieldName, String columName) {
        this.method = method;
        this.fieldName = fieldName;
        this.columnName = columName;
    }

    public Method getMethod() {
        return method;
    }

    public String getFieldName() {
        return fieldName;
    }

    public String getColumnName() {
        return columnName;
    }

    public String getFieldPath() {
        return fieldPath;
    }

    public void setFieldPath(String fieldPath) {
        this.fieldPath = fieldPath;
    }

    public Object get(Object obj) {
        try {
            return method.invoke(obj);//获取字段的值，getXXX()
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public Class getReferType() {
        return referType;
    }

    public void setReferType(Class referType) {
        this.referType = referType;
    }

}
