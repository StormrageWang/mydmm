package com.stormrage.mydmm.server.task.status;

public class StatusScanThread extends Thread {

	private static final int PERIOD = 1000;
	
	private TaskStatusManager statusManager;
	private boolean running = true;
	
	public StatusScanThread(TaskStatusManager statusManager) {
		super();
		this.statusManager = statusManager;
		setName("【" + statusManager.getName() +  "】任务状态管理扫描线程");
		setDaemon(true);
	}
	
	@Override
	public void run() {
		while(running){
			statusManager.updateStatus();
			try {
				Thread.sleep(PERIOD);
			} catch (InterruptedException e) {
				TaskStatusException ex = new TaskStatusException("任务状态管理扫面线程中断异常", e, TaskStatusErrorCode.RUNNINF_INTERRUPT);
				statusManager.handleStatusException(ex);
			}
		}
	}
	
	public void finish(){
		running = false;
	}
}
