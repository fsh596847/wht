package com.xiaowei.android.wht.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TFUtil {
	
	public static boolean isNull(String str){
		
		if(str == null || str.equals("null")){
			return true;
		}
		
		return false;
	}


	/**
	 * 判断字符是否数字
	 * @param str
	 * @return
	 */
	public static boolean isNumeric(String str)
	{
		Pattern pattern = Pattern.compile("[0-9]*");
		Matcher isNum = pattern.matcher(str);
		if( !isNum.matches() )
		{
			return false;
		}
		return true;
	}

	/*
	 * 判断是否手机号
	 */
	public static boolean isPhone(String str){

		Pattern pattern = Pattern.compile("^((13[0-9])|(15[^4,\\D])|(18[0-9])|(17[6-8]))\\d{8}$");
		Matcher isMobiles = pattern.matcher(str);
		return isMobiles.matches();
	}

	public static boolean isValidatePicturePath(String picturePath)
	{
		Pattern p = Pattern.compile(".+(.JPEG|.jpeg|.JPG|.jpg|.BMP|.bmp|.PNG|.png)$");
		Matcher m = p.matcher(picturePath);
		return m.matches();
	}

}
