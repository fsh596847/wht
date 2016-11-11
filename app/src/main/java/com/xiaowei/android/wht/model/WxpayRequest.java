package com.xiaowei.android.wht.model;

import java.io.Serializable;

public class WxpayRequest implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5240050530477508759L;

	String sign;//":"5455A5AB09532C608B36710AA9E9A5EC","
	String appId;//":"wxead4dd2a6eada581","
	String timeStamp;//":"1440133336","
	String partnerId;//":"1264767001","
	String packageValue;//":"Sign=WXPay","
	String nonceStr;//":"8e296a067a37563370ded05f5a3bf3ec","
	String prepayId;//":"wx201508211302157185b323490197806062"}
	public String getSign() {
		return sign;
	}
	public void setSign(String sign) {
		this.sign = sign;
	}
	public String getAppId() {
		return appId;
	}
	public void setAppId(String appId) {
		this.appId = appId;
	}
	public String getTimeStamp() {
		return timeStamp;
	}
	public void setTimeStamp(String timeStamp) {
		this.timeStamp = timeStamp;
	}
	public String getPartnerId() {
		return partnerId;
	}
	public void setPartnerId(String partnerId) {
		this.partnerId = partnerId;
	}
	public String getPackageValue() {
		return packageValue;
	}
	public void setPackageValue(String packageValue) {
		this.packageValue = packageValue;
	}
	public String getNonceStr() {
		return nonceStr;
	}
	public void setNonceStr(String nonceStr) {
		this.nonceStr = nonceStr;
	}
	public String getPrepayId() {
		return prepayId;
	}
	public void setPrepayId(String prepayId) {
		this.prepayId = prepayId;
	}
	
	
	
}
