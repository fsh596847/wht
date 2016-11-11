package com.xiaowei.android.wht.model;

import java.io.Serializable;

import com.xiaowei.android.wht.beans.BaseInfo;

/**
 * 患者个人信息
 * @author wudw
 *
 */
public class PatientInfo extends BaseInfo implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2611470647171285817L;
	String birthday;//":null,"
	String username;//":null,"
	int sex;//":null,"
	String nickname;//":null,"
	String headimg;
	String userid;//":"f9a8fe65555892150155595bb02d000a","
	String mobile;//":"13713981766"
	String areaid;
	String address;
	public String getAreaid() {
		return areaid;
	}
	public void setAreaid(String areaid) {
		this.areaid = areaid;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getBirthday() {
		return birthday;
	}
	public void setBirthday(String birthday) {
		this.birthday = birthday;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public int getSex() {
		return sex;
	}
	public void setSex(int sex) {
		this.sex = sex;
	}
	public String getNickname() {
		return nickname;
	}
	public void setNickname(String nickname) {
		this.nickname = nickname;
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
	public String getHeadimg() {
		return headimg;
	}
	public void setHeadimg(String headimg) {
		this.headimg = headimg;
	}
	
	
	
}
