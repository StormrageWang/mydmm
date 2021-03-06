package com.stormrage.mydmm.server.actress.task;

import com.stormrage.mydmm.server.task.dispatch.IDispatchTask;
import com.stormrage.mydmm.server.task.dispatch.IDispatchTaskFactory;

/**
 * 获取演员信息的需要分发的请求任务工厂
 * @author StormrageWang
 * @date 2014年5月18日
 */
public class ActressTaskFactory implements IDispatchTaskFactory {

	private String url;
	
	public ActressTaskFactory(String url){
		this.url = url;
	}
	
	@Override
	public IDispatchTask getTask() {
		ActressTask actressTask = new ActressTask(url);
		return actressTask;
	}

}
