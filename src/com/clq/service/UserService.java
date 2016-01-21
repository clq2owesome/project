package com.clq.service;

import java.util.List;

import com.clq.entity.User;

public interface UserService {
	int saveUser(User user) throws Exception;
	List<User> listUser() throws Exception;
}
