package com.xiaowei.android.wht.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.xiaowei.android.wht.ApplicationTool;
import com.xiaowei.android.wht.Config;
import com.xiaowei.android.wht.R;
import com.xiaowei.android.wht.SpData;

public class WebMeetingDetailsActivity extends Activity {
	
	WebView controlsView;
	String meetid;
	double mny;
	
	@SuppressLint("SetJavaScriptEnabled")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_meeting_details_web);
		ApplicationTool.getInstance().activitis.add(this);//把当前Activity放入集合中
		controlsView = (WebView) findViewById(R.id.webView_meeting_details );
		
		/*//右滑退出
		SildingFinishLayout mSildingFinishLayout = (SildingFinishLayout) findViewById(R.id.sildingFinishLayout_meeting_detail); 
		mSildingFinishLayout.setOnSildingFinishListener(new OnSildingFinishListener() {

			@Override
			public void onSildingFinish() {
				WebMeetingDetailsActivity.this.finish();
			}
		});
		// touchView
		mSildingFinishLayout.setTouchView(controlsView);*/

		// 设置setWebChromeClient对象
		controlsView.setWebChromeClient(wvcc);

		WebSettings webSettings = controlsView.getSettings();
		//设置编码
		webSettings.setDefaultTextEncodingName("utf-8"); 
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
		controlsView.addJavascriptInterface(WebMeetingDetailsActivity.this, "whtApp");
		
		meetid = getIntent().getStringExtra("id");
		mny = getIntent().getDoubleExtra("mny", 0);
		controlsView.loadUrl( Config.getMeetDetailUrl+"?id="+ meetid+
				"&mobile="+new SpData(getApplicationContext()).getStringValue(SpData.keyPhoneUser, null));
		try{

			//loadUrl 



		}catch( Exception ex ){

			ex.printStackTrace();

		}
		//如果页面中链接，如果希望点击链接继续在当前browser中响应，
		
		//而不是新开Android的系统browser中响应该链接，必须覆盖 webview的WebViewClient对象
		
		controlsView.setWebViewClient(new android.webkit.WebViewClient(){
			
			public boolean shouldOverrideUrlLoading(WebView view, String url)
			
			{
				
				view.loadUrl(url);
				
				return true;
				
			}
			
			
		});
		
		findViewById(R.id.iv_meeting_details_back).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(System.currentTimeMillis()-exitTime>500){
	                exitTime=System.currentTimeMillis();
	                if(controlsView.canGoBack()){
	    				controlsView.goBack();       
	    			}else{
	    				Finish();
	    			}
	            }else{
	            	Finish();
	            }
			}
		});
		
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{

		//如果不做任何处理，浏览网页，点击系统“Back”键，整个Browser会调用finish()而结束自身
		
		//如果希望浏览的网 页回退而不是推出浏览器，需要在当前Activity中处理并消费掉该Back事件

		if ((keyCode == KeyEvent.KEYCODE_BACK)) {
			if(System.currentTimeMillis()-exitTime>500){
                exitTime=System.currentTimeMillis();
                if(controlsView.canGoBack()){
    				controlsView.goBack();       
    				return true;       
    			}else{
    				return super.onKeyDown(keyCode, event);
    			}
            }else{
            	return super.onKeyDown(keyCode, event);
            }
		} 
		return super.onKeyDown(keyCode, event);
	}
	long exitTime;
	
	// Press the back button in mobile phone
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		overridePendingTransition(0, R.anim.out_right);
	}
	
	WebChromeClient wvcc = new WebChromeClient() {
	};
	
	@JavascriptInterface
	public void appRegMeet(){
		startActivityForResult(new Intent(WebMeetingDetailsActivity.this, MeetingApply.class)
		.putExtra("meetid",meetid)
		.putExtra("mny", mny)
		, MeetingApply.RESULTCODE_MeetingApply_ApplyOK);
	}
	
	@JavascriptInterface
	public void Finish(){
		finish();
		overridePendingTransition(0, R.anim.out_right);
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (resultCode) {
		case MeetingApply.RESULTCODE_MeetingApply_ApplyOK:
			setResult(MeetingApply.RESULTCODE_MeetingApply_ApplyOK);
			finish();
			break;

		}
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		ApplicationTool.getInstance().activitis.remove(this);//把当前Activity移出集合中
	}

}
