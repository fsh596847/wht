package com.xiaowei.android.wht.beans;

import java.io.Serializable;

public class SectionOffice implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 9173668831134046560L;
	
	private String id;
	private String departname;
	private String departcode;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getDepartname() {
		return departname;
	}
	public void setDepartname(String departname) {
		this.departname = departname;
	}
	public String getDepartcode() {
		return departcode;
	}
	public void setDepartcode(String departcode) {
		this.departcode = departcode;
	}

}
