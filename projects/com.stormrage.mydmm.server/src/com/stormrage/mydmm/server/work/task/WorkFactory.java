package com.stormrage.mydmm.server.work.task;

import com.stormrage.mydmm.server.task.dispatch.IDispatchTask;
import com.stormrage.mydmm.server.task.dispatch.IDispatchTaskFactory;
import com.stormrage.mydmm.server.task.status.ITaskFinishListener;
import com.stormrage.mydmm.server.task.status.ITaskStatusProvider;
import com.stormrage.mydmm.server.task.status.TaskStatus;
import com.stormrage.mydmm.server.workfind.WorkPageType;

/**
 * 获取座屏信息需要分发的请求任务工厂
 * @author StormrageWang
 * @date 2014年5月18日
 */
public class WorkFactory implements IDispatchTaskFactory, ITaskFinishListener, ITaskStatusProvider {

	private String actressGuid;
	private String workTitle;
	private WorkPageType pageType;
	private int workIndex;
	private String url;
	private TaskStatus taskStatus = TaskStatus.UN_FINISH;
	
	public WorkFactory(String actressGuid, String workTitle, WorkPageType pageType, int workIndex, String url) {
		this.actressGuid = actressGuid;
		this.workTitle = workTitle;
		this.pageType = pageType;
		this.workIndex = workIndex;
		this.url = url;
	}
	
	
	@Override
	public IDispatchTask getTask() {
		WorkTask workTask =  new WorkTask(actressGuid, workTitle, pageType, url);
		workTask.setFinishListener(this);
		return workTask;
	}

	public String getActressGuid() {
		return actressGuid;
	}

	public String getWorkTitle() {
		return workTitle;
	}


	public WorkPageType getPageType() {
		return pageType;
	}

	public int getWorkIndex() {
		return workIndex;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}


	@Override
	public void finish() {
		taskStatus = TaskStatus.FINISH;
	}


	@Override
	public TaskStatus getStatus() {
		return taskStatus;
	}
	
}