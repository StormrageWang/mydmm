package com.stormrage.mydmm.server;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.stormrage.mydmm.server.request.DefaultDispatchExceptionHandler;
import com.stormrage.mydmm.server.request.RequestFactoryManagerInstance;
import com.stormrage.mydmm.server.request.RequestUtils;
import com.stormrage.mydmm.server.task.dispatch.DispatchTaskFactoryManager;

/**
 * 服务启动监听器
 * @author StormrageWang
 * @date 2014年5月18日 下午5:12:15 
 */
public class ServerListener implements ServletContextListener {

	private DispatchTaskFactoryManager factoryManager = RequestFactoryManagerInstance.getInstance();
	
	@Override
	public void contextDestroyed(ServletContextEvent e) {
		factoryManager.stopDispatch();
	}

	@Override
	public void contextInitialized(ServletContextEvent e) {
		//使用代理设置
		RequestUtils.useProxy("127.0.0.1", "8087");
		//添加需要访问的请求
		factoryManager.setDispacthExceptionHandler(new DefaultDispatchExceptionHandler());
		factoryManager.startDispatch();
	}

}
