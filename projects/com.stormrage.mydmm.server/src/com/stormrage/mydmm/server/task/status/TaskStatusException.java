package com.stormrage.mydmm.server.task.status;

public class TaskStatusException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3434451421030725184L;

	private int errorCode;

	public TaskStatusException() {
		super();
		this.errorCode = TaskStatusErrorCode.UNDEFINE;
	}

	public TaskStatusException(String message) {
		super(message);
		this.errorCode = TaskStatusErrorCode.UNDEFINE;
	}
	
	public TaskStatusException(String message, int errorCode) {
		super(message);
		this.errorCode = errorCode;
	}

	public TaskStatusException(Throwable cause) {
		super(cause.getMessage(), cause);
		if (cause instanceof TaskStatusException){
			this.errorCode = ((TaskStatusException)cause).errorCode;
		} else {
			this.errorCode = TaskStatusErrorCode.UNDEFINE;
		}
	}
	
	public TaskStatusException(Throwable cause, int errorCode) {
		super(cause.getMessage(), cause);
		this.errorCode = errorCode;
	}

	public TaskStatusException(String message, Throwable cause) {
		super(message, cause);
		if (cause instanceof TaskStatusException) {
			this.errorCode = ((TaskStatusException)cause).errorCode;
		} else {
			this.errorCode = TaskStatusErrorCode.UNDEFINE;
		}
	}

	public TaskStatusException(String message, Throwable cause, int errorCode) {
		super(message, cause);
		this.errorCode = errorCode;
	}
	
	@Override
	public String toString() {
		return super.toString() + " (" + Integer.toString(errorCode) + ")";
	}
	
	/**
	 * 获取异常代码
	 * @return 异常代码
	 */
	public int getErrorCode() {
		return errorCode;
	}
	
}
