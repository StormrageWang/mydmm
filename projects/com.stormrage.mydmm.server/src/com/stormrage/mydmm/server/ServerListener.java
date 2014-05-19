package com.stormrage.mydmm.server;


import javax.naming.NamingException;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.stormrage.mydmm.server.task.TaskFactoryManagerInstance;
import com.stormrage.mydmm.server.task.TaskUtils;
import com.stormrage.mydmm.server.task.dispatch.DefaultDispatchExceptionHandler;
import com.stormrage.mydmm.server.task.dispatch.DispatchTaskFactoryManager;

/**
 * 服务启动监听器
 * @author StormrageWang
 * @date 2014年5月18日
 */
public class ServerListener implements ServletContextListener {

	private DispatchTaskFactoryManager factoryManager = TaskFactoryManagerInstance.getInstance();
	private static Logger logger = LogManager.getLogger();
	
	@Override
	public void contextDestroyed(ServletContextEvent event) {
		factoryManager.stopDispatch();
	}

	@Override
	public void contextInitialized(ServletContextEvent event) {
		//初始化数据源
		try {
			ConnectionProvider.getInstance().initDataSource();
		} catch (NamingException e) {
			logger.error("初始化数据源失败：" + e.getMessage(), e);
		}
		//使用代理设置
		TaskUtils.useProxy("127.0.0.1", "8087");
		//添加需要访问的请求
		factoryManager.setDispacthExceptionHandler(new DefaultDispatchExceptionHandler());
		factoryManager.startDispatch();
	}
	
}
