package com.stormrage.mydmm.server.workdistinct;

import java.util.Date;

import com.stormrage.mydmm.server.work.WorkActressType;

public class WorkDistinctBean {

	private String code;
	private String title;
	private Date date;
	private WorkActressType actressType;
	
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
	
	public Date getDate() {
		return date;
	}
	
	public void setDate(Date date) {
		this.date = date;
	}
	
	public WorkActressType getActressType() {
		return actressType;
	}
	
	public void setActressType(WorkActressType actressType) {
		this.actressType = actressType;
	}
	
}
