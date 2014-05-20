package com.stormrage.mydmm.server.task.status;

public class TaskStatusErrorCode {

	public static final int	OK = 0x00000000;
	
	public static final int	UNDEFINE = 0x00100000;
	
	private static final int PREPARE = 0x00200000;
	public static final int PREPARE_ADD = PREPARE + 1;
	
	private static final int RUNNING = 0x00200000;
	public static final int RUNNINF_INTERRUPT = RUNNING + 1;
	
}
