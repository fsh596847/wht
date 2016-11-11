package com.xiaowei.android.wht.beans;

import java.io.Serializable;
import java.util.List;

public class MeetingNoticeDoctor implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5842359331138067217L;
	
	private String endtime;//结束时间
	private double mny;//报名费
	private String phone;//手机号
	private String ts;//
	private String remark;//备注
	private int orderstate;//是否报名  0：未报名  1：已报名
	private String img;//
	private String state;//
	private String city;//城市
	private String createtime;//
	private String id;//会议ID
	private String createuser;//
	private String meetprofile;//会议简介
	private String dr;//
	private String contactunit;//连续单位
	private String host;//主办方
	private String starttime;//开始时间
	private String id2;//
	private String meetcont;//会议内容
	private String whtmeetshowimg;//
	private String jd;//
	private String email;//
	private String address;//地址
	private List<MeetingImg> meetingImgs;
	private String imgdata;
	private String orderid;
	private String filename;//
	private String wd;//
	private String meetcode;//
	private String telephone;//连续电话
	private String meetname;//会议名称
	private String meetshort;//会议简称
	private String imgUrl;
	private String meetid;
	public String getImgUrl() {
		return imgUrl;
	}
	public void setImgUrl(String imgUrl) {
		this.imgUrl = imgUrl;
	}
	public String getMeetid() {
		return meetid;
	}
	public void setMeetid(String meetid) {
		this.meetid = meetid;
	}
	public String getOrderid() {
		return orderid;
	}
	public void setOrderid(String orderid) {
		this.orderid = orderid;
	}
	public String getImgdata() {
		return imgdata;
	}
	public void setImgdata(String imgdata) {
		this.imgdata = imgdata;
	}
	public List<MeetingImg> getMeetingImgs() {
		return meetingImgs;
	}
	public void setMeetingImgs(List<MeetingImg> meetingImgs) {
		this.meetingImgs = meetingImgs;
	}
	public String getEndtime() {
		return endtime;
	}
	public void setEndtime(String endtime) {
		this.endtime = endtime;
	}
	public double getMny() {
		return mny;
	}
	public void setMny(double mny) {
		this.mny = mny;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getTs() {
		return ts;
	}
	public void setTs(String ts) {
		this.ts = ts;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public int getOrderstate() {
		return orderstate;
	}
	public void setOrderstate(int orderstate) {
		this.orderstate = orderstate;
	}
	public String getImg() {
		return img;
	}
	public void setImg(String img) {
		this.img = img;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getCreatetime() {
		return createtime;
	}
	public void setCreatetime(String createtime) {
		this.createtime = createtime;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getCreateuser() {
		return createuser;
	}
	public void setCreateuser(String createuser) {
		this.createuser = createuser;
	}
	public String getMeetprofile() {
		return meetprofile;
	}
	public void setMeetprofile(String meetprofile) {
		this.meetprofile = meetprofile;
	}
	public String getDr() {
		return dr;
	}
	public void setDr(String dr) {
		this.dr = dr;
	}
	public String getContactunit() {
		return contactunit;
	}
	public void setContactunit(String contactunit) {
		this.contactunit = contactunit;
	}
	public String getHost() {
		return host;
	}
	public void setHost(String host) {
		this.host = host;
	}
	public String getStarttime() {
		return starttime;
	}
	public String getStarttimeDayMonth() {
		if(starttime != null && starttime.contains("-")){
			return starttime.substring(starttime.indexOf("-")+1, starttime.length());
		}
		return starttime;
	}
	public void setStarttime(String starttime) {
		this.starttime = starttime;
	}
	public String getId2() {
		return id2;
	}
	public void setId2(String id2) {
		this.id2 = id2;
	}
	public String getMeetcont() {
		return meetcont;
	}
	public void setMeetcont(String meetcont) {
		this.meetcont = meetcont;
	}
	public String getWhtmeetshowimg() {
		return whtmeetshowimg;
	}
	public void setWhtmeetshowimg(String whtmeetshowimg) {
		this.whtmeetshowimg = whtmeetshowimg;
	}
	public String getJd() {
		return jd;
	}
	public void setJd(String jd) {
		this.jd = jd;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getFilename() {
		return filename;
	}
	public void setFilename(String filename) {
		this.filename = filename;
	}
	public String getWd() {
		return wd;
	}
	public void setWd(String wd) {
		this.wd = wd;
	}
	public String getMeetcode() {
		return meetcode;
	}
	public void setMeetcode(String meetcode) {
		this.meetcode = meetcode;
	}
	public String getTelephone() {
		return telephone;
	}
	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}
	public String getMeetname() {
		return meetname;
	}
	public void setMeetname(String meetname) {
		this.meetname = meetname;
	}
	public String getMeetshort() {
		return meetshort;
	}
	public void setMeetshort(String meetshort) {
		this.meetshort = meetshort;
	}
}
