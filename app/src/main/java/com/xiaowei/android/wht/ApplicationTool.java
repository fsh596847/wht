package com.xiaowei.android.wht;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.Application;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Handler;
import android.support.multidex.MultiDex;
import com.tencent.android.tpush.XGNotifaction;
import com.tencent.android.tpush.XGPushConfig;
import com.tencent.android.tpush.XGPushManager;
import com.tencent.android.tpush.XGPushNotifactionCallback;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.xiaowei.android.wht.exception.CrashHandler;
import com.xiaowei.android.wht.model.UserInfo;
import com.xiaowei.android.wht.utils.mLog;
import java.util.ArrayList;
import java.util.List;
import org.apache.http.cookie.Cookie;

public class ApplicationTool extends Application {

	static ApplicationTool mInstance = null;
	UserInfo userInfo;
	List<Activity> actList ;
	public Cookie mntCookie = null;//登录管理后台的cookie
	public String loginUserId, LoginPassword;
	public SpData spData = null;
	public boolean hasLogined = false;//当前会话是否已经登录，以便MainActivity的登录处理
	// 把刚才下得框架的jar拷进来 我估计你这加载图片是用volley自己封装的
	public List<Activity> activitis = new ArrayList<Activity>();//保存启动的Activity的集合
	public static double px4dp =1;//一个dp对应的像素

	public static IWXAPI wxApi;
	public static boolean isWxShare = false;
	public static boolean isWXLogin = false;

	private Typeface typeface;

	@Override
	public void onCreate() {
		super.onCreate();
		wxApi = WXAPIFactory.createWXAPI(this, null);
		wxApi.registerApp("wx2b867b7a6651c5fd");
		
		CrashHandler crashHandler = CrashHandler.getInstance();
		// 注册crashHandler
		crashHandler.init(getApplicationContext());
		mInstance = this;
		actList = new ArrayList<Activity>();
		spData = new SpData(getApplicationContext());
		typeface = Typeface.createFromAsset(mInstance.getAssets(), "tvfont.ttf");
		setPush();
	}

	public static ApplicationTool getInstace() {
		return mInstance;
	}

	public Typeface getTypeface() {
		return typeface;
	}

	public void setTypeface(Typeface typeface) {
		this.typeface = typeface;
	}

	Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {

			default:
				break;
			}
		}
	};
	
	
	public void setPush() {
		// 在主进程设置信鸽相关的内容
		if (isMainProcess()) {
			// 为保证弹出通知前一定调用本方法，需要在application的onCreate注册
			// 收到通知时，会调用本回调函数。
			// 相当于这个回调会拦截在信鸽的弹出通知之前被截取
			// 一般上针对需要获取通知内容、标题，设置通知点击的跳转逻辑等等
			XGPushManager
					.setNotifactionCallback(new XGPushNotifactionCallback() {

						@Override
						public void handleNotify(XGNotifaction xGNotifaction) {
							mLog.i("test", "处理信鸽通知：" + xGNotifaction);
							// 获取标签、内容、自定义内容
							String title = xGNotifaction.getTitle();
							String content = xGNotifaction.getContent();
							String customContent = xGNotifaction
									.getCustomContent();
//							HlpUtils.showToast(getApplicationContext(), content);
							// 其它的处理
							// 如果还要弹出通知，可直接调用以下代码或自己创建Notifaction，否则，本通知将不会弹出在通知栏中。
							xGNotifaction.doNotify();
						}
					});
		}
	}
	
	public void startPushService(String account){
		mLog.d("MyApplication", "startPushService");
		//Toast.makeText(this, "start", Toast.LENGTH_SHORT).show();
		// 开启logcat输出，方便debug，发布时请关闭
		 XGPushConfig.enableDebug(this, false);
		// 如果需要知道注册是否成功，请使用registerPush(getApplicationContext(), XGIOperateCallback)带callback版本
		// 如果需要绑定账号，请使用registerPush(getApplicationContext(),account)版本
		// 具体可参考详细的开发指南
		// 传递的参数为ApplicationContext
		Context context = getApplicationContext();
		XGPushManager.registerPush(context,account);

		// 2.36（不包括）之前的版本需要调用以下2行代码
//		Intent service = new Intent(context, XGPushService.class);
//		context.startService(service);


		// 其它常用的API：
		// 绑定账号（别名）注册：registerPush(context,account)或registerPush(context,account, XGIOperateCallback)，其中account为APP账号，可以为任意字符串（qq、openid或任意第三方），业务方一定要注意终端与后台保持一致。
		// 取消绑定账号（别名）：registerPush(context,"*")，即account="*"为取消绑定，解绑后，该针对该账号的推送将失效
		// 反注册（不再接收消息）：unregisterPush(context)
		// 设置标签：setTag(context, tagName)
		// 删除标签：deleteTag(context, tagName)
	}
	
	public void stopPushService(){
		//Toast.makeText(this, "stop", Toast.LENGTH_SHORT).show();
		//XGPushManager.registerPush(getApplicationContext(),"*");
		XGPushManager.unregisterPush(getApplicationContext());
		
		SpData spData = new SpData(getApplicationContext());
		spData.setStringValue(SpData.keyPhoneUser, null);
		spData.setStringValue(SpData.keyId, null);
		spData.setStringValue(SpData.KeyClientType, null);
		spData.setIntValue(SpData.KeyApprovestate, -1);
	}

	public boolean isMainProcess() {
		ActivityManager am = ((ActivityManager) getSystemService(Context.ACTIVITY_SERVICE));
		List<RunningAppProcessInfo> processInfos = am.getRunningAppProcesses();
		String mainProcessName = getPackageName();
		int myPid = android.os.Process.myPid();
		for (RunningAppProcessInfo info : processInfos) {
			if (info.pid == myPid && mainProcessName.equals(info.processName)) {
				return true;
			}
		}
		return false;
	}
	public void addActivity(Activity act){
		actList.add(act);
	}

	public UserInfo getUserInfo() {
		return userInfo;
	}
	public void setUserInfo(UserInfo userInfo) {
		this.userInfo = userInfo;
	}
	
	public static ApplicationTool getInstance() {
		return mInstance;
	}
	@Override
	public void onTerminate() {
		exitApp();
		super.onTerminate();
	}
	/**
	 * 退出App
	 */
	public void exitApp(){
		SpData sp = new SpData(getApplicationContext());
//		sp.setStringValue(SpData.keyMobile, null);
		hasLogined = false;
		
		for (Activity act:actList){
			if (act != null ){
				try{
					act.finish();
				}catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	@Override protected void attachBaseContext(Context base) {
		super.attachBaseContext(base);
		MultiDex.install(this);
	}
}