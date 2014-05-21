package com.stormrage.mydmm.server.task.dispatch;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 任务分发线程
 * @author StormrageWang
 * @date 2014年5月18日
 */
public class DispatchThread extends Thread {

	/**
	 * 间隔0.2秒
	 */
	private static final int INTERVAL = 200;
	/**
	 * 最多能有10个线程
	 */
	private static final int MAX_THREAD_COUNT = 10;
	private static Logger logger = LogManager.getLogger();
	
	private int threadCount = 0;
	
	private boolean running = true;
	
	private DispatchTaskFactoryManager factoryManager = null;
	
	public DispatchThread(DispatchTaskFactoryManager factoryManager) {
		this.setDaemon(true);
		this.setName("任务【" + factoryManager.getName()+ "】分发线程");
		this.factoryManager = factoryManager;
	}
	
	public void setRunning(boolean running) {
		this.running = running;
	}
	
	protected synchronized void releaseOneThread(){
		threadCount--;
	}
	
	@Override
	public void run() {
		while(running){
			if(threadCount > MAX_THREAD_COUNT){
				try {
					Thread.sleep(INTERVAL);
				} catch (InterruptedException e) {
					handleDispacthException(new DispatchTaskException("线程挂起出现异常", e.fillInStackTrace(), DispatchTaskErrorCode.INTERRUPT_SLEEP));
				}
			} else {
				IDispatchTaskFactory factory = factoryManager.takeFirstRequestFactory();
				if(!running){
					return;
				}
				if(factory == null){
					handleDispacthException(new DispatchTaskException("不能分发空的任务工厂", DispatchTaskErrorCode.NULL_FACTORY));
				}
				IDispatchTask task = factory.getTask();
				if(task == null){
					handleDispacthException(new DispatchTaskException("不能分发空的请求", DispatchTaskErrorCode.NULL_FACTORY));
				}
				TaskThread thread = new TaskThread(this, task);
				thread.start();
				threadCount ++ ;
			}
		}
	}
	
	
	/**
	 * 处理分发异常
	 * @param e 分发异常
	 */
	protected void handleDispacthException(DispatchTaskException e){
		IDispatchExceptionHandler handler = factoryManager.getDispatchExceptionHandler();
		if(handler == null){
			logger.error("任务分发出错：" + e.getMessage(), e);
		}
		handler.handle(e);
	}
}
