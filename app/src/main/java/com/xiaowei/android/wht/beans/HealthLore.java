package com.xiaowei.android.wht.beans;

import java.io.Serializable;

public class HealthLore implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7486703638683295459L;
	
	private String id;
	private String title;
	private String detailurl;
	private String img;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getDetailurl() {
		return detailurl;
	}
	public void setDetailurl(String detailurl) {
		this.detailurl = detailurl;
	}
	public String getImg() {
		return img;
	}
	public void setImg(String img) {
		this.img = img;
	}

}
