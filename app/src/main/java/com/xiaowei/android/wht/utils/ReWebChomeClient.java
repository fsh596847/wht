package com.xiaowei.android.wht.utils;

import android.content.Context;
import android.net.Uri;
import android.webkit.JsResult;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

/**
 * ReWebChomeClient
 *
 * @Author KenChung
 */
public class ReWebChomeClient extends WebChromeClient {

    private OpenFileChooserCallBack mOpenFileChooserCallBack;
    Context mContext;
    public ReWebChomeClient(OpenFileChooserCallBack openFileChooserCallBack,Context context) {
        mOpenFileChooserCallBack = openFileChooserCallBack;
		this.mContext = context;
    }

    //For Android 3.0+
    public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType) {
        mOpenFileChooserCallBack.openFileChooserCallBack(uploadMsg, acceptType);
    }

    // For Android < 3.0
    public void openFileChooser(ValueCallback<Uri> uploadMsg) {
        openFileChooser(uploadMsg, "");
    }

    // For Android  > 4.1.1
    public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
        openFileChooser(uploadMsg, acceptType);
    }

    public interface OpenFileChooserCallBack {
        void openFileChooserCallBack(ValueCallback<Uri> uploadMsg, String acceptType);
    }
    @Override
	public void onReceivedTitle(WebView view, String title) {
		super.onReceivedTitle(view, title);
	}
//	//支持alert弹出 
//	@Override 
//	public boolean onJsAlert(WebView view, String url, String message, 
//	JsResult result) { 
//		return super.onJsAlert(view, url, message, result); 
//	} 
//	@Override 
//	public boolean onJsConfirm(WebView view, String url, 
//	String message, JsResult result) { 
//		return super.onJsConfirm(view, url, message, result); 
//	} 
}
