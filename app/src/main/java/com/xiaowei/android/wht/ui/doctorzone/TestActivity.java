package com.xiaowei.android.wht.ui.doctorzone;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import com.xiaowei.android.wht.R;

/**
 * Created by HIPAA on 2016/11/17.
 */
@SuppressLint("SetJavaScriptEnabled")
public class TestActivity extends BaseActivity {
  private WebView webview;
  EditText showjs;
  EditText jsshow;
  Button clickjs;

  @Override protected void setContentView() {
    setContentView(R.layout.activity_test);
  }

  @Override public void init(Bundle savedInstanceState) {
    webview = (WebView) findViewById(R.id.webview);
    showjs = (EditText) findViewById(R.id.showjs);
    jsshow = (EditText) findViewById(R.id.jsshow);
    clickjs = (Button) findViewById(R.id.clickjs);
    webview.getSettings().setDefaultTextEncodingName("utf-8");
    webview.getSettings().setJavaScriptEnabled(true);
    webview.addJavascriptInterface(new JavaScriptInterface(this), "androidjs");
    webview.loadUrl("file:///android_asset/test2.html");
    clickjs.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        //String msg = showjs.getText().toString().trim();
        //webview.loadUrl("javascript:androidGetJs('" + msg + "')");
        webview.loadUrl("javascript:javaCallJs(" + "'" + showjs.getText().toString() + "'" + ")");
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
          jsshow.setText(name);
        }
      });
      return "I'M FROM ANDROID!!!";
    }
  }
}
