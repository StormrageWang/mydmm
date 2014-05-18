package com.stormrage.mydmm.server.actress.request;

import com.stormrage.mydmm.server.actress.ActressBean;
import com.stormrage.mydmm.server.task.dispatch.IDispatchTask;
import com.stormrage.mydmm.server.task.dispatch.IDispatchTaskFactory;

/**
 * 获取演员信息的需要分发的请求任务工厂
 * @author StormrageWang
 * @date 2014年5月18日
 */
public class ActressTaskFactory implements IDispatchTaskFactory {

	private ActressBean actressBean;
	
	public ActressTaskFactory(String url){
		actressBean = new ActressBean();
		actressBean.setUrl(url);
	}
	
	@Override
	public IDispatchTask getTask() {
		return new ActressTask(actressBean);
	}

}
