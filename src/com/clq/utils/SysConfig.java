package com.clq.utils;

import java.util.Properties;

/**
 * 读取配置信息（由spring读取并注入）
 * @author clq
 * @update date 2015-04-14
 * 
 */
public class SysConfig {
	private Properties properties;

    public SysConfig(Properties properties) {

        this.properties = properties;
        System.out.println("********************"+getConfig("pcbaby.root"));
    }

    public String getConfig(String key) {

        return properties.getProperty(key);
    }

    public String getConfig(String key, String defaultValue) {

        return properties.getProperty(key, defaultValue);
    }
}
