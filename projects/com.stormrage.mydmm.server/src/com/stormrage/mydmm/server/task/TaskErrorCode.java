package com.stormrage.mydmm.server.task;

/**
 * 任务处理错误代码
 * @author StormrageWang
 * @date 2014年5月18日
 */
public class TaskErrorCode {


	public static final int	OK = 0x00000000;
	
	public static final int	UNDEFINE = 0x00100000;
	
	private static final int TASK_REQUEST_START = 0x00200000;
	public static final int TASK_REQUEST_ENCODE = TASK_REQUEST_START + 1;
	public static final int TASK_REQUEST_IO = TASK_REQUEST_START + 2;
	public static final int TASK_REQUEST_EFORMAT = TASK_REQUEST_START + 3;
	
	private static final int TASK_ANALYTICS_START =  0x00300000;
	public static final int TASK_ANALYTICS_GET =  TASK_ANALYTICS_START + 2;
	public static final int TASK_ANALYTICS_UNMATCH =  TASK_ANALYTICS_START + 3;
	public static final int TASK_ANALYTICS_DATABASE = TASK_ANALYTICS_START + 4;
	
}
