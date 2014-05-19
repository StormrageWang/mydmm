package com.stormrage.mydmm.server.work;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.stormrage.mydmm.server.workfind.WorkPageType;



public class WorkBean {

	public static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	
	private String guid;//guid
	private String title;//作品链接上的名称
	private String fullTitle;//作品具体的名称
	private String chTitle;//中文名称
	private String fullCode;//完整番号
	private String code;//处理后的番号
	private Date date;//作品完成时间
	private int timeLength;//时长（分钟）
	private String coverGuid;//封面小图
	private String fullCoverGuid;//封面大图
	private String url;
	private WorkPageType pageType;
	
	public String getGuid() {
		return guid;
	}
	public void setGuid(String guid) {
		this.guid = guid;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getFullTitle() {
		return fullTitle;
	}
	public void setFullTitle(String fullTitle) {
		this.fullTitle = fullTitle;
	}
	public String getChTitle() {
		return chTitle;
	}
	public void setChTitle(String chTitle) {
		this.chTitle = chTitle;
	}
	public String getFullCode() {
		return fullCode;
	}
	public void setFullCode(String fullCode) {
		this.fullCode = fullCode;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
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
	
	public String getCoverGuid() {
		return coverGuid;
	}
	
	public void setCoverGuid(String coverGuid) {
		this.coverGuid = coverGuid;
	}
	
	public String getFullCoverGuid() {
		return fullCoverGuid;
	}
	
	public void setFullCoverGuid(String fullCoverGuid) {
		this.fullCoverGuid = fullCoverGuid;
	}

	public WorkPageType getPageType() {
		return pageType;
	}
	
	public void setPageType(WorkPageType pageType) {
		this.pageType = pageType;
	}
	
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	
	public String getDescription(){
		return  fullCode + " \t " + code + " \t " + title + 
				" \t " + sdf.format(date) + " \t " + timeLength + "\t" + url ;
	}
}
