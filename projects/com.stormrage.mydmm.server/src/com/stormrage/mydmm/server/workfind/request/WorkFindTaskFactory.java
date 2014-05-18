package com.stormrage.mydmm.server.workfind.request;

import com.stormrage.mydmm.server.actress.ActressBean;
import com.stormrage.mydmm.server.task.dispatch.IDispatchTask;
import com.stormrage.mydmm.server.task.dispatch.IDispatchTaskFactory;
import com.stormrage.mydmm.server.workfind.WorkFindBean;


public class WorkFindTaskFactory implements IDispatchTaskFactory {

	private WorkFindBean workFindBean;
	
	public WorkFindTaskFactory(ActressBean actressBean, String url){
		workFindBean = new WorkFindBean();
		workFindBean.setUrl(url);
		workFindBean.setActressBean(actressBean);
	}
	
	public WorkFindBean getWorkFindBean() {
		return workFindBean;
	}


	@Override
	public IDispatchTask getTask() {
		WorkFindTask workFindTask = new WorkFindTask(workFindBean);
		return workFindTask;
	}

}
