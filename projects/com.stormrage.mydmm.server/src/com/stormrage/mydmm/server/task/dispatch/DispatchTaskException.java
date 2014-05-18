package com.stormrage.mydmm.server.task.dispatch;

public class DispatchTaskException extends Exception {

	/**
	 * 序列号
	 */
	private static final long serialVersionUID = 2362982593832075183L;

	private int errorCode;

	public DispatchTaskException() {
		super();
		this.errorCode = DispatchTaskErrorCode.UNDEFINE;
	}

	public DispatchTaskException(String message) {
		super(message);
		this.errorCode = DispatchTaskErrorCode.UNDEFINE;
	}
	
	public DispatchTaskException(String message, int errorCode) {
		super(message);
		this.errorCode = errorCode;
	}

	public DispatchTaskException(Throwable cause) {
		super(cause.getMessage(), cause);
		if (cause instanceof DispatchTaskException){
			this.errorCode = ((DispatchTaskException)cause).errorCode;
		} else {
			this.errorCode = DispatchTaskErrorCode.UNDEFINE;
		}
	}
	
	public DispatchTaskException(Throwable cause, int errorCode) {
		super(cause.getMessage(), cause);
		this.errorCode = errorCode;
	}

	public DispatchTaskException(String message, Throwable cause) {
		super(message, cause);
		if (cause instanceof DispatchTaskException) {
			this.errorCode = ((DispatchTaskException)cause).errorCode;
		} else {
			this.errorCode = DispatchTaskErrorCode.UNDEFINE;
		}
	}

	public DispatchTaskException(String message, Throwable cause, int errorCode) {
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
