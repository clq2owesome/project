package com.clq.utils;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.clq.core.ClqDao;
import com.clq.core.ClqOrm;
import com.clq.entity.CacheBlock;
import com.whalin.MemCached.MemCachedClient;


/**
 * 缓存管理服务类
 * @author  clq
 * @create date 2015-12-11
 */

@Component
public class CacheBlockUtils {
	
	//protected static Logger logger = Logger.getLogger("syslog");
	
/*    @Autowired
    SysConfig sysConfig;
    
    @Autowired 
    MemCachedClient mcc;
    
    @Autowired
    ClqDao clqDao;
    
    @Autowired
    ClqOrm clqOrm;*/
    

    
    
    /**
     * 根据md5查找块缓存
     * @param keyMd5
     * @return CacheBlock
     * @author clq
     * @update date 2015-12-11
     */
    public CacheBlock findByKeyMd5(String cacheBlockKey){
    	try{
	    	SqlBuilder sql=new SqlBuilder();
	
	    	sql.appendSql("SELECT cacheBlockId FROM ").appendSql(SpringContextHolder.getBean(ClqOrm.class).getTableName(CacheBlock.class))
	    	   .appendSql(" WHERE cacheBlockKey = ").appendValue(cacheBlockKey);
	    	
	    	List<CacheBlock> list = SpringContextHolder.getBean(ClqDao.class).list(CacheBlock.class, sql.getSql(), sql.getValues());
	    	
	    	if(null != list && list.size() > 0) return list.get(0);
	    	
    	}catch(Exception e){
    		e.printStackTrace();
    		//logger.error(e.getMessage());
    	}
    	
    	return null;
    }
    
    
    /**
     * 根据cacheBlockType查询列表
     * @param cacheBlockType
     * @return List<CacheKey>
     * @author clq
     * @update date 2015-12-11
     */
    public List<CacheBlock> getByCacheBlockType(int cacheBlockType){
    	try{
	    	SqlBuilder sql=new SqlBuilder();
	    	
	    	sql.appendSql("SELECT cacheBlockId FROM ").appendSql(SpringContextHolder.getBean(ClqOrm.class).getTableName(CacheBlock.class))
	    	   .appendSql(" WHERE cacheBlockType = ").appendValue(cacheBlockType);
	    	
	    	return SpringContextHolder.getBean(ClqDao.class).list(CacheBlock.class, sql.getSql(), sql.getValues());
	    	
    	}catch(Exception e){
    		e.printStackTrace();
    		//logger.error(e.getMessage());
    	}
    	return null;
    }
    
 
   /**
   * 删除块缓存
   * @param cacheBlockType 指定缓存的块类型
   * @author clq
   * @update date 2015-12-11
   */
    public void clearBlockCache(int cacheBlockType) {
    	List<CacheBlock> cacheKeyList = getByCacheBlockType(cacheBlockType);
    	
        if(cacheKeyList != null && cacheKeyList.size() > 0){
    		for(CacheBlock cachekey : cacheKeyList){
    			SpringContextHolder.getBean(MemCachedClient.class).delete(cachekey.getCacheBlockKey());//mcc删除缓存
    			SpringContextHolder.getBean(ClqDao.class).delete(CacheBlock.class, cachekey.getCacheBlockId());
    		}
    	}
    }
   
	
    /**
	 * 清除ATS缓存
	 * @param url 指定清除的页面URL
	 * @author clq
	 * @update date 2015-12-11
	 */
    /*public void clearAtsCache(String url){
    	if(StringUtils.isBlank(url)) return;
    	try{
    		String atsUrls = sysConfig.getConfig("ats.urls");//从配置读取ats的URL
    		if(StringUtils.isBlank(atsUrls)) return;
    		
    		String[] atsUrlArr= atsUrls.split(",");//多个
    		for(String atsUrl :atsUrlArr){
    			rClient.get(atsUrl+"delete_url?url="+url, "best.pconline.com.cn", false, 1, TimeUnit.SECONDS ,0);
    		}
    	}catch(Exception e){
    		logger.error(e.getMessage());
    	}
    }*/

   


    
}
