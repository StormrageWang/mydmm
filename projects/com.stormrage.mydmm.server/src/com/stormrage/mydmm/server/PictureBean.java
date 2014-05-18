package com.stormrage.mydmm.server;

/**
 * 图片信息
 * @author StormrageWang
 * @date 2014年5月18日
 */
public class PictureBean {
	private String guid;//guid
	private String url;//网络上的地址
	private byte[] data;//数据
	
	public String getGuid() {
		return guid;
	}
	public void setGuid(String guid) {
		this.guid = guid;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public byte[] getData() {
		return data;
	}
	public void setData(byte[] data) {
		this.data = data;
	}
	
}
