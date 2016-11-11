package com.xiaowei.android.wht.ui;

import java.io.File;
import java.util.Date;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXWebpageObject;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.xiaowei.android.wht.ApplicationTool;
import com.xiaowei.android.wht.Config;
import com.xiaowei.android.wht.R;
import com.xiaowei.android.wht.SpData;
import com.xiaowei.android.wht.model.UserInfo;
import com.xiaowei.android.wht.uibase.BaseNoTitleBarActivity;
import com.xiaowei.android.wht.utils.ImageUtil;
import com.xiaowei.android.wht.utils.ReWebChomeClient;
import com.xiaowei.android.wht.utis.DownLoadImage;
import com.xiaowei.android.wht.utis.DownLoadImage.BitmapCallBack;
import com.xiaowei.android.wht.utis.HlpUtils;
import com.xiaowei.android.wht.utis.HttpUtil;
import com.xiaowei.android.wht.utis.Utils;
import com.xiaowei.android.wht.utis.WXUtil;

public class WebBrowserActivity extends BaseNoTitleBarActivity implements ReWebChomeClient.OpenFileChooserCallBack{

	// 自己微信应用的 appId
	public static String WX_APP_ID = "wx6a781c53be72cdc2";
	// 自己微信应用的 appSecret
	public static String WX_SECRET = "d4624c36b6795d1d99dcf0547af5443d";
	
	public static String WX_CODE = "";

//	public static IWXAPI wxApi;
//	public static boolean isWXLogin = false;
	ImageView bg_imageView;
	private WebView webview;
	private Dialog progressDialog;//
//	final String homeUrl = "http://www.tdwlqf.com/index.php/home/index/phoneAuth?code=";
	String homeUrl = Config.indexUrl;//"file:///android_asset/t.html";//
	//"http://192.168.1.103:8080/xjmmnt/login.jsp" ;//
//	final String payOkUrl="http://www.tdwlqf.com/index.php/Home/Distributor/index/saleuid/$saleuid";
//	final String reportUrl = "http://www.91xiaowei.com/tdwl/index.php/home/index/phoneAuth?code=";
//	final String payOkUrl="http://www.91xiaowei.com/tdwl/index.php/Home/Distributor/index/saleuid/$saleuid";
	public static boolean payOk = false;//表示是否支付完成后的显示

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_browser);
		ApplicationTool.getInstance().activitis.add(this);//把当前Activity放入集合中
		ApplicationTool.wxApi = WXAPIFactory.createWXAPI(this, WX_APP_ID, true);
		ApplicationTool.wxApi.registerApp(WX_APP_ID);
		init();
		fixDirPath();
		autoLogin();
//		wxLogin();
//		isWXAppInstalledAndSupported(getApplicationContext(), wxApi);
//		loadUrlWithCookie(reportUrl);
	}
	private void autoLogin() {
		SpData sp = new SpData(getApplicationContext());
		String s = sp.getStringValue(SpData.keyUserInfo, null);
		if (!HlpUtils.isEmpty(s)){
			UserInfo ui = JSON.parseObject(s,UserInfo.class);
			if (ui != null){
				ApplicationTool.getInstance().setUserInfo(ui);
				ApplicationTool.getInstance().hasLogined = true;
//				homeUrl = ui.getUrl();
			}
		}
	}
	private void reload(){
		if (progressDialog == null){
			progressDialog = Utils.createLoadingDialog(this, "正在加载...");
		}
		if (!progressDialog.isShowing()){
			progressDialog.show();
		}
	}


	private void wxLogin(){
		ApplicationTool.isWXLogin = true;
		SendAuth.Req req = new SendAuth.Req();
		req.scope = "snsapi_userinfo";
		req.state = "wechat_sdk_demo";
		ApplicationTool.wxApi.sendReq(req);
	}
	protected void onResume() {
		super.onResume();
//		if (ApplicationTool.getInstance().hasLogined){
//			if (!homeUrl.equals(Config.homeUrl)){
//				homeUrl = Config.homeUrl;
//				hasLoad = false;
//			}
//		}
		if (ApplicationTool.isWXLogin) {
//			loadWXUserInfo();
			
		}
		if (payOk){
			openUrl(homeUrl);
			payOk = false;
		}
//		webview.loadUrl("http://www.czbbb.cn/mnt");//file:///android_asset/test.html");
		if (!hasLoad){
			//第一次启动后加载
			openUrl(homeUrl);
		}
	};
	/**
	 * 获得微信用户信息
	 */
	private void loadWXUserInfo() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				String accessTokenUrl = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=" + WX_APP_ID + "&secret=" + WX_SECRET + "&code=" + WX_CODE + "&grant_type=authorization_code";
				try{
					String tokenResult = HttpUtil.getUrl(getApplicationContext(),accessTokenUrl);
					if (null != tokenResult) {
						JSONObject tokenObj = JSON.parseObject(tokenResult);
						String accessToken = tokenObj.getString("access_token");
						final String openId = tokenObj.getString("openid");
						String userUrl = "https://api.weixin.qq.com/sns/userinfo?access_token=" + accessToken + "&openid=" + openId;
						runOnUiThread(new Runnable() {
							
							@Override
							public void run() {
	//							openUrl(openId);
							}
						});
						String wxUserInfo = HttpUtil.getUrl(getApplicationContext(),userUrl);
						// JSONObject userObj = JSON.parseObject(wxUserInfo);
	//					Toast.makeText(WebBrowserActivity.this, wxUserInfo, 5000).show();
					}
				}catch (Exception e){
					
				}
				
			}
		}).start();
		ApplicationTool.isWXLogin = false;
	}
	@JavascriptInterface
	public void shareWx(int type,String title,String desc,String url){
		String imageUrl = "";
		shareWxWithImage(type, title, desc, url, imageUrl);
	}

	@JavascriptInterface
	public void shareWxWithImage(int type,String title,String desc,String url,String imageUrl ){
//		int type=0;
//		String title="67890-";
//		String desc="dddddddddddddddddd";
//		String url = "http://www.baidu.com";
		final SendMessageToWX.Req req = new SendMessageToWX.Req();
		WXWebpageObject webpage = new WXWebpageObject();
		// 要跳转的地址
		webpage.webpageUrl = url;// "http://www.baidu.com";
		final WXMediaMessage msg = new WXMediaMessage(webpage);
		msg.title = title;// "标题";
		msg.description = desc;//"要分享到微信的内容,要换行使用 \n 已经换行";
		// 网络图片地址 png 格式
		// eg： http://h.hiphotos.baidu.com/image/w%3D310/sign=58272176271f95caa6f594b7f9177fc5/aa18972bd40735fa29e06ab19c510fb30f2408a1.png
//		String imageUrl = "";
		// 0:发送到朋友 1:发送到朋友圈 2:收藏
		final int shareWhat = type;// 0;
		if (imageUrl == null || imageUrl.length() == 0) {
			// 分享的图片不能超过32k 否则弹不出微信分享框
			Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);
			msg.thumbData = WXUtil.bmpToByteArray(bmp, true);
			req.transaction = buildTransaction("webpage");
			req.message = msg;
			req.scene = shareWhat;
			ApplicationTool.wxApi.sendReq(req);
		} else {
			// 主线程不能访问网络，开启线程下载图片
			new DownLoadImage(imageUrl).loadBitmap(new BitmapCallBack() {
				@Override
				public void getBitmap(Bitmap bitmap) {
					// 分享的图片不能超过32k 压缩图片
					Bitmap thumbBmp = Bitmap.createScaledBitmap(bitmap, 100, 100, true);
					bitmap.recycle();
					msg.thumbData = WXUtil.bmpToByteArray(thumbBmp, true);
					req.transaction = buildTransaction("webpage");
					req.message = msg;
					req.scene = shareWhat;
					ApplicationTool.wxApi.sendReq(req);
				}
			});
		}
	}

	private String buildTransaction(final String type) {
		return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
	}
	private void openUrl(String url) {
//		if (HlpUtils.isEmpty(WX_CODE)){
////			HlpUtils.showToast(getApplicationContext(), "获取微信登录信息失败");
//		}else{
//			webview.loadUrl(url+WX_CODE);
////			webview.loadUrl("file:///android_asset/test.html");
//		}
		UserInfo ui = ApplicationTool.getInstance().getUserInfo();
		if (ui != null){
			if (!url.contains("&mobile=")){
				if (url.contains("?")){
					url += "&mobile="+ui.getMobile();
				}else{
					url += "?mobile="+ui.getMobile();
				}
			}
		}
		webview.loadUrl(url);
	}
//	private ValueCallback<Uri> mUploadMessage;
//	private final static int FILECHOOSER_RESULTCODE = 1;
	WebChromeClient wvcc1 = new WebChromeClient() {
		@Override
		public void onReceivedTitle(WebView view, String title) {
			super.onReceivedTitle(view, title);
		}
		//支持alert弹出 
		@Override 
		public boolean onJsAlert(WebView view, String url, String message, 
		JsResult result) { 
			return super.onJsAlert(view, url, message, result); 
		} 
		@Override 
		public boolean onJsConfirm(WebView view, String url, 
		String message, JsResult result) { 
			return super.onJsConfirm(view, url, message, result); 
		} 
//		public void openFileChooser(ValueCallback<Uri> uploadMsg) {
//			      mUploadMessage = uploadMsg;
//			      Intent i = new Intent(Intent.ACTION_GET_CONTENT);
//			      i.addCategory(Intent.CATEGORY_OPENABLE);
//			      i.setType("image/*");
//			      WebBrowserActivity.this.startActivityForResult(Intent.createChooser(i,"File Chooser"), FILECHOOSER_RESULTCODE);
//			 }
//			 // For Android 3.0+
//			 public void openFileChooser( ValueCallback<Uri> uploadMsg, String acceptType ) {
//			    mUploadMessage = uploadMsg;
//			    Intent i = new Intent(Intent.ACTION_GET_CONTENT);
//			    i.addCategory(Intent.CATEGORY_OPENABLE);
//			    i.setType("*/*");
//			    WebBrowserActivity.this.startActivityForResult(
//			    Intent.createChooser(i, "File Browser"),
//			    FILECHOOSER_RESULTCODE);
//			 }
//			 //For Android 4.1
//			 public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture){
//			    mUploadMessage = uploadMsg;
//			    Intent i = new Intent(Intent.ACTION_GET_CONTENT);
//			    i.addCategory(Intent.CATEGORY_OPENABLE);
//			    i.setType("image/*");
//			    WebBrowserActivity.this.startActivityForResult( Intent.createChooser( i, "File Chooser" ), WebBrowserActivity.FILECHOOSER_RESULTCODE );
//			 }

	};
	private void loadUrlWithCookie(String url4Load) {
		openUrl(url4Load);
	}

	final int msgLoginOk = 1001;
	final int msgLoginFail = 1002;
	Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case msgLoginOk:
				loadUrlWithCookie(homeUrl);
				webview.setVisibility(View.VISIBLE);
				if (progressDialog != null && !progressDialog.isShowing()){
					progressDialog.show();
				}
				break;
			case msgLoginFail:
				if (progressDialog != null && progressDialog.isShowing()){
					progressDialog.hide();
				}
				HlpUtils.showToast(getApplicationContext(), "无法登录系统,请稍后再试");
				finish();
				break;

			default:
				break;
			}
		};
	};

	@Override
	protected void onStart() {
		super.onStart();
		
	}

	private void init() {
		bg_imageView = (ImageView)findViewById(R.id.bg_imageView);
		webview = (WebView) findViewById(R.id.webview);
//		WebChromeClient wvcc = new WebChromeClient() {
//			@Override
//			public void onReceivedTitle(WebView view, String title) {
//				super.onReceivedTitle(view, title);
//			}
//
//		};
		// 设置setWebChromeClient对象
		webview.setWebChromeClient(new ReWebChomeClient(this, this));

		
		HelloWebViewClient client = new HelloWebViewClient();
		webview.setWebViewClient(client);
		WebSettings webSettings = webview.getSettings();
		// 支持javascript
		webSettings.setJavaScriptEnabled(true);
		// 设置可以支持缩放
		webSettings.setSupportZoom(true);
		// 设置出现缩放工具
		webSettings.setBuiltInZoomControls(true);
		// 扩大比例的缩放
		webSettings.setUseWideViewPort(true);
		// 去掉縮放工具 api最低版本11
		// webSettings.setDisplayZoomControls(false);
		// 设置控件属性，网页大小适应屏幕
		webSettings.setLoadWithOverviewMode(true);
		webSettings.setUseWideViewPort(true);
		// webSettings.setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);
		
		webview.addJavascriptInterface(WebBrowserActivity.this, "whtApp");// 在html页面中
		// html能访问的方法：
		//(关闭当前窗口)window.czbbb.nativeCloseWindow() ;
	}

	@JavascriptInterface
	public void nativeCloseWindow() {
		
		runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				WebBrowserActivity.this.finish();
			}
		});
	}
	@JavascriptInterface
	public void nativeGoBack() {
		//由于js的在一些手机上是在WebViewCoreThread执行，而WebView的方法调用需要在UI线程调用
		runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				//原生的回退方法，代替web 页面的History.go(-1)而引起的A->B->A的循环中
				if (WebBrowserActivity.this.webview.canGoBack()){
					WebBrowserActivity.this.webview.goBack(); // //回退到上一个页面
				}else{
					WebBrowserActivity.this.finish();
				}
			}
		});

	}
	String meetingId;
	@JavascriptInterface
	public void appRegMeet(final String meetingId) {
		this.meetingId = meetingId;
		//由于js的在一些手机上是在WebViewCoreThread执行，而WebView的方法调用需要在UI线程调用
		runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				UserInfo ui = ApplicationTool.getInstance().getUserInfo();
				if (ui == null){
					Intent it = new Intent(WebBrowserActivity.this,LoginActivity.class);
					it.putExtra("meetingId", meetingId);
					startActivityForResult(it, requestCodeLogin);
				}else{
					String params = "mobile="+ui.getMobile()+"&userid="+ui.getId()+"&meetid="+meetingId;
					String url = ui.getUrl()+"?"+params;
//					HlpUtils.showToastLong(getApplicationContext(),url);
					loadUrlWithCookie(url);
				}
			}
		});

	}
	@JavascriptInterface
	public void appGoPay(final String userId,final String orderId,final String mny) {
		payOk = false;
//		HlpUtils.showToast(getApplicationContext(), "-"+userId+" "+orderId+" "+mny);
		//由于js的在一些手机上是在WebViewCoreThread执行，而WebView的方法调用需要在UI线程调用
		runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				Intent it = new Intent(WebBrowserActivity.this,PayActivity.class);
				it.putExtra("userId", userId);
				it.putExtra("orderId", orderId);
				it.putExtra("mny", mny);//mny);
				startActivity(it);
			}
		});

	}
	public void finish(View v) {
		finish();
	}

	int retryTimes = 0;
	public class HelloWebViewClient extends WebViewClient {
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			UserInfo ui = ApplicationTool.getInstance().getUserInfo();
			if (ui != null){
				if (!url.contains("&mobile=")){
					if (url.contains("?")){
						url += "&mobile="+ui.getMobile();
					}else{
						url += "?mobile="+ui.getMobile();
					}
				}
			}else{
//				auto
			}
			view.loadUrl(url);
//			HlpUtils.showToast(getApplicationContext(), url);
			return true;
//			if (url != null ){
//				if (url.contains("login.jsp")){
//					webview.setVisibility(View.GONE);
//					retryTimes++;
//					if (retryTimes>5){
//						handler.sendEmptyMessage(msgLoginFail);
//					}else{
//						//如果过期，则重新登录加载
//						reload();
//					}
//					return true;
//				}else if (url.startsWith("tel:")){
////					runOnUiThread(new Runnable() {
////						
////						@Override
////						public void run() {
////							Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse(url));    
////				            startActivity(intent); 
////						}
////					});
//					return true;
//				}else{
////					setCookie(url);
//					return false;
//				}
//			}else{
////				setCookie(url);
//				return false;
//			}
//			
		}

		
		@Override
		public void onPageFinished(WebView view, String url) {
			super.onPageFinished(view, url);
//			if (url != null && url.contains("login.jsp")){
//				webview.setVisibility(View.GONE);
//			}else{
//				webview.setVisibility(View.VISIBLE);
//			}
			if (progressDialog != null && progressDialog.isShowing()) {
				progressDialog.dismiss();
			}
			if (!hasLoad){
				hasLoad = true;
				bg_imageView.setVisibility(View.GONE);
			}
		}
		@Override
		public void onReceivedError(WebView view, int errorCode,
				String description, String failingUrl) {
			super.onReceivedError(view, errorCode, description, failingUrl);
			if (progressDialog != null && progressDialog.isShowing()) {

				progressDialog.dismiss();
			}
		}
	}
	boolean hasLoad = false;
	@Override
	protected void onDestroy() {
		super.onDestroy();
		ApplicationTool.getInstance().activitis.remove(this);//把当前Activity移出集合中
	}
	long lastTime =0;
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK) ) {
//			if (webview.canGoBack()){
//				webview.goBack(); // //回退到上一个页面
//	            return true;
//			}
			long now = (new Date()).getTime();
			if (now-lastTime>2000){
				lastTime = now;
				HlpUtils.showToast(getApplicationContext(), "再按一次将退出系统");
				return true;
			}
//			if (hasTitleBar){
//				if (webview.canGoBack()){
//					webview.goBack(); // //回退到上一个页面
//		            return true;
//				}else{
//					return super.onKeyDown(keyCode,event);
//				}
//			}else{
//				webview.loadUrl("javascript:if (typeof(webappGoBack) != 'undefined'){webappGoBack();}else{window.czbbb.nativeCloseWindow();}"); 
//				return true;
//			}
        } 
        return super.onKeyDown(keyCode,event);
    }
	private static boolean isWXAppInstalledAndSupported(Context context,
			IWXAPI api) {
		boolean sIsWXAppInstalledAndSupported = api.isWXAppInstalled()
				&& api.isWXAppSupportAPI();
		if (!sIsWXAppInstalledAndSupported) {
			HlpUtils.showToast(context, "微信客户端未安装，无法登录，请确认");
		}

		return sIsWXAppInstalledAndSupported;
	}
	PayReq req;
	ProgressDialog dialog = null;
	final IWXAPI msgApi = WXAPIFactory.createWXAPI(this, null);
	@JavascriptInterface
	public void doWeixinPay(final String payRequestStr) {
		payOk = false;
		dialog = ProgressDialog.show(WebBrowserActivity.this, "提示信息", "正在调起微信支付...");
//		HlpUtils.showToast(getApplicationContext(), payRequestStr);
		runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				final IWXAPI msgApi = WXAPIFactory.createWXAPI(WebBrowserActivity.this, null);
				PayReq req = JSON.parseObject(payRequestStr.replace("\"package\"", "\"packageValue\""),PayReq.class);
				msgApi.registerApp(req.appId);
				msgApi.sendReq(req);
				if (dialog != null && dialog.isShowing()){
					dialog.dismiss();
				}
			}
		});
	}


    private static final int REQUEST_CODE_PICK_IMAGE = 0;
    private static final int REQUEST_CODE_IMAGE_CAPTURE = 1;
    private static final int requestCodeOcr = 1001;
    private static final int requestCodeLogin = 1002;
    
    private ValueCallback<Uri> mUploadMsg;
    private Intent mSourceIntent;
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if (resultCode != Activity.RESULT_OK) {
//            return;
//        }

        switch (requestCode) {
        	case requestCodeLogin:
        		if (resultCode == RESULT_OK){
    	    		String meetingId = data.getStringExtra("meetingId");
    	    		appRegMeet(meetingId);
    	    	}
        		break;
            case REQUEST_CODE_IMAGE_CAPTURE:
            case REQUEST_CODE_PICK_IMAGE: {
                try {
                    if (mUploadMsg == null) {
                        return;
                    }
                    String sourcePath = ImageUtil.retrievePath(this, mSourceIntent, data);
                    if (!TextUtils.isEmpty(sourcePath) && new File(sourcePath).exists()) {
	                    Uri uri = Uri.fromFile(new File(sourcePath));
	                    mUploadMsg.onReceiveValue(uri);
                    }else{
                    	mUploadMsg.onReceiveValue(null);
                        mUploadMsg = null;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            }
        }
    }

	@Override
	public void openFileChooserCallBack(ValueCallback<Uri> uploadMsg,
			String acceptType) {
		mUploadMsg = uploadMsg;
        showOptions();
    }

    public void showOptions() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setOnCancelListener(new ReOnCancelListener());
        alertDialog.setTitle(R.string.options);
        alertDialog.setItems(R.array.options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {
                            mSourceIntent = ImageUtil.choosePicture();
                            startActivityForResult(mSourceIntent, REQUEST_CODE_PICK_IMAGE);
                        } else {
                            mSourceIntent = ImageUtil.takeBigPicture();
                            startActivityForResult(mSourceIntent, REQUEST_CODE_IMAGE_CAPTURE);
                        }
                    }
                }
        );
        alertDialog.show();
    }

    private void fixDirPath() {
        String path = ImageUtil.getDirPath();
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }
    }

    private class ReOnCancelListener implements DialogInterface.OnCancelListener {

        @Override
        public void onCancel(DialogInterface dialogInterface) {
            if (mUploadMsg != null) {
                mUploadMsg.onReceiveValue(null);
                mUploadMsg = null;
            }
        }
    }
}
