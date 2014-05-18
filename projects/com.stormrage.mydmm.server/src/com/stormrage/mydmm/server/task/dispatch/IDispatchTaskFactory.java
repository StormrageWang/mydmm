package com.stormrage.mydmm.server.task.dispatch;

/**
 * 要分发的任务工厂
 * @author StormrageWang
 * @date 2014年5月18日
 */
public interface IDispatchTaskFactory {

	public IDispatchTask getTask();
	
}
