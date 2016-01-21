package com.clq.utils;

import java.io.IOException;
import java.util.Date;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;
import javax.servlet.jsp.tagext.TryCatchFinally;

import org.apache.commons.codec.digest.DigestUtils;

import com.clq.core.ClqDao;
import com.clq.entity.CacheBlock;
import com.whalin.MemCached.MemCachedClient;

/**
 * mc页面缓存标签类
 * @author  clq
 * @update date 2015-12-11
 */
public class MemcachedTag extends BodyTagSupport implements TryCatchFinally {
	
	
	long time = 600;//默认10分钟
	String key;
	String content;
	String cacheBlockName;//缓存块名
	int cacheBlockType;//缓存块类型
	boolean refresh = false;//刷新参数
	boolean isSaveDB = false;//是否保存进数据库，以便支持手动清除缓存
	
	public void setTime(long time) {
		this.time = time;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public void setRefresh(String refresh){
		this.refresh = "true".equals(refresh);
	}

	public void setIsSaveDB(boolean isSaveDB) {
		this.isSaveDB = isSaveDB;
	}

	public void setCacheBlockType(int cacheBlockType) {
		this.cacheBlockType = cacheBlockType;
	}

	public void setCacheBlockName(String cacheBlockName) {
		this.cacheBlockName = cacheBlockName;
	}
	
	

	private String getKey() {
		if (key != null) {
			return key;
		}
		return makeKey(pageContext.getRequest());
	}
	
	/**
	 * 根据请求url和参数构造默认的key
	 * @param servletRequest
	 * @return String
	 * @author clq
	 * @update date 2015-12-19
	 */
	private String makeKey(ServletRequest servletRequest) {
		if (null == servletRequest) {
			throw new RuntimeException("ServletRequest cannot be null");
		}

		HttpServletRequest request = (HttpServletRequest) servletRequest;
		
		StringBuilder sb = new StringBuilder();
		sb.append(request.getRequestURL()).append("?").append(request.getQueryString());
		return sb.toString();
	}

	private String getMd5Key() {
		return DigestUtils.md5Hex(getKey());//key用md5
	}
	
	/**
	 * 保存缓存的key
	 * @param md5Key
	 * @author clq
	 * @update date 2015-12-19
	 */
	private void saveKey(String md5Key){
		
		CacheBlockUtils cacheManager = SpringContextHolder.getBean(CacheBlockUtils.class);
		ClqDao clqDao = SpringContextHolder.getBean(ClqDao.class);
		CacheBlock cacheKey = cacheManager.findByKeyMd5(md5Key);
		
		if(cacheKey == null){//如不存在则创建
			cacheKey = new CacheBlock();
			cacheKey.setCacheBlockName(cacheBlockName);
			cacheKey.setCacheBlockKey(getMd5Key());
			cacheKey.setCacheBlockType(cacheBlockType);
			clqDao.create(cacheKey);
		}
		
	}
	
	
	@Override
	public int doStartTag() throws JspException {
		//刷新缓存
		if(refresh){
			return EVAL_BODY_BUFFERED;
		}
		
		content = (String) McClient.get(getMd5Key());
System.out.println("content"+content);
		if (content != null) {
			try {
				pageContext.getOut().write(content);
			} catch (IOException ex) {
				throw new RuntimeException(ex);
			}

			return SKIP_BODY;
		}

		return EVAL_BODY_BUFFERED;
	}

	
	@Override
	public int doAfterBody() throws JspException {
		try {
			content = bodyContent.getString();
			String md5Key = getMd5Key();
			
			if (time > 0) {
				McClient.set(md5Key, content, time);
			}
			
			if(true == isSaveDB){//判断是否支持清除缓存
				saveKey(md5Key);
			}
			
			bodyContent.clearBody();
			bodyContent.write(content);
			bodyContent.writeOut(bodyContent.getEnclosingWriter());
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}

		return SKIP_BODY;
	}

	@Override
	public void doCatch(Throwable ex) throws Throwable {
		throw ex;
	}

	@Override
	public void doFinally() {
		key = null;
		content = null;
	}

	
}
