package com.clq.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;


/**
 * json与实体转换工具类
 * @author chenliqiang
 * @update date 2015-08-27
 *
 */
public class JsonEntityUtil {
	
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
	 * 将json格式的字符串转为list
	 * @param type list中对象的类型
	 * @param value json格式的字符串
	 * @return
	 */
	public static <T> List<T> string2List(Class<T> type, String value) {
		List<T> list = new ArrayList<T>();
		JSONArray array;
		
		try {
			array = JSON.parseArray(value);
			
			if(array != null && array.size() > 0){
				for(int i=0;i<array.size();i++){
					list.add(string2Object(type, array.getString(i)));
				}
			}
			
		} catch(Exception ex) {
			ex.printStackTrace();
            throw new RuntimeException(ex);
		}
		
		return list;
	}
	
	/**
	 * 将list转为json格式的字符串
	 * @param list
	 * @return
	 */
	public static <T> String list2String(List<T> list) {
		JSONArray array = new JSONArray();
		
		try {
			if(list != null && list.size()>0){
				
				for(T entity : list){
					array.add(object2String(entity));
				}
				return array.toJSONString();
			}
			
		} catch(Exception ex) {
			ex.printStackTrace();
			throw new RuntimeException(ex);
		}
		
		return "";
	}
	
	/**
	 * json格式的字符串转为Map类型
	 * @param ktype  key的类型
	 * @param type   List中对象的类型
	 * @param value  json格式的字符串
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T, K, V> Map<K,V> string2Map(Class<K> ktype, Class<T> type, String value) {
		Map<K,V> result = new HashMap<K,V>();
		HashMap<K, String> map;
		
		try {
			map = string2Object(HashMap.class, value);
			
			for(Map.Entry<K, String> entry : map.entrySet()) {
				
				Object json = JSON.parse(entry.getValue());
				
				if(json instanceof java.util.List) {
					result.put(entry.getKey(), (V) string2List(type, entry.getValue()));
				} else{
					result.put(entry.getKey(), (V) string2Object(getObjType(json), entry.getValue()));
				}
			}
			
		} catch(Exception ex) {
			ex.printStackTrace();
			throw new RuntimeException(ex);
		}
		
		return result;
	}
	
	/**
	 * 将map转为json格式的字符串
	 * @param map
	 * @return
	 */
	public static <K,V> String map2String(Map<K,V> map) {
        if(null == map) {
       		return "";
       	}
        
        HashMap<Object,String> result = new HashMap<Object,String>();
        try {

        	for(Map.Entry<K, V> entry : map.entrySet()){
        		
            	if(entry.getValue()  instanceof java.util.List){
            		
            		result.put(entry.getKey(), list2String((List<?>) entry.getValue()));
            	} else{
            		result.put(entry.getKey(), object2String(entry.getValue()));
            	}
            }
        	
        } catch(Exception ex) {
        	ex.printStackTrace();
			throw new RuntimeException(ex);
        }
        
        return object2String(result);
    }
	
	/**
	 * 得到对象的类型
	 * @param obj  对象
	 * @return
	 */
	public static Class<?> getObjType(Object obj) {
		Class<?> c = null;
		String objName;
		
		try {
			objName = obj.getClass().getCanonicalName();
			
			switch (objName) {
			case "java.util.List":
				c = List.class;
				break;
			case "java.util.ArrayList":
				c = ArrayList.class;
				break;
			case "java.util.LinkedList":
				c = LinkedList.class;
				break;
			case "java.util.Map":
				c = Map.class;
				break;
			case "java.util.HashMap":
				c = HashMap.class;
				break;
			case "java.lang.Integer":
				c = Integer.class;
				break;
			case "java.lang.Boolean":
				c = Boolean.class;
				break;
			case "java.lang.String":
				c = String.class;
				break;
			case "java.lang.Double":
				c = Double.class;
				break;
			case "java.lang.Float":
				c = Float.class;
				break;
			case "java.lang.Long":
				c = Long.class;
				break;
			case "java.lang.Enum":
				c = Enum.class;
				break;
			case "java.lang.Short":
				c = Short.class;
				break;
			default:
				c = Object.class;
				break;
			}
			
		} catch(Exception ex) {
			ex.printStackTrace();
			throw new RuntimeException(ex);
		}

		return c;
		
	}
	
	
	
}
