package com.stormrage.mydmm.server.task.dispatch;

/**
 * 任务分发异常处理器
 * @author StormrageWang
 * @date 2014年5月18日
 */
public interface IDispatchExceptionHandler {

	public void handle(DispatchTaskException e);
}
