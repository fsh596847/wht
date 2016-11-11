package com.xiaowei.android.wht.beans;

import java.io.Serializable;

public class XGNotification implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 5490352437944649141L;
	private String msgType;
	private String id;
	private String opType;


	public String getMsgType() {
		return msgType;
	}

	public void setMsgType(String msgType) {
		this.msgType = msgType;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getOpType() {
		return opType;
	}

	public void setOpType(String opType) {
		this.opType = opType;
	}

}
