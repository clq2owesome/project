package com.clq.serviceImpl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.clq.core.ClqDao;
import com.clq.dao.UserDao;
import com.clq.entity.User;
import com.clq.service.UserService;

@Component("userService")
public class UserServiceImpl implements UserService {
	@Resource(name="userDaoImpl")
	UserDao userDao;
	
	@Override
	public int saveUser(User user) throws Exception {
		
		int id = userDao.saveUser(user);
		
		return id;
	}

	@Override
	public List<User> listUser() throws Exception {
		return userDao.listUser();
	}
	
	
}
