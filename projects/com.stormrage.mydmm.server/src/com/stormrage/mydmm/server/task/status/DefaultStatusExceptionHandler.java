package com.stormrage.mydmm.server.task.status;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DefaultStatusExceptionHandler implements ITaskStatusExceptionHandler {

	private static Logger logger = LogManager.getLogger();
	@Override
	public void handle(TaskStatusException e) {
		logger.error("管理任务状态出错：" + e.getMessage(), e);
	}

}
