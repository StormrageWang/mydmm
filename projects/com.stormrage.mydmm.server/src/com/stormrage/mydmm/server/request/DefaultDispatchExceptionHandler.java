package com.stormrage.mydmm.server.request;

import com.stormrage.mydmm.server.task.dispatch.DispatchTaskException;
import com.stormrage.mydmm.server.task.dispatch.IDispatchExceptionHandler;


public class DefaultDispatchExceptionHandler implements IDispatchExceptionHandler {

	@Override
	public void handle(DispatchTaskException e) {
		e.printStackTrace();
	}

}