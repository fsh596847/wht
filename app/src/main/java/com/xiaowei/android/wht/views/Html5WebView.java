package com.xiaowei.android.wht.views;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.webkit.GeolocationPermissions;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import com.xiaowei.android.wht.utis.NetStatusUtil;
import com.xiaowei.android.wht.utis.Utils;

/**
 * Wing_Li 2016/9/9.
 */
public class Html5WebView extends WebView {

  private Context mContext;

  public Html5WebView(Context context) {
    super(context);
    mContext = context;
    init();
  }

  public Html5WebView(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public Html5WebView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
  }

  private void init() {

    WebSettings mWebSettings = getSettings();
    mWebSettings.setSupportZoom(true);
    mWebSettings.setLoadWithOverviewMode(true);
    mWebSettings.setUseWideViewPort(true);
    mWebSettings.setDefaultTextEncodingName("utf-8");
    mWebSettings.setLoadsImagesAutomatically(true);
    //运行webview通过URI获取安卓文件
    mWebSettings.setAllowFileAccess(true);
    mWebSettings.setAllowFileAccessFromFileURLs(true);
    mWebSettings.setAllowUniversalAccessFromFileURLs(true);
    //调用JS方法.安卓版本大于17,加上注解 @JavascriptInterface
    //允许JavaScript执行
    mWebSettings.setJavaScriptEnabled(true);
    mWebSettings.setLoadsImagesAutomatically(true);
    mWebSettings.setSupportMultipleWindows(true);
    //缓存数据
    saveData(mWebSettings);
    newWin(mWebSettings);
    setWebChromeClient(webChromeClient);
    setWebViewClient(webViewClient);
  }

  /**
   * 多窗口的问题
   */
  private void newWin(WebSettings mWebSettings) {
    //html中的_bank标签就是新建窗口打开，有时会打不开，需要加以下
    //然后 复写 WebChromeClient的onCreateWindow方法
    mWebSettings.setSupportMultipleWindows(false);
    mWebSettings.setJavaScriptCanOpenWindowsAutomatically(true);
  }

  /**
   * HTML5数据存储
   */
  private void saveData(WebSettings mWebSettings) {
    //有时候网页需要自己保存一些关键数据,Android WebView 需要自己设置

    if (NetStatusUtil.isConnected(mContext)) {
      mWebSettings.setCacheMode(WebSettings.LOAD_DEFAULT);//根据cache-control决定是否从网络上取数据。
    } else {
      mWebSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);//没网，则从本地获取，即离线加载
    }

    mWebSettings.setDomStorageEnabled(true);
    mWebSettings.setDatabaseEnabled(true);
    mWebSettings.setAppCacheEnabled(true);
    String appCachePath = mContext.getCacheDir().getAbsolutePath();
    mWebSettings.setAppCachePath(appCachePath);
  }

  public WebViewClient webViewClient = new WebViewClient() {
    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
      super.onPageStarted(view, url, favicon);
      reload("加载中");
    }

    @Override
    public void onPageFinished(WebView view, String url) {
      super.onPageFinished(view, url);
      closeLoadingDialog();
    }

    /**
     * 多页面在同一个WebView中打开，就是不新建activity或者调用系统浏览器打开
     */
    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
      view.loadUrl(url);
      Log.d("Url:", url);
      return true;
    }
  };

  WebCall webCall;

  public void setCallBack(WebCall webCall) {
    this.webCall = webCall;
  }

  public interface WebCall {
    void fileChose3(ValueCallback<Uri> mUploadMessage);

    void fileChose3(ValueCallback<Uri> mUploadMessage, String acceptType);

    void fileChose4(ValueCallback<Uri> mUploadMessage);

    void fileChose5(ValueCallback<Uri[]> mUploadMessage);
  }

  WebChromeClient webChromeClient = new WebChromeClient() {
    // For Android 3.0+
    public void openFileChooser(ValueCallback<Uri> uploadMsg) {
      Log.d(Html5WebView.class.getSimpleName(), "onShowFileChooser");
      if (webCall != null) {
        webCall.fileChose3(uploadMsg);
      }
      //mUploadMessage = uploadMsg;
      //Intent i = new Intent(Intent.ACTION_GET_CONTENT);
      //i.addCategory(Intent.CATEGORY_OPENABLE);
      //i.setType("image/*");
      //startActivityForResult(Intent.createChooser(i, "File Chooser"), FILE_SELECT_CODE);

    }

    // For Android 3.0+
    public void openFileChooser(ValueCallback uploadMsg, String acceptType) {
      Log.d(Html5WebView.class.getSimpleName(), "onShowFileChooser");
      if (webCall != null) {
        webCall.fileChose3(uploadMsg, acceptType);
      }
      //mUploadMessage = uploadMsg;
      //Intent i = new Intent(Intent.ACTION_GET_CONTENT);
      //i.addCategory(Intent.CATEGORY_OPENABLE);
      //i.setType("*/*");
      //startActivityForResult(Intent.createChooser(i, "File Browser"), FILE_SELECT_CODE);
    }

    // For Android 4.1
    public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
      Log.d(Html5WebView.class.getSimpleName(), "onShowFileChooser");
      if (webCall != null) {
        webCall.fileChose4(uploadMsg);
      }
      //mUploadMessage = uploadMsg;
      //Intent i = new Intent(Intent.ACTION_GET_CONTENT);
      //i.addCategory(Intent.CATEGORY_OPENABLE);
      //i.setType("image/*");
      //startActivityForResult(Intent.createChooser(i, "File Chooser"), FILE_SELECT_CODE);
    }

    // For Android 5.0+
    public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback,
        WebChromeClient.FileChooserParams fileChooserParams) {
      Log.d(Html5WebView.class.getSimpleName(), "onShowFileChooser");
      if (webCall != null) {
        webCall.fileChose5(filePathCallback);
      }
      //mUploadCallbackAboveL = filePathCallback;
      //Intent i = new Intent(Intent.ACTION_GET_CONTENT);
      //i.addCategory(Intent.CATEGORY_OPENABLE);
      //i.setType("*/*");
      //startActivityForResult(
      //    Intent.createChooser(i, "File Browser"),
      //    FILE_SELECT_CODE);
      return true;
    }

    @Override
    public void onReceivedIcon(WebView view, Bitmap icon) {
      super.onReceivedIcon(view, icon);
    }

    @Override
    public void onGeolocationPermissionsHidePrompt() {
      super.onGeolocationPermissionsHidePrompt();
    }

    @Override
    public void onGeolocationPermissionsShowPrompt(final String origin,
        final GeolocationPermissions.Callback callback) {
      callback.invoke(origin, true, false);//注意个函数，第二个参数就是是否同意定位权限，第三个是是否希望内核记住
      super.onGeolocationPermissionsShowPrompt(origin, callback);
    }
    //=========HTML5定位==========================================================

    //=========多窗口的问题==========================================================
    @Override
    public boolean onCreateWindow(WebView view, boolean isDialog, boolean isUserGesture,
        Message resultMsg) {
      WebViewTransport transport = (WebViewTransport) resultMsg.obj;
      transport.setWebView(view);
      resultMsg.sendToTarget();
      return true;
    }

    //=========多窗口的问题==========================================================
  };

  private void reload(String text) {
    if (loadingDialog == null) {
      loadingDialog = Utils.createLoadingDialog(mContext, text);
    }
    if (!loadingDialog.isShowing() && !((Activity) mContext).isFinishing()) {
      loadingDialog.show();
    }
  }

  private Dialog loadingDialog = null;

  private void closeLoadingDialog() {
    if (null != loadingDialog) {
      loadingDialog.dismiss();
      loadingDialog = null;
    }
  }
}
