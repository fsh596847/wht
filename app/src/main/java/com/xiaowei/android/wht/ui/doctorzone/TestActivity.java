package com.xiaowei.android.wht.ui.doctorzone;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.webkit.ValueCallback;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import com.xiaowei.android.wht.R;
import com.xiaowei.android.wht.views.SharePopupwindow;

/**
 * Created by HIPAA on 2016/11/17.
 */
//@SuppressLint("SetJavaScriptEnabled")
public class TestActivity extends BaseActivity implements MyWebChromeClient.WebCall {
  private WebView webview;
  EditText showjs;
  EditText jsshow;
  Button clickjs;

  private SharePopupwindow popup;
  private LinearLayout viewParent;

  @Override protected void setContentView() {
    setContentView(R.layout.activity_test);
  }

  @TargetApi(Build.VERSION_CODES.KITKAT)
  @Override public void init(Bundle savedInstanceState) {
    viewParent = (LinearLayout) findViewById(R.id.lyt_my_invite);
    webview = (WebView) findViewById(R.id.webview);
    showjs = (EditText) findViewById(R.id.showjs);
    jsshow = (EditText) findViewById(R.id.jsshow);
    clickjs = (Button) findViewById(R.id.clickjs);
    webview.getSettings().setDefaultTextEncodingName("utf-8");
    webview.getSettings().setJavaScriptEnabled(true);
    WebSettings webSettings = webview.getSettings();
    // 设置WebView属性，能够执行Javascript脚本
    webSettings.setJavaScriptEnabled(true);
    // 设置可以访问文件
    webSettings.setAllowFileAccess(true);
    MyWebChromeClient myWebChromeClient = new MyWebChromeClient();
    webview.setWebChromeClient(myWebChromeClient);
    myWebChromeClient.setWebCall(this);
    //webview.setWebViewClient(Html5WebView.webViewClient);
    //webview.addJavascriptInterface(new JavaScriptInterface(this), "Android");

    //webview.loadUrl("http://121.40.126.229:8082/wht/phone_releaseCaseIndex.action?userid=f9a8fe655846b399015846c603980002&caseclass=0");
    webview.loadUrl("file:///android_asset/tl_share.html");
  }

  @Override public void setListener() {

  }

  @Override public void fileChose(ValueCallback<Uri> uploadMsg) {
    openFileChooserImpl(uploadMsg);
  }

  @Override public void fileChose5(ValueCallback<Uri[]> uploadMsg) {
    openFileChooserImplForAndroid5(uploadMsg);
  }

  public final static int FILECHOOSER_RESULTCODE = 1;
  public final static int FILECHOOSER_RESULTCODE_FOR_ANDROID_5 = 2;
  public ValueCallback<Uri> mUploadMessage;
  public ValueCallback<Uri[]> mUploadMessageForAndroid5;

  private void openFileChooserImpl(ValueCallback<Uri> uploadMsg) {
    mUploadMessage = uploadMsg;
    Intent i = new Intent(Intent.ACTION_GET_CONTENT);
    i.addCategory(Intent.CATEGORY_OPENABLE);
    i.setType("image/*");
    startActivityForResult(Intent.createChooser(i, "File Chooser"),
        FILECHOOSER_RESULTCODE);
  }

  private void openFileChooserImplForAndroid5(ValueCallback<Uri[]> uploadMsg) {
    mUploadMessageForAndroid5 = uploadMsg;
    Intent contentSelectionIntent = new Intent(Intent.ACTION_GET_CONTENT);
    contentSelectionIntent.addCategory(Intent.CATEGORY_OPENABLE);
    contentSelectionIntent.setType("image/*");

    Intent chooserIntent = new Intent(Intent.ACTION_CHOOSER);
    chooserIntent.putExtra(Intent.EXTRA_INTENT, contentSelectionIntent);
    chooserIntent.putExtra(Intent.EXTRA_TITLE, "Image Chooser");

    startActivityForResult(chooserIntent,
        FILECHOOSER_RESULTCODE_FOR_ANDROID_5);
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode,
      Intent intent) {
    if (resultCode != Activity.RESULT_OK) {
      return;
    }

    switch (requestCode) {
      case FILECHOOSER_RESULTCODE_FOR_ANDROID_5: {
        if (Build.VERSION.SDK_INT >= 21) {//5.0以上版本处理
          Uri uri = intent.getData();
          Uri[] uris = new Uri[] {uri};
                   /* ClipData clipData = data.getClipData();  //选择多张
                    if (clipData != null) {
                        for (int i = 0; i < clipData.getItemCount(); i++) {
                            ClipData.Item item = clipData.getItemAt(i);
                            Uri uri = item.getUri();
                            uris[i]=uri;
                        }
                    }*/
          mUploadMessageForAndroid5.onReceiveValue(uris);//回调给js
        } else {//4.4以下处理
          Uri uri = intent.getData();
          mUploadMessage.onReceiveValue(uri);
        }
      }
      break;
    }
  }
}
