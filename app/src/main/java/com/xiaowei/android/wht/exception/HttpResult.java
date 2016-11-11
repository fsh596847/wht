package com.xiaowei.android.wht.exception;

import java.io.Serializable;

public class HttpResult implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8134729666247041097L;
	boolean success;
	String message;
	String fileName;
	public boolean isSuccess() {
		return success;
	}
	public void setSuccess(boolean success) {
		this.success = success;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	
}
