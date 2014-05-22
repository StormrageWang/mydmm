package com.stormrage.mydmm.server.workdistinct;

import com.stormrage.mydmm.server.task.dispatch.IDispatchTask;

public class TorrentFindTask implements IDispatchTask {

	private String workCode;
	
	public TorrentFindTask(String workCode){
		this.workCode = workCode;
	}
	
	@Override
	public String getName() {
		return null;
	}

	@Override
	public void run() {

	}

}
