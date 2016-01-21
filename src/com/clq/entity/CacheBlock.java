package com.clq.entity;

import org.hibernate.validator.constraints.NotEmpty;

import com.clq.annotation.Entity;
import com.clq.annotation.Id;
import com.clq.annotation.Label;



/**
 * 块缓存key
 * @author  clq
 * @create date 2015-12-11
 */
@Entity(tableName="clq_cacheBlock")
@Label("块缓存Key管理")
public class CacheBlock {
	
	public static final int PAGE_CACHE = 1;
	public static final int INTF_CACHE = 2;

	@Id
    @Label("缓存ID")
    long cacheBlockId;
	
	@Label("缓存key")
    @NotEmpty
    String cacheBlockKey;
	
	@Label("缓存名称")
	@NotEmpty
	String cacheBlockName;

    @Label("块缓存类型")
    @NotEmpty
    int cacheBlockType;
    
	public long getCacheBlockId() {
		return cacheBlockId;
	}

	public void setCacheBlockId(long cacheBlockId) {
		this.cacheBlockId = cacheBlockId;
	}

	public String getCacheBlockKey() {
		return cacheBlockKey;
	}

	public void setCacheBlockKey(String cacheBlockKey) {
		this.cacheBlockKey = cacheBlockKey;
	}

	public String getCacheBlockName() {
		return cacheBlockName;
	}

	public void setCacheBlockName(String cacheBlockName) {
		this.cacheBlockName = cacheBlockName;
	}

	public int getCacheBlockType() {
		return cacheBlockType;
	}

	public void setCacheBlockType(int cacheBlockType) {
		this.cacheBlockType = cacheBlockType;
	}


	//@Display
    public String display() {
        StringBuilder buf = new StringBuilder();
        buf.append(cacheBlockType).append('-').append(cacheBlockKey);
        return buf.toString();
    }
	
}
