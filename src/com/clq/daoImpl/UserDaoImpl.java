package com.clq.daoImpl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.clq.core.ClqDao;
import com.clq.dao.UserDao;
import com.clq.entity.User;
import com.clq.utils.Collector;
import com.clq.utils.JsonEntityUtil;
import com.whalin.MemCached.MemCachedClient;

@Component(value="userDaoImpl")
public class UserDaoImpl implements UserDao {
	
	@Autowired
	SqlSessionTemplate sqlSessionTemplate;
	
	@Autowired
	MemCachedClient mcc;
	
	@Autowired
	ClqDao clqDao;
	
	
	private String getNamespace() {
		return "user.";
	}

	@Override
	public int saveUser(User user) throws Exception {
		clqDao.create(user);
		return 0;
	}
	
	
	public List<User> listUser() throws Exception {
		Collector c = new Collector();
		c.start("listUser");
		List<User> list = sqlSessionTemplate.selectList(getNamespace() + "listUser");
		c.stop();
System.out.println("c="+c.show());
		String str = JsonEntityUtil.list2String(list);
		Map<String, Object> map = new HashMap<String, Object>();
System.out.println("str=" + str);	
		map.put("haha", list);
		map.put("ts", true);
		map.put("Str", "尼玛b");
		
		String mapStr = JsonEntityUtil.map2String(map);
		Map<String, Object> map1 =  JsonEntityUtil.string2Map(String.class, User.class, mapStr);
		List<User> b =   (List<User>) map1.get("haha");
		for(User u : b) {
			System.out.println("name="+u.getName());
		}
		

		
		return list;
	}
	
	public static void main(String[] args) {
		Map<String, Boolean> map = new HashMap<String, Boolean>();
		User u =  new User();
		User u2 = u;
		u2 = null;
		System.out.println(u);
		System.out.println(u2);
		System.out.println(u == u2);
	}
	
}
