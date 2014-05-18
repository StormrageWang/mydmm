package com.stormrage.mydmm.server.work;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.stormrage.mydmm.server.workfind.WorkPageType;



public class WorkBean {

	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	
	private String guid;
	private String fullCode;
	private String simpleCode;
	private String jpName;
	private String chName;
	private Date date;
	private int timeLength;
	private String coverSimpleUrl;
	private byte[] coverSimpleData;
	private String coverFullUrl;
	private byte[] coverFullData;
	private String[] pictureUrls;
	private String title;
	private String url;
	private WorkPageType pageType;
	
	public String getGuid() {
		return guid;
	}
	public void setGuid(String guid) {
		this.guid = guid;
	}
	public String getFullCode() {
		return fullCode;
	}
	public void setFullCode(String fullCode) {
		this.fullCode = fullCode;
	}
	public String getSimpleCode() {
		return simpleCode;
	}
	public void setSimpleCode(String simpleCode) {
		this.simpleCode = simpleCode;
	}
	public String getJpName() {
		return jpName;
	}
	public void setJpName(String jpName) {
		this.jpName = jpName;
	}
	public String getChName() {
		return chName;
	}
	public void setChName(String chName) {
		this.chName = chName;
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

	public String getCoverSimpleUrl() {
		return coverSimpleUrl;
	}
	public void setCoverSimpleUrl(String coverSimpleUrl) {
		this.coverSimpleUrl = coverSimpleUrl;
	}
	public byte[] getCoverSimpleData() {
		return coverSimpleData;
	}
	public void setCoverSimpleData(byte[] coverSimpleData) {
		this.coverSimpleData = coverSimpleData;
	}
	public String getCoverFullUrl() {
		return coverFullUrl;
	}
	public void setCoverFullUrl(String coverFullUrl) {
		this.coverFullUrl = coverFullUrl;
	}
	public byte[] getCoverFullData() {
		return coverFullData;
	}
	public void setCoverFullData(byte[] coverFullData) {
		this.coverFullData = coverFullData;
	}
	public String[] getPictureUrls() {
		return pictureUrls;
	}
	public void setPictureUrls(String[] pictureUrls) {
		this.pictureUrls = pictureUrls;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
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
	
	public String getInfo(){
		return  fullCode + " \t " + simpleCode + " \t " + jpName + 
				" \t " + sdf.format(date) + " \t " + timeLength + "\t" + url ;
	}
}
