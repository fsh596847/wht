package com.xiaowei.android.wht.beans;

import java.io.Serializable;

public class DoctorPerson implements Serializable {

  private String groupids;
  private String groupname;

  private String doctorname;//姓名

  private String jobtitle;//职称
  private String duty;//职务
  private int sex = 1;//0；男；1女
  private String headimg;//头像
  private String hospital;//所属医院
  private String dept;//所属科室
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
  private String paper;//
  private int approvestate;
  private String userid;
  //private byte[] drawable;//头像图片,传递数据使用时免网络再次下载，直接使用
  /*public Drawable getDrawable() {
    return BitmapChange.Bytes2Drawable(drawable);
	}
	public void setDrawable(Drawable drawable) {
		this.drawable = BitmapChange.Drawable2Bytes(drawable);
	}*/
  private int userclass;//userclass	int	0:小医生；1：大医生，2；专家 2016-06-21

  public String getGroupname() {
    return groupname;
  }

  public void setGroupname(String groupname) {
    this.groupname = groupname;
  }

  public String getGroupids() {
    return groupids;
  }

  public void setGroupids(String groupids) {
    this.groupids = groupids;
  }

  public String getDetp() {
    return detp == null ? dept : detp;
  }

  public void setDetp(String detp) {
    this.detp = detp;
  }

  public DoctorPerson() {
  }

  /**
   *
   */
  private static final long serialVersionUID = -4772686064658084891L;

  public String getDoctorname() {
    if (doctorname.trim().length() == 2) {
      String str = "";
      str = doctorname.substring(0, 1);
      str += "    ";
      str += doctorname.substring(1, 2);
      return str;
    }
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

  public String getDept() {
    return dept;
  }

  public void setDept(String dept) {
    this.dept = dept;
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

  public String getPaper() {
    return paper;
  }

  public void setPaper(String paper) {
    this.paper = paper;
  }

  public String getUserid() {
    return userid;
  }

  public void setUserid(String userid) {
    this.userid = userid;
  }

  public int getUserclass() {
    return userclass;
  }

  public void setUserclass(int userclass) {
    this.userclass = userclass;
  }
}
