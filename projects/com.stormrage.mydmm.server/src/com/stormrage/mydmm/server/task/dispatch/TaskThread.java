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
		task.run();
		dispatchThread.releaseOneThread();
	}

}
