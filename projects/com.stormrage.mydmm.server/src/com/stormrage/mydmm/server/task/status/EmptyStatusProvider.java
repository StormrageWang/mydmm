package com.stormrage.mydmm.server.task.status;

public class EmptyStatusProvider implements ITaskStatusProvider {

	private static final EmptyStatusProvider prvider = new EmptyStatusProvider();
	
	private EmptyStatusProvider(){
		
	}
	
	public static EmptyStatusProvider getInstance(){
		return prvider;
	}
	
	@Override
	public TaskStatus getStatus() {
		return TaskStatus.FINISH;
	}

}
