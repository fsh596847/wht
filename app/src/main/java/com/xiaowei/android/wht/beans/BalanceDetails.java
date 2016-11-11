package com.xiaowei.android.wht.beans;

import java.io.Serializable;

import com.xiaowei.android.wht.R;

import android.content.res.Resources;

public class BalanceDetails implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -9029326506188394737L;
	
	private String id;
	private String name;
	private String billdate;
	private int incometype;//收支付类型 o 收入；1支出
	private String createtime;
	private String mny;
	private int bustype;//业务类型 0 转诊；1； 提现；
	private String tradecode;
	private int paytype;//(患者端) 支付类型（0微信；1：支付宝；2：银联  
	private String acceptdoctorname;//(患者端) 接诊专家
	public String getAcceptdoctorname() {
		return acceptdoctorname;
	}
	public void setAcceptdoctorname(String acceptdoctorname) {
		this.acceptdoctorname = acceptdoctorname;
	}
	public int getPaytypeImgResourcesId() {
		switch (paytype) {
		case 0:
			return R.drawable.pay_wechat;

		case 1:
			return R.drawable.pay_alipay2;
			
		case 2:
			return R.drawable.pay_yinlian;
		}
		return R.drawable.pay_wechat;
	}
	public String getPaytypeStr() {
		switch (paytype) {
		case 0:
			return "微信支付";

		case 1:
			return "支付宝支付";
			
		case 2:
			return "银联支付";
		}
		return "";
	}
	public int getPaytype() {
		return paytype;
	}
	public void setPaytype(int paytype) {
		this.paytype = paytype;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getBilldate() {
		return billdate;
	}
	public void setBilldate(String billdate) {
		this.billdate = billdate;
	}
	public int getIncometype() {
		return incometype;
	}
	public void setIncometype(int incometype) {
		this.incometype = incometype;
	}
	/*public String getIncometypeS() {
		switch (incometype) {
		case 0:
			return "收入";

		case 1:
			return "转出";
		}
		return "";
	}*/
	public String getIncometypeF() {
		switch (incometype) {
		case 0:
			return "+";

		case 1:
			return "-";
		}
		return "";
	}
	public String getCreatetime() {
		return createtime;
	}
	public String getCreatetimeMMddHHmm() {
		if(createtime.contains("-") && createtime.contains(":")){
			return createtime.substring(createtime.indexOf("-")+1, createtime.indexOf(":")+3);
		}
		return createtime;
	}
	public void setCreatetime(String createtime) {
		this.createtime = createtime;
	}
	public String getMny() {
		return mny;
	}
	public void setMny(String mny) {
		this.mny = mny;
	}
	public int getBustype() {
		return bustype;
	}
	/*public String getBustypeS() {
		switch (bustype) {
		case 0:
			return "接诊";

		case 1:
			return "提现";
		}
		return "";
	}*/
	public void setBustype(int bustype) {
		this.bustype = bustype;
	}
	public String getTradecode() {
		return tradecode;
	}
	public void setTradecode(String tradecode) {
		this.tradecode = tradecode;
	}

}
