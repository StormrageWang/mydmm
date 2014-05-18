package com.stormrage.mydmm.server.work.request;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.stormrage.mydmm.server.request.RequestErrorCode;
import com.stormrage.mydmm.server.request.RequestException;
import com.stormrage.mydmm.server.utils.StringUtils;
import com.stormrage.mydmm.server.work.WorkBean;



public class WorkUtils {
	
	private static final String TIME_LENGTH_SUFFIX = "分";
	private static final String DATE_FORMAT = "yyyy/MM/dd";
	private static final SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
	
	
	public static void fullWorkByAnimationPage(WorkBean workBean, Document doc) throws RequestException {
		//基本信息
		Element infoTable = doc.select("table").get(2);
		Elements infoTrs = infoTable.select("tr");
		//日期
		Element dateTr = infoTrs.get(2);
		String dateStr = dateTr.child(1).html();
		workBean.setDate(getDate(dateStr));
		//时长
		Element timeLengthTr = infoTrs.get(4);
		String timeLengthStr = timeLengthTr.child(1).html();
		workBean.setTimeLength(getTimeLength(timeLengthStr));
		//番号
		Element codeTr = infoTrs.get(11);
		String codeStr = codeTr.child(1).html();
		workBean.setFullCode(codeStr);
		workBean.setSimpleCode(getSimpleCode(codeStr));

	}
	
	public static void fullWorkByMailOrderPage(WorkBean workBean, Document doc) throws RequestException {
		//基本信息
		Element infoTable = doc.select("table").get(2);
		Elements infoTrs = infoTable.select("tr");
		//日期
		Element dateTr = infoTrs.get(1);
		String dateStr = dateTr.child(1).html();
		workBean.setDate(getDate(dateStr));
		//时长
		Element timeLengthTr = infoTrs.get(2);
		String timeLengthStr = timeLengthTr.child(1).html();
		workBean.setTimeLength(getTimeLength(timeLengthStr));
		//番号
		Element codeTr = infoTrs.get(9);
		String codeStr = codeTr.child(1).html();
		workBean.setFullCode(codeStr);
		workBean.setSimpleCode(getSimpleCode(codeStr));

	}
	
	public static Date getDate(String dateStr) throws RequestException {
		
		try {
			return sdf.parse(dateStr);
		} catch (ParseException e) {
			throw new RequestException("日期字符串【" + dateStr + "】无法转为数字", RequestErrorCode.WEB_ANALYTICS_GET);
		}
	}
	
	public static int getTimeLength(String fullStr) throws RequestException {
		int index = fullStr.indexOf(TIME_LENGTH_SUFFIX);
		String timeLengthStr = fullStr.substring(0, index);
		try{
			return Integer.valueOf(timeLengthStr);
		} catch (NumberFormatException e){
			throw new RequestException("时长【" + timeLengthStr + "】无法转为数字", RequestErrorCode.WEB_ANALYTICS_GET);
		}
	}

	/**
	 * 代码转成简单代码
	 * @param fullCode
	 * @return
	 */
	public static String getSimpleCode(String fullCode) throws RequestException {
		//从后往前分析
		char[] codeChars = new char[fullCode.length()];
		char[] signChars = new char[fullCode.length()];
		int codeIndex = 0, signIndex = 0;
		boolean codeCompleted = false, signCompleted = false;
		for(int i = fullCode.length() - 1 ; i >= 0; i--){
			char c = fullCode.charAt(i);
			if(codeCompleted && signCompleted){//2个都已解析出来
				break;
			} else if(codeCompleted & !signCompleted){//只解析出来代码
				if(isLowerLetter(c)){
					signChars[signIndex] = c;
					signIndex ++;
				} else {
					signCompleted = true;
				}
			} else if(!codeCompleted){//代码还未解析出来
				if(isNumber(c)){
					codeChars[codeIndex] = c;
					codeIndex ++;
				} else {
					codeCompleted = true;
					signChars[signIndex] = c;
					signIndex ++;
				}
			}
		}
		String codeStr = new String(codeChars, 0, codeIndex);
		String signStr = new String(signChars, 0, signIndex);
		codeStr = StringUtils.reverse(codeStr);
		signStr = StringUtils.reverse(signStr);
		if(StringUtils.isEmpty(codeStr)
				||StringUtils.isEmpty(signStr)){
			throw new RequestException("无法获取【" + fullCode + "】的番号", RequestErrorCode.WEB_ANALYTICS_GET);
		}
		int code = Integer.parseInt(codeStr);
		codeStr = String.format("%03d", code);
		return signStr + "-" + codeStr;
	}
	
	public static boolean isLowerLetter(char c){
		if(c < 'a' || c > 'z'){
			return false;
		}
		return true;
	}
	
	public static boolean isNumber(char c){
		if(c < '0' || c > '9'){
			return false;
		}
		return true;
	}
	
}