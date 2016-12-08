package com.xiaowei.android.wht.service;

import android.content.Context;
import com.xiaowei.android.wht.Config;
import com.xiaowei.android.wht.utils.mLog;
import com.xiaowei.android.wht.utis.HttpUtil;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;

public class DataService  {
	
	/**
	 * 未读信息查询
	 * @param context
	 * @param mobile
	 * @param msgtype  医生端的:DOCTOR，患者端:PATIENT
	 * @return
	 * @throws Exception
	 */
	public static String queryNotReadNoctice(Context context, String mobile, String msgtype)
			throws Exception{
		Map<String,String> params = new LinkedHashMap<String, String>();
		if(mobile != null && msgtype != null){
			params.put("mobile", mobile);
			params.put("msgtype", msgtype);
		}else{
			return null;
		}
		String s = HttpUtil.postUrl(context, Config.queryNotReadNocticeUrl, params);
		return s;
	}
	
	/**
	 * 修改患者信息
	 * @param context
	 * @param mobile
	 * @param userid
	 * @param username
	 * @param sex
	 * @param birthday
	 * @param headimg
	 * @return
	 * @throws Exception
	 */
	public static String updatePatientPerson(Context context, String mobile, String userid, String username
			, int sex, String birthday, String headimg, String areaid, String address)
			throws Exception{
		Map<String,String> params = new LinkedHashMap<String, String>();
		if(mobile != null){
			params.put("mobile", mobile);
			params.put("userid", userid);
			params.put("username", username);
			params.put("sex", String.valueOf(sex));
			params.put("birthday", birthday);
			params.put("areaid", areaid);
			params.put("address", address);
			if(headimg != null){
				params.put("headimg", headimg);
			}
		}else{
			return null;
		}
		String s = HttpUtil.postUrl(context, Config.updatePatientPersonUrl, params);
		return s;
	}
	
	/**
	 * 检查版本
	 * @param context
	 * @param mobile
	 * @return
	 * @throws Exception
	 */
	public static String getVersion(Context context, String mobile)
			throws Exception{
		Map<String,String> params = new LinkedHashMap<String, String>();
		if(mobile != null){
			params.put("mobile", mobile);
		}else{
			return null;
		}
		String s = HttpUtil.postUrl(context, Config.getVersionUrl, params);
		return s;
	}
	
	/**
	 * 消息标记为已读
	 * @param context
	 * @param mobile
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public static String updateMyNotice(Context context, String mobile, String id, String state)
			throws Exception{
		Map<String,String> params = new LinkedHashMap<String, String>();
		if(mobile != null && id != null){
			params.put("mobile", mobile);
			params.put("id", id);
			params.put("state", state);
		}else{
			return null;
		}
		String s = HttpUtil.postUrl(context, Config.updateMyNoticeUrl, params);
		return s;
	}
	
	/**
	 * 消息查询
	 * @param context
	 * @param mobile
	 * @param pagesize 每页显示多少条 (可空,不传默认10)
	 * @param p --当前页(从1开始)
	 * @return
	 * @throws Exception
	 */
	public static String getMyNotice(Context context, String mobile, int pagesize, int p, String msgtype)
			throws Exception{
		Map<String,String> params = new LinkedHashMap<String, String>();
		if(mobile != null && p >= 1){
			params.put("mobile", mobile);
			params.put("p", String.valueOf(p));
			params.put("pagesize", String.valueOf(pagesize));
			params.put("msgtype", msgtype);
		}else{
			return null;
		}
		String s = HttpUtil.postUrl(context, Config.getMyNoticeUrl, params);
		return s;
	}
	
	/**
	 * 拒绝接诊
	 * @param context
	 * @param mobile
	 * @param referid 转诊主键（必传）
	 * @return
	 * @throws Exception
	 */
	public static String doctorRejec(Context context,String mobile,String referid)
			throws Exception{
		Map<String,String> params = new LinkedHashMap<String, String>();
		if(mobile != null && referid != null){
			params.put("mobile", mobile);
			params.put("referid", referid);
		}else{
			return null;
		}
		String s = HttpUtil.postUrl(context, Config.doctorRejectUrl, params);
		return s;
	}
	
	/**
	 * 再转诊
	 * @param context
	 * @param mobile
	 * @param acceptdoctor 接诊医生主键（0必传）
	 * @param bustype  0 ;请求转诊；1；系统转诊 （必传）
	 * @param referid 转诊主键（必传）
	 * @return
	 * @throws Exception
	 */
	public static String againRefer(Context context,String mobile,String acceptdoctor,int bustype,String referid)
			throws Exception{
		Map<String,String> params = new LinkedHashMap<String, String>();
		if(mobile != null && referid != null && ((bustype == 0 && acceptdoctor != null) || bustype ==1)){
			params.put("mobile", mobile);
			params.put("acceptdoctor", acceptdoctor);
			params.put("bustype", String.valueOf(bustype));
			params.put("referid", referid);
		}else{
			return null;
		}
		String s = HttpUtil.postUrl(context, Config.againReferUrl, params);
		return s;
	}
	
	/**
	 * 会议支付宝支付
	 * @param context
	 * @param userid
	 * @param orderid
	 * @param mny
	 * @param meetid
	 * @return
	 * @throws Exception
	 */
	public static String getAliPay(Context context,String userid,String orderid,double mny,String meetid)
			throws Exception{
		Map<String,String> params = new LinkedHashMap<String, String>();
		if(userid != null && orderid != null && meetid != null && mny > 0){
			params.put("userid", userid);
			params.put("orderid", orderid);
			params.put("mny", String.valueOf(mny));
			params.put("meetid", meetid);
		}else{
			return null;
		}
		String s = HttpUtil.postUrl(context, Config.getAliPayUrl, params);
		return s;
	}

	/**
	 * 打赏支付宝支付
	 *
	 * @throws Exception
	 */
	public static String getAliPayAreward(Context context, String userid, String caseid, double mny)
			throws Exception {
		Map<String, String> params = new LinkedHashMap<String, String>();
		if (userid != null && caseid != null && mny > 0) {
			params.put("userid", userid);
			params.put("mny", String.valueOf(mny));
			params.put("caseid", caseid);
		} else {
			return null;
		}
		String s = HttpUtil.postUrl(context, Config.ailPayPayAreward, params);
		return s;
	}

	/**
	 * 打赏微信支付
	 */
	public static String getwetChatAreward(Context context, String userid, String caseid, double mny)
			throws Exception {
		Map<String, String> params = new LinkedHashMap<String, String>();
		if (userid != null && caseid != null && mny > 0) {
			params.put("userid", userid);
			params.put("mny", String.valueOf(mny));
			params.put("caseid", caseid);
		} else {
			return null;
		}
		String s = HttpUtil.postUrl(context, Config.weiChatPayAreward, params);
		return s;
	}
	
	/**
	 * 会议报名
	 * @param context
	 * @param mobile
	 * @param meetid
	 * @param username
	 * @param sex
	 * @param hospital
	 * @param dept 科室
	 * @param address
	 * @param areaid
	 * @param doctorno 执业证
	 * @param jobtitle 职务
	 * @param duty 职称
	 * @param isstay 是否住宿
	 * @return
	 * @throws Exception
	 */
	public static String meetingApply(Context context,String mobile, String meetid, String username, int sex, String hospital
			, String dept, String address, String areaid, String doctorno, String jobtitle, String duty, boolean isstay)
			throws Exception{
		Map<String,String> params = new LinkedHashMap<String, String>();
		if(mobile != null && meetid != null && username != null && (sex == 0 || sex == 1)&& hospital != null
				&& dept != null && address != null && areaid != null && doctorno != null && jobtitle != null && duty != null){
			params.put("mobile", mobile);
			params.put("meetid", meetid);
			params.put("username", username);
			params.put("sex", String.valueOf(sex));
			params.put("hospital", hospital);
			params.put("dept", dept);
			params.put("address", address);
			params.put("areaid", areaid);
			params.put("doctorno", doctorno);
			params.put("jobtitle", jobtitle);
			params.put("duty", duty);
			params.put("isstay", isstay ? "1" : "0");
		}else{
			return null;
		}
		String s = HttpUtil.postUrl(context, Config.addMeetOrderUrl, params);
		return s;
	}
	
	/**
	 * 参会记录查询
	 * @param context
	 * @param mobile
	 * @param type 查询类型（必传）0：待参会；1：已参会
	 * @return
	 * @throws Exception
	 */
	public static String queryMeetRecord(Context context, String mobile, int type, int p, int pagesize)
			throws Exception{
		Map<String,String> params = new LinkedHashMap<String, String>();
		if(mobile != null && (type == 0 || type == 1)){
			params.put("mobile", mobile);
			params.put("type", String.valueOf(type));
			params.put("p", String.valueOf(p));
			params.put("pagesize", String.valueOf(pagesize));
		}else{
			return null;
		}
		String s = HttpUtil.postUrl(context, Config.queryMeetRecordUrl, params);
		return s;
	}
	
	/**
	 * 查询会议
	 * @param context
	 * @param type 1，正在进行；2，会议预报；3，往期回顾
	 * @param userid
	 * @param mobile
	 * @return
	 * @throws Exception
	 */
	public static String queryMeetingNotice(Context context, int type, String userid, String mobile, int p, int pagesize)
			throws Exception{
		Map<String,String> params = new LinkedHashMap<String, String>();
		if(mobile != null && userid != null){
			params.put("userid", userid);
			params.put("mobile", mobile);
			params.put("p", String.valueOf(p));
			params.put("pagesize", String.valueOf(pagesize));
		}else{
			return null;
		}
		String url = Config.meetdocIngUrl;
		switch (type) {
		case 2:
			url = Config.meetdocBeginUrl;
			break;

		case 3:
			url = Config.meetdocEndUrl;
			break;
		}
		String s = HttpUtil.postUrl(context, url, params);
		return s;
	}
	
	/**
	 * 
	 * @param context
	 * @param receipttype 类型   0;微信；1：支付宝；2：银联(必传)
	 * @param accountname
	 * @param accountcode
	 * @param bankname
	 * @param accountmny
	 * @param zbankname 支行名称(必传)
	 * @param mobile
	 * @param password 安全密码(必传) 六位数字
	 * @return
	 * @throws Exception
	 */
	public static String addTakeMny(Context context,int receipttype, String accountname, String accountcode, String bankname
			,  String accountmny, String zbankname, String mobile, String password, String boundcardid, String takemny)
			throws Exception{
		Map<String,String> params = new LinkedHashMap<String, String>();
		if(accountname != null && accountcode != null && bankname != null && zbankname != null && mobile != null
				&& password != null && accountmny != null && boundcardid != null && takemny != null){
			params.put("bankname", bankname);
			params.put("mobile", mobile);
			params.put("receipttype", String.valueOf(receipttype));
			params.put("accountcode", accountcode);
			params.put("accountname", accountname);
			params.put("accountmny", accountmny);
			params.put("zbankname", zbankname);
			params.put("safepwd", password);
			params.put("boundcardid", boundcardid);
			params.put("takemny", takemny);
			
		}else{
			return null;
		}
		String s = HttpUtil.postUrl(context, Config.addTakeMnyUrl, params);
		return s;
	}
	
	/**
	 * 查询绑定的银行卡
	 * @param context
	 * @param mobile 必须
	 * @param userid 必须
	 * @return
	 * @throws Exception
	 */
	public static String queryBoundCard(Context context, String mobile, String userid)
			throws Exception{
		Map<String,String> params = new LinkedHashMap<String, String>();
		if(mobile != null && userid != null){
			params.put("mobile", mobile);
			params.put("userid", userid);
		}else{
			return null;
		}
		String s = HttpUtil.postUrl(context, Config.queryBoundCardUrl, params);
		return s;
	}
	
	/**
	 * 提现密码修改
	 * @param context
	 * @param safepwd
	 * @param userid
	 * @param mobile
	 * @param confirmsafepwd
	 * @return
	 * @throws Exception
	 */
	public static String updateSafePwd(Context context, String safepwd, String userid
			, String mobile, String confirmsafepwd)
			throws Exception{
		Map<String,String> params = new LinkedHashMap<String, String>();
		if(safepwd != null && userid != null && confirmsafepwd != null && mobile != null){
			params.put("userid", userid);
			params.put("mobile", mobile);
			params.put("safepwd", safepwd);
			params.put("confirmsafepwd", confirmsafepwd);
		}else{
			return null;
		}
		String s = HttpUtil.postUrl(context, Config.updateSafePwdUrl, params);
		return s;
	}
	
	/**
	 * 提现密码修改前验证
	 * @param context
	 * @param receipttype
	 * @param accountname
	 * @param accountcode
	 * @param userid
	 * @param mobile
	 * @param boundcardid
	 * @return
	 * @throws Exception
	 */
	public static String checkBoundCard(Context context,int receipttype, String accountname, String accountcode, String userid
			, String mobile, String boundcardid)
			throws Exception{
		Map<String,String> params = new LinkedHashMap<String, String>();
		if(accountname != null && accountcode != null && userid != null && boundcardid != null && mobile != null){
			params.put("userid", userid);
			params.put("mobile", mobile);
			params.put("accountname", accountname);
			params.put("accountcode", accountcode);
			params.put("receipttype", String.valueOf(receipttype));
			params.put("boundcardid", boundcardid);
		}else{
			return null;
		}
		String s = HttpUtil.postUrl(context, Config.checkBoundCardUrl, params);
		return s;
	}
	
	/**
	 * 修改绑定的银行卡
	 * @param context
	 * @param receipttype
	 * @param accountname
	 * @param accountcode
	 * @param bankname
	 * @param zbankname
	 * @param mobile
	 * @param password
	 * @param code
	 * @param boundcardid 主键ID
	 * @return
	 * @throws Exception
	 */
	public static String updateBoundCard(Context context,int receipttype, String accountname, String accountcode, String bankname
			, String zbankname, String mobile, String password, String code, String boundcardid)
			throws Exception{
		Map<String,String> params = new LinkedHashMap<String, String>();
		if(accountname != null && accountcode != null && bankname != null && zbankname != null && mobile != null
				&& password != null && code != null && boundcardid != null){
			params.put("boundcardid", boundcardid);
			params.put("bankname", bankname);
			params.put("accountname", accountname);
			params.put("accountcode", accountcode);
			params.put("receipttype", String.valueOf(receipttype));
			params.put("zbankname", zbankname);
			params.put("mobile", mobile);
			params.put("password", password);
			params.put("code", code);
		}else{
			return null;
		}
		String s = HttpUtil.postUrl(context, Config.updateBoundCardUrl, params);
		return s;
	}
	
	/**
	 * 添加绑定银行卡
	 * @param context
	 * @param receipttype 0;微信；1：支付宝；2：银联(必传)
	 * @param accountname 持卡人
	 * @param accountcode 银行卡
	 * @param bankname 开户行
	 * @param zbankname 开户网点
	 * @param mobile 手机
	 * @param password 交易密码
	 * @param mcode 验证码
	 * @return
	 * @throws Exception
	 */
	public static String addBoundCard(Context context,int receipttype, String accountname, String accountcode, String bankname
			, String zbankname, String mobile, String password, String confirmsafepwd, String code)
			throws Exception{
		Map<String,String> params = new LinkedHashMap<String, String>();
		if(accountname != null && accountcode != null && bankname != null && zbankname != null && mobile != null
				&& password != null && code != null && confirmsafepwd != null){
			params.put("bankname", bankname);
			params.put("accountname", accountname);
			params.put("accountcode", accountcode);
			params.put("receipttype", String.valueOf(receipttype));
			params.put("zbankname", zbankname);
			params.put("mobile", mobile);
			params.put("safepwd", password);
			params.put("confirmsafepwd", confirmsafepwd);
			params.put("mcode", code);
		}else{
			return null;
		}
		String s = HttpUtil.postUrl(context, Config.addBoundCardUrl, params);
		return s;
	}
	
	/**
	 * 余额明细查询
	 * @param context
	 * @param mobile
	 * @param userid
	 * @return
	 * @throws Exception
	 */
	public static String queryAccountRecord(Context context, String userid, String mobile, int p, int pagesize)
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
		String s = HttpUtil.postUrl(context, Config.queryAccountRecordUrl, params);
		return s;
	}
	
	/**
	 * 查询余额
	 * @param context
	 * @param userid
	 * @return
	 * @throws Exception
	 */
	public static String queryUserMny(Context context, String userid)
			throws Exception{
		Map<String,String> params = new LinkedHashMap<String, String>();
		if(userid != null){
			params.put("userid", userid);
		}else{
			return null;
		}
		String s = HttpUtil.postUrl(context, Config.queryUserMnyUrl, params);
		return s;
	}
	
	/**
	 * 接诊
	 * @param context
	 * @param referralid 转诊主键（必传）
	 * @return
	 * @throws Exception
	 */
	public static String updateDoctorAccept(Context context, String referralid,String mobile,String acceptid)
			throws Exception{
		Map<String,String> params = new LinkedHashMap<String, String>();
		if(referralid != null && mobile != null && acceptid != null){
			params.put("referralid", referralid);
			params.put("mobile", mobile);
			params.put("acceptid", acceptid);
		}else{
			return null;
		}
		String s = HttpUtil.postUrl(context, Config.updateDoctorAcceptUrl, params);
		return s;
	}
	
	/**
	 * 我（医生）的接诊
	 * @param context
	 * @param state 转诊状态  （0：待接诊，1：待预约，2：已预约，3：已完成）
	 * @param userid 用户的主键(非必须)
	 * @param mobile 用户手机(必须)
	 * @param areaid 地区主键
	 * @return
	 * @throws Exception
	 */
	public static String queryDoctorAccept(Context context, int state, String userid, String mobile
			, String areaid, String msgType, String id, int p, int pagesize)
			throws Exception{
		Map<String,String> params = new LinkedHashMap<String, String>();
		if(mobile != null){
			if(state >= 0 && state <= 5){
				mLog.d("http", "state:"+state);
				params.put("state", String.valueOf(state));
			}
			params.put("userid", userid);
			params.put("mobile", mobile);
			params.put("areaid", areaid);
			params.put("msgType", msgType);
			params.put("id", id);
			params.put("p", String.valueOf(p));
			params.put("pagesize", String.valueOf(pagesize));
			mLog.d("http", "userid:"+userid+",mobile:"+mobile+",areaid:"+areaid+",msgType:"+msgType+",id:"+id+",p:"+p+",pagesize:"+pagesize);
		}else{
			return null;
		}
		String s = HttpUtil.postUrl(context, Config.queryDoctorAcceptUrl, params);
		return s;
	}
	
	/**
	 * 我（医生）的转诊
	 * @param context
	 * @param state 转诊状态  （0：待接诊，1：待预约，2：已预约，3：已完成）
	 * @param userid 用户的主键(非必须)
	 * @param mobile 用户手机(必须)
	 * @param areaid 地区主键
	 * @return
	 * @throws Exception
	 */
	public static String queryDoctorReferral(Context context, int state, String userid, String mobile
			, String areaid, String msgType, String id, int p, int pagesize)
			throws Exception{
		Map<String,String> params = new LinkedHashMap<String, String>();
		if(mobile != null){
			if(state >= 0 && state <= 5){
				mLog.d("http", "state:"+state);
				params.put("state", String.valueOf(state));
				mLog.w("http", "state:"+state);
			}
			params.put("userid", userid);
			params.put("mobile", mobile);
			params.put("areaid", areaid);
			params.put("msgType", msgType);
			params.put("id", id);
			params.put("p", String.valueOf(p));
			params.put("pagesize", String.valueOf(pagesize));
			mLog.d("http", "userid:"+userid+",mobile:"+mobile+",areaid:"+areaid+",msgType:"+msgType+",id:"+id+",p:"+p+",pagesize:"+pagesize);
		}else{
			return null;
		}
		String s = HttpUtil.postUrl(context, Config.queryDoctorReferralUrl, params);
		return s;
	}
	
	/**
	 * 
	 * @param context
	 * @param bustype 0:人工转诊；1：；系统转诊
	 * @param 患者
	 * @param patientname 患者名字（必录）
	 * @param sex 性别
	 * @param birthdate 生日
	 * @param areaid 地区ID 
	 * @param address 地区
	 * @param patientmobile 手机号
	 * @param headimg 头像
	 * @param patientdesc 病情描述
	 * @param patientimgone 图片1
	 * @param patientimgtwo 图片2
	 * @param patientimgthree 图片3
	 * @param illnessname 病情名称  如：感冒
	 * @param 用户医生
	 * @param mobile 手机
	 * @param referdoctor ID
	 * @param 接诊医生
	 * @param acceptdoctor  ID 
	 * @param acceptdoctorname  姓名
	 * @return
	 * @throws Exception
	 */
	public static String addReferral(Context context, String bustype
			, String patientname, int sex, String birthdate, String areaid
			, String address, String patientmobile, String headimg, String patientdesc, String patientimgone
			, String patientimgtwo, String patientimgthree, String illnessname
			, String mobile, String referdoctor,   String acceptdoctor, String acceptdoctorname)
			throws Exception{
		Map<String,String> params = new LinkedHashMap<String, String>();
		if(patientname != null && birthdate != null && address != null && mobile != null /*&& headimg != null*/ 
				&& patientdesc != null && illnessname != null && bustype != null && areaid != null){
			params.put("patientname", patientname);
			params.put("sex", String.valueOf(sex));
			params.put("birthdate", birthdate);
			params.put("address", address);
			params.put("mobile", mobile);
			params.put("headimg", headimg);
			params.put("patientdesc", patientdesc);
			params.put("patientimgone", patientimgone);
			params.put("patientimgtwo", patientimgtwo);
			params.put("patientimgthree", patientimgthree);
			params.put("illnessname", illnessname);
			params.put("patientmobile", patientmobile);
			params.put("bustype", bustype);
			params.put("areaid", areaid);
			params.put("referdoctor", referdoctor);
			params.put("acceptdoctor", acceptdoctor);
			params.put("acceptdoctorname", acceptdoctorname);
		}else{
			return null;
		}
		String s = HttpUtil.postUrl(context, Config.addReferralUrl, params);
		return s;
	}
	
	/**
	 * get administrative office
	 * @param context
	 * @return
	 * @throws Exception
	 */
	public static String getDepart(Context context) throws Exception{
		String s = HttpUtil.postUrl(context, Config.getDepartUrl, null);
		return s;
	}
	
	/**
	 * 转诊找专家
	 * @param context
	 * @param mobile 用户手机 （必须）
	 * @param doctorid 医生主键
	 * @param detpname 科室
	 * @param areaid 地区ID
	 * @param doctorname 医生名
	 * @param hospital 医院
	 * @return
	 * @throws Exception
	 */
	public static String queryDoctor(Context context, String mobile, String doctorid, String detpname
			, String areaid, String doctorname, String hospital, int p, int pagesize)
			throws Exception{
		Map<String,String> params = new LinkedHashMap<String, String>();
		/*if(mobile != null){*/
			params.put("mobile", mobile);
			params.put("doctorid", doctorid);
			params.put("detpname", detpname);
			params.put("areaid", areaid);
			params.put("doctorname", doctorname);
			params.put("hospital", hospital);
			params.put("p", String.valueOf(p));
			params.put("pagesize", String.valueOf(pagesize));
		/*}else{
			return null;
		}*/
		String s = HttpUtil.postUrl(context, Config.queryDoctorUrl, params);
		return s;
	}
	
	/**
	 * 健康知识
	 * @param context
	 * @param p
	 * @param pagesize
	 * @return
	 * @throws Exception
	 */
	public static String queryHealthLore(Context context, int p, int pagesize)
			throws Exception{
		Map<String,String> params = new LinkedHashMap<String, String>();
		params.put("p", String.valueOf(p));
		params.put("pagesize", String.valueOf(pagesize));
		String s = HttpUtil.postUrl(context, Config.queryHealthLoreUrl, params);
		return s;
	}
	
	/**
	 * 课程资料
	 * @param context
	 * @param p
	 * @param pagesize
	 * @return
	 * @throws Exception
	 */
	public static String queryCourse(Context context, int p, int pagesize)
			throws Exception{
		Map<String,String> params = new LinkedHashMap<String, String>();
		params.put("p", String.valueOf(p));
		params.put("pagesize", String.valueOf(pagesize));
		String s = HttpUtil.postUrl(context, Config.queryCourseUrl, params);
		return s;
	}
	
	/**
	 * 查询注册信息
	 * @param context
	 * @param userid
	 * @param mobile
	 * @return
	 * @throws Exception
	 */
	public static String queryDoctorNoAudit(Context context, String mobile, String userid)
			throws Exception{
		Map<String,String> params = new LinkedHashMap<String, String>();
		if(userid != null && mobile != null){
			params.put("mobile", mobile);
			//params.put("userid", userid);
		}else{
			return null;
		}
		String s = HttpUtil.postUrl(context, Config.queryDoctorNoAuditUrl, params);
		return s;
	}
	
	/**
	 * upload register info
	 * @param context
	 * @param mobile 手机号码(必传)
	 * @param userid 用户主键(必传)
	 * @param doctorid 医生档案（非必传）
	 * @param doctorname 医生姓名
	 * @param headimg 医生头像
	 * @param hospital 所属医院
	 * @param address 地址
	 * @param areaid 地址ID
	 * @param detp 科室
	 * @param sex 0；男；1女
	 * @param birthday 生日期
	 * @param workcard 工作证
	 * @param licensecard 执业证
	 * @param qualifiedcard 资格证
	 * @param goodfield 擅长领域
	 * @param jobresults 学术成果
	 * @param paper 社会职务
	 * @param qualifiedimg 证书照片.xxx.jpg
	 * @return
	 * @throws Exception
	 */
	public static String uploadRegisterInfo(Context context, String mobile, String userid, String doctorid, String doctorname
			, String headimg, String hospital, String address, String areaid, String detp, String sex, String birthday
			, String workcard, String licensecard, String qualifiedcard, String goodfield, String jobresults, String paper
			, String qualifiedimg, String weixincode, String alipaycode, String unionpay, String accountname, String accountcode
			, String jobtitle, String duty)
			throws Exception{
		Map<String,String> params = new LinkedHashMap<String, String>();
		if(mobile != null && userid != null && doctorname != null){
			params.put("mobile", mobile);
			params.put("userid", userid);
			params.put("doctorid", doctorid);
			params.put("doctorname", doctorname);
			params.put("headimg", headimg);
			params.put("hospital", hospital);
			params.put("address", address);
			params.put("areaid", areaid);
			params.put("detp", detp);
			params.put("sex", sex);
			params.put("birthday", birthday);
			params.put("workcard", workcard);
			params.put("licensecard", licensecard);
			params.put("qualifiedcard", qualifiedcard);
			params.put("goodfield", goodfield);
			params.put("jobresults", jobresults);
			params.put("paper", paper);
			params.put("qualifiedimg", qualifiedimg);
			params.put("weixincode", weixincode);
			params.put("alipaycode", alipaycode);
			params.put("unionpay", unionpay);
			params.put("accountname", accountname);
			params.put("accountcode", accountcode);
			params.put("jobtitle", jobtitle);
			params.put("duty", duty);
		}else{
			return null;
		}
		String s = HttpUtil.postUrl(context, Config.regAuditUrl, params);
		return s;
	}
	
	/**
	 * query post
	 * @param context
	 * @return
	 * @throws Exception
	 */
	public static String queryPostdoc(Context context) throws Exception{
		String s = HttpUtil.postUrl(context, Config.queryPostdocUrl, null);
		return s;
	}

	/**
	 * query queryCircletdoc
	 *
	 * @throws Exception
	 */
	public static String queryCircletdoc(Context context, String phone) throws Exception {
		Map<String, String> params = new LinkedHashMap<String, String>();
		if (phone != null) {
			params.put("mobile", phone);
		}
		String s = HttpUtil.postUrl(context, Config.queryCircledocUrl, params);
		return s;
	}

	/**
	 * query queryCircletdoc
	 *
	 * @throws Exception
	 */
	public static String queryCompanydoc(Context context, String phone) throws Exception {
		Map<String, String> params = new LinkedHashMap<String, String>();
		if (phone != null) {
			params.put("mobile", phone);
		}
		String s = HttpUtil.postUrl(context, Config.queryCompanydocUrl, params);
		return s;
	}
	/**
	 * query job title
	 * @param context
	 * @return
	 * @throws Exception
	 */
	public static String queryTitledoc(Context context) throws Exception{
		String s = HttpUtil.postUrl(context, Config.queryTitledocUrl, null);
		return s;
	}
	
	/**
	 * upload image
	 * @param context
	 * @param userid
	 * @param imgtype 
	 * 		userlogoimg:用户头像  
	 * 		patientimgone:患者映射1IMG patientimgtwo:患者映射2IMG patientimgthree:患者映射3IMG  
	 * 		qualifiedimg:医生认证1IMG
	 * @param iosimg Base64的二进制字符串
	 * @return
	 * @throws Exception
	 */
	public static String uploadUserImg(Context context, String userid, String imgtype, String iosimg)
			throws Exception{
		Map<String,String> params = new LinkedHashMap<String, String>();
		if(userid != null && imgtype != null && iosimg != null){
			params.put("userid", userid);
			params.put("uploadImage", "uploadImage");//Form表单里的File的name固定为uploadImage
			params.put("imgtype", imgtype);
			params.put("iosimg", iosimg);
		}else{
			return null;
		}
		String s = HttpUtil.postUrl(context, Config.uploadUserImgUrl, params);
		return s;
	}
	
	/**
	 * register
	 * @param context
	 * @param clienttype
	 * @param mcode
	 * @param mobile
	 * @return
	 * @throws Exception
	 */
	public static String getRegister(Context context, String clienttype, String mcode, String mobile) throws Exception{
		Map<String,String> params = new LinkedHashMap<String, String>();
		if(mobile != null && clienttype != null && mcode != null){
			params.put("clienttype", clienttype);
			params.put("mobile", mobile);
			params.put("mcode", mcode);
		}else{
			return null;
		}
		String s = HttpUtil.postUrl(context, Config.getRegisterUrl, params);
		return s;
	}
	
	/**
	 * get code from network
	 * @param context
	 * @param mobile
	 * @return
	 * @throws Exception
	 */
	public static String getCode(Context context, String mobile) throws Exception{
		Map<String,String> params = new LinkedHashMap<String, String>();
		if(mobile != null){
			params.put("mobile", mobile);
		}else{
			return null;
		}
		String s = HttpUtil.postUrl(context, Config.getCodeUrl, params);
		return s;
	}
	
	/** 
     * Get image from newwork 
     * @param path The path of image 
     * @return byte[] 
     * @throws Exception 
     */  
    public static byte[] getImage(String path) throws Exception{  
    	if(path == null){
    		return null;
    	}
    	byte[] by = null;
        URL url = new URL(path);  
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();  
        conn.setConnectTimeout(10000);  
        conn.setRequestMethod("GET");  
        InputStream inStream = conn.getInputStream();  
        if(conn.getResponseCode() == HttpURLConnection.HTTP_OK){  
        	by = readStream(inStream);  
        }  
        inStream.close();
        conn.disconnect();
        return by;  
    } 
    
    /** 
     * Get data from stream 
     * @param inStream 
     * @return byte[] 
     * @throws Exception 
     */  
    public static byte[] readStream(InputStream inStream) throws Exception{  
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();  
        byte[] buffer = new byte[1024];  
        int len = 0;  
        while( (len=inStream.read(buffer)) != -1){  
            outStream.write(buffer, 0, len);  
        }  
        outStream.close();  
        inStream.close();  
        return outStream.toByteArray();  
    }
	
	/**
	 * 
	 * @param context
	 * @return
	 * @throws Exception
	 */
	public static String getDoctorHomeImg(Context context) throws Exception{
		String s = HttpUtil.postUrl(context, Config.getDoctorHomeImgUrl, null);
		return s;
	}
	/**
	 * 会议通知首页轮播图
	 * @param context
	 * @return
	 * @throws Exception
	 */
	public static String getMainmeetimg(Context context) throws Exception{
		String s = HttpUtil.postUrl(context, Config.phone_getMainmeetimg, null);
		return s;
	}
	
	/**
	 * 5.地区档案查询接口
	 * @param context
	 * @param uppk 地区父主键
	 * @param areaid 地区主键
	 * @param level 等级：值范围如下：“省”，“市”，“区”
	 * @return
	 * @throws Exception
	 * 注：三个参数不能同时为空，为and的关系,中文参数请用POST方法请求
	 */
	public static String getArea(Context context,String uppk,String areaid,String level) throws Exception{
		Map<String,String> params = new LinkedHashMap<String, String>();
		if(uppk != null){
			params.put("uppk", uppk);
			mLog.d("http", "uppk:"+uppk);
		}
		if(uppk != null){
			params.put("areaid", areaid);
			mLog.d("http", "areaid:"+areaid);
		}
		if(level != null){
			params.put("level", level);
		}
		String s = HttpUtil.postUrl(context, Config.getAreaUrl, params);
		return s;
	}
	/**
	 * 科室查询
	 * @param context
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public static String queryDepartment(Context context, String id) throws Exception{
		Map<String,String> params = new LinkedHashMap<String, String>();
		if(id != null){
			params.put("id", id);
		}
		String s = HttpUtil.postUrl(context, Config.queryDepartmentUrl, params);
		return s;
	}
	/**
	 * 登录注册
	 * @param context
	 * @param phone
	 * @param pwd
	 * @throws Exception
	 */
	public static String regUser(Context context,String phone,String pwd) throws Exception{
		Map<String,String> params = new LinkedHashMap<String, String>();
		params.put("mobile", phone);
		params.put("mcode", pwd);
		String s = HttpUtil.postUrl(context, Config.registerUrl, params);
		return s;
	}
	/**
	 * 获取验证码
	 * @param context
	 * @param phone
	 * @throws Exception
	 */
	public static String getVefiyNo(Context context,String phone) throws Exception{
		Map<String,String> params = new LinkedHashMap<String, String>();
		params.put("mobile", phone);
		String s = HttpUtil.postUrl(context, Config.getVerifyNoUrl, params);
		return s;
	}
}
