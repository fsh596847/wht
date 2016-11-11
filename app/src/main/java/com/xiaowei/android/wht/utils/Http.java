package com.xiaowei.android.wht.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.text.TextUtils;

public class Http {
	
	/**
	 * 发送普通的键值对
	 * @param url
	 * @param paraMap
	 * @return
	 */
	public static String post(String url, Map<String, String> paraMap){
		String resStr = null;
		try {
			//NameValuePair实现请求参数的封装 
	        List <NameValuePair> params = new ArrayList <NameValuePair>();  
	        if(paraMap != null){
	        	Iterator<String> ite = paraMap.keySet().iterator();
	        	while(ite.hasNext()){
	        		String key = ite.next();
	        		params.add(new BasicNameValuePair(key, paraMap.get(key))); 
	        	}
	        }
	        
	        HttpPost request = new HttpPost(url);  
	        
	        /* 添加请求参数到请求对象*/  
	        request.setEntity((HttpEntity) new UrlEncodedFormEntity(params, HTTP.UTF_8)); 
			
			// 发送请求  
	        HttpResponse httpResponse = new DefaultHttpClient().execute(request);  

			mLog.d("Http", "toCash:"+httpResponse.getStatusLine().getStatusCode());
			if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) { 
				// 得到应答的字符串，这也是一个 JSON 格式保存的数据  
				resStr = EntityUtils.toString(httpResponse.getEntity());  
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return resStr;
	}
	
	/**
	 * 发送JOSN或XML
	 * @param url
	 * @param json
	 * @return
	 */
	public static String post(String url, String json){
		
		String resStr = null;
		try {
	        
	        HttpPost request = new HttpPost(url);  
	        StringEntity entity = new StringEntity(json, HTTP.UTF_8);
	        /* 添加请求参数到请求对象*/  
	        request.setEntity(entity); 
			// 发送请求  
	        HttpResponse httpResponse = new DefaultHttpClient().execute(request);  

			mLog.d("Http", "toCash:"+httpResponse.getStatusLine().getStatusCode());
			if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) { 
				// 得到应答的字符串，这也是一个 JSON 格式保存的数据  
				resStr = EntityUtils.toString(httpResponse.getEntity());  
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return resStr;
	}
	
	/**
	 * 处理结束
	 * @param json
	 * @return
	 */
	public static Map<String, Object> dealJsonStr(String json) {
		Map<String, Object> map = new HashMap<String, Object>();
		if(TextUtils.isEmpty(json)){
			map.put("errormsg", "远程服务器响应异常,请检查网络是否通畅!");
			return map;
		}
		try {
			JSONObject result = new JSONObject(json);
			int status = result.getInt("status");
			JSONObject data = result.getJSONObject("data");
			if (status == 1) {
				map.put("data", data);
			}else{
				map.put("errormsg", data);
			}
		} catch (JSONException e) {
			e.printStackTrace();
			map.put("errormsg", "异常:"+e.getMessage());
		}
		return map;
	}

}
