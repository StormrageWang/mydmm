package com.stormrage.mydmm.server.task;

/**
 * 网络请求异常
 * @author StormrageWang
 * @date 2014年5月18日
 */
public class TaskException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5919801865437480727L;
	
	private int errorCode;

	public TaskException() {
		super();
		this.errorCode = TaskErrorCode.UNDEFINE;
	}

	public TaskException(String message) {
		super(message);
		this.errorCode = TaskErrorCode.UNDEFINE;
	}
	
	public TaskException(String message, int errorCode) {
		super(message);
		this.errorCode = errorCode;
	}

	public TaskException(Throwable cause) {
		super(cause.getMessage(), cause);
		if (cause instanceof TaskException){
			this.errorCode = ((TaskException)cause).errorCode;
		} else {
			this.errorCode = TaskErrorCode.UNDEFINE;
		}
	}
	
	public TaskException(Throwable cause, int errorCode) {
		super(cause.getMessage(), cause);
		this.errorCode = errorCode;
	}

	public TaskException(String message, Throwable cause) {
		super(message, cause);
		if (cause instanceof TaskException) {
			this.errorCode = ((TaskException)cause).errorCode;
		} else {
			this.errorCode = TaskErrorCode.UNDEFINE;
		}
	}

	public TaskException(String message, Throwable cause, int errorCode) {
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
