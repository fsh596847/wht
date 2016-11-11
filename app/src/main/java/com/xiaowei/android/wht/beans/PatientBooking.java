package com.xiaowei.android.wht.beans;

import java.io.Serializable;

public class PatientBooking implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7365941865858436988L;
	String bookingid;
	String acceptid;
	String doctorscheduleid;
	String mobile;
	String msg;
	double mny;
	public String getBookingid() {
		return bookingid;
	}
	public void setBookingid(String bookingid) {
		this.bookingid = bookingid;
	}
	public String getAcceptid() {
		return acceptid;
	}
	public void setAcceptid(String acceptid) {
		this.acceptid = acceptid;
	}
	public String getDoctorscheduleid() {
		return doctorscheduleid;
	}
	public void setDoctorscheduleid(String doctorscheduleid) {
		this.doctorscheduleid = doctorscheduleid;
	}
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public double getMny() {
		return mny;
	}
	public void setMny(double mny) {
		this.mny = mny;
	}

	
}
