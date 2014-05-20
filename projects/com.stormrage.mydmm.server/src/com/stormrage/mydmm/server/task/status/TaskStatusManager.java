package com.stormrage.mydmm.server.task.status;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

@Deprecated 
public class TaskStatusManager implements ITaskStatusProvider {

	private String name;
	private int subTaskCount = 0;
	private TaskStatus status = TaskStatus.UN_FINISH;
	private Set<ITaskStatusProvider> subStatusSet = null;
	private StatusScanThread scanThread;
	private ITaskFinishListener finishListener;
	private ITaskStatusExceptionHandler exceptionHandler;
	
	public TaskStatusManager(String name, int subTaskCount){
		this.name = name;
		this.subTaskCount = subTaskCount;
		subStatusSet = new HashSet<ITaskStatusProvider>(subTaskCount);
		scanThread = new StatusScanThread(this);
		scanThread.start();
	}
	
	public String getName() {
		return name;
	}
	
	protected void updateStatus() {
		TaskStatus newStatus = getNewStatus();
		if(newStatus == TaskStatus.FINISH) {
			status = TaskStatus.FINISH;
			if(finishListener != null){
				finishListener.finish();
			}
			scanThread.finish();
		}
	}
	
	protected void handleStatusException(TaskStatusException e){
		if(exceptionHandler != null){
			exceptionHandler.handle(e);
		}
	}
	
	public void addStatusProvider(ITaskStatusProvider provider) {
		if(subStatusSet.size() >= subTaskCount){
			handleStatusException(new TaskStatusException("添加的任务状态提供器超过了定义的个数", TaskStatusErrorCode.PREPARE_ADD));
		}
		subStatusSet.add(provider);
	}
	
	public void setFinishListener(ITaskFinishListener finishListener) {
		this.finishListener = finishListener;
	}
	
	public void setStatusExceptionHandler(ITaskStatusExceptionHandler exceptionHandler) {
		this.exceptionHandler = exceptionHandler;
	}
	
	@Override
	public TaskStatus getStatus() {
		return status;
	}

	
	private TaskStatus getNewStatus(){
		if(subTaskCount < subStatusSet.size()){
			return TaskStatus.UN_FINISH; 
		}
		Iterator<ITaskStatusProvider> subStatusIterator = subStatusSet.iterator();
		while(subStatusIterator.hasNext()){
			ITaskStatusProvider provider = subStatusIterator.next();
			if(provider.getStatus() == TaskStatus.UN_FINISH){
				return TaskStatus.UN_FINISH;
			}
		}
		return TaskStatus.FINISH;
	}
	
}
