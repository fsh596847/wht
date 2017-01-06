package com.xiaowei.android.wht.ui;



import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.alibaba.fastjson.JSON;
import com.alipay.sdk.app.PayTask;
import com.alipaysdk.PayResult;
import com.alipaysdk.SignUtils;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.xiaowei.android.wht.ApplicationTool;
import com.xiaowei.android.wht.Config;
import com.xiaowei.android.wht.R;
import com.xiaowei.android.wht.model.HttpResult;
import com.xiaowei.android.wht.model.OrderInfo;
import com.xiaowei.android.wht.service.DataService;
import com.xiaowei.android.wht.service.PayService;
import com.xiaowei.android.wht.ui.enevnt.MessageEvent;
import com.xiaowei.android.wht.uibase.BaseNoTitleBarActivity;
import com.xiaowei.android.wht.utils.mLog;
import com.xiaowei.android.wht.utis.HlpUtils;
import com.xiaowei.android.wht.views.SildingFinishLayout;
import com.xiaowei.android.wht.views.SildingFinishLayout.OnSildingFinishListener;
import com.xiaowei.android.wht.wxapi.WXPayEntryActivity;
import java.util.Map;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class PayActivity extends BaseNoTitleBarActivity implements 
	View.OnClickListener{
	ImageView back_button;
	OrderInfo orderInfo;
	TextView yinlianPay_textView,
		wechatPay_textView,aliPay_textView;
	RelativeLayout wechat_div,alipay_div,ylPay_div,pay_div;
	TextView price_textView,price1_textView;
	ImageView yinlianPicked_textView,aliPicked_textView,wechatPicked_textView;
	String userId;
	String orderId;
	Double mny;
	String meetid;
	
	private EventReceiver receiver;
	public class EventReceiver extends BroadcastReceiver {

	    @Override
	    public void onReceive(Context context, Intent intent) {
	    	PayActivity.this.setResult(MeetingApply.RESULTCODE_MeetingApply_ApplyOK);
			finish();
			overridePendingTransition(0, R.anim.out_right);
	    }
	}
	
	// Press the back button in mobile phone
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		overridePendingTransition(0, R.anim.out_right);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pay);
		ApplicationTool.getInstance().activitis.add(this);//把当前Activity放入集合中
		orderInfo = (OrderInfo)getIntent().getSerializableExtra("orderInfo");
		userId = getIntent().getStringExtra("userId");
		orderId = getIntent().getStringExtra("orderId");
		meetid = getIntent().getStringExtra("meetid");
		String mnyStr = getIntent().getStringExtra("mny");
		if (HlpUtils.isEmpty(userId) && savedInstanceState != null){
			userId = savedInstanceState.getString("userId");
			orderId = savedInstanceState.getString("orderId");
			mnyStr = savedInstanceState.getString("mny");
			meetid = savedInstanceState.getString("meetid");
		}
		if (!HlpUtils.isEmpty(mnyStr)){
			mny = Double.parseDouble(mnyStr);
		}
//		HlpUtils.showToast(getApplicationContext(), ""+mny);
		initView();
		
		receiver = new EventReceiver();
		registerReceiver(receiver, new IntentFilter(WXPayEntryActivity.ACTION_PAY_SUCCESS)); 
		
		//右滑退出
		SildingFinishLayout mSildingFinishLayout = (SildingFinishLayout) findViewById(R.id.sildingFinishLayout_pay); 
		mSildingFinishLayout.setOnSildingFinishListener(new OnSildingFinishListener() {

			@Override
			public void onSildingFinish() {
				PayActivity.this.finish();
			}
		});
		// touchView
		mSildingFinishLayout.setTouchView(mSildingFinishLayout);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putString("userId", userId);
		outState.putString("orderId", orderId);
		outState.putString("mny", ""+mny);
		outState.putString("meetid", meetid);
		super.onSaveInstanceState(outState);
	}


	/*private void displayOrderInfo() {
		if (orderInfo != null){
//			queryCouponData();

		}
	}*/



	@Override
	protected void onResume() {
		super.onResume();
		if (WebBrowserActivity.payOk){
			//已支付完成，关闭返回主界面
			//finish();
		}
	}
	
	@Override
	protected void onPause() {
		super.onPause();
	}

	private void initView() {
		wechat_div = (RelativeLayout)findViewById(R.id.wechat_div);
		back_button = (ImageView)findViewById(R.id.back_button);
		back_button.setOnClickListener(this);
		wechatPay_textView = (TextView)findViewById(R.id.wechatPay_textView);
		aliPay_textView = (TextView)findViewById(R.id.aliPay_textView);
		wechatPay_textView.setOnClickListener(this);
		aliPay_textView.setOnClickListener(this);
		yinlianPay_textView = (TextView)findViewById(R.id.yinlianPay_textView);
		yinlianPay_textView.setOnClickListener(this);
		price_textView = (TextView)findViewById(R.id.price_textView);
		price1_textView = (TextView)findViewById(R.id.price1_textView);
		price_textView.setText(""+mny.intValue());
		int fen = (int)((mny-mny.intValue())*100);
		price1_textView.setText("."+(fen<10?"0"+fen:fen));
		yinlianPicked_textView = (ImageView)findViewById(R.id.yinlianPicked_textView);
		aliPicked_textView = (ImageView)findViewById(R.id.aliPicked_textView);
		wechatPicked_textView = (ImageView)findViewById(R.id.wechatPicked_textView);
		pickPay(null);
		pay_div = (RelativeLayout)findViewById(R.id.pay_div);
		pay_div.setOnClickListener(this);
		wechat_div = (RelativeLayout)findViewById(R.id.wechat_div);
		wechat_div.setOnClickListener(this);
		alipay_div = (RelativeLayout)findViewById(R.id.alipay_div);
		alipay_div.setOnClickListener(this);
		ylPay_div = (RelativeLayout)findViewById(R.id.ylPay_div);
		ylPay_div.setOnClickListener(this);
	}

	@Override
	public void onStart() {
		super.onStart();
		EventBus.getDefault().register(this);
	}

	@Override
	public void onStop() {
		EventBus.getDefault().unregister(this);
		super.onStop();
	}

	@Subscribe
	public void onMessageEvent(MessageEvent event) {
		Toast.makeText(PayActivity.this, "支付成功！", Toast.LENGTH_SHORT).show();
		startActivity(new Intent(PayActivity.this, MyMeetingActivity.class));
		finish();
		//EventBus.getDefault().post(new MessageEvent());
	}


	final public static int MsgCodeXXX = 1001;
	final public static int msgQueryFail = 1002;
	final public static int msgAliPayOk = 1003;
	final public static int msgAlipayFail = 1004;
	@SuppressLint("HandlerLeak")
	Handler	handler  = new Handler(){
		@Override
		public void dispatchMessage(Message msg) {
			HttpResult hr = null;

			if (dialog != null && dialog.isShowing()){
				dialog.dismiss();
			}
			switch (msg.what) {
			case msgAliPayOk:
				PayResult payResult = new PayResult((String) msg.obj);
				
				// 支付宝返回此次支付结果及加签，建议对支付宝签名信息拿签约时支付宝提供的公钥做验签
				//String resultInfo = payResult.getResult();
				
				String resultStatus = payResult.getResultStatus();

				// 判断resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
				if (TextUtils.equals(resultStatus, "9000")) {
					//Toast.makeText(PayActivity.this, "支付成功", Toast.LENGTH_LONG).show();
					//WebBrowserActivity.payOk = true;
					Toast.makeText(PayActivity.this, "支付成功！", Toast.LENGTH_SHORT).show();
					startActivity(new Intent(PayActivity.this, MyMeetingActivity.class));
					finish();
					//setResult(MeetingApply.RESULTCODE_MeetingApply_ApplyOK);
					//finish();
				} else {
					// 判断resultStatus 为非“9000”则代表可能支付失败
					// “8000”代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
					if (TextUtils.equals(resultStatus, "8000")) {
						Toast.makeText(PayActivity.this, "支付结果确认中",
								Toast.LENGTH_LONG).show();

					} else if (TextUtils.equals(resultStatus, "4000")) {
						Toast.makeText(PayActivity.this, "为了方便您的支付【请安装支付宝客户端】",
								Toast.LENGTH_LONG).show();
					} else {
						// 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
						Toast.makeText(PayActivity.this, payResult.getMemo(),
								Toast.LENGTH_LONG).show();
					}
				}
				break;
			case msgQueryFail:
				hr = (HttpResult)msg.obj;
				if (hr != null){
					HlpUtils.showToastLong(getApplicationContext(), hr.getData()==null?"查询支付信息失败，请稍后再试":hr.getData().toString());
				}
				break;
			case msgAlipayFail:
				hr = (HttpResult)msg.obj;
				if (hr != null){
					HlpUtils.showToastLong(getApplicationContext(), hr.getData()==null?"支付失败，请稍后再试":hr.getData().toString());
				}
				break;
			default:
				break;
			}
			super.dispatchMessage(msg);
		}
	};

	@Override
	protected void onDestroy() {
		super.onDestroy();
		ApplicationTool.getInstance().activitis.remove(this);//把当前Activity移出集合中
		unregisterReceiver(receiver);
	}



	View currentPayView = null;
	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.back_button:
			finish();
			overridePendingTransition(0, R.anim.out_right);
			break;
		case R.id.wechatPay_textView:
		case R.id.wechat_div:
			pickPay(view);
			break;
		case R.id.aliPay_textView:
		case R.id.alipay_div:
			pickPay(view);
			break;
		case R.id.yinlianPay_textView:
		case R.id.ylPay_div:
			pickPay(view);
			break;
		case R.id.pay_div:
			doPay();
			break;
		default:
			break;
		}
	}

	private void doPay() {
		boolean done = false;
		if (currentPayView==aliPay_textView){
			done = true;
			doAliPay();
		}else if (currentPayView == wechatPay_textView){
			done = true;
			doWeixinPay();
		}else if (currentPayView == yinlianPay_textView){
			done = true;
			doYinlianPay();
		}
		if (!done){
			HlpUtils.showToast(getApplicationContext(), "请先选择支付方式");
		}
	}

	private void doYinlianPay() {
		HlpUtils.showToast(getApplicationContext(), "正在开发中");
	}


	private void pickPay(View v) {
		currentPayView = null;
		yinlianPicked_textView.setVisibility(View.GONE);
		aliPicked_textView.setVisibility(View.GONE);
		wechatPicked_textView.setVisibility(View.GONE);
		if (v != null){
			switch (v.getId()) {
			case R.id.wechatPay_textView:
			case R.id.wechat_div:
				currentPayView = wechatPay_textView;
				wechatPicked_textView.setVisibility(View.VISIBLE);
				break;
			case R.id.aliPay_textView:
			case R.id.alipay_div:
				currentPayView = aliPay_textView;
				aliPicked_textView.setVisibility(View.VISIBLE);
				break;
			case R.id.yinlianPay_textView:
			case R.id.ylPay_div:
				currentPayView = yinlianPay_textView;
				yinlianPicked_textView.setVisibility(View.VISIBLE);
				break;
			default:
				break;
			}
		}
	}

	private void doAliPay() {
		dialog = ProgressDialog.show(PayActivity.this, "提示信息", "正在调起支付宝支付...");
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				//PayService ods = new PayService(getApplicationContext());
			    int tryTimes=0;
			    while (tryTimes<3){
					try {
//						HttpResult hr = ods.getAppAliPayInfo(userId, orderId, ""+mny, meetid);
						String s = DataService.getAliPay(getApplicationContext(), userId, orderId, mny, meetid);
						if(s != null && !s.isEmpty()){
							final HttpResult hr = JSON.parseObject(s,HttpResult.class);
							if (hr != null && hr.isSuccess()){
//							final AlipayInfo ali = JSON.parseObject(hr.getData().toString(),AlipayInfo.class);
								final Map<String,Object> ali = JSON.parseObject(hr.getData().toString());
								if (ali != null){
									// 构造PayTask 对象
									PayTask alipay = new PayTask(PayActivity.this);
//								String orderInfo;
									try {
										//{"status":1,"data":{"body":"74","private_key":"MIICeAIBADANBgkqhkiG9w0BAQEFAASCAmIwggJeAgEAAoGBAOxRRtbhLnA4xQu8Li2Nov0mrojNofx8aipDP6ewxbpks8ZOL5sNOTvd1Rh8Bp9mO08QGvxxps5vHN/5g5QTwdmH/HWDAIxOMJlxLLgOh6NN6fCWQ7avpHN+4uI/vxHsDx820v5+9xvoyBP5HVDxvBKkd3d9sBEWdRpPmgBMUHZXAgMBAAECgYEAkf9wvYanMwvPqP1cpiYQIUhJohkwz5Vp3axoYkiTECHw8z4QqUFVL8hQQ+00BjyZvbHcikQI4xbAhxtXEB/YrUUkOh1lti6wjhph1nMW2WqUxMxeXi8i6kgNT/8TmhIb/3IScsmm9pyHuwTjfwwtAMMkN69WXoYATMkx5UVKJeECQQD5luLocgd6lG0wTYBU6DqE51BCeWiRnmaDwi2D3G/cWK+zZ/y5duCJ5uRZ+j6a7Z0jXkBierKRPUQMrFPWVdCnAkEA8mMfySmXmJ7xionEczLR6poDxRrPA9wSViVKJk4mU+8nL/80p+5iaQNErB0/s0NY5KSuvbIA1aNBNpNroguy0QJBAKCs4Bqf/eyNzNUBr5H5hmK0vthsbEMCZCCCSubObDy/4yxtzyZePyLHv02ladQ3D6gLLwu2zWnutPYrojUjPZUCQQCeX2AUa0WvJiNCVvPyJUF9XTabYgHjTrb4bJL/Zk6qxvc+6Cw6kYrhfI4xO0c01QtPEb9PGPBspqfkhfxsu+IRAkANHuCk4fO0vCLaCR5NluMS9myAD7ddB/gsywsoJizBYzAvdb5PUW+Lc4jba6YmoeOKX/EW7b8yZ9CGsFggrIki","subject":"验证支付宝支付金额","sign_type":"RSA","notify_url":"http://liesheng.gicp.net:8080/wcar/alipay/phone_alipayPayNotify.action","out_trade_no":"Binding_ZFB_OD1440080240692_877","sign":null,"_input_charset":"utf-8","total_fee":"0.01","service":"mobile.securitypay.pay","partner":"2088021234709183","seller_id":"2088021234709183","payment_type":"1"}}
										
//									orderInfo = PayService.getAliPayOrderInfo(ali);
//									// 对订单做RSA 签名
//									String sign = aliSign(orderInfo,ali.getPrivate_key());
//									// 仅需对sign 做URL编码
//									sign = URLEncoder.encode(sign, "UTF-8");
//									String payInfo = orderInfo + "&sign=\"" + sign + "\"&"
//											+ getAliSignType();
										String payInfo = (String)ali.get("payInfo");
										// 调用支付接口，获取支付结果
										String result = alipay.pay(payInfo,true);
										
										Message msg = new Message();
										msg.what = msgAliPayOk;
										msg.obj = result;
										handler.sendMessage(msg);
										break;
									} catch (Exception e1) {
										e1.printStackTrace();
										HttpResult hr2 = new HttpResult();
										hr2.setData(e1.getMessage());
										handler.obtainMessage(msgAlipayFail,hr2).sendToTarget();
									}
								}else{
									handler.obtainMessage(msgAlipayFail,hr).sendToTarget();
								}
							}else{
								handler.obtainMessage(msgAlipayFail,hr).sendToTarget();
							}
						}else{
							tryTimes = 3;
							handler.obtainMessage(msgAlipayFail,new HttpResult()).sendToTarget();
						}
					} catch (Exception e) {
						e.printStackTrace();
						mLog.e("http", "Exception:"+e.getMessage());
						HttpResult hr = new HttpResult();
						hr.setData((Config.debug?e.getMessage():""));
						handler.obtainMessage(msgAlipayFail,hr).sendToTarget();
					}
					tryTimes++;
			    }
			}
		}).start();
		
		
	}
	//商户私钥，pkcs8格式
//	public static final String AliPay_RSA_PRIVATE = "";
	//支付宝公钥
	public static final String AliPay_RSA_PUBLIC = "";


	//private static final int AliPay_SDK_PAY_FLAG = 1;

	//private static final int AliPay_SDK_CHECK_FLAG = 2;
	/**
	 * sign the order info. 对订单信息进行签名
	 * 
	 * @param content
	 *            待签名订单信息
	 */
	public String aliSign(String content,String privateKey) {
		return SignUtils.sign(content, privateKey);//AliPay_RSA_PRIVATE);
	}
	/**
	 * get the sign type we use. 获取签名方式
	 * 
	 */
	public String getAliSignType() {
		return "sign_type=\"RSA\"";
	}
	PayReq req;
	ProgressDialog dialog = null;
	private void doWeixinPay() {
		/*final UserInfo ui = (UserInfo)((ApplicationTool)getApplication()).getUserInfo();
		if (ui == null){
			return;
		}*/
		dialog = ProgressDialog.show(PayActivity.this, "提示信息", "正在调起微信支付...");
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				PayService ods = new PayService(getApplicationContext());
				try {
					final HttpResult hr = ods.getWeixinPayRequest(userId, orderId, ""+mny, meetid);
					if (hr != null && hr.isSuccess()){
						runOnUiThread(new Runnable() {
							
							@Override
							public void run() {
								final IWXAPI msgApi = WXAPIFactory.createWXAPI(PayActivity.this, null);
								String s = hr.getData().toString();
								s = s.replace("package", "packageValue");
								PayReq req = JSON.parseObject(s,PayReq.class);
								msgApi.registerApp(req.appId);
								msgApi.sendReq(req);
								if (dialog != null && dialog.isShowing()){
									dialog.dismiss();
								}
							}
						});
						
					}else{
						handler.obtainMessage(msgQueryFail,hr).sendToTarget();
					}
				} catch (Exception e) {
					e.printStackTrace();
					HttpResult hr = new HttpResult();
					hr.setData((Config.debug?e.getMessage():""));
					handler.obtainMessage(msgQueryFail,hr).sendToTarget();
				}
			}
		}).start();
	}
	
}
