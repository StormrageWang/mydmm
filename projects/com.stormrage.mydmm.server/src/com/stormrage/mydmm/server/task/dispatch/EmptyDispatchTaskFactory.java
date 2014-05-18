package com.stormrage.mydmm.server.task.dispatch;

/**
 * 空的要分发的任务工厂
 * @author StormrageWang
 * @date 2014年5月18日
 */
public class EmptyDispatchTaskFactory implements IDispatchTaskFactory{

	@Override
	public IDispatchTask getTask() {
		return null;
	}

}
