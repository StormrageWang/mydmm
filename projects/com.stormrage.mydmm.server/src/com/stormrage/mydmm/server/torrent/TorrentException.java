package com.stormrage.mydmm.server.torrent;


public class TorrentException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3041895501615005889L;

	private int errorCode;

	public TorrentException() {
		super();
		this.errorCode = TorrentErrorCode.UNDEFINE;
	}

	public TorrentException(String message) {
		super(message);
		this.errorCode = TorrentErrorCode.UNDEFINE;
	}
	
	public TorrentException(String message, int errorCode) {
		super(message);
		this.errorCode = errorCode;
	}

	public TorrentException(Throwable cause) {
		super(cause.getMessage(), cause);
		if (cause instanceof TorrentException){
			this.errorCode = ((TorrentException)cause).errorCode;
		} else {
			this.errorCode = TorrentErrorCode.UNDEFINE;
		}
	}
	
	public TorrentException(Throwable cause, int errorCode) {
		super(cause.getMessage(), cause);
		this.errorCode = errorCode;
	}

	public TorrentException(String message, Throwable cause) {
		super(message, cause);
		if (cause instanceof TorrentException) {
			this.errorCode = ((TorrentException)cause).errorCode;
		} else {
			this.errorCode = TorrentErrorCode.UNDEFINE;
		}
	}

	public TorrentException(String message, Throwable cause, int errorCode) {
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
