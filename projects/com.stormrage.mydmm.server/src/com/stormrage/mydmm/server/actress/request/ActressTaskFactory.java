package com.stormrage.mydmm.server.actress.request;

import com.stormrage.mydmm.server.actress.ActressBean;
import com.stormrage.mydmm.server.task.dispatch.IDispatchTask;
import com.stormrage.mydmm.server.task.dispatch.IDispatchTaskFactory;

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
