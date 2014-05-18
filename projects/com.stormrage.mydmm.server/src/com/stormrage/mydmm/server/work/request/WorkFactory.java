package com.stormrage.mydmm.server.work.request;

import com.stormrage.mydmm.server.actress.ActressBean;
import com.stormrage.mydmm.server.task.dispatch.IDispatchTask;
import com.stormrage.mydmm.server.task.dispatch.IDispatchTaskFactory;
import com.stormrage.mydmm.server.work.WorkBean;
import com.stormrage.mydmm.server.workfind.WorkFindBean;

public class WorkFactory implements IDispatchTaskFactory{

	private ActressBean actressBean;
	private WorkBean workBean;
	
	public WorkFactory(WorkFindBean workFindBean) {
		actressBean = workFindBean.getActressBean();
		workBean = new WorkBean();
		workBean.setUrl(workFindBean.getWorkUrl());
		workBean.setTitle(workFindBean.getWorkTitle());
		workBean.setPageType(workFindBean.getPageType());
	}
	
	@Override
	public IDispatchTask getTask() {
		return new WorkTask(actressBean, workBean);
	}

}