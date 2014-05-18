package com.stormrage.mydmm.server.request;

import com.stormrage.mydmm.server.task.dispatch.DispatchTaskFactoryManager;



public class RequestFactoryManagerInstance {
	
	private static DispatchTaskFactoryManager factoryManager = new DispatchTaskFactoryManager("影片信息抓取");
	
	private RequestFactoryManagerInstance(){
		
	}
	
	public static DispatchTaskFactoryManager getInstance(){
		return factoryManager;
	}
}
