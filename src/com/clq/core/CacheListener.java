package com.clq.core;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 缓存监听器
 * @author 陈礼强
 * @update date 2015-09-15
 */
public class CacheListener {
	public static final String CACHE_UPDATE = "cache_update";  //更新缓存
	public static final String CACHE_DELETE = "cache_delete";  //删除缓存
	public static final String CACHE_QUERY = "cache_query";  //查询缓存（多条）
	public static final String CACHE_FIND = "cache_find";  //查询缓存（对象）
	public static final String CACHE_ADD = "cache_add";  //添加缓存
	
	public static final String RESULT_SUCCESS = "success!!";  //添加缓存
	public static final String RESULT_FAIL = "fail!!";  //添加缓存
	
	CacheItem cacheItem;
	List<CacheItem> itemList = null;
	
	public void start(String key, String type) {
		cacheItem = new CacheItem(key, type);
		cacheItem.start = System.currentTimeMillis();
	}
	
	public void stop(String result) {
		cacheItem.result = result;
		cacheItem.usedMillis = System.currentTimeMillis() - cacheItem.start;
		cacheLog();
		cacheItem = null;
    }
	
	public void startAll(String key, String type) {
		start(key, type);
		if(null == itemList) itemList = new ArrayList<CacheItem>();
		itemList.add(cacheItem);
	}
	
	public void stopAll(String result) {
		long totalMillis = 0;
		StringBuilder buf = new StringBuilder();
		for(CacheItem ci : itemList) {
			long ms = System.currentTimeMillis() - ci.start;
			totalMillis += ms;
			buf.append(ci.key + ",");
		}
		buf.setCharAt(buf.length() - 1, ' ');
		
		StringBuilder sb = new StringBuilder();
		SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		sb.append("[" + sf.format(new Date(System.currentTimeMillis())) + "] ")
		  .append("[size:" + itemList.size() + "] ")
		  .append("[mc-key:" + buf.toString() + "] ")
		  .append("[result:"+result+"] ")
		  .append("use " + totalMillis + "ms");
		
System.out.println(sb.toString());
		itemList = null;
	}
	
	public String cacheLog() {
		System.out.println(cacheItem.toString());
		return cacheItem.toString();
	}
	
	
	class CacheItem {
		String key;
		long start;
		long usedMillis;
		String type;
		String result;
		
		public CacheItem(String key, String type) {
			this.key = key;
			this.type = type;
		}
		
		@Override
        public String toString() {
			StringBuilder sb = new StringBuilder();
			SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			sb.append("[" + sf.format(new Date(System.currentTimeMillis())) + "] ")
			  .append("[" + type + "] ")
			  .append("[mc-key:"+key+"] ")
			  .append("[result:"+result+"] ")
			  .append("use " + usedMillis + "ms");

            return sb.toString();
        }
	}

}
