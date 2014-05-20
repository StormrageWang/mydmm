package com.stormrage.mydmm.server.picture;

/**
 * 图片信息
 * @author StormrageWang
 * @date 2014年5月18日
 */
public class PictureBean {
	
	public static final String EMPTY_URL = " ";
	
	private String guid;//guid
	private PictureType type;//类型
	private String url;//网络上的地址
	private byte[] data;//数据
	
	
	public String getGuid() {
		return guid;
	}
	
	public void setGuid(String guid) {
		this.guid = guid;
	}
	
	public PictureType getType() {
		return type;
	}
	
	public void setType(PictureType type) {
		this.type = type;
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
