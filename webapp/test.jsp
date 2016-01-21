<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@include file="/WEB-INF/jspf/import.jspf"%>
<%
	try {
		Env env = EnvUtils.getEnv();
		String cacheKey = env.buildMcKey("http://localhost:8080/springMvcMybatis/test.jsp");
		int time = 1000;
		int CacheBlockType = CacheBlock.PAGE_CACHE;
		String cacheBlockName = "尼玛";
		boolean isSaveDB = true;
		
		pageContext.setAttribute("cacheKey", cacheKey);
		pageContext.setAttribute("time", time);
		pageContext.setAttribute("CacheBlockType", CacheBlockType);
		pageContext.setAttribute("cacheBlockName", cacheBlockName);
		pageContext.setAttribute("isSaveDB", isSaveDB);
	} catch(Exception ex) {
		ex.printStackTrace();
	} 
	
%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
</head>
<body>
	<form action="http://localhost:8080/springMvcMybatis/admin/saveUser.do"  method="post" >
			<div>
				<label>姓名：</label>
				<input  type="text" name="name"  />
			</div>
			
			<div>
				<label>密码：</label>
				<input  type="text" name="password"  />
			</div>
			<input type="submit"  value="提交" />
	</form>
	
	<a href="http://localhost:8080/springMvcMybatis/admin/listUser.do" >获取用户信息</a>
	
	<memcached:cache key="${cacheKey}" time="${time}" cacheBlockName="${cacheBlockName }" 
					 cacheBlockType="${CacheBlockType}" isSaveDB="${isSaveDB }">
	这是缓存块哈aaaaa
	</memcached:cache> 
</body>
</html>