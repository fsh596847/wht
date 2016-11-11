package com.xiaowei.android.wht.beans;

import java.io.Serializable;

public class DoctorBankCardBindingInfo implements Serializable {
	
	public int getReceipttype() {
		return receipttype;
	}
	public void setReceipttype(int receipttype) {
		this.receipttype = receipttype;
	}
	public String getBoundcardid() {
		return boundcardid;
	}
	public void setBoundcardid(String boundcardid) {
		this.boundcardid = boundcardid;
	}
	public String getBankname() {
		return bankname;
	}
	public void setBankname(String bankname) {
		this.bankname = bankname;
	}
	public String getAccountcode() {
		return accountcode;
	}
	public void setAccountcode(String accountcode) {
		this.accountcode = accountcode;
	}
	public String getAccountname() {
		return accountname;
	}
	public void setAccountname(String accountname) {
		this.accountname = accountname;
	}
	public String getZbankname() {
		return zbankname;
	}
	public void setZbankname(String zbankname) {
		this.zbankname = zbankname;
	}
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public String getUserid() {
		return userid;
	}
	public void setUserid(String userid) {
		this.userid = userid;
	}
	/**
	 * 
	 */
	private static final long serialVersionUID = -2478992409326503094L;
	private int receipttype;
	private String boundcardid;
	private String bankname;
	private String accountcode;
	private String accountname;
	private String zbankname;
	private String mobile;
	private String userid;

}
