package com.xiaowei.android.wht.beans;

import java.io.Serializable;

import android.graphics.drawable.Drawable;

import com.xiaowei.android.wht.utis.BitmapChange;

public class Patient implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3446825733673751736L;
	
	public String getPatientname() {
		return patientname;
	}
	public void setPatientname(String patientname) {
		if(patientname == null){
			isFull = false;
		}
		else{
			this.patientname = patientname;
		}
	}
	public int getSex() {
		return sex;
	}
	public String getSexStr(){
		if (sex==0){
			return "男";
		}else if (sex==1){
			return "女";
		}else{
			return "";
		}
	}
	public void setSex(int sex) {
		this.sex = sex;
	}
	public String getBirthdate() {
		return birthdate;
	}
	public void setBirthdate(String birthdate) {
		if(birthdate == null){
			isFull = false;
		}
		else{
			this.birthdate = birthdate;
		}
	}
	public String getAreaid() {
		return areaid;
	}
	public void setAreaid(String areaid) {
		if(areaid == null){
			isFull = false;
		}
		else{
			this.areaid = areaid;
		}
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		if(address == null){
			isFull = false;
		}
		else{
			this.address = address;
		}
	}
	public String getPatientmobile() {
		return patientmobile;
	}
	public void setPatientmobile(String patientmobile) {
		if(patientmobile == null){
			isFull = false;
		}
		else{
			this.patientmobile = patientmobile;
		}
	}
	public String getHeadimg() {
		return headimg;
	}
	public void setHeadimg(String headimg) {
		if(headimg == null){
			isFull = false;
		}
		else{
			this.headimg = headimg;
		}
	}
	public String getPatientdesc() {
		return patientdesc;
	}
	public void setPatientdesc(String patientdesc) {
		if(patientdesc == null){
			isFull = false;
		}
		else{
			this.patientdesc = patientdesc;
		}
	}
	public String getIllnessname() {
		return illnessname;
	}
	public void setIllnessname(String illnessname) {
		if(illnessname == null){
			isFull = false;
		}
		else{
			this.illnessname = illnessname;
		}
	}
	public String getPatientimgone() {
		return patientimgone;
	}
	public void setPatientimgone(String patientimgone) {
		if(patientimgone == null){
			isFull = false;
		}
		else{
			this.patientimgone = patientimgone;
		}
	}
	public String getPatientimgtwo() {
		return patientimgtwo;
	}
	public void setPatientimgtwo(String patientimgtwo) {
		this.patientimgtwo = patientimgtwo;
	}
	public String getPatientimgthree() {
		return patientimgthree;
	}
	public void setPatientimgthree(String patientimgthree) {
		this.patientimgthree = patientimgthree;
	}
	public boolean isFull() {
		return isFull;
	}
	public void setFull(boolean isFull) {
		this.isFull = isFull;
	}
	private boolean isFull = true;
	private int sex;
	private String booking;//预约时间
	private String paystate;//付款状态 0:待支付 1:已支付
	private String referdoctor;//转诊医生主键
	private String acceptdoctorname;//接诊医生名称
	private String patientmobile;//手机号
	private String illnessname;//病情名称
	private String patientname;
	private String patientid;//
	private String referralid;//转诊单主键
	private String referdoctorname;//转诊医生
	private String birthdate;
	private String areaid;
	private String address;
	private String headimg;
	private String patientdesc;//病情描述
	private String patientimgone;
	private String patientimgtwo;
	private String patientimgthree;
	private String mobile;//
	private int state;//状态 转诊状态  （0：待接诊，1：待预约，2：已预约，3：已完成）
	private byte[] drawableHead;//头像
	private String acceptid;
	
	public String getAcceptid() {
		return acceptid;
	}
	public void setAcceptid(String acceptid) {
		this.acceptid = acceptid;
	}
	public Drawable getDrawable() {
		return BitmapChange.Bytes2Drawable(drawableHead);
	}
	public void setDrawable(Drawable drawable) {
		this.drawableHead = BitmapChange.Drawable2Bytes(drawable);
	}

	public String getStateStr(){
		if (state==0){
			return "待接诊";
		}else if (state==1){
			return "待预约";
		}else if (state==2){
			return "已预约";
		}else if (state==3){
			return "已完成";
		}else if(state==4){
			return "已拒绝";
		}else if(state==5){
			return "未响应";
		}else {
			return "";
		}
	}
	public int getState() {
		return state;
	}
	public void setState(int state) {
		this.state = state;
	}
	public String getBooking() {
		return booking;
	}
	public String getBookingDayMonth() {
		if(booking != null && booking.contains("-") && booking.contains(":")){
			return booking.substring(booking.indexOf("-")+1, booking.indexOf("-")+6);
		}
		return booking;
	}
	public void setBooking(String booking) {
		this.booking = booking;
	}
	public String getPaystate() {
		return paystate;
	}
	public void setPaystate(String paystate) {
		this.paystate = paystate;
	}
	public String getReferdoctor() {
		return referdoctor;
	}
	public void setReferdoctor(String referdoctor) {
		this.referdoctor = referdoctor;
	}
	public String getAcceptdoctorname() {
		return acceptdoctorname;
	}
	public void setAcceptdoctorname(String acceptdoctorname) {
		this.acceptdoctorname = acceptdoctorname;
	}
	public String getPatientid() {
		return patientid;
	}
	public void setPatientid(String patientid) {
		this.patientid = patientid;
	}
	public String getReferralid() {
		return referralid;
	}
	public void setReferralid(String referralid) {
		this.referralid = referralid;
	}
	public String getReferdoctorname() {
		if(referdoctorname.trim().length() == 2){
			String str = "";
			str = referdoctorname.substring(0, 1);
			str += "    ";
			str += referdoctorname.substring(1, 2);
			return str;
		}
		return referdoctorname;
	}
	public void setReferdoctorname(String referdoctorname) {
		this.referdoctorname = referdoctorname;
	}
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

}
