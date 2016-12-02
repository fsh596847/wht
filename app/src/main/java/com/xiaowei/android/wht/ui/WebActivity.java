package com.xiaowei.android.wht.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Window;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Toast;
import com.xiaowei.android.wht.R;

public class WebActivity extends Activity {

  WebView controlsView = null;

  @SuppressLint({"SetJavaScriptEnabled", "JavascriptInterface"})
  @Override

  protected void onCreate(Bundle savedInstanceState) {

    super.onCreate(savedInstanceState);

    this.requestWindowFeature(Window.FEATURE_NO_TITLE);

    setContentView(R.layout.activity_test);

    controlsView = (WebView) findViewById(R.id.webview);

    //		controlsView.setWebChromeClient(wvcc);
    //
    //		controlsView.getSettings().setDefaultTextEncodingName("utf-8");
    //
    //		controlsView.getSettings().setJavaScriptEnabled(true);

    // 设置setWebChromeClient对象
    controlsView.setWebChromeClient(wvcc);

    WebSettings webSettings = controlsView.getSettings();
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

    controlsView.addJavascriptInterface(WebActivity.this, "push");

    //		pb = (ProgressBar) findViewById(R.id.webview_pb);
    //
    //		pb.setMax(100);

    try {

      controlsView.loadUrl(
          "http://121.40.126.229:8082/wht/phone_releaseCaseIndex.action?userid=f9a8fe655846b399015846c603980002&caseclass=0");
      //loadUrl

      //如果页面中链接，如果希望点击链接继续在当前browser中响应，

      //而不是新开Android的系统browser中响应该链接，必须覆盖 webview的WebViewClient对象

      controlsView.setWebViewClient(new android.webkit.WebViewClient() {

        public boolean shouldOverrideUrlLoading(WebView view, String url)

        {

          view.loadUrl(url);

          return true;
        }
      });
    } catch (Exception ex) {

      ex.printStackTrace();
    }
  }

  @JavascriptInterface
  public void startPushService(String account) {
    Log.d("MyApplication", "startPushService");
    Toast.makeText(this, "web  start", Toast.LENGTH_SHORT).show();
    this.startPushService(account);
  }

  @JavascriptInterface
  public void stopPushService() {
    Log.d("MyApplication", "startPushService");
    Toast.makeText(this, "start", Toast.LENGTH_SHORT).show();
  }

	/*@Override
  public boolean onKeyDown(int keyCode, KeyEvent event)
	{


		//如果不做任何处理，浏览网页，点击系统“Back”键，整个Browser会调用finish()而结束自身

		//如果希望浏览的网 页回退而不是推出浏览器，需要在当前Activity中处理并消费掉该Back事件

		if ((keyCode == KeyEvent.KEYCODE_BACK) && controlsView.canGoBack()) {
			controlsView.goBack();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}*/

  @Override
  public boolean onKeyDown(int keyCode, KeyEvent event) {

    //如果不做任何处理，浏览网页，点击系统“Back”键，整个Browser会调用finish()而结束自身

    //如果希望浏览的网 页回退而不是推出浏览器，需要在当前Activity中处理并消费掉该Back事件

    if ((keyCode == KeyEvent.KEYCODE_BACK)) {
      Log.e("http", "time:" + (System.currentTimeMillis() - exitTime));
      if (System.currentTimeMillis() - exitTime > 500) {
        exitTime = System.currentTimeMillis();
        if (controlsView.canGoBack()) {
          controlsView.goBack();
          return true;
        }
      } else {
        return super.onKeyDown(keyCode, event);
      }
      return true;
    }
    return super.onKeyDown(keyCode, event);
  }

  long exitTime;

  private ValueCallback<Uri> mUploadMessage;
  private final static int FILECHOOSER_RESULTCODE = 1;
  WebChromeClient wvcc = new WebChromeClient() {
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

    public void openFileChooser(ValueCallback<Uri> uploadMsg) {
      mUploadMessage = uploadMsg;
      Intent i = new Intent(Intent.ACTION_GET_CONTENT);
      i.addCategory(Intent.CATEGORY_OPENABLE);
      i.setType("image/*");
      WebActivity.this.startActivityForResult(Intent.createChooser(i, "File Chooser"),
          FILECHOOSER_RESULTCODE);
    }

    // For Android 3.0+
    public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType) {
      mUploadMessage = uploadMsg;
      Intent i = new Intent(Intent.ACTION_GET_CONTENT);
      i.addCategory(Intent.CATEGORY_OPENABLE);
      i.setType("*/*");
      WebActivity.this.startActivityForResult(
          Intent.createChooser(i, "File Browser"),
          FILECHOOSER_RESULTCODE);
    }

    //For Android 4.1
    public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
      mUploadMessage = uploadMsg;
      Intent i = new Intent(Intent.ACTION_GET_CONTENT);
      i.addCategory(Intent.CATEGORY_OPENABLE);
      i.setType("image/*");
      WebActivity.this.startActivityForResult(Intent.createChooser(i, "File Chooser"),
          FILECHOOSER_RESULTCODE);
    }
  };

  @Override
  protected void onActivityResult(int requestCode, int resultCode,
      Intent intent) {
    if (requestCode == FILECHOOSER_RESULTCODE) {
      if (null == mUploadMessage) return;
      Uri result = intent == null || resultCode != RESULT_OK ? null
          : intent.getData();
      mUploadMessage.onReceiveValue(result);
      mUploadMessage = null;
    }
    //		else if (requestCodeLogin == requestCode){
    //			if (resultCode == RESULT_OK){
    //				String meetingId = intent.getStringExtra("meetingId");
    //				appRegMeet(meetingId);
    //			}
    //		}
  }
}
