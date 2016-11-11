package com.xiaowei.android.wht.beans;

import java.io.Serializable;

public class BaseInfo implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2074971196392870295L;

	public String getSexStr(int sex){
		if (sex==0){
			return "男";
		}else if (sex==1){
			return "女";
		}else{
			return "";
		}
	}
}
