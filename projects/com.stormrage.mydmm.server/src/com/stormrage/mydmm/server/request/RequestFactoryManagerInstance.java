package com.stormrage.mydmm.server.request;

import com.stormrage.mydmm.server.task.dispatch.DispatchTaskFactoryManager;

/**
 * 网络请求任务工厂管理器
 * @author StormrageWang
 * @date 2014年5月18日
 */
public class RequestFactoryManagerInstance {
	
	private static DispatchTaskFactoryManager factoryManager = new DispatchTaskFactoryManager("影片信息抓取");
	
	private RequestFactoryManagerInstance(){
		
	}
	
	public static DispatchTaskFactoryManager getInstance(){
		return factoryManager;
	}
}
