package com.stormrage.mydmm.server.task.dispatch;

public class DispatchThread extends Thread {

	/**
	 * 间隔2秒
	 */
	private static int INTERVAL = 2000;
	/**
	 * 一次发10个请求
	 */
	private static int COUNT = 10;
	
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
	
	@Override
	public void run() {
		while(running){
			for(int i = 0; i < COUNT; i++){
				try{
					IDispatchTaskFactory factory = factoryManager.takeFirstRequestFactory();
					if(!running){
						return;
					}
					if(factory == null){
						throw new DispatchTaskException("不能分发空的任务工厂", DispatchTaskErrorCode.NULL_FACTORY);
					}
					IDispatchTask task = factory.getTask();
					if(task == null){
						throw new DispatchTaskException("不能分发空的请求", DispatchTaskErrorCode.NULL_FACTORY);
					}
					TaskThread thread = new TaskThread(task);
					thread.start();
				} catch (DispatchTaskException e){
					handleDispacthException(e);
				}
			}
			try {
				Thread.sleep(INTERVAL);
			} catch (InterruptedException e) {
				handleDispacthException(new DispatchTaskException("线程挂起出现异常", e.fillInStackTrace(), DispatchTaskErrorCode.INTERRUPT_SLEEP));
			}
		}
	}
	
	/**
	 * 处理分发异常
	 * @param e 分发异常
	 */
	private void handleDispacthException(DispatchTaskException e){
		IDispatchExceptionHandler handler = factoryManager.getDispatchExceptionHandler();
		if(handler == null){
			return;//TODO 打印日志
		}
		handler.handle(e);
	}
	
	
}
