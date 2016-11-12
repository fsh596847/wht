package com.xiaowei.android.wht.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebSettings.PluginState;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.xiaowei.android.wht.ApplicationTool;
import com.xiaowei.android.wht.R;
import com.xiaowei.android.wht.utils.mLog;

public class WebHealthActivity extends Activity {
	
	TextView tvTitle;
	WebView controlsView;
	private FrameLayout video_fullView;// 全屏时视频加载view
    private View xCustomView;
    private WebChromeClient.CustomViewCallback xCustomViewCallback;
    //double mny;
	
	@SuppressLint("SetJavaScriptEnabled")
	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE); 
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_meeting_details_web);
		ApplicationTool.getInstance().activitis.add(this);//把当前Activity放入集合中
		//mny = getIntent().getDoubleExtra("mny", 0);
		String url = getIntent().getStringExtra("url");
		String title = getIntent().getStringExtra("title");
		
		video_fullView = (FrameLayout) findViewById(R.id.framelayout_meeting);
		controlsView = (WebView) findViewById(R.id.webView_meeting_details );
		tvTitle = (TextView) findViewById(R.id.tv_meeting_detail_title);
		tvTitle.setText(title==null?"健康知识":title);
		mLog.d("http", "url:"+url);
		
		/*//右滑退出
		SildingFinishLayout mSildingFinishLayout = (SildingFinishLayout) findViewById(R.id.sildingFinishLayout_meeting_detail); 
		mSildingFinishLayout.setOnSildingFinishListener(new OnSildingFinishListener() {

			@Override
			public void onSildingFinish() {
				WebHealthActivity.this.finish();
			}
		});
		// touchView
		mSildingFinishLayout.setTouchView(controlsView);*/

		
		WebSettings webSettings = controlsView.getSettings();
		webSettings.setAllowFileAccess(true);
		webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
		// 开启支持视频
		webSettings.setPluginState(PluginState.ON);
		webSettings.setGeolocationEnabled(true);
		// 开启DOM缓存。
		webSettings.setDomStorageEnabled(true);
		webSettings.setDatabaseEnabled(true);
		webSettings.setDatabasePath(getApplicationContext().getCacheDir()
				.getAbsolutePath());
		webSettings.setAppCacheEnabled(true);
		webSettings.setAppCachePath(getApplicationContext().getCacheDir()
				.getAbsolutePath());
		webSettings.setAppCacheMaxSize(Integer.MAX_VALUE);
		controlsView.requestFocus();


		
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
		controlsView.addJavascriptInterface(WebHealthActivity.this, "whtApp");
		

		// 设置setWebChromeClient对象
		controlsView.setWebChromeClient(wvcc);
		//loadUrl 
		controlsView.loadUrl( url);
		
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
				if (xCustomView != null) {
	                hideCustomView();
	            }
	            else if(System.currentTimeMillis()-exitTime>500){
	                exitTime=System.currentTimeMillis();
	                if(controlsView.canGoBack()){
	    				controlsView.goBack();       
	    			}
	            }
	            	controlsView.loadUrl("about:blank");
	                finish();
	                overridePendingTransition(0, R.anim.out_right);
				/*if(System.currentTimeMillis()-exitTime>500){
	                exitTime=System.currentTimeMillis();
	                if(controlsView.canGoBack()){
	    				controlsView.goBack();
	    				return;
	    			}
	            }
				finish();
				overridePendingTransition(0, R.anim.out_right);*/
			}
		});
		
	}
	
	WebChromeClient wvcc = new WebChromeClient() {
		 private View xprogressvideo;
	        // 播放网络视频时全屏会被调用的方法
	        @Override
	        public void onShowCustomView(View view, CustomViewCallback callback) {
	            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
	            controlsView.setVisibility(View.GONE);
	            // 如果一个视图已经存在，那么立刻终止并新建一个
	            if (xCustomView != null) {
	                callback.onCustomViewHidden();
	                return;
	            }
	            video_fullView.addView(view);
	            xCustomView = view;
	            xCustomViewCallback = callback;
	            video_fullView.setVisibility(View.VISIBLE);
	        }
	        // 视频播放退出全屏会被调用的
	        @Override
	        public void onHideCustomView() {
	            if (xCustomView == null)// 不是全屏播放状态
	                return;
	            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
	            xCustomView.setVisibility(View.GONE);
	            video_fullView.removeView(xCustomView);
	            xCustomView = null;
	            video_fullView.setVisibility(View.GONE);
	            xCustomViewCallback.onCustomViewHidden();
	            controlsView.setVisibility(View.VISIBLE);
	        }
	};
	
	@JavascriptInterface
	public void appRegMeet(String meetid,double mny){
		startActivityForResult(new Intent(WebHealthActivity.this, MeetingApply.class)
		.putExtra("meetid",meetid)
		.putExtra("mny", mny)
		, MeetingApply.RESULTCODE_MeetingApply_ApplyOK);
	}

	//分享

	
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
	protected void onResume() {
		super.onResume();
		controlsView.onResume();
		controlsView.resumeTimers();
		/**
         * 设置为横屏
         */
        if (getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
	}

	/**
	 * 解决退出仍有声音的bug
	 */
	@Override
	protected void onPause() {
		super.onPause();
		controlsView.onPause();
		controlsView.pauseTimers();
	}
	
	@Override
	public void finish() {
		ViewGroup view = (ViewGroup) getWindow().getDecorView();
		view.removeAllViews();
		super.finish();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		ApplicationTool.getInstance().activitis.remove(this);//把当前Activity移出集合中
		video_fullView.removeAllViews();
		controlsView.loadUrl("about:blank");
		controlsView.stopLoading();
		controlsView.setWebChromeClient(null);
		controlsView.setWebViewClient(null);
		controlsView.clearCache(true);
		controlsView.destroy();
		controlsView = null;
	}
	
	// Press the back button in mobile phone
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		overridePendingTransition(0, R.anim.out_right);
	}
	
	/**
     * 全屏时按返加键执行退出全屏方法
     */
    public void hideCustomView() {
    	wvcc.onHideCustomView();
        //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }
    
    /**
     * 判断是否是全屏，如果是就隐藏，否则就退出当前的页面
     * @param keyCode
     * @param event
     * @return
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (xCustomView != null) {
                hideCustomView();
                return true;
            }
            else if(System.currentTimeMillis()-exitTime>500){
                exitTime=System.currentTimeMillis();
                if(controlsView.canGoBack()){
    				controlsView.goBack();       
    				return true;       
    			}else{
    				return super.onKeyDown(keyCode, event);
    			}
            }
            else {
            	controlsView.loadUrl("about:blank");
                finish();
                overridePendingTransition(0, R.anim.out_right);
            }
        }
        return super.onKeyDown(keyCode, event);
    }
    long exitTime;
}
