package com.clq.test;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.clq.core.ClqDao;
import com.clq.core.Mid;
import com.clq.entity.User;


public class UserTest extends AbstractTestCase {
	
	@Autowired
	ClqDao clqDao;

	@Test
	public void getById() {
		System.out.println("clqDao="+clqDao);
		User user = clqDao.find(User.class, new Mid(132, 1));
		System.out.println("user="+user.getCreateBy());
	}
}
