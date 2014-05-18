package com.stormrage.mydmm.server.workfind;

import com.stormrage.mydmm.server.actress.ActressBean;

public class WorkFindBean {

	
	private ActressBean actressBean;
	private String workTitle;
	private String workUrl;
	private String url;
	private WorkPageType pageType;
	

	public ActressBean getActressBean() {
		return actressBean;
	}

	public void setActressBean(ActressBean actressBean) {
		this.actressBean = actressBean;
	}

	public String getWorkTitle() {
		return workTitle;
	}

	public void setWorkTitle(String workTitle) {
		this.workTitle = workTitle;
	}
	
	public String getWorkUrl() {
		return workUrl;
	}

	public void setWorkUrl(String workUrl) {
		this.workUrl = workUrl;
	}

	public String getUrl() {
		return url;
	}
	
	public void setUrl(String url) {
		this.url = url;
	}
	
	public WorkPageType getPageType() {
		return pageType;
	}
	
	public void setPageType(WorkPageType pageType) {
		this.pageType = pageType;
	}
}
