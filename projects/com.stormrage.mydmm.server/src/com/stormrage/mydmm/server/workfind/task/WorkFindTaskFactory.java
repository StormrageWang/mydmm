package com.stormrage.mydmm.server.workfind.task;

import com.stormrage.mydmm.server.actress.ActressBean;
import com.stormrage.mydmm.server.task.dispatch.IDispatchTask;
import com.stormrage.mydmm.server.task.dispatch.IDispatchTaskFactory;
import com.stormrage.mydmm.server.workfind.WorkFindBean;

/**
 * 获取作品列表的需要分发的请求任务工厂
 * @author StormrageWang
 * @date 2014年5月18日
 */
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
