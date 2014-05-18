package com.stormrage.mydmm.server.task.dispatch;

import java.util.concurrent.LinkedBlockingQueue;

public class DispatchTaskFactoryManager {
	
	/**
	 * 任务分发管理器名称
	 */
	private String name;
	/**
	 * 任务分发异常处理器
	 */
	private IDispatchExceptionHandler handler = null;
	/**
	 * 任务分发线程
	 */
	private DispatchThread dispatchThread = null;
	/**
	 * 一个支持线程同步的队列
	 */
	private LinkedBlockingQueue<IDispatchTaskFactory> taskQueue = null;
	
	public DispatchTaskFactoryManager(String name){
		this.name = name;
		taskQueue = new LinkedBlockingQueue<IDispatchTaskFactory>();
		dispatchThread = new DispatchThread(this);
	}
	
	public String getName() {
		return name;
	}
	
	public void startDispatch() {
		dispatchThread.start();
	}
	
	public void stopDispatch(){
		dispatchThread.setRunning(false);
		//添加一个空请求以唤醒等待的线程
		addDispatchFactory(new EmptyDispatchTaskFactory());
	}
	
	/**
	 * 设置分发异常处理器
	 * @param handler
	 */
	public void setDispacthExceptionHandler(IDispatchExceptionHandler handler){
		this.handler = handler;
	}
	
	/**
	 * 获取任务异常处理器
	 * @return
	 */
	public IDispatchExceptionHandler getDispatchExceptionHandler(){
		return handler;
	}
	
	/**
	 * 处理分发异常
	 * @param e 分发异常
	 */
	private void handleDispacthException(DispatchTaskException e){
		if(handler == null){
			return;//TODO 打印日志
		}
		handler.handle(e);
	}
	
	/**
	 * 添加分发任务工厂
	 * @param factory 分发任务工厂
	 */
	public void addDispatchFactory(IDispatchTaskFactory factory) {
		if(factory == null){
			handleDispacthException( 
					new DispatchTaskException("不能添加空的分发任务工厂", DispatchTaskErrorCode.NULL_FACTORY));
		}
		taskQueue.add(factory);
	}
	
	/**
	 * 取分发任务工厂
	 * @return 分发任务工厂
	 */
	protected IDispatchTaskFactory takeFirstRequestFactory() {
		try {
			return taskQueue.take();
		} catch (InterruptedException e) {
			handleDispacthException(
					new DispatchTaskException("取分发任务工厂出现异常", e.fillInStackTrace(), DispatchTaskErrorCode.INTERRUPT_TAKE));
			return null;
		}
	}
	
}
