package com.stormrage.mydmm.server.task.dispatch;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;



/**
 * 默认的网络请求任务分发错误异常处理器
 * @author StormrageWang
 * @date 2014年5月18日
 */
public class DefaultDispatchExceptionHandler implements IDispatchExceptionHandler {

	private static Logger logger = LogManager.getLogger();
	
	@Override
	public void handle(DispatchTaskException e) {
		logger.error("任务分发出错：" + e.getMessage(), e);
		e.printStackTrace();
	}

}
