package com.stormrage.mydmm.server.actress;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.stormrage.mydmm.server.work.WorkBean;



public class ActressBean {

	private String guid;
	private String name;
	private String jpName;
	private String pictureGuid;
	private byte[] pictureData;
	private String url;
	private String pictureUrl;
	private List<WorkBean> works = new ArrayList<WorkBean>(50);
	
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
	
	public String getJpName() {
		return jpName;
	}
	
	public void setJpName(String jpName) {
		this.jpName = jpName;
	}
	
	public String getPictureGuid() {
		return pictureGuid;
	}
	
	public void setPictureGuid(String pictureGuid) {
		this.pictureGuid = pictureGuid;
	}
	
	public byte[] getPictureData() {
		return pictureData;
	}
	
	public void setPictureData(byte[] pictureData) {
		this.pictureData = pictureData;
	}
	
	public String getUrl() {
		return url;
	}
	
	public void setUrl(String url) {
		this.url = url;
	}
	
	public String getPictureUrl() {
		return pictureUrl;
	}
	
	public void setPictureUrl(String pictureUrl) {
		this.pictureUrl = pictureUrl;
	}

	public Iterator<WorkBean> getWorkIterator(){
		return works.iterator();
	}
	
	public void addWorkBean(WorkBean workBean){
		works.add(workBean);
	}
	
}
