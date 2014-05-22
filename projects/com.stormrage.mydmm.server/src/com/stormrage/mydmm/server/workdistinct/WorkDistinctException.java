package com.stormrage.mydmm.server.workdistinct;

/**
 * @author StormrageWang
 * @date 2014年5月18日
 */
public class WorkDistinctException extends Exception {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8216862636968157098L;
	
	private int errorCode;

	public WorkDistinctException() {
		super();
		this.errorCode = WorkDistinctErrorCode.UNDEFINE;
	}

	public WorkDistinctException(String message) {
		super(message);
		this.errorCode = WorkDistinctErrorCode.UNDEFINE;
	}
	
	public WorkDistinctException(String message, int errorCode) {
		super(message);
		this.errorCode = errorCode;
	}

	public WorkDistinctException(Throwable cause) {
		super(cause.getMessage(), cause);
		if (cause instanceof WorkDistinctException){
			this.errorCode = ((WorkDistinctException)cause).errorCode;
		} else {
			this.errorCode = WorkDistinctErrorCode.UNDEFINE;
		}
	}
	
	public WorkDistinctException(Throwable cause, int errorCode) {
		super(cause.getMessage(), cause);
		this.errorCode = errorCode;
	}

	public WorkDistinctException(String message, Throwable cause) {
		super(message, cause);
		if (cause instanceof WorkDistinctException) {
			this.errorCode = ((WorkDistinctException)cause).errorCode;
		} else {
			this.errorCode = WorkDistinctErrorCode.UNDEFINE;
		}
	}

	public WorkDistinctException(String message, Throwable cause, int errorCode) {
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
