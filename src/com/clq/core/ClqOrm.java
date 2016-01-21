package com.clq.core;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.filter.TypeFilter;
import org.springframework.util.ClassUtils;

import com.alibaba.fastjson.serializer.SimplePropertyPreFilter;
import com.clq.annotation.Column;
import com.clq.annotation.Entity;
import com.clq.annotation.Id;
import com.clq.annotation.Label;
import com.clq.annotation.NotCreate;
import com.clq.annotation.NotUpdate;
import com.clq.annotation.Refer;
import com.clq.annotation.Transient;
import com.clq.entity.User;

/**
 * 对象关系映射 Object Relational Mapping
 * @author chenliqiang
 * @update date 2015-09-06
 */

public class ClqOrm {
	private String tablePrefix = "clq_";
	private Map<Class, KeyValue> keyValueMap = new HashMap<Class, KeyValue>();//单一主键封装集合
	private Map<Class, KeysValue> keysValueMap = new HashMap<Class, KeysValue>();//多主键封装集合
	
	private Map<Class, String> tableNameMap = new HashMap<Class, String>();//表名集合
	
	//private Map<Class, String> keyColumnMap = new HashMap<Class, String>();//单一主键字段(数据库列)集合
    //private Map<Class, String[]> keysColumnMap = new HashMap<Class, String[]>();//联合主键字段(数据库列)集合
    //private Map<Class, String> keyFieldMap = new HashMap<Class, String>();//单一主键字段(实体字段)集合
    //private Map<Class, String[]> keysFieldMap = new HashMap<Class, String[]>();//联合主键字段(实体字段)集合
    //private Map<Class, Class> keyTypeMap = new HashMap<Class, Class>();//单一主键的类型
    //private Map<Class, Class> keysTypeMap = new HashMap<Class, Class>();//联合主键的类型
    
    private Map<Class, Set<String>> notUpdateMap = new HashMap<Class, Set<String>>();//不更新的字段集合
    private Map<Class, Set<String>> notCreateMap = new HashMap<Class, Set<String>>();//不创建的字段集合
    
    private Map<String, String> field2columnMap = new HashMap<String, String>();//根据类名+字段名获取列名
    private Map<String, String> field2labelMap = new HashMap<String, String>();//根据类名+字段名获取注释名
    
    //private Map<Class, ValueSetter> createAtMap = new HashMap<Class, ValueSetter>();//创建时间的集合
    //private Map<Class, ValueSetter> createByIdMap = new HashMap<Class, ValueSetter>();//创建人的集合
    //private Map<Class, ValueSetter> updateAtMap = new HashMap<Class, ValueSetter>();//更新时间的集合
    //private Map<Class, ValueSetter> updateByIdMap = new HashMap<Class, ValueSetter>();//更新人的集合
    
    private Map<Class, List<ValueSetter>> setterMap = new HashMap<Class, List<ValueSetter>>();//实体所有字段ValueSetter集合
    private Map<Class, List<ValueGetter>> getterMap = new HashMap<Class, List<ValueGetter>>();//实体所有字段ValueGetter集合
    
    private Map<Class, List<String>> fieldListMap = new HashMap<Class, List<String>>();//实体所有字段的集合
    private Map<Class, List<String>> columnListMap = new HashMap<Class, List<String>>();//实体所有字段(数据库列)的集合
    
    private Map<Class, SimplePropertyPreFilter> propPreFilterMap =
            new HashMap<Class, SimplePropertyPreFilter>(); //fastjson进行数据持久化时用到的字段过滤器的集合
    
    private Map<Class, Method> getIdMap = new HashMap<Class, Method>();//主键get方法（Method）的集合
    private Map<Class, Method[]> getIdsMap = new HashMap<Class, Method[]>();//联合主键get方法（Method）的集合
    private Map<Class, Method> setIdMap = new HashMap<Class, Method>();//主键set方法（Method）的集合
    
    private Map<Class, Boolean> cacheMap = new HashMap<Class, Boolean>();//对象是否开启缓存集合
    
    private Map<Class, String> createSqlMap = new HashMap<Class, String>();//添加语句的集合
    private Map<Class, String> selectSqlMap = new HashMap<Class, String>();//查询语句的集合
    private Map<Class, String> existsSqlMap = new HashMap<Class, String>();//判断是否存在语句的集合
    private Map<Class, String> deleteSqlMap = new HashMap<Class, String>();//删除语句的集合
    private Map<Class, String> updateSqlMap = new HashMap<Class, String>();//更新语句的集合
	
	private ClqOrm(){}  //私有构造函数，防止new实例
	
	public void setEntityList(List<String> entityList) {
        for (String entity : entityList) {
            setEntity(entity);
        }
    }
	
	public void setEntity(String entity) {
        ClassPathScanningCandidateComponentProvider scan = 
                new ClassPathScanningCandidateComponentProvider(false);

        scan.addIncludeFilter(new TypeFilter() {
            @Override
            public boolean match(MetadataReader metadataReader, 
                    MetadataReaderFactory metadataReaderFactory) throws IOException {
                return true;
            }
        });

        List<Class> list = new ArrayList<Class>();

        for (BeanDefinition candidate : scan.findCandidateComponents(entity)) {
            try {
            	
                Class cls = ClassUtils.resolveClassName(candidate.getBeanClassName(),
                        ClassUtils.getDefaultClassLoader());
                list.add((Class) cls);
                
            } catch (Throwable ex) {
                throw new RuntimeException(ex);
            }
        }

        registerEntityList(list);
    }
	
	public void registerEntityList(List<Class> typeList) {
        for (Class type : typeList) {
            registerEntity(type);
        }
    }
	
	public void registerEntity(Class type) {
		try {
			prepareCacheMap(type);

	        prepareKeyMap(type);
	        
	        prepareSetterGetterMap(type);

	        prepareTableMap(type);

	        prepareGetIdMap(type);
	        prepareSetIdMap(type);

	        prepareCreateSqlMap(type);
	        prepareUpdateSqlMap(type);
	        prepareDeleteSqlMap(type);
	        prepareSelectSqlMap(type);
	        
		} catch(Exception ex) {
			ex.printStackTrace();
		}
        
        
        System.out.println("tableName=" + getTableName(type));
    }

	/* *********************prepareCacheMap   开始*************************** */
	private void prepareCacheMap(Class type) {
        Entity annotation = (Entity) type.getAnnotation(Entity.class);
        
        if (annotation != null) {
            boolean cache = false;
            if (annotation.memCache()) {
                cache = true;
            }
            cacheMap.put(type, cache);
            
        }
    }
	/* *********************prepareCacheMap   结束*************************** */
	
	/* *********************prepareTableMap   开始*************************** */
	private void prepareTableMap(Class type) {
        Entity annotation = (Entity) type.getAnnotation(Entity.class);
        String tableName = null;
        
        if (annotation != null) {
            if (!"".equals(annotation.tableName())) {
                tableName = annotation.tableName();
            }
        } 

        if (tableName == null) {
            String className = type.getSimpleName();
            tableName = tablePrefix + Character.toLowerCase(className.charAt(0))
                    + className.substring(1);
        }

        tableNameMap.put(type, tableName);
    }
	
	/* *********************prepareTableMap   结束*************************** */
	
	
	/* *********************prepareKeyMap   开始*************************** */
	
	private void prepareKeyMap(Class type) {

        List<Field> keys = new ArrayList<Field>();
        for (Field field : type.getDeclaredFields()) {
            Id idAnno = (Id) field.getAnnotation(Id.class);
            if (idAnno != null) {
                keys.add(field);
            }
        }
        
        if (keys.size() == 1) { //单一主键
        	Field field = keys.get(0);
            Column colAnno = (Column) field.getAnnotation(Column.class);
            Label labelAnno = (Label) field.getAnnotation(Label.class);
            Method getter = getter(type, field);
            Method setter = setter(type, field);
            
        	String fieldName = field.getName();
        	String labelName = (null == labelAnno ? "":labelAnno.value());
        	String colAnnoName = (null == colAnno ? fieldName:colAnno.value());//数据库名不存在则用字段名代替
        	KeyValue kv = new KeyValue(setter, getter, fieldName, colAnnoName, 
        			labelName, field.getType());
        	
        	keyValueMap.put(type, kv);
            
            /*if (colAnno == null) {//没有设置@Column注解时，用字段(field)代替
                keyColumnMap.put(type, field.getName());
            } else {
                keyColumnMap.put(type, colAnno.value());
            }
            keyFieldMap.put(type, field.getName());
            keyTypeMap.put(type, field.getType());*/
            addNotOperation(type, field.getName(), notUpdateMap);
            return;
            
        } else if (keys.size() > 1) { //联合主键
        	int keysLength = keys.size();
            String[] keyColumns = new String[keysLength];
            String[] keyFields = new String[keysLength];
            String[] keyLabels = new String[keysLength];
            Method[] getters = new Method[keysLength];
            Method[] setters = new Method[keysLength];
            Class[] types = new Class[keysLength];
            
            for (int i = 0; i < keyColumns.length; i ++) {
                Field field = keys.get(i);
                keyFields[i] = field.getName();
                types[i] = field.getType();
                Column colAnno = (Column) field.getAnnotation(Column.class);
                Label labelAnno = (Label) field.getAnnotation(Label.class);
                keyColumns[i] = (null == colAnno ? keyFields[i]:colAnno.value());//数据库名不存在则用字段名代替
                keyLabels[i] = (null == labelAnno ? "":labelAnno.value());
                
                getters[i] = getter(type, field);
                setters[i] = setter(type, field);
                
                addNotOperation(type, field.getName(), notUpdateMap);
            }
            
            KeysValue ksv = new KeysValue(setters, getters, keyFields, keyColumns, keyLabels, types);
            keysValueMap.put(type, ksv);
            
            //keysFieldMap.put(type, keyFields);
            //keysColumnMap.put(type, keyColumns);
            //keysTypeMap.put(type, Mid.class);
            return;
        }
        
        //没有设置@Id注解时，根据表名拼主键
        String className = type.getSimpleName();
        String fieldKey = Character.toLowerCase(className.charAt(0))
                + className.substring(1) + "Id";
        try {
            Field field = type.getDeclaredField(fieldKey);
            Column colAnno = (Column) field.getAnnotation(Column.class);
            Label labelAnno = (Label) field.getAnnotation(Label.class);
            Method getter = getter(type, field);
            Method setter = setter(type, field);
            
        	String fieldName = field.getName();
        	String labelName = (null == labelAnno ? "":labelAnno.value());
        	String colAnnoName = (null == colAnno ? fieldName:colAnno.value());//数据库名不存在则用字段名代替
        	KeyValue kv = new KeyValue(setter, getter, fieldName, colAnnoName, 
        			labelName, field.getType());
        	
        	keyValueMap.put(type, kv);
            
            
            /*if (colAnno == null) {
                keyColumnMap.put(type, fieldKey);
            } else {
                keyColumnMap.put(type, colAnno.value());
            }
            keyFieldMap.put(type, fieldKey);
            keyTypeMap.put(type, field.getType());*/
            
            addNotOperation(type, field.getName(), notUpdateMap);
            
        } catch (NoSuchFieldException ex) {
            
            throw new RuntimeException("not found default Id field: "
                    + fieldKey + " OR user define annotation @Id");
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
        
    }
	
	/* *********************prepareKeyMap   结束*************************** */
	
	
	/* *********************prepareSetterGetterMap 结束*************************** */
	
	private void prepareSetterGetterMap(Class type) {
        List<ValueSetter> setterList = new ArrayList<ValueSetter>();
        List<ValueGetter> getterList = new ArrayList<ValueGetter>();
        List<String> fieldList = new ArrayList<String>();
        List<String> columnList = new ArrayList<String>();
        List<String> transientList = new ArrayList<String>();

        for (Field field : type.getDeclaredFields()) {
            int modifiers = field.getModifiers();
            Transient tt = field.getAnnotation(Transient.class);
            if (! isSupportedProperty(field.getType())) {
                continue;
            } else {
                if (tt != null && tt.cache()) {
                    transientList.add(field.getName());
                }
            }

            if (tt == null && !Modifier.isTransient(modifiers)
                    && !Modifier.isStatic(modifiers)) {//需要持久化，数据库存在与实体对应的字段,不是静态字段   开始

                String fieldName = field.getName();
                String columnName = fieldName;
                Method getter = getter(type, field);
                Method setter = setter(type, field);
                Column colAnno = (Column) field.getAnnotation(Column.class);
                Label labelAnno = (Label) field.getAnnotation(Label.class);
                
                if (colAnno != null) {
                    columnName = colAnno.value();
                    field2columnMap.put(type.getSimpleName() + '-' + fieldName,
                            columnName);
                }
                if (labelAnno != null) {
                    field2labelMap.put(type.getSimpleName() + '-' + fieldName,
                    		labelAnno.value());
                }
                
                if (getter != null && setter != null) {
                    ValueSetter vs = new ValueSetter(setter, fieldName, columnName);
                    ValueGetter vg = new ValueGetter(getter, fieldName, columnName);

                    Refer refer = field.getAnnotation(Refer.class);
                    if (refer != null) {//假如字段是关联对象
                        vs.setFieldPath(refer.field());
                        vg.setFieldPath(refer.field());
                        vg.setReferType(refer.type());
                    }
                    vs.setGetter(vg);
                    
                    
                    /*if (vs.isCreateAt()) {//假如字段是创建时间
                        createAtMap.put(type, vs); 
                        addNotOperation(type, fieldName, notCreateMap);
                        addNotOperation(type, fieldName, notUpdateMap);
                    }
                    if (vs.isCreateById()) { //假如字段是创建人ID
                        createByIdMap.put(type, vs); 
                        addNotOperation(type, fieldName, notCreateMap);
                        addNotOperation(type, fieldName, notUpdateMap);
                    }
                    if (vs.isUpdateAt()) { //假如字段是更新时间
                        updateAtMap.put(type, vs); 
                        addNotOperation(type, fieldName, notCreateMap);
                        addNotOperation(type, fieldName, notUpdateMap);
                    }
                    if (vs.isUpdateById()) {//假如字段是更新人ID
                        updateByIdMap.put(type, vs); 
                        addNotOperation(type, fieldName, notCreateMap);
                        addNotOperation(type, fieldName, notUpdateMap);
                    }*/
                    
                    setterList.add(vs);
                    getterList.add(vg);
                    fieldList.add(fieldName);
                    columnList.add(columnName);
                    
                    NotCreate noCreate = field.getAnnotation(NotCreate.class);
                    if(null != noCreate) addNotOperation(type, fieldName, notCreateMap);
                    
                    NotUpdate noUpdate = field.getAnnotation(NotUpdate.class);
                    if(null != noUpdate) addNotOperation(type, fieldName, notUpdateMap);
                    
                }//getter&setter结束
            } //需要持久化，数据库存在与实体对应的字段   结束
        }//实体所有字段循环结束
        
        if (setterList.size() > 0) {
            setterMap.put(type, setterList);
            getterMap.put(type, getterList);
            fieldListMap.put(type, fieldList);
            columnListMap.put(type, columnList);
            String[] props = new String[setterList.size() + transientList.size()];
            int i = 0;
            for (; i < setterList.size(); i ++) {
                props[i] = setterList.get(i).getFieldName();
            }
            for (int l = i; i < props.length; i ++) {
                props[i] = transientList.get(i - l);
            }
            //fastjson进行数据持久化，即数据库字段与实体字段映射时的过滤器
            SimplePropertyPreFilter pf = new SimplePropertyPreFilter(props);
            propPreFilterMap.put(type, pf);  
        }
    }

    private static Method getter(Class type, Field field) {
        String fieldName = field.getName();
        String methodName = "get" + Character.toUpperCase(fieldName.charAt(0))
                + fieldName.substring(1);
        Method result = null;
        
        try {
            result = type.getMethod(methodName);
            
        } catch (NoSuchMethodException ex) {
            if (field.getType() == boolean.class
                    || field.getType() == Boolean.class) {
                methodName = "is" + Character.toUpperCase(fieldName.charAt(0))
                        + fieldName.substring(1);
                try {
                    result = type.getMethod(methodName);
                } catch (NoSuchMethodException ex1) {
                    throw new RuntimeException(ex1);
                }
            } else {
                throw new RuntimeException(ex);
            }
        }
        
        if (result == null) {
            return null;
        }
        
        if (result.getReturnType() != field.getType()) {
            return null;
        }
        
        return result;
    }

    private static Method setter(Class type, Field field) {
        String fieldName = field.getName();
        String methodName = "set" + Character.toUpperCase(fieldName.charAt(0))
                + fieldName.substring(1);
        Method result = null;
        
        try {
            result = type.getMethod(methodName, field.getType());
            
        } catch (NoSuchMethodException ex) {
            throw new RuntimeException(ex);
        }
        
        if (result == null) {
            return null;
        }
        
        if (result.getReturnType() != void.class) {
            return null;
        }
        
        return result;
    }
    
    
    /* *********************prepareSetterGetterMap 结束*************************** */
    
    /* *******prepareGetIdMap,preparedGetIdsMap,prepareSetIdMap 开始***************** */
    private void prepareGetIdMap(Class type) {
        //String keyFieldName = keyFieldMap.get(type);
    	KeyValue kv = keyValueMap.get(type);
    	if (kv == null) {
    		preparedGetIdsMap(type);
    		return;
    	}
    	String keyFieldName = kv.getFieldName();
        String methodName = "get"
                + Character.toUpperCase(keyFieldName.charAt(0))
                + keyFieldName.substring(1);
        try {
            Method getterMethod = type.getMethod(methodName);
            getIdMap.put(type, getterMethod);
            //keyTypeMap.put(type, getterMethod.getReturnType());
        } catch(NoSuchMethodException nsme) {
        	throw new RuntimeException("method: "+methodName+"no found");
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
    
    private void preparedGetIdsMap(Class type) {
    	KeysValue ksv = keysValueMap.get(type);
        String[] keysField = ksv.getFieldNames();
        Method[] methods = new Method[keysField.length];
        for (int i = 0; i < keysField.length; i ++) {
            String methodName = "get"
                    + Character.toUpperCase(keysField[i].charAt(0))
                    + keysField[i].substring(1);
            try {
                methods[i] = type.getMethod(methodName);
                
            } catch(NoSuchMethodException nsme) {
            	throw new RuntimeException("method: "+methodName+"no found");
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        }
        getIdsMap.put(type, methods);
    }
    
    private void prepareSetIdMap(Class type) {
        //String keyFieldName = keyFieldMap.get(type);
    	KeyValue kv = keyValueMap.get(type);
    	if (kv == null) {
    		return;
    	}
    	
        String keyFieldName = kv.getFieldName();
        
        String methodName = "set"
                + Character.toUpperCase(keyFieldName.charAt(0))
                + keyFieldName.substring(1);
        try {
            setIdMap.put(type, type.getMethod(methodName, kv.getType()));
            
        } catch(NoSuchMethodException nsme) {
        	throw new RuntimeException("method: "+methodName+"no found");
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
    
    /* *******prepareGetIdMap,preparedGetIdsMap,prepareSetIdMap 结束********** */
    
    /* *********************   增删改查sql语句         开始  *************************** */
    private void prepareCreateSqlMap(Class type) {
        StringBuilder buf = new StringBuilder();
        Set<String> notCreateFields = notCreateMap.get(type);//不参与创建的字段
        
        buf.append("insert into ").append(tableNameMap.get(type)).append(" (");
        
        for (ValueSetter setter : setterMap.get(type)) {
        	String columnName = setter.getColumnName();
        	boolean isUpdate = true;
        	
        	if(null != notCreateFields) {
        		for(String fieldName : notCreateFields) {
            		if(fieldName.equalsIgnoreCase(columnName)) {
            			isUpdate = false;
            			break;
            		}
            	}
        	}
        	
            if(isUpdate) buf.append(columnName).append(',');
        }
        
        buf.setCharAt(buf.length() - 1, ')');
        buf.append(" values (");
        
        for (ValueSetter setter : setterMap.get(type)) {
        	String columnName = setter.getColumnName();
        	boolean isUpdate = true;
        	
        	if(null != notCreateFields) {
        		for(String fieldName : notCreateFields) {
            		if(fieldName.equalsIgnoreCase(columnName)) {
            			isUpdate = false;
            			break;
            		}
            	}
        	}
        	
        	if(isUpdate) buf.append(':').append(setter.getColumnName()).append(',');
        }
        
        buf.setCharAt(buf.length() - 1, ')');
        
        createSqlMap.put(type, buf.toString());
    }
    
    private void prepareUpdateSqlMap(Class type) {
    	KeyValue kv = keyValueMap.get(type);
        StringBuilder buf = new StringBuilder();
        Set<String> notUpdateFields = notUpdateMap.get(type);//不参与更新的字段
        
        buf.append("update ").append(tableNameMap.get(type)).append(" set ");
        if (kv != null) {
        	String key = kv.getColumnName();
        	
            for (ValueSetter setter : setterMap.get(type)) {
                String columnName = setter.getColumnName();
                boolean isUpdate = true;
                
                if(null != notUpdateFields) {
                	for(String fieldName: notUpdateFields) {
                    	if(fieldName.equalsIgnoreCase(columnName)) {
                    		isUpdate = false;
                    		break;
                    	}
                    }
                }
                
                
                if (!columnName.equalsIgnoreCase(key) && isUpdate) {
                    buf.append(columnName).append("=:").append(columnName).append(',');
                }
                
            }
            
            buf.setCharAt(buf.length() - 1, ' ');
            buf.append("where ").append(key).append("=:").append(key);
            
        } else {
        	KeysValue ksv = keysValueMap.get(type);
            String[] keys = ksv.getColumnNames();
            String[] fieldNames = ksv.getFieldNames();
            
            for (ValueSetter setter : setterMap.get(type)) {
                String fieldName = setter.getFieldName();
                String columnName = setter.getColumnName();
                
                boolean iskey = false;
                for (String k : fieldNames) {
                    if (fieldName.equalsIgnoreCase(k)) {
                        iskey = true;
                        break;
                    }
                }
                
                boolean isUpdate = true;
                if(null != notUpdateFields) {
                	for(String fName: notUpdateFields) {
                    	if(fName.equalsIgnoreCase(fieldName)) {
                    		isUpdate = false;
                    		break;
                    	}
                    }
                }
                
                
                if (!iskey && isUpdate) {
                    buf.append(columnName).append("=:").append(columnName).append(',');
                }
            }
            
            buf.setCharAt(buf.length() - 1, ' ');
            buf.append("where ");
            
            for (int i = 0, c = keys.length; i < c; i ++) {
                String k = keys[i];
                buf.append(k).append("=:").append(k);
                if (i < c - 1) {
                    buf.append(" and ");
                }
            }
            
        }

        updateSqlMap.put(type, buf.toString());
    }
    
    private void prepareDeleteSqlMap(Class type) {
        StringBuilder buf = new StringBuilder();
        buf.append("delete from ").append(tableNameMap.get(type));

        KeyValue kv = keyValueMap.get(type);
        
        if (null != kv) {
        	String key = kv.getColumnName();
            buf.append(" where ").append(key).append("=?");
        } else {
        	KeysValue ksv = keysValueMap.get(type);
            String[] keys = ksv.getColumnNames();
            buf.append(" where ");
            for (int i = 0, c = keys.length; i < c; i ++) {
                String k = keys[i];
                buf.append(k).append("=?");
                if (i < c - 1) {
                    buf.append(" and ");
                }
            }
        }

        deleteSqlMap.put(type, buf.toString());
    }
    
    private void prepareSelectSqlMap(Class type) {
        StringBuilder buf = new StringBuilder();
        buf.append("select * from ").append(tableNameMap.get(type));

        KeyValue kv = keyValueMap.get(type);
        
        if (null != kv) {
        	String key = kv.getColumnName();
            buf.append(" where ").append(key).append("=?");
            
        } else {
        	KeysValue ksv = keysValueMap.get(type);
            String[] keys = ksv.getColumnNames();
            buf.append(" where ");
            for (int i = 0, c = keys.length; i < c; i ++) {
                String k = keys[i];
                buf.append(k).append("=?");
                if (i < c - 1) {
                    buf.append(" and ");
                }
            }
        }
        
        selectSqlMap.put(type, buf.toString());
        existsSqlMap.put(type, buf.toString().replace("*", "count(1)"));
    }
    
    public String getUpdateCustomSql(Class type, String[] fields, int sqlType) {
    	KeyValue kv = keyValueMap.get(type);
        StringBuilder buf = new StringBuilder();
        String updateSql = "";
        
        buf.append("update ").append(tableNameMap.get(type)).append(" set ");
        
        if (null != kv) {
        	String key = kv.getColumnName();
        	
            for (ValueSetter setter : setterMap.get(type)) {
                String columnName = setter.getColumnName();
                String fieldName = setter.getFieldName();
                boolean update = false;
                
                for(String fName: fields) {
                	if(fName.equalsIgnoreCase(fieldName)) {
                		update = true;
                		break;
                	}
                }
                
                String placeholder = "";
                if(1 == sqlType) placeholder = "?";//jdbcTemplate查询方式
                else if(2 == sqlType) placeholder = ":"+columnName;//namedParameterJdbcTemplate查询方式
                
                if (!columnName.equalsIgnoreCase(key) && update) {
                    buf.append(columnName).append("=").append(placeholder).append(",");
                }
                
            }
            
            String placeholder = "";
            if(1 == sqlType) placeholder = "?";//jdbcTemplate查询方式
            else if(2 == sqlType) placeholder = ":"+key;//namedParameterJdbcTemplate查询方式
            
            buf.setCharAt(buf.length() - 1, ' ');
            buf.append("where ").append(key).append("=").append(placeholder);
            
        } else {
        	KeysValue ksv = keysValueMap.get(type);
            String[] keys = ksv.getColumnNames();
            String[] fieldNames = ksv.getFieldNames();
            
            for (ValueSetter setter : setterMap.get(type)) {
                String columnName = setter.getColumnName();
                String fieldName = setter.getFieldName();
                
                boolean iskey = false;
                for (String k : fieldNames) {
                    if (fieldName.equalsIgnoreCase(k)) {
                        iskey = true;
                        break;
                    }
                }
                
                boolean update = false;
                for(String fName: fields) {
                	if(fName.equalsIgnoreCase(fieldName)) {
                		update = true;
                		break;
                	}
                }
                
                String placeholder = "";
                if(1 == sqlType) placeholder = "?";//jdbcTemplate查询方式
                else if(2 == sqlType) placeholder = ":"+columnName;//namedParameterJdbcTemplate查询方式
                
                if (!iskey && update) {
                    buf.append(columnName).append("=").append(placeholder).append(',');
                }
            }
            
            buf.setCharAt(buf.length() - 1, ' ');
            buf.append("where ");
            
            for (int i = 0, c = keys.length; i < c; i ++) {
                String k = keys[i];
                
                String placeholder = "";
                if(1 == sqlType) placeholder = "?";//jdbcTemplate查询方式
                else if(2 == sqlType) placeholder = ":"+k;//namedParameterJdbcTemplate查询方式
                
                buf.append(k).append("=").append(placeholder);
                if (i < c - 1) {
                    buf.append(" and ");
                }
            }
            
        }
        
        updateSql = buf.toString();
System.out.println("自定义更新语句="+updateSql);
        return updateSql;
    }
    
    
    /* *********************   增删改查sql语句         结束  *************************** */
    
    /* *******************************公共方法        开始******************************** */
    
    //不更新的字段集合
  	private void addNotOperation(Class type, String f, Map<Class, Set<String>>m) {
	  	Set<String> set = m.get(type);
	  	if (set == null) {
	      set = new HashSet<String>();
	      m.put(type, set);
	    }
	    set.add(f);
    }
  	
  	private boolean isSupportedProperty(Class type) {
        if (
                type == String.class ||
                type == long.class ||
                type == int.class ||
                type == Date.class ||
                type == Timestamp.class ||
                type == boolean.class ||
                type == double.class ||
                type == long[].class ||
                type == Long.class ||
                type == Integer.class ||
                type == Boolean.class ||
                type == Double.class ||
                type == byte.class ||
                type == short.class ||
                type == float.class ||
                type == Byte.class ||
                type == Short.class ||
                type == Float.class
           ) {
            return true;
        }

        return false;
    }
  	
  	public Map<String, Object> builderEntityMap(Object obj) {//调用get方法获取对象各字段的值
        Map<String, Object> m = new HashMap<String, Object>();
        
        for (ValueGetter getter : getterMap.get(obj.getClass())) {
            m.put(getter.getColumnName(), getter.get(obj));
        }
        return m;
    }
  	
  	public void setObjectId(Object obj, Object id) {
  		
    	try {
            Method setId = setIdMap.get(obj.getClass());
            setId.invoke(obj, id);

        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }
  	
  	
  	public <T> T mapRow(Class<T> type, ResultSet rs) throws SQLException {
        T obj = null;
        try {
            obj = type.newInstance();
            for (ValueSetter setter : setterMap.get(type)) {
                setter.set(obj, rs);
            }
        } catch (InstantiationException ex) {
            throw new RuntimeException(ex);
        } catch (IllegalAccessException ex) {
            throw new RuntimeException(ex);
        }

        return obj;
    }
  	
  	/* *******************************公共方法       结束******************************** */
  	
	
  	/* *******************************get方法       开始******************************** */
  	
	public String getTableName(Class type) {
        return tableNameMap.get(type);
    }
	
	public String getKeyColumn(Class type) {
		KeyValue kv = keyValueMap.get(type);
        String result = (kv != null ? kv.getColumnName() : "");
        return result;
    }
	
	public String[] getKeyColumns(Class type) {
		KeysValue ksv = keysValueMap.get(type);
		String[] keyColumns = ksv.getColumnNames();
		return keyColumns;
	}
	
	public String getKeyField(Class type) {
		KeyValue kv = keyValueMap.get(type);
        String result = (kv != null ? kv.getFieldName() : "");
        return result;
    }
	
	public String[] getKeyFields(Class type) {
		KeysValue ksv = keysValueMap.get(type);
		String[] keyFields = ksv.getFieldNames();
		return keyFields;
	}
	
	public List<ValueSetter> getSetterList(Class c) {
		List<ValueSetter> list = setterMap.get(c);
		return list;
	}

	public String getCreateSql(Class type) {
		String sql = createSqlMap.get(type);
		if(null == sql) return null;
		return sql;
	}


	public String getSelectSql(Class type) {
		String sql = selectSqlMap.get(type);
		if(null == sql) return null;
		return sql;
	}


	public String getExistsSql(Class type) {
		String sql = existsSqlMap.get(type);
		if(null == sql) return null;
		return sql;
	}


	public String getDeleteSql(Class type) {
		String sql = deleteSqlMap.get(type);
		if(null == sql) return null;
		return sql;
	}


	public String getUpdateSql(Class type) {
		String sql = updateSqlMap.get(type);
		if(null == sql) return null;
		return sql;
	}

	public boolean getMemCache(Class type) {
		boolean cache;
		
		try {
			cache = cacheMap.get(type);
		} catch(NullPointerException ex) {
			return true;  // default true
		}
        
        return cache;
    }
	
	public Object getObjectId(Object obj) {
        try {
            Method getId = getIdMap.get(obj.getClass());
            if (getId != null) {
                return getId.invoke(obj);
            } else {
                Method[] getIds = getIdsMap.get(obj.getClass());
                Object[] ids = new Object[getIds.length];
                for (int i = 0; i < getIds.length; i ++) {
                    ids[i] = getIds[i].invoke(obj);
                }
                return new Mid(ids);
            }
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
	
	public Class getObjectIdType(Class type) {
		KeyValue kv = keyValueMap.get(type);
		KeysValue ksv = keysValueMap.get(type);
		return kv != null ? kv.getKeyType() : ksv.getKeyType();
    }
	
	public int getKeyCount(Class type) {
        return keyValueMap.get(type) != null ? 1 : 2;
    }
	
	public KeyValue getKeyValue(Class type) {
		return keyValueMap.get(type);
	}
	
	
	/* *******************************get方法       结束******************************** */
	
	

}
