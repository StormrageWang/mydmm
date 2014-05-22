package com.stormrage.mydmm.server.workdistinct;

import com.stormrage.mydmm.server.task.dispatch.IDispatchTask;
import com.stormrage.mydmm.server.task.dispatch.IDispatchTaskFactory;

public class TorrentFindTaskFactory implements IDispatchTaskFactory{

	private String workCode;
	
	public TorrentFindTaskFactory(String workCode){
		this.workCode = workCode;
	}

	@Override
	public IDispatchTask getTask() {
		return null;
	}
	
	
}
