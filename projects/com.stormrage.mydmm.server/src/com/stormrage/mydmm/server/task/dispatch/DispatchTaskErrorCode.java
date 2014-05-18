package com.stormrage.mydmm.server.task.dispatch;

public class DispatchTaskErrorCode {

	/**
	 * 正常，不会用到
	 */
	public static int OK = 0x00000000;
	/**
	 * 未定义
	 */
	public static int UNDEFINE = 0x00000001;
	
	/**
	 * 对象为空
	 */
	public static int NULL = 0x00100000;
	/**
	 * 任务工厂对象为空
	 */
	public static int NULL_FACTORY = NULL + 1;
	/**
	 * 任务对象为空
	 */
	public static int NULL_TASK = NULL_FACTORY + 1;
	
	
	/**
	 * 中断异常
	 */
	public static int INTERRUPT = 0x00200000;
	/**
	 * 取任务工厂失败
	 */
	public static int INTERRUPT_TAKE = INTERRUPT + 1;
	/**
	 * 线程挂起失败
	 */
	public static int INTERRUPT_SLEEP = INTERRUPT_TAKE + 1;
}
