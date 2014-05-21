package com.stormrage.mydmm.server.actress.task;

import com.stormrage.mydmm.server.task.dispatch.IDispatchTask;
import com.stormrage.mydmm.server.task.dispatch.IDispatchTaskFactory;
import com.stormrage.mydmm.server.task.status.TaskStatus;

/**
 * 获取演员信息的需要分发的请求任务工厂
 * @author StormrageWang
 * @date 2014年5月18日
 */
public class ActressTaskFactory implements IDispatchTaskFactory {

	private String url;
<<<<<<< HEAD
	private TaskStatus taskStatus = TaskStatus.UN_FINISH;
	
=======
>>>>>>> 19044d8455f9fb8840269e3fb4d3cd8b2884baa0
	public ActressTaskFactory(String url){
		this.url = url;
	}
	
	@Override
	public IDispatchTask getTask() {
<<<<<<< HEAD
		ActressTask actressTask = new ActressTask(url);
		return actressTask;
=======
		return new ActressTask(url);
>>>>>>> 19044d8455f9fb8840269e3fb4d3cd8b2884baa0
	}

}
