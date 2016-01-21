package com.clq.controller;

import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.clq.core.ClqDao;
import com.clq.core.ResultHandler;
import com.clq.entity.User;
import com.clq.service.UserService;
import com.clq.utils.Env;
import com.clq.utils.EnvUtils;
import com.clq.utils.SqlBuilder;


@Controller
@RequestMapping(value="/admin")
@Scope("prototype")
public class UserController extends BaseController {
	
	
	@Resource(name="userService")
	UserService userService;

	
	@RequestMapping(value="/saveUser.do", method=RequestMethod.POST )
	public void save(HttpServletRequest req, HttpServletResponse rep) {
		
		try {
			
			Env env = EnvUtils.getEnv();
			ResultHandler<List<User>> rh = ResultHandler.extract(req, env, true);
			Date d = new Date(System.currentTimeMillis());
			Timestamp ts = new Timestamp(System.currentTimeMillis());
			String name = env.param("name");
			String password = env.param("password");
			User user = new User();
			user.setName(name);
			user.setPassword(password);
			user.setId2(1);
			user.setCreateAt(ts);
			user.setCreateBy("尼玛");
			user.setUpdateAt(d);
			user.setUpdateBy("你妹");
			
			int id = userService.saveUser(user);
System.out.println("id="+id);

			//测试结果集，一般clqDao不出现在此层，这里为了方便
			SqlBuilder sql = new SqlBuilder();
			sql.appendSql("select * from clq_user");
			ClqDao clqDao = env.getBean(ClqDao.class);
			List<User> list = clqDao.list(User.class, sql.getSql());
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("id2", 0);
			int total = clqDao.count("select count(1) from clq_user where id2 = ?", 0);
			rh.setData(list);
			rh.setTotal(total);
			String result = rh.toStringResult();
System.out.println("result=" + result);
			
		} catch(Exception e) {
			e.printStackTrace();
		}
		
	}
	
	
	@RequestMapping(value="/listUser.do", method=RequestMethod.GET )
	public void listUser(HttpServletRequest req, HttpServletResponse rep) {
		
		try {
			System.out.println("进来啦！！");
			logger.debug("****************尼玛******************");
			syslog.debug("**********你妈＊＊＊＊＊＊＊＊＊＊＊");
			//List<User> list = userService.listUser();
			
		} catch(Exception e) {
			e.printStackTrace();
		}
		
	}
	
	@RequestMapping(value="test1.do", method=RequestMethod.GET)
	public String test1(HttpServletRequest req, HttpServletRequest resp) {
		return "page1";
	}
	
	@RequestMapping(value="test2.do", method=RequestMethod.GET)
	public String test2(HttpServletRequest req, HttpServletRequest resp) {
		return "page2";
	}
	

	
	
}
