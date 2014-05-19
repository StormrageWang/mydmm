package com.stormrage.mydmm.server.workfind.task;

import com.stormrage.mydmm.server.task.dispatch.IDispatchTask;
import com.stormrage.mydmm.server.task.dispatch.IDispatchTaskFactory;

/**
 * 获取作品列表的需要分发的请求任务工厂
 * @author StormrageWang
 * @date 2014年5月18日
 */
public class WorkFindTaskFactory implements IDispatchTaskFactory {

	private String actressGuid;
	private String actressName; 
	private int pageIndex;
	private String url;
	
	public WorkFindTaskFactory(String actressGuid, String actressName, int pageIndex, String url){
		this.actressGuid = actressGuid;
		this.actressName = actressName;
		this.pageIndex = pageIndex;
		this.url = url;
	}

	@Override
	public IDispatchTask getTask() {
		WorkFindTask workFindTask = new WorkFindTask(actressGuid, actressName, pageIndex, url);
		return workFindTask;
	}
	
	public String getUrl() {
		return url;
	}

}
