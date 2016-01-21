package com.clq.utils;

import java.util.Date;

import org.apache.log4j.Logger;

import com.schooner.MemCached.MemcachedItem;
import com.whalin.MemCached.MemCachedClient;
import com.whalin.MemCached.SockIOPool;
 

/**
 * @ClassName: MemcachedUtils
 * @Description: Memcached工具类
 * @author clq
 * @date 2015-12-1
 */

public class McClient {

    private static final Logger logger = Logger.getLogger(McClient.class); 
        
    public static final int THIRTY_MINUTE_EXPIRE = 30 * 60; //缓存30分钟
    
    public static final int ONE_HOUR_EXPIRE = 60 * 60; //缓存一个小时
    
    public static final int ONE_DAY_EXPIRE = 24 * 60 * 60; //缓存一天
    
    public static final int ONE_WEEK_EXPIRE = 7 * 24 * 60 * 60; //缓存一个星期
    
    public static final int ONE_MONTH_EXPIRE = 30 * 24 * 60 * 60; //缓存一个月


	private static MemCachedClient memcachedClient;

    static {
    	//名字需要取applicationContext.xml中memcached线程池中构造函数的名称
    	if(null == memcachedClient) memcachedClient = new MemCachedClient("clq");
    }
    
    private McClient() {} 


    /**
     * 向缓存添加新的键值对。如果键已经存在，则之前的值将被替换。
     * 若key值不存在则插入，存在则更新，使用mc默认缓存
     * @param key键
     * @param value值
     * @return
     */ 
    public  static boolean set(String key, Object value) { 
    	
    	return setExp(key, value, null); 

    } 

 

    /**
     * 向缓存添加新的键值对。如果键已经存在，则之前的值将被替换。
     * 若key值不存在则插入，存在则更新，自定义缓存时间
     * @param key键
     * @param value值
     * @param expire 过期时间 New Date(1000*10)：十秒后过期
     * @return
     */ 
    public static boolean set(String key, Object value, long expire) { 

        return setExp(key, value, new Date(System.currentTimeMillis() + 1000 * expire)); 

    } 

 

    /**
     * 向缓存添加新的键值对。如果键已经存在，则之前的值将被替换。
     * 若key值不存在则插入，存在则更新，set总方法
     * @param key键
     * @param value值
     * @param expire 过期时间 New Date(1000*10)：十秒后过期
     * @return
     */ 
    private static boolean setExp(String key, Object value, Date expire) { 

        boolean flag = false; 

        try { 

            flag = memcachedClient.set(key, value, expire); 

        } catch (Exception e) { 
            // 记录Memcached日志 
            logger.error("Memcached set方法报错，key值：" + key + "        value值：" + value + "\r\n"); 
            e.printStackTrace();
        } 

        return flag; 
    } 

 

    /**
     * 仅当缓存中不存在键时，add 命令才会向缓存中添加一个键值对。
     * 若key值已存在，则返回false，使用mc默认缓存
     * @param key键
     * @param value值
     * @return
     */ 
    public static boolean add(String key, Object value) { 

        return addExp(key, value, null); 

    } 

 

    /**
     * 仅当缓存中不存在键时，add 命令才会向缓存中添加一个键值对。
     * 若key值已存在，则返回false，自定义缓存时间
     * @param key键
     * @param value值
     * @param expire 过期时间 New Date(1000*10)：十秒后过期
     * @return
     */ 
    public static boolean add(String key, Object value, long expire) { 

        return addExp(key, value, new Date(System.currentTimeMillis() + 1000 * expire)); 

    }

 

    /**
     * 仅当缓存中不存在键时，add 命令才会向缓存中添加一个键值对。
     * 若key值已存在，则返回false，add总方法
     * @param key键
     * @param value值
     * @param expire 过期时间 New Date(1000*10)：十秒后过期
     * @return
     */ 
    private static boolean addExp(String key, Object value, Date expire) { 

        boolean flag = false; 

        try { 

            flag = memcachedClient.add(key, value, expire); 

        } catch (Exception e) { 
            // 记录Memcached日志 
            logger.error("Memcached add方法报错，key值：" + key + "       value值：" + value +"\r\n"); 
            e.printStackTrace();
        } 

        return flag; 

    } 

 

    /**
     * 仅当键已经存在时，replace 命令才会替换缓存中的键。
     * 当key值不存在时返回false,使用mc默认缓存
     * @param key键
     * @param value值
     * @return
     */ 
    public static boolean replace(String key, Object value) { 

        return replaceExp(key, value, null); 

    } 

 

    /**
     * 仅当键已经存在时，replace 命令才会替换缓存中的键。
     * 当key值不存在时返回false,自定义缓存时间
     * @param key键
     * @param value值
     * @param expire 过期时间 New Date(1000*10)：十秒后过期
     * @return
     */ 
    public static boolean replace(String key, Object value, long expire) { 

        return replaceExp(key, value, new Date(System.currentTimeMillis() + 1000 * expire)); 

    } 

 

    /**
     * 仅当键已经存在时，replace 命令才会替换缓存中的键。
     * 当key值不存在时返回false,replace总方法
     * @param key键
     * @param value值
     * @param expire 过期时间 New Date(1000*10)：十秒后过期
     * @return
     */ 
    private static boolean replaceExp(String key, Object value, Date expire) { 

        boolean flag = false; 

        try { 

            flag = memcachedClient.replace(key, value, expire); 

        } catch (Exception e) { 
        	// 记录Memcached日志 
            logger.error("Memcached replace方法报错，key值：" + key + "       value值：" + value +"\r\n"); 
            e.printStackTrace();
        } 

        return flag; 
    } 
    
    /**
     * 原子性操作，先检查当前版本号与所存是否一致，
     * 相同则保存，否则不保存，返回false，与gets联合使用
     * 使用mc默认缓存时间
     * @param key
     * @param value
     * @param num
     * @return
     */
    public static boolean css(String key, Object value, long num) {
    	
    	return casExp(key, value, null, num);
    	
    }
    
    /**
     * 原子性操作，先检查当前版本号与所存是否一致，
     * 相同则保存，否则不保存，返回false，与gets联合使用
     * 使用自定义缓存时间
     * @param key
     * @param value
     * @param num
     * @return
     */
    public static boolean css(String key, Object value, long expire, long num) {
    	
    	return casExp(key, value, new Date(System.currentTimeMillis() + 1000 * expire), num);
    	
    }
    
    /**
     * cas总方法，原子性操作，先检查当前版本号与所存是否一致，
     * 相同则保存，否则不保存，返回false，与gets联合使用
     * @param key
     * @param value
     * @param num
     * @return
     */
    private static boolean casExp(String key , Object value, Date expire, long num) {
    	
    	boolean flag = false;
    	
    	try {
    		flag = memcachedClient.cas(key, value, expire, num);
    		
    	} catch(Exception e) {
    		// 记录Memcached日志 
            logger.error("Memcached replace方法报错，key值：" + key + "       value值：" + value +"\r\n"); 
            e.printStackTrace();
    	}
    	
    	return flag;
    }
    
    /**
     * 获取MemcachedItem对象，与cas方法一起使用
     * @param key
     * @return
     */
    public static MemcachedItem gets(String key) {
    	
    	MemcachedItem obj = null; 

        try { 

            obj = memcachedClient.gets(key);

        } catch (Exception e) { 
        	// 记录Memcached日志 
            logger.error("Memcached get方法报错，key值：" + key + "\r\n"); 
            e.printStackTrace();
        } 
        return obj; 
    }

 

    /**
     * get 命令用于检索与之前添加的键值对相关的值。
     * @param key键
     * @return
     */ 
    public static Object get(String key) { 

        Object obj = null; 

        try { 

            obj = memcachedClient.get(key); 

        } catch (Exception e) { 
        	// 记录Memcached日志 
            logger.error("Memcached get方法报错，key值：" + key + "\r\n"); 
            e.printStackTrace();
        } 
        return obj; 

    } 

 

    /**
     * 删除 memcached 中的任何现有值。
     * 删除总方法(deprecated)
     * @param key键
     * @return
     */ 
    public static boolean delete(String key) { 

        boolean flag = false; 

        try { 
            flag = memcachedClient.delete(key); 

        } catch (Exception e) { 
        	// 记录Memcached日志 
            logger.error("Memcached delete方法报错，key值：" + key + "\r\n");
            e.printStackTrace();
        } 

        return flag; 
    } 

 

    /**
     * 清理缓存中的所有键/值对
     * @return
     */ 
    public static boolean flashAll() { 

        boolean flag = false; 

        try { 

            flag = memcachedClient.flushAll(); 

        } catch (Exception e) { 
        	// 记录Memcached日志 
            logger.error("Memcached flashAll方法报错 \r\n"); 

        } 

        return flag; 
    } 

 
    
    public static void main(String[] args) {
    	//初始化SockIOPool，管理memcached的连接池  
        String[] servers = { "127.0.0.1:11211" };  
        SockIOPool pool = SockIOPool.getInstance("test");  
        pool.setServers(servers);  
        pool.setFailover(true);  
        pool.setInitConn(10);  
        pool.setMinConn(5);  
        pool.setMaxConn(250);  
        pool.setMaintSleep(30);  
        pool.setNagle(false);  
        pool.setSocketTO(3000);  
        pool.setAliveCheck(true);  
        pool.initialize();   
       //建立MemcachedClient实例
        MemCachedClient mcc = new MemCachedClient("test");
    	
        mcc.set("aa", "123");
    	System.out.println("aa="+mcc.get("aa"));
    	
    	MemcachedItem mi1 = mcc.gets("aa");
    	
    	System.out.println(mi1.getCasUnique());
    	
    	boolean b1 = mcc.cas("aa", "456", mi1.getCasUnique());
    	boolean b2 = mcc.cas("aa", "789", mi1.getCasUnique());
    	
    	MemcachedItem mi2 = mcc.gets("aa");
    	System.out.println(mi2.getCasUnique());
    	System.out.println("aa="+mcc.get("aa"));
    	System.out.println(b1);
    	System.out.println(b2);
    }

        

}
