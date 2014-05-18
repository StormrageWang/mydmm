package com.stormrage.mydmm.server.request;

import com.stormrage.mydmm.server.task.dispatch.DispatchTaskException;
import com.stormrage.mydmm.server.task.dispatch.IDispatchExceptionHandler;


/**
 * 默认的网络请求任务分发错误异常处理器
 * @author StormrageWang
 * @date 2014年5月18日
 */
public class DefaultDispatchExceptionHandler implements IDispatchExceptionHandler {

	@Override
	public void handle(DispatchTaskException e) {
		e.printStackTrace();
	}

}
