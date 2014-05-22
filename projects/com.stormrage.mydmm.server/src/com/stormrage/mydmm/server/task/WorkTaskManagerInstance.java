package com.stormrage.mydmm.server.task;

import com.stormrage.mydmm.server.task.dispatch.DispatchTaskFactoryManager;

/**
 * 任务工厂管理器
 * @author StormrageWang
 * @date 2014年5月18日
 */
public class WorkTaskManagerInstance {
	
	private static DispatchTaskFactoryManager factoryManager = new DispatchTaskFactoryManager("影片信息抓取");
	
	private WorkTaskManagerInstance(){
		
	}
	
	public static DispatchTaskFactoryManager getInstance(){
		return factoryManager;
	}
}
