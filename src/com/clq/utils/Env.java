package com.clq.utils;

import javax.servlet.ServletContext;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.PageContext;

import org.springframework.beans.factory.NoUniqueBeanDefinitionException;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;


public class Env {
    private HttpServletRequest request;
    private HttpServletResponse response;
    private PageContext pageContext;
    private ServletContext servletContext;

    /*
     * Web context part
     */
    public HttpServletRequest getRequest() {
        return request;
    }
    public void setRequest(HttpServletRequest request) {
        this.request = request;
    }
    public HttpServletResponse getResponse() {
        return response;
    }
    public void setResponse(HttpServletResponse response) {
        this.response = response;
    }
    public PageContext getPageContext() {
        return pageContext;
    }
    public void setPageContext(PageContext pageContext) {
        this.pageContext = pageContext;
    }
    public ServletContext getServletContext() {
        return servletContext;
    }
    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }
    
    /**
     * 构建页面区块缓存的MC key
     * @param uri 统一资源标示符，可以传入访问路径
     * @param params 需要作为缓存key的参数名称
     * @return
     * String 缓存 key
     * @author Jason Peng
     * @update date 2015年1月27日
     */
    public String buildMcKey(String uri, String... params){
    	if(params == null) return uri;
    	StringBuilder sb = new StringBuilder(uri);
    	
        for(String paramName : params){
            String[] values = request.getParameterValues(paramName);
        	if(values != null){
        		for(String value : values)
        			sb.append("_").append(paramName).append("=").append(value);
        	}
        }
        
        return sb.toString();
    }

    /*
     * Bean factory part
     */
    public WebApplicationContext getApplicationContext(){
        if (request != null) {
            return WebApplicationContextUtils.getWebApplicationContext(servletContext);
        } else {
            return null;
        }
    }

    public <T>T getBean(Class<T> type) {
        try {
			return getApplicationContext().getBean(type);
		} catch (NoUniqueBeanDefinitionException e) {
			String beanName = getApplicationContext().getBeanNamesForType(type)[0];
			return  getApplicationContext().getBean(beanName, type);
        }
    }

    public <T>T getBean(String beanName, Class<T> type) {
        return getApplicationContext().getBean(beanName, type);
    }

    public String param(String name) {
        return request.getParameter(name);
    }

    public String param(String name, String def) {
        String v = request.getParameter(name);
        return v == null ? def : v;
    }

    public int paramInt(String name) {
        return paramInt(name, 0);
    }

    public int paramInt(String name, int def) {
        String v = request.getParameter(name);
        if (v == null || v.length() == 0) {
            return def;
        }
        try {
            return Integer.parseInt(v);
        } catch (Exception ex) {
            return def;
        }
    }

    public long paramLong(String name) {
        return paramLong(name, 0);
    }

    public long paramLong(String name, long def) {
        String v = request.getParameter(name);
        if (v == null || v.length() == 0) {
            return def;
        }
        try {
            return Long.parseLong(request.getParameter(name));
        } catch (Exception ex) {
            return def;
        }
    }

    public double paramDouble(String name) {
        return paramDouble(name, 0.0);
    }

    public double paramDouble(String name, double def) {
        String v = request.getParameter(name);
        if (v == null || v.length() == 0) {
            return def;
        }
        try {
            return Double.parseDouble(request.getParameter(name));
        } catch (Exception ex) {
            return def;
        }
    }

    public String cookie(String cookieName) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie c : cookies) {
                if (c.getName().equals(cookieName)) {
                    return c.getValue();
                }
            }
        }
        return null;
    }

    public void noCache() {
        response.setHeader("Pragma", "No-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setHeader("Cache-Control", "no-store");
        response.setDateHeader("Expires", 0);
    }

}
