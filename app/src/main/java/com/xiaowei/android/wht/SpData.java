package com.xiaowei.android.wht;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
/**
 * 专门的SharedPreferences类用于本项目
 * @author Administrator
 *
 */
public class SpData {
	String FILE = "wht";// 用于保存SharedPreferences的文件
	SharedPreferences sp = null;// 声明一个SharedPreferences
	public final static String keyPassword = "keyPassword";
	public final static String keyUserInfo = "keyUserInfo";
	/**
	 * 审核状态
	 *  int
	 */
	public final static String KeyApprovestate = "KeyApprovestate";
	/**
	 * 客户类型
	 *  String
	 */
	public final static String KeyClientType = "KeyClientType";
	/**
	 * 用户登录用手机号 
	 * String
	 */
	public final static String keyPhoneUser="keyPhoneUser";
	/**
	 * 医生端用户ID 
	 * String
	 */
	public final static String keyId="keyId";
	/**
	 * 用户验证码
	 * String
	 */
	public final static String keyCode="keyCode";
//	/**
//	 * 患者端登录用手机号 
//	 * String
//	 */
//	public final static String keyPhonePatient="keyPhonePatient";
	
	
	public SpData(Context context) {
		sp = context.getSharedPreferences(FILE, Activity.MODE_PRIVATE);
	}
	public String getStringValue(String key,String defaultValue){
		String ret = sp.getString(key, defaultValue);
		return ret;
	}
	public void setStringValue(String key,String value){
		Editor e = sp.edit();
		e.putString(key, value);
		e.commit();
	}
	public boolean getBooleanValue(String key,boolean defaultValue){
		boolean ret = sp.getBoolean(key, defaultValue);
		return ret;
	}
	public void setBooleanValue(String key,boolean value){
		Editor e = sp.edit();
		e.putBoolean(key, value);
		e.commit();
	}
	

	public float getFloatValue(String key,float defaultValue){
		float ret = sp.getFloat(key, defaultValue);
		return ret;
	}
	public void setFloatValue(String key,float value){
		Editor e = sp.edit();
		e.putFloat(key, value);
		e.commit();
	}

	public int getIntValue(String key,int defaultValue){
		int ret = sp.getInt(key, defaultValue);
		return ret;
	}
	public void setIntValue(String key,int value){
		Editor e = sp.edit();
		e.putInt(key, value);
		e.commit();
	}

	public long getLongValue(String key,long defaultValue){
		long ret = sp.getLong(key, defaultValue);
		return ret;
	}
	public void setLongValue(String key,long value){
		Editor e = sp.edit();
		e.putLong(key, value);
		e.commit();
	}

}
