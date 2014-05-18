package com.stormrage.mydmm.server.workfind;

import com.stormrage.mydmm.server.actress.ActressBean;

/**
 * 作品链接信息
 * @author StormrageWang
 * @date 2014年5月18日
 */
public class WorkFindBean {

	private String guid;//guid
	private ActressBean actressBean;//所属的演员
	private String url;
	
	public String getGuid() {
		return guid;
	}

	public void setGuid(String guid) {
		this.guid = guid;
	}

	public ActressBean getActressBean() {
		return actressBean;
	}

	public void setActressBean(ActressBean actressBean) {
		this.actressBean = actressBean;
	}

	public String getUrl() {
		return url;
	}
	
	public void setUrl(String url) {
		this.url = url;
	}
	
}
