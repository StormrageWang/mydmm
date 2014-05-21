package com.stormrage.mydmm.server.actress;

/**
 * 演员信息
 * @author StormrageWang
 * @date 2014年5月18日 
 */
public class ActressBean {

	private String name;//姓名
	private String fullName;//显示的姓名
	private String url;//演员信息的页面url
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public String getUrl() {
		return url;
	}
	
	public void setUrl(String url) {
		this.url = url;
	}
	
}
