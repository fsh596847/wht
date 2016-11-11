package com.xiaowei.android.wht.beans;

import java.io.Serializable;

public class Area implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2141633568685075387L;
	
	private String id;
	private String mergername;
	private String areaname;
	private String uppk;
	private String areacode;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getMergername() {
		return mergername;
	}
	public void setMergername(String mergername) {
		this.mergername = mergername;
	}
	public String getAreaname() {
		return areaname;
	}
	public Area setAreaname(String areaname) {
		this.areaname = areaname;
		return this;
	}
	public String getUppk() {
		return uppk;
	}
	public void setUppk(String uppk) {
		this.uppk = uppk;
	}
	public String getAreacode() {
		return areacode;
	}
	public void setAreacode(String areacode) {
		this.areacode = areacode;
	}

}
