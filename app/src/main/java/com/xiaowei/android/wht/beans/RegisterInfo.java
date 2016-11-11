package com.xiaowei.android.wht.beans;

import java.io.Serializable;

public class RegisterInfo implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -1958519388539955365L;



	public String getDoctorname() {
		return doctorname;
	}

	public void setDoctorname(String doctorname) {
		this.doctorname = doctorname;
	}

	public String getJobtitle() {
		return jobtitle;
	}

	public void setJobtitle(String jobtitle) {
		this.jobtitle = jobtitle;
	}

	public String getDuty() {
		return duty;
	}

	public void setDuty(String duty) {
		this.duty = duty;
	}

	public int getSex() {
		return sex;
	}

	public void setSex(int sex) {
		this.sex = sex;
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

	public String getDetp() {
		return detp;
	}

	public void setDetp(String detp) {
		this.detp = detp;
	}

	public String getWorkcard() {
		return workcard;
	}

	public void setWorkcard(String workcard) {
		this.workcard = workcard;
	}

	public String getLicensecard() {
		return licensecard;
	}

	public void setLicensecard(String licensecard) {
		this.licensecard = licensecard;
	}

	public String getQualifiedcard() {
		return qualifiedcard;
	}

	public void setQualifiedcard(String qualifiedcard) {
		this.qualifiedcard = qualifiedcard;
	}

	public String getGoodfield() {
		return goodfield;
	}

	public void setGoodfield(String goodfield) {
		this.goodfield = goodfield;
	}

	public String getJobresults() {
		return jobresults;
	}

	public void setJobresults(String jobresults) {
		this.jobresults = jobresults;
	}

	public String getAcceptdesc() {
		return acceptdesc;
	}

	public void setAcceptdesc(String acceptdesc) {
		this.acceptdesc = acceptdesc;
	}

	public String getQualifiedimg() {
		return qualifiedimg;
	}

	public void setQualifiedimg(String qualifiedimg) {
		this.qualifiedimg = qualifiedimg;
	}

	public String getDoctorid() {
		return doctorid;
	}

	public void setDoctorid(String doctorid) {
		this.doctorid = doctorid;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getAreaid() {
		return areaid;
	}

	public void setAreaid(String areaid) {
		this.areaid = areaid;
	}

	public String getBirthday() {
		return birthday;
	}

	public void setBirthday(String birthday) {
		this.birthday = birthday;
	}

	public int getApprovestate() {
		return approvestate;
	}

	public void setApprovestate(int approvestate) {
		this.approvestate = approvestate;
	}
	private String doctorname;//姓名
	private String jobtitle;//职称
	private String duty;//职务
	private int sex = 1;//0；男；1女
	private String headimg;//头像
	private String hospital;//所属医院
	private String detp;//所属科室
	private String workcard;//工作证
	private String licensecard;//执行证
	private String qualifiedcard;//资格证
	private String goodfield;//擅长领域
	private String jobresults;//学术成就
	private String acceptdesc;//接诊说明
	private String qualifiedimg;//认证图片
	private String doctorid;
	private String address;
	private String areaid;
	private String birthday;
	private int approvestate;
	private String headimgname;//头像
	private String qualifiedimgname;//认证图片


	public String getHeadimgname() {
		return headimgname;
	}

	public void setHeadimgname(String headimgname) {
		this.headimgname = headimgname;
	}

	public String getQualifiedimgname() {
		return qualifiedimgname;
	}

	public void setQualifiedimgname(String qualifiedimgname) {
		this.qualifiedimgname = qualifiedimgname;
	}

	private RegisterInfo() {}  
	private static RegisterInfo single=null;  
	//静态工厂方法   
	public static RegisterInfo getInstance() {  
		if (single == null) {    
			single = new RegisterInfo();  
		}    
		return single;  
	} 

	public static void setInstance(RegisterInfo user) {  
		if (user != null) {    
			single = user;  
		}    
	}

	public void clear(){
		single = new RegisterInfo();
	}

}
