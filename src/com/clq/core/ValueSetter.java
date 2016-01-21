package com.clq.core;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

/**
 * 字段的封装类，包含字段的get，set方法，字段名，字段类型，依赖对象，列名，
 * @author chenliqiang
 * @update date 2015-09-07
 */
public class ValueSetter {
    private Method method;
    private String fieldName;
    private String fieldPath; //关联对象所用的字段(或者说是外键)
    private String columnName;
    private Class fieldType;//字段的类型
    private ValueGetter getter;

/*    public ValueSetter(Method method, String fieldName) {
        this.method = method;
        this.fieldName = fieldName;
        this.columnName = fieldName;
        this.fieldType = method.getParameterTypes()[0];
    }
*/
    public ValueSetter(Method method, String fieldName, String columnName) {
        this.method = method;
        this.fieldName = fieldName;
        this.columnName = columnName;
        this.fieldType = method.getParameterTypes()[0];
    }

    public String getFieldName() {
        return fieldName;
    }

    public String getColumnName() {
        return columnName;
    }

    public Class getFieldType() {
        return fieldType;
    }

    public String getFieldPath() {
        return fieldPath;
    }

    void setFieldPath(String fieldPath) {
        this.fieldPath = fieldPath;
    }

    static boolean isEmpty(String s) {
        if (s == null) {
            return true;
        }
        return "".equals(s.trim());
    }

    static Date parseDate(String value) {
        if (value == null || isEmpty(value)) {
            return null;
        }

        boolean isAllNumber = true;
        for (int i = 0, c = value.length(); i < c; ++i) {
            char ch = value.charAt(i);
            if (ch < '0' || ch > '9') {
                isAllNumber = false;
                break;
            }
        }
        
        if (isAllNumber) {
            return new Date(Long.parseLong(value));
        }

        if (value.length() == 10) {
            return java.sql.Date.valueOf(value);
        }

        if (value.length() == 19) {
            return java.sql.Timestamp.valueOf(value);
        }
        
        return null;
    }
    
    public void set(Object obj, Map<String, String> map) {
        String value = map.get(fieldName);
        if (value == null) {
            if (fieldPath != null) {
                value = map.get(fieldPath);
            }
            if (value == null) {
                return;
            }
        }
        try {
            if (fieldType == String.class) {
                method.invoke(obj, value);
            } else if (fieldType == long.class) {
                if (isEmpty(value))
                    method.invoke(obj, 0);
                else
                    method.invoke(obj, Long.parseLong(value));
            } else if (fieldType == int.class) {
                if (isEmpty(value))
                    method.invoke(obj, 0);
                else
                    method.invoke(obj, Integer.parseInt(value));
            } else if (fieldType == Date.class) {
                method.invoke(obj, parseDate(value));
            } else if (fieldType == boolean.class) {
                method.invoke(obj, "true".equalsIgnoreCase(value));
            } else if (fieldType == double.class) {
                if (isEmpty(value))
                    method.invoke(obj, 0.0);
                else
                    method.invoke(obj, Double.parseDouble(value));
            } else if (fieldType == Long.class) {
                if (isEmpty(value))
                    ;//method.invoke(obj, 0);
                else
                    method.invoke(obj, Long.parseLong(value));
            } else if (fieldType == Integer.class) {
                if (isEmpty(value))
                    ;//method.invoke(obj, 0);
                else
                    method.invoke(obj, Integer.parseInt(value));
            } else if (fieldType == Boolean.class) {
                method.invoke(obj, "true".equalsIgnoreCase(value));
            } else if (fieldType == Double.class) {
                if (isEmpty(value))
                    ;//method.invoke(obj, 0);
                else
                    method.invoke(obj, Double.parseDouble(value));
            } else if (fieldType == byte.class) {
                if (isEmpty(value))
                    method.invoke(obj, 0);
                else
                    method.invoke(obj, Byte.parseByte(value));
            } else if (fieldType == short.class) {
                if (isEmpty(value))
                    method.invoke(obj, 0);
                else
                    method.invoke(obj, Short.parseShort(value));
            } else if (fieldType == float.class) {
                if (isEmpty(value))
                    method.invoke(obj, 0);
                else
                    method.invoke(obj, Float.parseFloat(value));
            } else if (fieldType == Byte.class) {
                if (isEmpty(value))
                    ;//method.invoke(obj, 0);
                else
                    method.invoke(obj, Byte.parseByte(value));
            } else if (fieldType == Short.class) {
                if (isEmpty(value))
                    ;//method.invoke(obj, 0);
                else
                    method.invoke(obj, Short.parseShort(value));
            } else if (fieldType == Float.class) {
                if (isEmpty(value))
                    ;//method.invoke(obj, 0);
                else
                    method.invoke(obj, Float.parseFloat(value));
            } else {
                throw new RuntimeException(fieldType + " is not supported! only support: String, long, int, java.util.Date, boolean.");
                //method.invoke(obj, rs.getObject(columnName));
            }
        } catch (IllegalAccessException ex) {
            throw new RuntimeException(ex);
        } catch (InvocationTargetException ex) {
            throw new RuntimeException(ex);
        }
    }


    public void set(Object obj, ResultSet rs) throws SQLException {
        try {
            if (fieldType == String.class) {
                method.invoke(obj, rs.getString(columnName));
            } else if (fieldType == long.class) {
                method.invoke(obj, rs.getLong(columnName));
            } else if (fieldType == int.class) {
                method.invoke(obj, rs.getInt(columnName));
            } else if (fieldType == Date.class) {
            	Timestamp ts = rs.getTimestamp(columnName);//为了获取到时间，rs.getDate只能获取到日期
            	if(null != ts) method.invoke(obj, new Date(ts.getTime()));
            	else method.invoke(obj, ts);
            } else if(fieldType == Timestamp.class) {
            	method.invoke(obj, rs.getTimestamp(columnName));
            } else if (fieldType == boolean.class) {
                method.invoke(obj, rs.getBoolean(columnName));
            } else if (fieldType == double.class) {
                method.invoke(obj, rs.getDouble(columnName));
            } else if (fieldType == Long.class) {
                method.invoke(obj, rs.getLong(columnName));
            } else if (fieldType == Integer.class) {
                method.invoke(obj, rs.getInt(columnName));
            } else if (fieldType == Boolean.class) {
                method.invoke(obj, rs.getBoolean(columnName));
            } else if (fieldType == Double.class) {
                method.invoke(obj, rs.getDouble(columnName));
            } else if (fieldType == byte.class) {
                method.invoke(obj, rs.getByte(columnName));
            } else if (fieldType == short.class) {
                method.invoke(obj, rs.getShort(columnName));
            } else if (fieldType == float.class) {
                method.invoke(obj, rs.getFloat(columnName));
            } else if (fieldType == Byte.class) {
                method.invoke(obj, rs.getByte(columnName));
            } else if (fieldType == Short.class) {
                method.invoke(obj, rs.getShort(columnName));
            } else if (fieldType == Float.class) {
                method.invoke(obj, rs.getFloat(columnName));
            } else {
                throw new RuntimeException(fieldType + " is not supported! only support: String, long, int, java.util.Date, boolean.");
                //method.invoke(obj, rs.getObject(columnName));
            }
        } catch (IllegalAccessException ex) {
            throw new RuntimeException(ex);
        } catch (InvocationTargetException ex) {
            throw new RuntimeException(ex);
        }
    }

    public ValueGetter getGetter() {
        return getter;
    }

    public void setGetter(ValueGetter getter) {
        this.getter = getter;
    }

    /*public boolean isCreateAt() {
        return "createAt".equals(fieldName) && fieldType == Date.class;
    }

    public boolean isCreateById() {//不科学的判断方式啊!!!
        return "createById".equals(fieldName) && fieldType == long.class;
    }

    public boolean isUpdateAt() {
        return "updateAt".equals(fieldName) && fieldType == Date.class;
    }

    public boolean isUpdateById() {
        return "updateById".equals(fieldName) && fieldType == long.class;
    }*/

    void setValue(Object entity, Object value) {
        try {
            method.invoke(entity, value);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

}

