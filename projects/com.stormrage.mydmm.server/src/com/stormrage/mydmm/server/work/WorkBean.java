package com.stormrage.mydmm.server.work;

import java.text.SimpleDateFormat;
import java.util.Date;



public class WorkBean {

	public static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	
	private String code;//完整番号
	private String simpleCode;//处理后的番号
	private String title;//作品名称
	private String chTitle;//中文名称
	private Date date;//作品完成时间
	private int timeLength;//时长（分钟）
	private WorkActressType actressType;
	private String url;
	
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getSimpleCode() {
		return simpleCode;
	}
	public void setSimpleCode(String simpleCode) {
		this.simpleCode = simpleCode;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getChTitle() {
		return chTitle;
	}
	public void setChTitle(String chTitle) {
		this.chTitle = chTitle;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public int getTimeLength() {
		return timeLength;
	}
	public void setTimeLength(int timeLength) {
		this.timeLength = timeLength;
	}
	public WorkActressType getActressType() {
		return actressType;
	}
	public void setActressType(WorkActressType actressType) {
		this.actressType = actressType;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	
	public String getDescription(){
		return code + "\t" + simpleCode + "\t" + title + "\t" + chTitle + "\t" + 
				sdf.format(date) + "\t" + timeLength + "\t" + actressType.toString() + "\t" + url ;
	}
}
