package com.stormrage.mydmm.server.work.request;

import com.stormrage.mydmm.server.actress.ActressBean;
import com.stormrage.mydmm.server.task.dispatch.IDispatchTask;
import com.stormrage.mydmm.server.task.dispatch.IDispatchTaskFactory;
import com.stormrage.mydmm.server.utils.Guid;
import com.stormrage.mydmm.server.work.WorkBean;
import com.stormrage.mydmm.server.workfind.WorkFindBean;
import com.stormrage.mydmm.server.workfind.WorkPageType;

/**
 * 获取座屏信息需要分发的请求任务工厂
 * @author StormrageWang
 * @date 2014年5月18日
 */
public class WorkFactory implements IDispatchTaskFactory{

	private ActressBean actressBean;
	private WorkBean workBean;
	
	public WorkFactory(ActressBean actressBean, String title, WorkPageType type, String url) {
		this.actressBean = actressBean;
		workBean = new WorkBean();
		workBean.setGuid(Guid.newGuid());
		workBean.setUrl(url);
		workBean.setTitle(title);
		workBean.setPageType(type);
	}
	
	@Override
	public IDispatchTask getTask() {
		return new WorkTask(actressBean, workBean);
	}

}