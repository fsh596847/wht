package com.xiaowei.android.wht.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.TextView;

import com.xiaowei.android.wht.ApplicationTool;
import com.xiaowei.android.wht.Config;
import com.xiaowei.android.wht.R;
import com.xiaowei.android.wht.views.SildingFinishLayout;
import com.xiaowei.android.wht.views.SildingFinishLayout.OnSildingFinishListener;

public class WebAboutUsActivity extends Activity {
	
	TextView tvTitle;
	WebView controlsView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE); 
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_meeting_details_web);
		ApplicationTool.getInstance().activitis.add(this);//把当前Activity放入集合中
		controlsView = (WebView) findViewById(R.id.webView_meeting_details );
		tvTitle = (TextView) findViewById(R.id.tv_meeting_detail_title);
		tvTitle.setText("关于我们");
		
		/*//右滑退出
		SildingFinishLayout mSildingFinishLayout = (SildingFinishLayout) findViewById(R.id.sildingFinishLayout_meeting_detail); 
		mSildingFinishLayout.setOnSildingFinishListener(new OnSildingFinishListener() {

			@Override
			public void onSildingFinish() {
				WebAboutUsActivity.this.finish();
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
		//webSettings.setJavaScriptEnabled(true);
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
		//controlsView.addJavascriptInterface(WebAboutUsActivity.this, "whtApp");
		
		//loadUrl 
		controlsView.loadUrl( Config.queryaboutUs);
		
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
				finish();
				overridePendingTransition(0, R.anim.out_right);
			}
		});
		
	}
	
	WebChromeClient wvcc = new WebChromeClient() {
	};
	
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		ApplicationTool.getInstance().activitis.remove(this);//把当前Activity移出集合中
	}
	
	// Press the back button in mobile phone
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		overridePendingTransition(0, R.anim.out_right);
	}

}
