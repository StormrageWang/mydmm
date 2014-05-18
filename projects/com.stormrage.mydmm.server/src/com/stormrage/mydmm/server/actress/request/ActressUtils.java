package com.stormrage.mydmm.server.actress.request;

import com.stormrage.mydmm.server.request.RequestErrorCode;
import com.stormrage.mydmm.server.request.RequestException;


/**
 * 演员相关的工具类
 * @author StormrageWang
 * @date 2014年5月18日
 */
public class ActressUtils {

	private static final String WORK_COUNT_PREFIX = "全";
	private static final String WORK_COUNT_SUFFIX = "件";
	private static final String PAGE_CAPACITY_PREFIX = "&nbsp;";
	private static final String PAGE_CAPACITY_SUFFIX = "件";
	
	/**
	 * 
	 * 处理 如“全91件中 50件を表示”，返回91
	 * @param fullDescStr
	 * @return
	 * @throws RequestException
	 */
	public static int getTotalWorkCount(String fullDescStr) throws RequestException {
		int prtfixIndex = fullDescStr.indexOf(WORK_COUNT_PREFIX);
		int suffixIndex = fullDescStr.indexOf(WORK_COUNT_SUFFIX, prtfixIndex);
		String countStr = fullDescStr.substring(prtfixIndex + WORK_COUNT_PREFIX.length(), suffixIndex);
		try{
			return Integer.valueOf(countStr);
		} catch(NumberFormatException e){
			throw new RequestException("总作品数【" + countStr + "】无法转为数字", RequestErrorCode.WEB_ANALYTICS_GET);
		}
	}
	
	/**
	 * 
	 * 处理 如“全91件中 50件を表示”，返回50
	 * @param fullDescStr
	 * @return
	 * @throws RequestException
	 */
	public static int getCurrentWorkCount(String fullDescStr) throws RequestException {
		int prtfixIndex = fullDescStr.indexOf(PAGE_CAPACITY_PREFIX);
		int suffixIndex = fullDescStr.indexOf(PAGE_CAPACITY_SUFFIX, prtfixIndex);
		String capacityStr = fullDescStr.substring(prtfixIndex + PAGE_CAPACITY_PREFIX.length(), suffixIndex);
		try{
			return Integer.valueOf(capacityStr);
		} catch(NumberFormatException e){
			throw new RequestException("每页数【" + capacityStr + "】无法转为数字", RequestErrorCode.WEB_ANALYTICS_GET);
		}
	}
}
