package com.stormrage.mydmm.server.request;

/**
 * 网络请求错误代码
 * @author StormrageWang
 * @date 2014年5月18日
 */
public class RequestErrorCode {


	public static final int	OK = 0x00000000;
	
	public static final int	UNDEFINE = 0x00100000;
	
	private static final int WEB_REQUEST_START = 0x00200000;
	public static final int WEB_REQUEST_ENCODE = WEB_REQUEST_START + 1;
	public static final int WEB_REQUEST_IO = WEB_REQUEST_START + 2;
	public static final int WEB_REQUEST_EFORMAT = WEB_REQUEST_START + 3;
	
	private static final int WEB_ANALYTICS_START =  0x00300000;
	public static final int WEB_ANALYTICS_GET =  WEB_ANALYTICS_START + 2;
	public static final int WEB_ANALYTICS_UNMATCH =  WEB_ANALYTICS_START + 3;
	
	
}
