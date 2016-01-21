package com.clq.dao;

import java.util.List;

import com.clq.entity.User;

public interface UserDao {
	int saveUser(User user) throws Exception;
	List<User> listUser() throws Exception;
}
