package com.xiaowei.android.wht.beans;

import java.io.Serializable;

public class DoctorScheduleInfo implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2081311247520569195L;
	String times;
	String startdate;
	String enddate;
	String userid;
	String mobile;
	String isfirst;
	String doctorscheduleid;
	int maximum;
	int bookingnum;
	public String getTimes() {
		return times;
	}
	public void setTimes(String times) {
		this.times = times;
	}
	public String getStartdate() {
		return startdate;
	}
	public void setStartdate(String startdate) {
		this.startdate = startdate;
	}
	public String getEnddate() {
		return enddate;
	}
	public void setEnddate(String enddate) {
		this.enddate = enddate;
	}
	public String getUserid() {
		return userid;
	}
	public void setUserid(String userid) {
		this.userid = userid;
	}
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public String getIsfirst() {
		return isfirst;
	}
	public void setIsfirst(String isfirst) {
		this.isfirst = isfirst;
	}
	public int getMaximum() {
		return maximum;
	}
	public void setMaximum(int maximum) {
		this.maximum = maximum;
	}
	public int getBookingnum() {
		return bookingnum;
	}
	public void setBookingnum(int bookingnum) {
		this.bookingnum = bookingnum;
	}
	public String getDoctorscheduleid() {
		return doctorscheduleid;
	}
	public void setDoctorscheduleid(String doctorscheduleid) {
		this.doctorscheduleid = doctorscheduleid;
	}
	
	
}
