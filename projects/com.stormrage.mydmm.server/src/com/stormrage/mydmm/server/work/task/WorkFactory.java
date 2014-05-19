package com.stormrage.mydmm.server.work.task;

import com.stormrage.mydmm.server.task.dispatch.IDispatchTask;
import com.stormrage.mydmm.server.task.dispatch.IDispatchTaskFactory;
import com.stormrage.mydmm.server.workfind.WorkPageType;

/**
 * 获取座屏信息需要分发的请求任务工厂
 * @author StormrageWang
 * @date 2014年5月18日
 */
public class WorkFactory implements IDispatchTaskFactory{

	private String actressGuid;
	private String workTitle;
	private WorkPageType pageType;
	private String url;
	
	public WorkFactory(String actressGuid, String workTitle, WorkPageType pageType, String url) {
		this.actressGuid = actressGuid;
		this.workTitle = workTitle;
		this.pageType = pageType;
		this.url = url;
	}
	
	
	@Override
	public IDispatchTask getTask() {
		return new WorkTask(actressGuid, workTitle, pageType, url);
	}

	public String getActressGuid() {
		return actressGuid;
	}

	public void setActressGuid(String actressGuid) {
		this.actressGuid = actressGuid;
	}

	public String getWorkTitle() {
		return workTitle;
	}

	public void setWorkTitle(String workTitle) {
		this.workTitle = workTitle;
	}


	public WorkPageType getPageType() {
		return pageType;
	}

	public void setPageType(WorkPageType pageType) {
		this.pageType = pageType;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
	
}