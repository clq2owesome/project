package com.clq.controller;

import org.apache.log4j.Logger;

/**
 * Controller的基类
 * @author chenliqiang-mac
 *
 */
public abstract class BaseController {
	
	static Logger logger = Logger.getLogger(BaseController.class.getName());
	
	static Logger syslog = Logger.getLogger("syslog");


}
