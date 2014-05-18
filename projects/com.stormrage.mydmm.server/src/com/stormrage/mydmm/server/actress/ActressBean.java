package com.stormrage.mydmm.server.actress;

/**
 * 演员信息
 * @author StormrageWang
 * @date 2014年5月18日 
 */
public class ActressBean {

	private String guid;//guid
	private String name;//姓名
	private String fullName;//片假名
	private String pictureGuid;
	private String url;//演员信息的页面url
	
	public String getGuid() {
		return guid;
	}
	
	public void setGuid(String guid) {
		this.guid = guid;
	}
	
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
	
	public String getPictureGuid() {
		return pictureGuid;
	}

	public void setPictureGuid(String pictureGuid) {
		this.pictureGuid = pictureGuid;
	}
	
	public String getDescription(){
		return "[" + guid + "]" + " \t " + name  + " \t " + url;
	}
	
}
