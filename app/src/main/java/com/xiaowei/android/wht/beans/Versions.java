package com.xiaowei.android.wht.beans;

import java.io.Serializable;

public class Versions implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -9019460555720497236L;

	private String versionname;
	private String downUrl;
	public String getVersionname() {
		return versionname;
	}
	public void setVersionname(String versionname) {
		this.versionname = versionname;
	}
	public String getDownUrl() {
		return downUrl;
	}
	public void setDownUrl(String downUrl) {
		this.downUrl = downUrl;
	}

}
