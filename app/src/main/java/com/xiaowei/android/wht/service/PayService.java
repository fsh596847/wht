package com.xiaowei.android.wht.service;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import android.content.Context;

import com.alibaba.fastjson.JSON;
import com.xiaowei.android.wht.Config;
import com.xiaowei.android.wht.model.AlipayInfo;
import com.xiaowei.android.wht.model.HttpResult;
import com.xiaowei.android.wht.model.Parameter;
import com.xiaowei.android.wht.utils.mLog;
import com.xiaowei.android.wht.utis.HlpUtils;
import com.xiaowei.android.wht.utis.HttpUtil;

public class PayService {
	Context context;
	public PayService(Context context) {
		this.context = context;
	}
	/**
	 * create the order info. 创建订单信息
	 * @throws UnsupportedEncodingException 
	 * 
	 */
	static public String getAliPayOrderInfo(AlipayInfo api) throws UnsupportedEncodingException {
		// 签约合作者身份ID
		String orderInfo = "partner=" + "\"" + api.getPartner() + "\"";

		// 签约卖家支付宝账号
		orderInfo += "&seller_id=" + "\"" + api.getSeller_id() + "\"";

		// 商户网站唯一订单号
		orderInfo += "&out_trade_no=" + "\"" + api.getOut_trade_no() + "\"";

		// 商品名称
		orderInfo += "&subject=" + "\"" + api.getSubject() + "\"";

		// 商品详情
		orderInfo += "&body=" + "\"" + api.getBody() + "\"";

		// 商品金额
		orderInfo += "&total_fee=" + "\"" + api.getTotal_fee() + "\"";

		// 服务器异步通知页面路径
		orderInfo += "&notify_url=" + "\"" +api.getNotify_url()
				+ "\"";

		// 服务接口名称， 固定值
		orderInfo += "&service=\"mobile.securitypay.pay\"";

		// 支付类型， 固定值
		orderInfo += "&payment_type=\"1\"";

		// 参数编码， 固定值
		orderInfo += "&_input_charset=\"utf-8\"";

		// 设置未付款交易的超时时间
		// 默认30分钟，一旦超时，该笔交易就会自动被关闭。
		// 取值范围：1m～15d。
		// m-分钟，h-小时，d-天，1c-当天（无论交易何时创建，都在0点关闭）。
		// 该参数数值不接受小数点，如1.5h，可转换为90m。
		orderInfo += "&it_b_pay=\"30m\"";

		// extern_token为经过快登授权获取到的alipay_open_id,带上此参数用户将使用授权的账户进行支付
		// orderInfo += "&extern_token=" + "\"" + extern_token + "\"";

		// 支付宝处理完请求后，当前页面跳转到商户指定页面的路径，可空
//		orderInfo += "&return_url=\"m.alipay.com\"";

		// 调用银行卡支付，需配置此参数，参与签名， 固定值 （需要签约《无线银行卡快捷支付》才能使用）
		// orderInfo += "&paymethod=\"expressGateway\"";

		return orderInfo;
	}
	//f9a8fe6554b79bf60154b7ec20fe0002
	//f9a8fe6554c484150154c6241f2b001d
	//0.01
	//{"status":1,"data":{"othermsg":"这次成功了吧","payInfo":"_input_charset=utf-8&body=f9a8fe6554b79bf60154b7ec20fe0002&notify_url=http://121.40.126.229:8082/alipay/alipay_alipayPayNotify.action&out_trade_no=OD1463613191492_858&partner=2088221832154990&payment_type=1&seller_id=2088221832154990&service=mobile.securitypay.pay&subject=医生公会支付宝金额&total_fee=0.01&sign=QB2UqyowRNpwpDL9kQ8qN2M9F%2BZw7AOeR1xOHfpWG8qAXVBBUqmKuwlNLCEKx5RQjRdk9yNTzaJCPO3tf2uXNp%2FN79g%2B3Kp65s8ZwYbFZXRJEd2RFs7IujWshqyYna4HWvTqrLp%2Fssg32lznTLhaeTRIb0EiyFc36xt%2FbCv3Meo%3D&sign_type=RSA"}}

	public HttpResult getAppAliPayInfo(String 
			userid,String orderid,String mny,String meetid) {
		HttpResult hr = null;
		try {
			String url = Config.getAliPayUrl;
			Map<String, String> data = new HashMap<String, String>();
			data.put("orderid", orderid);
			data.put("userid", ""+userid);
			data.put("mny",""+mny);
			data.put("meetid",meetid);
			String s = HttpUtil.postUrl(context,url, data);
			if (null == s) {
				throw new Exception("无法与服务器通信，请稍后再试！");
			}
			hr = JSON.parseObject(s, HttpResult.class);
			
		} catch (Exception e) {
			hr = new HttpResult();
			hr.setData("处理失败:"+(Config.debug?e.getMessage():""));
			hr.setStatus(-999);
			return hr;
		}
		return hr;
	}
	
	public static String getAliPay(Context context,String userid,String orderid,double mny,String meetid) throws Exception{
		Map<String,String> params = new LinkedHashMap<String, String>();
		putParams(params, "userid", userid, true);
		putParams(params, "orderid", orderid, true);
		putParams(params, "mny", String.valueOf(mny), true);
		putParams(params, "meetid", meetid, true);
		String s = HttpUtil.postUrl(context, Config.getAliPayUrl, params);
		return s;
	}
	private static void putParams(Map<String,String> params,String key,String obj,boolean must) throws Exception{
		mLog.d("http", "obj:"+obj);
		if (HlpUtils.isEmpty(obj)){
			if (must){
				mLog.e("http", "obj:"+obj);
				throw new Exception("参数不能为空："+key);
			}
		}else{
			params.put(key, obj);
		}
	}
	public HttpResult getWeixinPayRequest(String 
			userid,String orderid,String mny,String meetid) throws Exception {
		HttpResult hr = null;
		try {
			String url = Config.getWeixinPayUrl;
			Map<String, String> data = new HashMap<String, String>();
			data.put("orderid", orderid);
			data.put("userid", ""+userid);
			data.put("mny",""+mny);
			data.put("meetid",meetid);
			String s = HttpUtil.postUrl(context,url, data);
			//{"status":1,"data":{"timestamp":"1463586365","sign":"2A5B3010CDA27D62BDC4C05472B9EE75","partnerid":"1264767001","noncestr":"1f9f9d8ff75205aa73ec83e543d8b571","prepayid":"wx201605182346053fdb2e5de60674651959","package":"Sign=WXPay","appid":"wxead4dd2a6eada581"}}

			if (null == s) {
				throw new Exception("无法与服务器通信，请稍后再试！");
			}
			hr = JSON.parseObject(s, HttpResult.class);
		} catch (Exception e) {
			hr = new HttpResult();
			hr.setData("处理失败:"+(Config.debug?e.getMessage():""));
			hr.setStatus(-999);
			return hr;
		}
		return hr;
	}
	public HttpResult getWeixinOpenId(int 
			userid) throws Exception {
		HttpResult hr = null;
		try {
			String url = Config.server +Parameter.PM_getOpenID;
			Map<String, String> data = new HashMap<String, String>();
			data.put("userid", ""+userid);
			String s = HttpUtil.postUrl(context,url, data);
			if (null == s) {
				throw new Exception("无法与服务器通信，请稍后再试！");
			}
			hr = JSON.parseObject(s, HttpResult.class);
		} catch (Exception e) {
			hr = new HttpResult();
			hr.setData("处理失败:"+(Config.debug?e.getMessage():""));
			hr.setStatus(-999);
			return hr;
		}
		return hr;
	}
}
