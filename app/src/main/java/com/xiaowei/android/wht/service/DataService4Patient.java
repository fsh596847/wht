package com.xiaowei.android.wht.service;

import java.util.LinkedHashMap;
import java.util.Map;

import android.content.Context;

import com.alibaba.fastjson.JSON;
import com.xiaowei.android.wht.Config;
import com.xiaowei.android.wht.SpData;
import com.xiaowei.android.wht.utis.HlpUtils;
import com.xiaowei.android.wht.utis.HttpUtil;
/**
 * 这里只提供患者端的数据服务，部分公共方法在DataService类中
 * @author wudw
 *
 */
public class DataService4Patient  {
	
	/**
	 * 支付明细查询
	 * @param context
	 * @param mobile
	 * @param userid
	 * @return
	 * @throws Exception
	 */
	public static String queryPatientPay(Context context, String userid, String mobile, int p, int pagesize)
			throws Exception{
		Map<String,String> params = new LinkedHashMap<String, String>();
		if(mobile != null && userid != null){
			params.put("mobile", mobile);
			params.put("userid", userid);
			params.put("p", String.valueOf(p));
			params.put("pagesize", String.valueOf(pagesize));
		}else{
			return null;
		}
		String s = HttpUtil.postUrl(context, Config.queryPatientPayUrl, params);
		return s;
	}
	/**
	 * 获得当前患者的手机号
	 * @param context
	 * @return
	 */
	public static String getMyMobile(Context context){
		SpData spData = new SpData(context);
		String mobile = spData.getStringValue(SpData.keyPhoneUser, null);
		return mobile;
	}
	/**
	 * 获得当前患者的id
	 * @param context
	 * @return
	 */
	public static String getMyUserId(Context context){
		SpData spData = new SpData(context);
		String id = spData.getStringValue(SpData.keyId, null);
		return id;
	}
	/**
	 * 
	 * @param context
	 * @return
	 * @throws Exception
	 */
	public static String getHomeImgUrls(Context context) throws Exception{
		String s = HttpUtil.postUrl(context, Config.getPatientHomeImgUrl, null);
		return s;
	}
	private static void putParams(Map<String,String> params,String key,String obj,boolean must) throws Exception{
		if (HlpUtils.isEmpty(obj)){
			if (must){
				throw new Exception("参数不能为空："+key);
			}
		}else{
			params.put(key, obj);
		}
	}
	/**
	 * 查询患者病历
	 * @param context
	 * @return
	 * @throws Exception
	 */
	public static String queryPatientRecord(Context context,String userid,String mobile,String areaid,String state
			, int p, int pagesize) throws Exception{
		Map<String,String> params = new LinkedHashMap<String, String>();
		putParams(params, "userid", userid, false);
		putParams(params, "mobile", mobile, true);
		putParams(params, "areaid", areaid, false);
		putParams(params, "state", state, false);
		putParams(params, "p", String.valueOf(p), false);
		putParams(params, "pagesize", String.valueOf(pagesize), false);
		String s = HttpUtil.postUrl(context, Config.queryPatientRecordUrl, params);
		return s;
	}
	/**
	 * 查询患者医生
	 * @param context
	 * @return
	 * @throws Exception
	 */
	public static String queryPatientDoctor(Context context,String userid,String mobile, int p, int pagesize) throws Exception{
		Map<String,String> params = new LinkedHashMap<String, String>();
		putParams(params, "userid", userid, false);
		putParams(params, "mobile", mobile, true);
		putParams(params, "p", String.valueOf(p), false);
		putParams(params, "pagesize", String.valueOf(pagesize), false);
		String s = HttpUtil.postUrl(context, Config.queryPatientDoctorUrl, params);
		return s;
	}
	/**
	 * 查询患者的转诊
	 * @param context
	 * @return
	 * @throws Exception
	 */
	public static String queryPatientReferral(Context context,String userid,String mobile
			,String areaid,String state,String msgType, String id, int p, int pagesize) throws Exception{
		Map<String,String> params = new LinkedHashMap<String, String>();
		putParams(params, "userid", userid, false);
		putParams(params, "mobile", mobile, true);
		putParams(params, "areaid", areaid, false);
		putParams(params, "state", state, false);
		putParams(params, "msgType", msgType, false);
		putParams(params, "id", id, false);
		putParams(params, "p", String.valueOf(p), false);
		putParams(params, "pagesize", String.valueOf(pagesize), false);
		String s = HttpUtil.postUrl(context, Config.queryPatientReferralUrl, params);
		return s;
	}
	/**
	 * 查询患者的预约
	 * @param context
	 * @return
	 * @throws Exception
	 */
	public static String queryPatientBooking(Context context,String userid,String mobile,String isfirst,String startdate) throws Exception{
		Map<String,String> params = new LinkedHashMap<String, String>();
		putParams(params, "userid", userid, false);
		putParams(params, "mobile", mobile, true);
		putParams(params, "isfirst", isfirst, false);
		putParams(params, "startdate", startdate, false);
		String s = HttpUtil.postUrl(context, Config.queryPatientBookingUrl, params);
		return s;
	}
	/**
	 * 查询患者的个人信息
	 * @param context
	 * @return
	 * @throws Exception
	 */
	public static String getPatientInfo(Context context,String userid,String mobile) throws Exception{
		Map<String,String> params = new LinkedHashMap<String, String>();
		putParams(params, "userid", userid, false);
		putParams(params, "mobile", mobile, true);
		String s = HttpUtil.postUrl(context, Config.getPatientInfoUrl, params);
		return s;
	}
	/**
	 * 查询医生个人信息
	 * @param context
	 * @param mobile
	 * @return
	 * @throws Exception
	 */
	public static String queryDoctorPerson(Context context,String mobile) throws Exception{
		Map<String,String> params = new LinkedHashMap<String, String>();
		putParams(params, "mobile", mobile, true);
		String s = HttpUtil.postUrl(context, Config.queryDoctorPerson, params);
		return s;
	}
	
	/**
	 * 增加预约信息
	 * @param context
	 * @return
	 * @throws Exception
	 */
	public static String addPatientBooking(Context context, String moblie, String acceptdoctor,String acceptid,String doctorscheduleid) throws Exception{
		Map<String,String> params = new LinkedHashMap<String, String>();
		putParams(params, "mobile", moblie, true);
		putParams(params, "doctorscheduleid", doctorscheduleid, true);
		putParams(params, "acceptid", acceptid, true);
		putParams(params, "acceptdoctor", acceptdoctor, true);
		System.out.print("===="+Config.addPatientBookingUrl+"?"+JSON.toJSONString(params));
		String s = HttpUtil.postUrl(context, Config.addPatientBookingUrl, params);
		return s;
	}
	
	public static String getAliPay(Context context,String userid,String orderid,String mny) throws Exception{
		Map<String,String> params = new LinkedHashMap<String, String>();
		putParams(params, "userid", userid, true);
		putParams(params, "orderid", orderid, true);
		putParams(params, "mny", mny, true);
		String s = HttpUtil.postUrl(context, Config.getBookingAppAliPayUrl, params);
		return s;
	}
	/**
	 * 微信支付
	 * @param context
	 * @param userid
	 * @param orderid
	 * @param mny
	 * @return
	 * @throws Exception
	 */
	public static String getWeixinPay(Context context,String userid,String orderid,String mny) throws Exception{
		Map<String,String> params = new LinkedHashMap<String, String>();
		putParams(params, "userid", userid, true);
		putParams(params, "orderid", orderid, true);
		putParams(params, "mny", mny, true);
		String s = HttpUtil.postUrl(context, Config.getBookingAppPayUrl, params);
		return s;
	}
}
