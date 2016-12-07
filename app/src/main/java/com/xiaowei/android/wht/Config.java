package com.xiaowei.android.wht;

//13924591025  1717
//18775361646 2853
public class Config {
	public static final String keyApkFileName = "apks/wcar.apk";
	public static final String keyVersionFileName = "apks/wcarversion.txt";
	public static boolean debug = false;
	//百度自动更新：
	//APP ID：6881405
//	API KEY：F3TwRSvmPsFdXRGpimpkEL9A
	// 测试环境,请不要修改如下配置
//	微信支付的参数
//	md5 757A27F2F132E11CE4333445310E566A
//	com.s66.weiche
	//测试环境
//	public static final int serverPort = 8080;// 用于接口调用端口，而不是普通文件（例如图片）的访问接口
//	public static final String server = "http://liesheng.gicp.net:8080/wcar/";//"http://112.74.27.10:8080/wcar/";
//	public static final String server4Pic = "http://liesheng.gicp.net:8080/wcar/upload/";
//	public static final String crashFileRecvServer = "http://121.199.3.48:10080/hlpEC/";// 闪退日志文件接收服务器,当debug=false有效
//	public static final String keyHttpUrlPref[] = new String[] { "http://121.199.3.48" };

	//正式环境
	public static final int serverPort = 8888;// 用于接口调用端口，而不是普通文件（例如图片）的访问接口
	public static final String server = "http://www.yishenggonghui.com:8888/wcar/";//"http://112.74.27.10:8080/wcar/";
	/**
	 * 用户注册认证登录
		   传入参数：
		mobile:手机号码;
		mcode:验证码
			http:// www.yishenggonghui.com:8888/wht/phone_registUser.action
		返回正确结果：
		  {"status":1,"data":{"id":"402881f254aec84e0154aecd73cd0002","url":"http://www.yishenggonghui.com:8888 /wht/phone_regMeet.action ","mobile":"13713981766"}}

	 */
	public static final String registerUrl = "http://www.yishenggonghui.com:8888/wht/phone_registUser.action";
	/**
	 * 获取验证码
			传入参数：
		mobile:手机号码;
		http://www.yishenggonghui.com:8888/wht/phone_getmcode.action
		返回正确结果：
		    {"status":1,"data":{"获取验证码成功"}}

	 */
	public static final String getVerifyNoUrl = "http://www.yishenggonghui.com:8888/wht/phone_getmcode.action";
	/**
	 * 首页地址
	 * http://www.yishenggonghui.com:8888/wht/wap/meet_phoneIndex.action
	 */
	public static final String indexUrl="http://www.yishenggonghui.com:8888/wht/wap/meet_phoneIndex.action";
	/**
	 * 登陆成功或是免登录跳转到的接口
			传入参数：
		mobile:手机号码;
		userid:用户id
		meetid:会议id
			http://www.yishenggonghui.com:8888/wht/phone_regMeet.action
	 */
	public static final String regMeetUrl="http://www.yishenggonghui.com:8888/wht/phone_regMeet.action";
	//public static final String getWeixinPayUrl="http://www.yishenggonghui.com:8888/wht/weixin/weixin_getMeetAppPay.action";
	//public static final String getAliPayUrl="http://www.yishenggonghui.com:8888/wht/alipay/alipay_getMeetAppAliPay.action";
	public static final String getYinlianPayUrl="http://www.yishenggonghui.com:8888/wht/unionpay/unionpay_getPayInfo.action";
	
	public static final String crashFileRecvServer = "http://121.199.3.48:10080/hlpEC/";// 闪退日志文件接收服务器,当debug=false有效
	public static final String keyHttpUrlPref[] = new String[] { "http://www.yishenggonghui.com:8888/wcar/" };
	//环境切换除了修改config.java外，还需要修改AndroidManifest.xml
	
	
//	private static final String SETTING = "http://www.yishenggonghui.com:8888/wht";//正式环境
	private static final String SETTING = "http://121.40.126.229:8082/wht";
	public static final String queryaboutUs = SETTING + "/phone_queryaboutUs.action";
	public static final String getMeetDetailUrl = SETTING + "/phone_getMeetDetail.action";
	public static final String getAreaUrl = SETTING + "/phone_getArea.action";
	public static final String queryDepartmentUrl = SETTING + "/phone_queryDepartment.action";
	public static final String getDoctorHomeImgUrl = SETTING + "/phone_doctorHomeImg.action";
	public static final String getCodeUrl = SETTING + "/phone_getmcode.action";
	public static final String getRegisterUrl = SETTING + "/phone_registUser.action";
	public static final String uploadUserImgUrl = SETTING + "/phone_uploadUserImg.action";
	public static final String queryTitledocUrl = SETTING + "/phone_queryTitledoc.action";
	public static final String queryDoctorPerson = SETTING + "/phone_queryDoctorPerson.action";
	public static final String updateDoctorPerson = SETTING + "/phone_updateDoctorPerson.action";
	public static final String queryPostdocUrl = SETTING + "/phone_queryPostdoc.action";
	public static final String regAuditUrl = SETTING + "/phone_regAudit.action";
	public static final String queryDoctorNoAuditUrl = SETTING + "/phone_queryDoctorNoAudit.action";
	public static final String queryDoctorUrl = SETTING + "/phone_queryDoctor.action";
	public static final String getDepartUrl = SETTING + "/phone_getDepart.action";
	public static final String addReferralUrl = SETTING + "/phone_addReferral.action";
	public static final String queryDoctorReferralUrl = SETTING + "/phone_queryDoctorReferral.action";
	public static final String queryDoctorAcceptUrl = SETTING + "/phone_queryDoctorAccept.action";
	public static final String updateDoctorAcceptUrl = SETTING + "/phone_updateDoctorAccept.action";
	public static final String queryUserMnyUrl = SETTING + "/phone_queryUserMny.action";
	public static final String addBoundCardUrl = SETTING + "/phone_addBoundCard.action";
	public static final String queryBoundCardUrl = SETTING + "/phone_queryBoundCard.action";
	public static final String updateBoundCardUrl = SETTING + "/phone_updateBoundCard.action";
	public static final String meetdocIngUrl = SETTING + "/phone_meetdocIng.action";
	public static final String meetdocBeginUrl = SETTING + "/phone_meetdocBegin.action";
	public static final String meetdocEndUrl = SETTING + "/phone_meetdocEnd.action";
	public static final String addMeetOrderUrl = SETTING + "/phone_addMeetOrder.action";
	public static final String getAliPayUrl = SETTING + "/alipay/alipay_getMeetAppAliPay.action";
	public static final String getWeixinPayUrl = SETTING + "/weixin/weixin_getMeetAppPay.action";
	public static final String doctorRejectUrl = SETTING + "/phone_doctorReject.action";
	public static final String againReferUrl = SETTING + "/phone_againRefer.action";
	public static final String queryMeetRecordUrl = SETTING + "/phone_queryMeetRecord.action";
	public static final String addTakeMnyUrl = SETTING + "/phone_addTakeMny.action";
	public static final String checkBoundCardUrl = SETTING + "/phone_checkBoundCard.action";
	public static final String updateSafePwdUrl = SETTING + "/phone_updateSafePwd.action";
	public static final String getMyNoticeUrl = SETTING + "/phone_getMyNotice.action";
	public static final String updateMyNoticeUrl = SETTING + "/phone_updateMyNotice.action";
	public static final String queryAccountRecordUrl = SETTING + "/phone_queryAccountRecord.action";
	public static final String getVersionUrl = SETTING + "/phone_getVersion.action";
	public static final String updatePatientPersonUrl = SETTING + "/phone_updatePatientPerson.action";
	public static final String queryNotReadNocticeUrl = SETTING + "/phone_queryNotReadNoctice.action";
	public static final String queryHealthLoreUrl = SETTING + "/phone_queryHealthLore.action";
	public static final String queryCourseUrl = SETTING + "/phone_queryCourse.action";
	public static final String queryReferralAgreementUrl = SETTING + "/phone_queryReferralAgreement.action";
	public static final String queryTimeAgreementUrl = SETTING + "/phone_queryTimeAgreement.action";
	
	public static final String getMeetContact = SETTING + "/phone_getMeetContact.action";//联系我们查询
	public static final String getMeetSpeaker = SETTING + "/phone_getMeetSpeaker.action";//讲者组织查询
	public static final String getMeetResource = SETTING + "/phone_getMeetResource.action";//大会PPT视频
	public static final String getMeeting = SETTING + "/phone_getMeeting.action";//会议内容
	public static final String getMeetSchedulet = SETTING + "/phone_getMeetSchedule.action";//日程查询
	public static final String getMeetWelcomeWord = SETTING + "/phone_getMeetWelcomeWord.action";//会议欢迎词信息
	public static final String getMeetBaseInfo = SETTING + "/phone_getMeetBaseInfo.action";//会议基本信息
	public static final String getMeetOrganizationals = SETTING + "/phone_getMeetOrganizationals.action";//组织结构
	
	public static final String phone_getMainmeetimg = SETTING + "/phone_getMainmeetimg.action";
	
	//public static int RESULTCODE_PHOTO_SET = 510;//PhotoSetActivity resultCode
	//private static final int RESULTCODE_JOB_TITLE = 511;//JobSelectActivity
	//private static final int RESULTCODE_POST_TITLE = 512;//JobSelectActivity
	//private static final int RESULTCODE_AREA_CHOOSE = 515;
	//public final int RESULTCODE_BankSelectActivity = 516;
	//public final int RESULTCODE_MeetingApply_ApplyOK = 517;
	
	/**
	 * 支付明细查询
	 */
	public static final String queryPatientPayUrl = SETTING + "/phone_queryPatientPay.action";
	/**
	 * 患者端首页轮播图接口
	 */
	public static final String getPatientHomeImgUrl = SETTING + "/phone_patientHomeImg.action";
	/**
	 * 查询患者个人信息接口
	 */
	public static final String getPatientInfoUrl = SETTING + "/phone_queryPatientPerson.action";
	/**
	 * 查询患者病历接口
	 */
	public static final String queryPatientRecordUrl =SETTING + "/phone_queryPatientRecord.action";
	/**
	 * 查询患者医生接口
	 */
	public static final String queryPatientDoctorUrl =SETTING + "/phone_queryPatientDoctor.action";
	/**
	 * 查询患者转诊接口
	 */
	public static final String queryPatientReferralUrl =SETTING + "/phone_queryPatientReferral.action";
	/**
	 * 查询患者预约接口
	 */
	public static final String queryPatientBookingUrl =SETTING + "/phone_queryPatientBooking.action";
	/**
	 * 增加患者预约接口
	 */
	public static final String addPatientBookingUrl =SETTING + "/phone_addPatientBooking.action";
	/**
	 * 预约支付宝支付
	 */
	public static final String getBookingAppAliPayUrl =SETTING + "/alipay/alipay_getBookingAppAliPay.action";
	/**
	 * 预约微信支付接口
	 */
	public static final String getBookingAppPayUrl =SETTING + "/weixin/weixin_getBookingAppPay.action";
	/**
	 * 医生社区
	 */
	public static final String getDoctorTalk = SETTING + "/phone_caseIndex.action?userid={USID}";
	/**
	 * 海外病例
	 */
	public static final String breadCase = SETTING + "/phone_brandCase.action?userid={USID}";
	/**
	 * 海外病例
	 */
	public static final String selectCase = SETTING + "/phone_selectCase.action?userid=";
	/**
	 * 海外病例
	 */
	public static final String classifyCase = SETTING + "/phone_queryclassify.action?userid=";
	/**
	 * 发布
	 */
	public static final String issueCase =
			SETTING + "/phone_releaseCaseIndex.action?userid={USID}&caseclass={TYPE}";
	/**
	 * 发布
	 */
	public static final String issueCaseIss = SETTING + "/phone_querydiscuss.action?userid={USID}";
	/**
	 * 发布
	 */
	public static final String issueCaseShare = SETTING + "/phone_queryshare.action?userid={USID}";

	/**
	 * 病例详情
	 */
	public static final String issueCaseDetaile =
			SETTING + "/phone_queryCaseinfo.action?id={id}&userid={userid}";
	public static final String issueCaseHeadDetaile =
			SETTING + "/phone_myinfo.action?userid={userid}";
	/**
	 * 圈子
	 */
	public static final String queryCircledocUrl = SETTING + "/phone_queryBranch.action";
	public static final String queryCompanydocUrl = SETTING + "/phone_queryCompanyGroup.action";
}
