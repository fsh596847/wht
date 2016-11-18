package com.xiaowei.android.wht.ui.doctorzone;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import com.xiaowei.android.wht.R;
import com.xiaowei.android.wht.ui.doctorzone.able.IWxShare;
import com.xiaowei.android.wht.ui.doctorzone.able.WxShare;
import com.xiaowei.android.wht.views.SharePopupwindow;

/**
 * Created by HIPAA on 2016/11/17.
 */
//@SuppressLint("SetJavaScriptEnabled")
public class TestActivity extends BaseActivity {
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
    webview.addJavascriptInterface(new JavaScriptInterface(this), "Android");
    webview.loadUrl("file:///android_asset/test2.html");

    clickjs.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        //String msg = showjs.getText().toString().trim();
        //webview.loadUrl("javascript:androidGetJs('" + msg + "')");
        //webview.loadUrl("javascript:javaCallJs(" + "'" + showjs.getText().toString() + "'" + ")");
        webview.evaluateJavascript("sum(1,2)", new ValueCallback<String>() {
          @Override
          public void onReceiveValue(String value) {
            Log.e(TestActivity.class.getSimpleName(), "onReceiveValue value=" + value);
          }
        });
      }
    });
    popup = new SharePopupwindow(this);
    popup.setOutsideTouchable(true);
    popup.setCallBack(new SharePopupwindow.CallBack() {

      @Override
      public void group() {
        IWxShare iWxShare = WxShare.getInstance(TestActivity.this);
        iWxShare.wxShare(1);
        //wxShare(1);
      }

      @Override
      public void friend() {
        IWxShare iWxShare = WxShare.getInstance(TestActivity.this);
        iWxShare.wxShare(0);
        //wxShare(0);
      }

      @Override
      public void dismiss() {
      }
    });

    //java调用JS方法

  }

  @Override public void setListener() {

  }

  public class JavaScriptInterface {

    Context context;

    JavaScriptInterface(Context context) {
      this.context = context;
    }

    //在js中调用 androidjs.showInfoFromJs("I'M FROM JS!!!")就会出发此方法
    @JavascriptInterface
    public String showInfoFromJs(final String name) {
      runOnUiThread(new Runnable() {
        @Override
        public void run() {
          //jsshow.setText(name);
          popup.showAtLocation(viewParent, Gravity.BOTTOM, 0, 0);
        }
      });
      return "I'M FROM ANDROID!!!";
    }
  }
}
