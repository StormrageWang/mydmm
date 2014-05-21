package com.stormrage.mydmm.server.work.task;

import com.stormrage.mydmm.server.task.dispatch.IDispatchTask;
import com.stormrage.mydmm.server.task.dispatch.IDispatchTaskFactory;

/**
 * 获取座屏信息需要分发的请求任务工厂
 * @author StormrageWang
 * @date 2014年5月18日
 */
public class WorkTaskFactory implements IDispatchTaskFactory {

	private String actressName;
	private String workTitle;
	private int workIndex;
	private String url;
	
	public WorkTaskFactory(String actressName, String workTitle, int workIndex, String url) {
		this.actressName = actressName;
		this.workTitle = workTitle;
		this.workIndex = workIndex;
		this.url = url;
	}
	
	
	@Override
	public IDispatchTask getTask() {
		WorkTask workTask =  new WorkTask(actressName, workTitle, url);
		return workTask;
	}
	
	public void setActressName(String actressName) {
		this.actressName = actressName;
	}
	
	public String getActressName() {
		return actressName;
	}
	
	public String getWorkTitle() {
		return workTitle;
	}
	
	public int getWorkIndex() {
		return workIndex;
	}
	
}