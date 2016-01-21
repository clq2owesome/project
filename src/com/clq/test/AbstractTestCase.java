package com.clq.test;

import javax.naming.NamingException;

import org.apache.log4j.Logger;
import org.junit.runner.RunWith;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.mock.jndi.SimpleNamingContextBuilder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;


/**
 * 
 * abstract unit test class
 * 
 * 1.单元测试抽象父类-可以mock jndi，极大简化了对环境的依赖，除了jdbc配置，包括r系统等常用依赖
 * 2.直接共用开发的配置文件，不需要再整一套配置文件，方便快捷
 * 
 * @author  clq
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration(locations = {"classpath:applicationContext.xml"})
//spring applicationContext.xml          
@ContextConfiguration(locations = {"file:/Users/chenliqiang-mac/Documents/workspace/springMvcMybatis/webapp/WEB-INF/applicationContext.xml"})
@TestExecutionListeners({DependencyInjectionTestExecutionListener.class})
public abstract class AbstractTestCase{
	protected Logger logger = Logger.getLogger("syslog");
	
    static {
        try {
            SimpleNamingContextBuilder builder = SimpleNamingContextBuilder.emptyActivatedContextBuilder();
            //java:comp/env/props  
            builder.bind("java:comp/env/props","/Users/chenliqiang-mac/Documents/data/clq_envirement.properties");
            
            //java:comp/env/memCachedClientConfig4RClient
            StringBuffer sb = new StringBuffer();
            sb.append("servers=192.168.20.118:11211\n");
            sb.append("initConn=20\n");
            sb.append("minConn=10\n");
            sb.append("maxConn=50\n");
            sb.append("maintSleep=30\n");
            sb.append("nagle=false\n");
            sb.append("socketTO=3000\n");
            builder.bind("java:comp/env/memCachedClientConfig4RClient", sb.toString());
            

            //binding datasource
            DriverManagerDataSource dataSource = new DriverManagerDataSource();
            dataSource.setDriverClassName("org.gjt.mm.mysql.Driver");
            dataSource.setUrl("jdbc:mysql://localhost:3306/clq?useUnicode=true&amp;characterEncoding=GBK&amp;zeroDateTimeBehavior=convertToNull");
            dataSource.setUsername("root");
            dataSource.setPassword("root");
            //builder.bind("jdbc/best", dataSource);
            builder.bind("java:comp/env/jdbc/best", dataSource); //modify by Mike He 20151129 !!!

          

            
            builder.activate();
            //This is default test output directory, if changed in pom.xml please modify this bingding too.
        } catch (NamingException ex) {
            ex.printStackTrace();
        }

    } 
}     
