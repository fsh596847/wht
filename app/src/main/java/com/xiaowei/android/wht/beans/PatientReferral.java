package com.xiaowei.android.wht.beans;

import java.io.Serializable;

public class PatientReferral extends BaseInfo implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3630867811318798063L;
	/**
	 * 职称
	 */
	String jobtitle;
	/**
	 * 医生名称
	 */
	String doctorname;
	/**
	 * 地址
	 */
	String address;
	/**
	 * 头像（路径）
	 */
	String headimg;
	/**
	 * 医院
	 */
	String hospital;
	/**
	 * 转诊状态  （0：待接诊，1：待预约，2：已预约，3：已完成）
	 */
	int state;
	/**
	 * 学术成就
	 */
	String jobresults;
	/**
	 * 发表著作和论文
	 */
	String paper;
	/**
	 * 地区主键
	 */
	String areaid;
	/**
	 * 性别  0；男；1；女
	 */
	int sex;
	/**
	 * 接诊单主键
	 */
	String acceptid;
	/**
	 * 职务
	 */
	String duty;
	/**
	 * 转诊单主键
	 */
	String referralid;
	/**
	 * 付款状态 0:待支付 1:已支付
	 */
	int paystate;
	/**
	 * 用户主键
	 */
	String userid;
	/**
	 * 预约时间
	 */
	String booking;
	/**
	 * 接诊医生手机
	 */
	String acceptmobile;
	/**
	 * 接诊医生id
	 */
	String acceptdoctor;
	String acceptdoctorname;
	String referdoctorname;
	String patientname;
	String dept;
	String patientbirthday;
	String patientdesc;
	String illnessname;
	String patientaddress;
	String patientareaid;
	String patientimgone;
	String patientimgtwo;
	String patientimgthree;
	String patientsex;
	
	public String getJobtitle() {
		return jobtitle;
	}
	public void setJobtitle(String jobtitle) {
		this.jobtitle = jobtitle;
	}
	public String getDoctorname() {
		return doctorname;
	}
	public void setDoctorname(String doctorname) {
		this.doctorname = doctorname;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getHeadimg() {
		return headimg;
	}
	public void setHeadimg(String headimg) {
		this.headimg = headimg;
	}
	public String getHospital() {
		return hospital;
	}
	public void setHospital(String hospital) {
		this.hospital = hospital;
	}
	public int getState() {
		return state;
	}
	public void setState(int state) {
		this.state = state;
	}
	public String getJobresults() {
		return jobresults;
	}
	public void setJobresults(String jobresults) {
		this.jobresults = jobresults;
	}
	public String getPaper() {
		return paper;
	}
	public void setPaper(String paper) {
		this.paper = paper;
	}
	public String getAreaid() {
		return areaid;
	}
	public void setAreaid(String areaid) {
		this.areaid = areaid;
	}
	public int getSex() {
		return sex;
	}
	public void setSex(int sex) {
		this.sex = sex;
	}
	public String getAcceptid() {
		return acceptid;
	}
	public void setAcceptid(String acceptid) {
		this.acceptid = acceptid;
	}
	public String getDuty() {
		return duty;
	}
	public void setDuty(String duty) {
		this.duty = duty;
	}
	public String getReferralid() {
		return referralid;
	}
	public void setReferralid(String referralid) {
		this.referralid = referralid;
	}
	public int getPaystate() {
		return paystate;
	}
	public void setPaystate(int paystate) {
		this.paystate = paystate;
	}
	public String getUserid() {
		return userid;
	}
	public void setUserid(String userid) {
		this.userid = userid;
	}
	
	public String getBooking() {
		return booking;
	}
	public void setBooking(String booking) {
		this.booking = booking;
	}
	public String getAcceptmobile() {
		return acceptmobile;
	}
	public void setAcceptmobile(String acceptmobile) {
		this.acceptmobile = acceptmobile;
	}
	public String getAcceptdoctor() {
		return acceptdoctor;
	}
	public void setAcceptdoctor(String acceptdoctor) {
		this.acceptdoctor = acceptdoctor;
	}
	
	public String getAcceptdoctorname() {
		return acceptdoctorname;
	}
	public void setAcceptdoctorname(String acceptdoctorname) {
		this.acceptdoctorname = acceptdoctorname;
	}
	public String getReferdoctorname() {
		return referdoctorname;
	}
	public void setReferdoctorname(String referdoctorname) {
		this.referdoctorname = referdoctorname;
	}
	public String getPatientname() {
		return patientname;
	}
	public void setPatientname(String patientname) {
		this.patientname = patientname;
	}
	
	
	public String getPatientdesc() {
		return patientdesc;
	}
	public void setPatientdesc(String patientdesc) {
		this.patientdesc = patientdesc;
	}
	public String getIllnessname() {
		return illnessname;
	}
	public void setIllnessname(String illnessname) {
		this.illnessname = illnessname;
	}
	public String getPatientaddress() {
		return patientaddress;
	}
	public void setPatientaddress(String patientaddress) {
		this.patientaddress = patientaddress;
	}
	public String getPatientareaid() {
		return patientareaid;
	}
	public void setPatientareaid(String patientareaid) {
		this.patientareaid = patientareaid;
	}
	public String getPatientimgone() {
		return patientimgone;
	}
	public void setPatientimgone(String patientimgone) {
		this.patientimgone = patientimgone;
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
	public String getPatientsex() {
		return patientsex;
	}
	public void setPatientsex(String patientsex) {
		this.patientsex = patientsex;
	}
	public String getPatientbirthday() {
		return patientbirthday;
	}
	public void setPatientbirthday(String patientbirthday) {
		this.patientbirthday = patientbirthday;
	}
	public String getDept() {
		return dept;
	}
	public void setDept(String dept) {
		this.dept = dept;
	}
	public String getStatusStr(){
		String ret = null;
		switch (state) {
		case StateWaitingDoctorAccept:
			ret = "待接诊";
			break;
		case StateToBooking:
			ret = "待预约";
			break;
		case StateBeBooked:
			ret = "已预约";
			break;
		case StateBeFinished:
			ret = "已完成";
			break;
		case StateToFinishing:
			ret = "已拒绝";
			break;
		case StateNoAccept:
			ret = "未响应";
			break;
		default:
			ret = "";
			break;
		}
		return ret;
	}
	final public static int StateWaitingDoctorAccept = 0;
	final public static int StateToBooking = 1;
	final public static int StateBeBooked = 2;
	final public static int StateBeFinished = 3;
	final public static int StateToFinishing = 4;
	final public static int StateNoAccept = 5;
}
