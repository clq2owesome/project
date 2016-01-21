package com.clq.core;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import com.alibaba.fastjson.JSON;
import com.clq.utils.SqlBuilder;
import com.whalin.MemCached.MemCachedClient;

public class ClqDao {
	
	private ClqOrm clqOrm;
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	private JdbcTemplate jdbcTemplate;
	private MemCachedClient memCachedClient;
	private CacheListener cacheListener;
	
	public static final int  QUERY_JDBCTEMPLATE = 1;//jdbcTemplate的sql语句形式(xxx=?)
	public static final int  QUERY_NPJDBCTEMPLATE = 2;//namedParameterJdbcTemplate的语句形式(xxx=:xxx)
	
	public void setClqOrm(ClqOrm clqOrm) {
        this.clqOrm = clqOrm;
    }
	
	public void setJdbcTemplate(JdbcTemplate simpleJdbcTemplate) {
        this.jdbcTemplate = simpleJdbcTemplate;
        this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
    }

    public JdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }

    public NamedParameterJdbcTemplate getNamedParameterJdbcTemplate() {
        return namedParameterJdbcTemplate;
    }
    
    public void setMemCachedClient(MemCachedClient memCachedClient) {
        this.memCachedClient = memCachedClient;
    }
    
    public void setCacheListener(CacheListener cacheListener) {
    	this.cacheListener = cacheListener;
    }
    
    
    /**
	 * 将json格式的字符串转为对象（如从缓存中取出转成相应的对象）
	 * @param type  对象的类型
	 * @param value json格式的字符串
	 * @return
	 */
	public static <T> T string2Object(Class<T> type, String value) {
		
        try {
            return JSON.parseObject(value, type);
            
        } catch (Exception ex) {
        	ex.printStackTrace();
            throw new RuntimeException(ex);
        }
    }
	
	/**
	 * 将对象转为json格式的字符串，便于存储于缓存中
	 * @param obj
	 * @return
	 */
	public static String object2String(Object obj) {
		
        try {
            return JSON.toJSONString(obj);
            
        } catch (Exception ex) {
        	ex.printStackTrace();
            throw new RuntimeException(ex);
        }
        
    }
    
    
    
    /**
     * 将对象从缓存中删除(key：对象名_id)
     * @param type 对象
     * @param id 对象的id
     * @author chenliqiang
     * @update date 2015-09-15
     */
    public void deleteCache(Class type, Object id) {
        String cacheKey = null;
        if (memCachedClient != null && clqOrm.getMemCache(type)) {
            cacheKey = getCacheKey(type, id);
            deleteCache(cacheKey);
        }
    }
    
    public static String getCacheKey(Class type, Object id) {
        return type.getSimpleName() + '_' + id;
    }
    
    /*public String builderSqlKey(Class type, String[] fields) throws DataAccessException {
    	if(fields.length == 0) {
    		throw new IllegalStateException("fields is null, builder sqlKey fail,return null");
    	}
    	
    	String tableName = clqOrm.getTableName(type);
    	String sqlKey = type.getSimpleName()+"_";
    	for(String field : fields) {
    		sqlKey += field+"_";
    	}
    	sqlKey = sqlKey.substring(0, sqlKey.length()-1);
    	return sqlKey;
    }*/
    
    public void deleteCache(String key) {
        if (cacheListener != null) {
            cacheListener.start(key, CacheListener.CACHE_DELETE);
        }

        boolean b = memCachedClient.delete(key);
System.out.println("b="+b);
        if (cacheListener != null) {
        	if(true == b) cacheListener.stop(CacheListener.RESULT_SUCCESS);
        	else cacheListener.stop(CacheListener.RESULT_FAIL);
        }
    }
	
    /**
	 * 创建并返回新增条数
	 * @param obj 实体对象
	 * @return int 新增条数
	 * @author chenliqiang
	 * @update date 2015-09-15
	 */
	public int create(Object obj) {
		Class cla = obj.getClass();
		int newCount = 0;
		
		try {
			String createSql = clqOrm.getCreateSql(cla);
System.out.println("createSql="+createSql);
			//Map<String, Object> valueMap = clqOrm.builderEntityMap(obj);
			SqlParameterSource sqlParams = new BeanPropertySqlParameterSource(obj); 
			newCount = namedParameterJdbcTemplate.update(createSql, sqlParams);
			
		} catch(Exception ex) {
			ex.printStackTrace();
			return 0;
		}
		
		return newCount;
	}
	
	/**
	 * 创建并返回自增id
	 * @param obj 实体对象
	 * @return long 自增id
	 * @author chenliqiang
	 * @update date 2015-09-15
	 */
	public long createReKey(Object obj) {
		Class cla = obj.getClass();
		long id = 0;
		
		try {
			String createSql = clqOrm.getCreateSql(cla);
			KeyHolder keyholder = new GeneratedKeyHolder();//获取自动增长的主键
			SqlParameterSource sqlParams = new BeanPropertySqlParameterSource(obj); 
			int i = namedParameterJdbcTemplate.update(createSql, sqlParams, keyholder);
			if(i > 0) id = keyholder.getKey().longValue();
System.out.println("createSql=" + createSql);
		
		} catch(Exception ex) {
			ex.printStackTrace();
			return 0;
		}
		
		return id;
	}
	
	/**
	 * 更新对象并返回更新的条数
	 * @param obj  对象
	 * @return 更新的条数
	 * @author chenliqiang
	 * @update date 2015-09-16
	 */
	public int update(Object obj) {
		Class cla = obj.getClass();
		int updateCount = 0;
		
		try {
			String updateSql = clqOrm.getUpdateSql(cla);
			SqlParameterSource sqlParams = new BeanPropertySqlParameterSource(obj); 
			updateCount = namedParameterJdbcTemplate.update(updateSql, sqlParams);
System.out.println("updateSql=" + updateSql);			
			
			deleteCache(cla, clqOrm.getObjectId(obj));
			
		} catch(Exception ex) {
			ex.printStackTrace();
			return 0;
		}
		
		return updateCount;
	}
	
	/**
	 * 用户自定义更新，提供sql语句和参数(此方法不会删除缓存，需要自行删除)
	 * @param sql 更新语句
	 * @param args  参数
	 * @return 更新的条数
	 * @throws DataAccessException
	 * @author chenliqiang
	 * @update date 2015-09-21
	 */
	public int update(String sql, Object...args) throws DataAccessException {
		int i = jdbcTemplate.update(sql, args);
		return i;
	}
	
	/**
	 * 根据用户提供的需要更新的字段进行更新
	 * @param obj 更新对象
	 * @param fields 更新字段名数组
	 * @return 更新的条数
	 * @throws DataAccessException
	 * @author chenliqiang
	 * @update date 2015-09-21
	 */
	public int update(Object obj, String[] fields) throws DataAccessException{
		Class type = obj.getClass();

		String updateSql = clqOrm.getUpdateCustomSql(type, fields, QUERY_NPJDBCTEMPLATE);
        SqlParameterSource sqlParams = new BeanPropertySqlParameterSource(obj);
        
        int i = namedParameterJdbcTemplate.update(updateSql, sqlParams);
        deleteCache(type, clqOrm.getObjectId(obj));
        return i;
	}
	
	/**
	 * 根据用户提供的需要更新的字段进行更新(此方法不会删除缓存，需要自行删除)
	 * @param type 对象类型
	 * @param fields 更新字段名数组
	 * @param args 不定参数（字段的值）,最后一个值为id
	 * @return 更新的条数
	 * @author chenliqiang
	 * @update date 2015-09-21
	 */
	public int update(Class type, String[] fields, Object...args) {
        String updateSql = clqOrm.getUpdateCustomSql(type, fields, QUERY_JDBCTEMPLATE);
        int i = jdbcTemplate.update(updateSql, args);
        return i;
	}
	
	
	
	/**
	 * 删除对象并返回删除的条数
	 * @param cla 对象类型
	 * @param id 对象的id
	 * @return 删除的条数
	 * @author chenliqiang
	 * @update date 2015-09-15
	 */
	public int delete(Class cla, Object id) {
		int result;
		
		try {
			if (id instanceof Mid) {
	            result = jdbcTemplate.update(clqOrm.getDeleteSql(cla), ((Mid)id).ids);
	            
	        } else {
	            result = jdbcTemplate.update(clqOrm.getDeleteSql(cla), id);
	        }
			
			deleteCache(cla, id);
			
		} catch(Exception ex) {
			ex.printStackTrace();
			return 0;
		}
		
		return result;
	}
	
	/**
	 * 删除指定id的对象并返回成功删除的条数
	 * @param cla 对象类型
	 * @param ids 要删除的对象id数组
	 * @return  成功删除的条数
	 * @author chenliqiang
	 * @update date 2015-09-16
	 */
	public int deleteAll(Class cla, Object[] ids) {
		int delCount = 0;
		for(Object id : ids) {
			int i = delete(cla, id);
			if(i > 0) delCount++;
		}
		
		return delCount;
	}
	
	/**
	 * 查询对象是否存在
	 * @param cla 对象类型
	 * @param id 对象id
	 * @return 存在返回1，不存在返回0
	 * @author chenliqiang
	 * @update date 2015-09-16
	 */
	public int isExists(Class cla, Object id) {
		int result = 0;
		
		try {
			String existSql = clqOrm.getExistsSql(cla);
			if (id instanceof Mid) {
				result = jdbcTemplate.queryForObject(existSql, Integer.class, ((Mid)id).ids);
	        } else {
	        	
	        	result = jdbcTemplate.queryForObject(existSql, Integer.class, id);
	        }
System.out.println("existSql=" + existSql);			
		} catch(Exception ex) {
			ex.printStackTrace();
			return 0;
		}
		
		return result;
	}
	
	/**
	 * 查找对象，先从缓存中查询，没有再从数据库查询
	 * @param type 对象类型
	 * @param id 对象id
	 * @return 对象
	 * @author chenliqiang
	 * @update date 2015-09-16
	 */
	public <T> T find(Class<T> type, Object id) {
        T obj = null;
        
        try {
        	boolean cached = (memCachedClient != null && clqOrm.getMemCache(type));

            if (cached) {
                obj = findCache(type, id);
            }

            if (obj == null) {
                obj = findDb(type, id);
                if (cached) {
                    setCache(type, obj, id);
                }
            }
            
        } catch(Exception ex) {
        	ex.printStackTrace();
        	throw new RuntimeException();
        }
        
        return obj;
    }
	
	/**
	 * 从缓存中查找对象，没有则返回null
	 * @param type 对象类型
	 * @param id 对象id
	 * @return 对象
	 * @author chenliqiang
	 * @update 2015-09-16
	 */
	public <T> T findCache(Class<T> type, Object id) {
        String cacheKey = getCacheKey(type, id);
        
        try {
        	if (memCachedClient != null && clqOrm.getMemCache(type)) {
                if (cacheListener != null) {
                	cacheListener.start(cacheKey, CacheListener.CACHE_FIND);
                }
                
                String objStr = (String) memCachedClient.get(cacheKey);
                
                if (cacheListener != null) {
                	if(null != objStr && "" != objStr) cacheListener.stop(CacheListener.RESULT_SUCCESS);
                	else cacheListener.stop(CacheListener.RESULT_FAIL);
                }

                // Found in cache
                if (objStr != null) {
                    T o = string2Object(type, objStr);
                    return o;
                }

            }
        	
        } catch(Exception ex) {
        	ex.printStackTrace();
        	throw new RuntimeException();
        }

        return null;
    }
	
	/**
	 * 从数据库中查找对象，没有则返回null
	 * @param type 对象类型
	 * @param id 对象id
	 * @return 对象
	 * @author chenliqiang 
	 * @update date 2015-09-16
	 */
	public <T> T findDb(Class<T> type, Object id) {
        T obj = null;
        
        try {
        	if (id instanceof Mid) {
                obj = jdbcTemplate.queryForObject(clqOrm.getSelectSql(type),
                        new ClqRowMapper<T>(type), ((Mid)id).ids);
                
            } else {
                obj = jdbcTemplate.queryForObject(clqOrm.getSelectSql(type),
                        new ClqRowMapper<T>(type), id);
            }
        	
        } catch(Exception ex) {
        	//处理查询不到数据时抛出的异常,查询不到返回null
        	if((ex instanceof IncorrectResultSizeDataAccessException)
                     &&((IncorrectResultSizeDataAccessException)ex).getActualSize()==0)
                return null;

        	ex.printStackTrace();
        }
        
        return obj;
    }
	
	/**
	 * 将对象设入缓存中
	 * @param type 对象类型
	 * @param obj 对象
	 * @param id 对象id
	 * @author chenliqiang
	 * @update date 2015-09-16
	 */
	public <T> void setCache(Class<T> type, Object obj, Object id) {
        String cacheKey = getCacheKey(type, id);
        if (memCachedClient != null && clqOrm.getMemCache(type)) {
            if (cacheListener != null) {
                cacheListener.start(cacheKey, CacheListener.CACHE_ADD);
            }

            boolean b = memCachedClient.set(cacheKey, object2String(obj));

            if (cacheListener != null) {
            	if(true == b) cacheListener.stop(CacheListener.RESULT_SUCCESS);
            	else cacheListener.stop(CacheListener.RESULT_FAIL);
            }
        }

    }
	
	/**
	 * 查询返回对象列表
	 * @param type 对象类型
	 * @param sql sql语句
	 * @param params 各种Object类型，Array
	 * @return List<T>
	 * @throws DataAccessException
	 * @author chenliqiang
	 * @update date 2015-09-18
	 */
	public <T> List<T> list(Class<T> type, String sql, Object... params)
            throws DataAccessException {
        if (memCachedClient != null && clqOrm.getMemCache(type)) {
            Class idType = clqOrm.getObjectIdType(type);
            
            if(idType == int.class || idType == long.class || idType == String.class) {
            	return list(type, jdbcTemplate.query(sql, new RowMapper<T>() {
                    @Override
                    public T mapRow(ResultSet rs, int i) throws SQLException {
                        return (T) rs.getObject(1);
                    }
                }, params));
            	//namedParameterJdbcTemplate.query(sql, paramSource, rowMapper)
                        
            } else if (idType == Mid.class) {
            	int keyCount = clqOrm.getKeyCount(type);
            	if (keyCount > 3) {
            		return Collections.emptyList();
                }
            	if(2 == keyCount) return list(type, jdbcTemplate.query(sql, midIdRowMapper2, params));
            	else if(3 == keyCount) return list(type, jdbcTemplate.query(sql, midIdRowMapper3, params));
                
            } else {
                throw new IllegalStateException("Key type must int, long, String, or mid!");
            }
        } else {//无缓存则直接从数据库中查询
            return jdbcTemplate.query(sql, new ClqRowMapper<T>(type),
                    params);
        }
        return null;
    }
	
	/**
	 * 根据id数组查询列表
	 * @param type 对象类型
	 * @param ids id数组
	 * @return List<T>
	 * @author chenliqiang
	 * @update date 2015-09-18
	 */
	public <T> List<T> list(Class<T> type, Object[] ids) {
		List<Object> idList = new ArrayList<Object>(ids.length);
        for (Object id : ids) {
            idList.add(id);
        }
        
        Object obj = null;
        if (memCachedClient != null && clqOrm.getMemCache(type)) {
        	return list(type, idList);
        } else {
        	List<Object> dataList = new ArrayList<Object>();
        	for(Object id : idList) {
        		obj = findDb(type, id);//查数据库
            	if(null != obj) {
            		dataList.add(obj);
            		setCache(type, obj, id);
            	}
        	}
        	return (List<T>) dataList;
        }
        
    }
	
	/**
	 * 查询总方法，根据传过来的id集合，先从缓存中查找，找不到再查询数据库，并存入缓存中
	 * @param type 对象类型
	 * @param idList id集合
	 * @return List<T>
	 * @author chenliqiang
	 * @update date 2015-09-18
	 */
	<T> List<T> list(Class<T> type, List idList) {
        List<Object> dataList = new ArrayList<Object>();
       
        if (idList.isEmpty()) {
        	return Collections.emptyList();
        }
        
        Object o = null;
        for (int i = 0; i < idList.size(); i ++) {
            Object id = idList.get(i);
            String key = getCacheKey(type, id);
            
        	if (cacheListener != null) {
                cacheListener.startAll(key, CacheListener.CACHE_FIND);
            }
            
            o = memCachedClient.get(key);//查缓存
            
            if(i == idList.size()-1) {
            	if (cacheListener != null) {
                	if(null != o) cacheListener.stopAll(CacheListener.RESULT_SUCCESS);
                	else cacheListener.stopAll(CacheListener.RESULT_FAIL);
                }
            }
            
            Object obj = null;
            if(null != o) {
            	obj = string2Object(type, (String)o);
            	dataList.add(obj);
            } else {
            	obj = findDb(type, id);//查数据库
            	if(null != obj) {
            		dataList.add(obj);
            		setCache(type, obj, id);
            	}
            }
        } 

        return (List<T>) dataList;
        
    }
	
	
	/**
	 * 分页查询数据
	 * @param type 对象类型
	 * @param sql sql语句
	 * @param pageNo 当前页码
	 * @param pageSize 每页查询条数
	 * @param params 查询参数
	 * @return List<T>
	 * @throws DataAccessException
	 * @author chenliqiang
	 * @update date 2015-09-18
	 */
	public <T> List<T> page(Class<T> type, String sql, int pageNo, int pageSize,
            Object... params) throws DataAccessException {

        return list(type, new SqlBuilder().buildPageSql(sql, pageNo, pageSize),
                params);
    }
	
	public <T> List<T> page(Class<T> type, String sql, int pageNo, int pageSize,
            Map<String, ?> paramMap) throws DataAccessException {

        return list(type, new SqlBuilder().buildPageSql(sql, pageNo, pageSize),
                paramMap);
    }
	
	
	
	//查询总数
	public <T>int count(Class<T> type, String criteria, Object... params)
            throws DataAccessException {

        return jdbcTemplate.queryForObject("select count(1) from " + 
                clqOrm.getTableName(type) + " where " + criteria, Integer.class, params);
    }

    public <T>int count(Class<T> type, String criteria, Map<String, ?> paramMap)
            throws DataAccessException {

        return jdbcTemplate.queryForObject("select count(1) from " + 
        		clqOrm.getTableName(type) + " where " + criteria, Integer.class, paramMap);
    }

    public <T>int count(String sql, Object... params)
            throws DataAccessException {

        return jdbcTemplate.queryForObject(sql, Integer.class, params);
    }

    public <T>int count(String sql, Map<String, ?> paramMap)
            throws DataAccessException {

        return jdbcTemplate.queryForObject(sql, Integer.class, paramMap);
    }
	
	
	
	public static RowMapper<Mid> midIdRowMapper2 = new RowMapper<Mid>() {
        @Override
        public Mid mapRow(ResultSet rs, int i) throws SQLException {
        	Object[] args = new Object[2];
            for (int j = 0; j < 2; j++) {
                args[j] = rs.getObject(j + 1);
            }
            return new Mid(args);
        }
    };
    
    public static RowMapper<Mid> midIdRowMapper3 = new RowMapper<Mid>() {
        @Override
        public Mid mapRow(ResultSet rs, int i) throws SQLException {
        	Object[] args = new Object[2];
            for (int j = 0; j < 3; j++) {
                args[j] = rs.getObject(j + 1);
            }
            return new Mid(args);
        }
    };
	
	public static RowMapper<Long> longIdRowMapper = new RowMapper<Long>() {
        @Override
        public Long mapRow(ResultSet rs, int i) throws SQLException {
            return rs.getLong(1);
        }
    };

    public static RowMapper<String> stringIdRowMapper = new RowMapper<String>() {
        @Override
        public String mapRow(ResultSet rs, int i) throws SQLException {
            return rs.getString(1);
        }
    };
    
    public static RowMapper<Integer> intIdRowMapper = new RowMapper<Integer>() {
        @Override
        public Integer mapRow(ResultSet rs, int i) throws SQLException {
            return rs.getInt(1);
        }
    };
	
	private class ClqRowMapper<T> implements RowMapper<T> {
        Class<T> type;
        public ClqRowMapper(Class<T> type) {
            this.type = type;
        }

        @Override
        public T mapRow(ResultSet rs, int i) throws SQLException {
            return clqOrm.mapRow(type, rs);
        }
    }
	
}
