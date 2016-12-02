package com.xiaowei.android.wht.ui.doctorzone;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import com.xiaowei.android.wht.R;

/**
 * Created by HIPAA on 2016/12/1.
 */

public class WebViewDemo extends FragmentActivity {
  private static final int FILE_SELECT_CODE = 0;

  private WebView webView;
  private ValueCallback<Uri> mUploadMessage;//回调图片选择，4.4以下
  private ValueCallback<Uri[]> mUploadCallbackAboveL;//回调图片选择，5.0以上

  @Override
  protected void onCreate(Bundle arg0) {
    super.onCreate(arg0);
    setContentView(R.layout.activity_test);
    initWebView();
  }

  @SuppressLint("SetJavaScriptEnabled")
  private void initWebView() {
    webView = (WebView) findViewById(R.id.webview);
    //允许JavaScript执行
    webView.getSettings().setJavaScriptEnabled(true);
    webView.getSettings().setLoadsImagesAutomatically(true);
    webView.setVerticalScrollBarEnabled(false);
    //运行webview通过URI获取安卓文件
    webView.getSettings().setAllowFileAccess(true);
    webView.getSettings().setAllowFileAccessFromFileURLs(true);
    webView.getSettings().setAllowUniversalAccessFromFileURLs(true);
    webView.requestFocus();
    webView.setWebChromeClient(new MyWebChromeClient());//设置可以打开图片管理器
    //webView.loadUrl("file:///android_asset/demo81.htm");
    webView.loadUrl("file:///android_asset/tl_share.html");
  }

  private class MyWebChromeClient extends WebChromeClient {

    // For Android 3.0+
    public void openFileChooser(ValueCallback<Uri> uploadMsg) {

      mUploadMessage = uploadMsg;
      Intent i = new Intent(Intent.ACTION_GET_CONTENT);
      i.addCategory(Intent.CATEGORY_OPENABLE);
      i.setType("image/*");
      startActivityForResult(Intent.createChooser(i, "File Chooser"), FILE_SELECT_CODE);
    }

    // For Android 3.0+
    public void openFileChooser(ValueCallback uploadMsg, String acceptType) {
      mUploadMessage = uploadMsg;
      Intent i = new Intent(Intent.ACTION_GET_CONTENT);
      i.addCategory(Intent.CATEGORY_OPENABLE);
      i.setType("*/*");
      startActivityForResult(Intent.createChooser(i, "File Browser"), FILE_SELECT_CODE);
    }

    // For Android 4.1
    public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
      mUploadMessage = uploadMsg;
      Intent i = new Intent(Intent.ACTION_GET_CONTENT);
      i.addCategory(Intent.CATEGORY_OPENABLE);
      i.setType("image/*");
      startActivityForResult(Intent.createChooser(i, "File Chooser"), FILE_SELECT_CODE);
    }

    // For Android 5.0+
    public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback,
        WebChromeClient.FileChooserParams fileChooserParams) {
      mUploadCallbackAboveL = filePathCallback;
      Intent i = new Intent(Intent.ACTION_GET_CONTENT);
      i.addCategory(Intent.CATEGORY_OPENABLE);
      i.setType("*/*");
      startActivityForResult(
          Intent.createChooser(i, "File Browser"),
          FILE_SELECT_CODE);
      return true;
    }
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (resultCode != Activity.RESULT_OK) {
      return;
    }

    switch (requestCode) {
      case FILE_SELECT_CODE: {
        if (Build.VERSION.SDK_INT >= 21) {//5.0以上版本处理
          Uri uri = data.getData();
          Uri[] uris = new Uri[] {uri};
                   /* ClipData clipData = data.getClipData();  //选择多张
                    if (clipData != null) {
                        for (int i = 0; i < clipData.getItemCount(); i++) {
                            ClipData.Item item = clipData.getItemAt(i);
                            Uri uri = item.getUri();
                            uris[i]=uri;
                        }
                    }*/
          mUploadCallbackAboveL.onReceiveValue(uris);//回调给js
        } else {//4.4以下处理
          Uri uri = data.getData();
          mUploadMessage.onReceiveValue(uri);
        }
      }
      break;
    }
  }
}
