package com.stormrage.mydmm.server.torrent.work;

import java.util.Date;

import com.stormrage.mydmm.server.workfind.WorkActressType;

public class WorkDistinctBean {

	private String code;
	private String title;
	private WorkActressType actressType;
	private Date date;
	
	public String getCode() {
		return code;
	}
	
	public void setCode(String code) {
		this.code = code;
	}
	
	public String getTitle() {
		return title;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	public WorkActressType getActressType() {
		return actressType;
	}
	
	public void setActressType(WorkActressType actressType) {
		this.actressType = actressType;
	}
	
	public Date getDate() {
		return date;
	}
	
	public void setDate(Date date) {
		this.date = date;
	}
	
}
