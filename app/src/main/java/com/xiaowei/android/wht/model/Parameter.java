package com.xiaowei.android.wht.model;

public class Parameter {

	/**
	 * 验证码获取
	 */
	public final static String PM_getVerifyNo = "phone_getmcode.action";
	/**
	 * 用户注册
	 */
	public final static String PM_registUser = "phone_registUserBase.action";
	/**
	 * 上传用户图像
	 */
	public final static String PM_uploadUserImg = "phone_uploadUserImg.action";
	/**
	 * 用户登录
	 */
	public final static String PM_login = "phone_login.action";
	/**
	 * 用户信息查询
	 */
	public final static String PM_getUserInfo = "phone_getUserInfo.action";
	/**
	 * 地区档案查询
	 */
	public final static String PM_getArea = "phone_getArea.action";
	/**
	 * 用户信息修改
	 */
	public final static String PM_updateUser = "phone_updateUser.action";
	/**
	 * 查询车辆实时经纬度
	 */
	public final static String PM_getCarLocationByUser = "phone_getCarLocationByUser.action";
	/**
	 * 更新车辆实时经纬度
	 */
	public final static String PM_updateCarLocation = "phone_updateCarLocation.action";
	/**
	 * 运费单价查询
	 */
	public final static String PM_getTransPrice = "phone_getTransPrice.action";
	/**
	 * 新增订单
	 */
	public final static String PM_createOrder = "phone_createOrder.action";
	/**
	 * 查询订单基础
	 */
	public final static String PM_queryOrder = "phone_queryOrder.action";
	/**
	 * 取消订单
	 */
	public final static String PM_cancelOrder = "phone_cancelOrder.action";
	/**
	 * 查询货主未完成订单接口
	 */
	public final static String PM_getOrderstatus = "phone_getOrderstatus.action";
	/**
	 * 车主刷单
	 */
	public final static String PM_getOrderByCardLocation = "phone_getOrderByCardLocation.action";
	/**
	 * 车主抢单
	 */
	public final static String PM_grabOrder = "phone_grabOrder.action";
	/**
	 * 货主送达
	 */
	public final static String PM_ownerArrived = "phone_ownerArrived.action";
	/**
	 * 微信授权并绑定系统用户接口 userid
	 */
	public final static String PM_getOpenID = "phone_getOpenID.action";
	/**
	 * 货主微信支付回调
	 */
	public final static String PM_weixinPayBack = "phone_weixinPayBack.action";
	/**
	 * 微信统一下单接口
	 */
	public final static String PM_weixinPayUnifiedorder = "weixinpay/phone_unifiedorder.action";

	/**
	 * 货主支付宝支付回调
	 */
	public final static String PM_getAppAliPayInfo = "phone_getAppAliPayInfo.action";
	
	/**
	 * 货主支付宝支付回调
	 */
	public final static String PM_alipayBack = "phone_alipayBack.action";
	/**
	 * 货主发票地址查询
	 */
	public final static String PM_getInvoiceAddress = "phone_getInvoiceAddress.action";
	/**
	 * 货主索取发票
	 */
	public final static String PM_invoiceApply = "phone_invoiceApply.action";
	/**
	 * 评价提交
	 */
	public final static String PM_submitUserJudge = "phone_submitUserJudge.action";
	/**
	 * 评价查询
	 */
	public final static String PM_getOrderJudge = "phone_getOrderJudge.action";
	/**
	 * 我的收入汇总查询
	 */
	public final static String PM_getMycash = "phone_getMycash.action";
	/**
	 * 我的收入明细查询
	 */
	public final static String PM_getMycashDetail = "phone_getMycashDetail.action";
	/**
	 * 订单小费修改
	 */
	public final static String PM_updateCasemny = "phone_updateCasemny.action";
	/**
	 * 获取搬运费提示信息
	 */
	public final static String PM_getTransInfo = "phone_getTransInfo.action";
	/**
	 * 已启用的城市查询接口
	 */
	public final static String PM_getOpenCity = "phone_getOpenCity.action";
	/**
	 * 已启用的城市查询接口
	 */
	public final static String PM_updateInvoiceAddress = "phone_updateInvoiceAddress.action";
	/**
	 * 获取广告查询接口
	 */
	public final static String PM_getAD = "phone_getAD.action";
	
	/**
	 * 获取我的优惠卷查询接口
	 */
	public final static String PM_getMyDiscount = "phone_getMyDiscount.action";
	
	/**
	 * 获取推送的消息
	 */
	public final static String PM_getPushMsg = "phone_getPushMsg.action";
}
