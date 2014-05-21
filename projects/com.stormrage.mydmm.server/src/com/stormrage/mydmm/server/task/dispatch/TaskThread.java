package com.stormrage.mydmm.server.task.dispatch;

/**
 * 任务执行线程
 * @author StormrageWang
 * @date 2014年5月18日
 */
public class TaskThread extends Thread {

	private IDispatchTask task;
	private DispatchThread dispatchThread;
	
	public TaskThread(DispatchThread dispatchThread, IDispatchTask task){
		this.task = task;
		this.dispatchThread = dispatchThread;
		this.setDaemon(true);
		this.setName("任务【" + task.getName() + "】处理线程");
	}
	
	public void run() {
		try{
			task.run();
			dispatchThread.releaseOneThread();
		} catch(Throwable e){
			dispatchThread.handleDispacthException(new DispatchTaskException("任务【" + task.getName() + "】处理线程发生了未知异常", e));
		}
	}

}
