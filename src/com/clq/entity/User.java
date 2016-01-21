package com.clq.entity;


import java.util.Date;

import javax.persistence.GeneratedValue;

import com.clq.annotation.Column;
import com.clq.annotation.Entity;
import com.clq.annotation.Id;
import com.clq.annotation.Label;
import com.clq.annotation.NotCreate;
import com.clq.annotation.NotUpdate;

@Entity(tableName = "clq_user")
@Label("用户表")
public class User {
	public static final int XX = 1;
	@Id
	@GeneratedValue
	private int id;
	
	@Id
	private int id2;
	
	@Label("用户名")
	private String name;
	
	@Label("密码")
	private String password;
	
	@NotUpdate
	@Label("创建人")
	@Column("createBy")
	private String createBy;
	
	@NotUpdate
	@Label("创建时间")
	@Column("createAt")
	private Date createAt;
	
	@NotCreate
	@Label("更新人")
	private String updateBy;
	
	@NotCreate
	@Label("更新时间")
	private Date updateAt;
	
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getId2() {
		return id2;
	}
	public void setId2(int id2) {
		this.id2 = id2;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getCreateBy() {
		return createBy;
	}
	public void setCreateBy(String createBy) {
		this.createBy = createBy;
	}
	public Date getCreateAt() {
		return createAt;
	}
	public void setCreateAt(Date createAt) {
		this.createAt = createAt;
	}
	public String getUpdateBy() {
		return updateBy;
	}
	public void setUpdateBy(String updateBy) {
		this.updateBy = updateBy;
	}
	public Date getUpdateAt() {
		return updateAt;
	}
	public void setUpdateAt(Date updateAt) {
		this.updateAt = updateAt;
	}
	
	
}
