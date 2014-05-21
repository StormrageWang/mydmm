package com.stormrage.mydmm.server.workfind.task;

import com.stormrage.mydmm.server.task.dispatch.IDispatchTask;
import com.stormrage.mydmm.server.task.dispatch.IDispatchTaskFactory;

/**
 * 获取作品列表的需要分发的请求任务工厂
 * @author StormrageWang
 * @date 2014年5月18日
 */
public class WorkFindTaskFactory implements IDispatchTaskFactory {

	private String actressName; 
	private int pageIndex;
	private String url;
	
	public WorkFindTaskFactory(String actressName, int pageIndex, String url){
		this.actressName = actressName;
		this.pageIndex = pageIndex;
		this.url = url;
	}

	@Override
	public IDispatchTask getTask() {
		WorkFindTask workFindTask = new WorkFindTask(actressName, pageIndex, url);
		return workFindTask;
	}
	
	public void setActressName(String actressName) {
		this.actressName = actressName;
	}
	
}
