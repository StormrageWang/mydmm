package com.stormrage.mydmm.server.task;

import com.stormrage.mydmm.server.task.dispatch.DispatchTaskFactoryManager;

public class TorrentTaskManagerInstance {

	private static DispatchTaskFactoryManager factoryManager = new DispatchTaskFactoryManager("影片种子抓取");
	
	private TorrentTaskManagerInstance(){
		
	}
	
	public static DispatchTaskFactoryManager getInstance(){
		return factoryManager;
	}
}
