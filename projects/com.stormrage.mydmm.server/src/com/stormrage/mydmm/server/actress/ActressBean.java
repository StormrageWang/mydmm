package com.stormrage.mydmm.server.actress;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.stormrage.mydmm.server.PictureBean;
import com.stormrage.mydmm.server.work.WorkBean;

/**
 * 演员信息
 * @author StormrageWang
 * @date 2014年5月18日 
 */
public class ActressBean {

	private String guid;//guid
	private String name;//姓名
	private String jpName;//片假名
	private PictureBean picture;
	private String url;//演员信息的页面url
	private List<WorkBean> works = new ArrayList<WorkBean>(50);//包含的作品
	
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
	
	public String getUrl() {
		return url;
	}
	
	public void setUrl(String url) {
		this.url = url;
	}
	
	public PictureBean getPicture() {
		return picture;
	}
	
	public void setPicture(PictureBean picture) {
		this.picture = picture;
	}

	public Iterator<WorkBean> getWorkIterator(){
		return works.iterator();
	}
	
	public void addWorkBean(WorkBean workBean){
		works.add(workBean);
	}
	
}
