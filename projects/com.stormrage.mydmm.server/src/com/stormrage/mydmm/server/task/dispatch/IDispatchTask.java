package com.stormrage.mydmm.server.task.dispatch;

/**
 * 要分发的任务
 * @author StormrageWang
 * @date 2014年5月18日
 */

public interface IDispatchTask {
	/**
	 * 获取任务名称
	 * @return
	 */
	public String getName();
	
	/**
	 * 执行任务
	 */
	public void run();
	
}
