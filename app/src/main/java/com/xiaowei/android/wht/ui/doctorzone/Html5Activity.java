package com.xiaowei.android.wht.ui.doctorzone;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.WebView;
import android.widget.LinearLayout;
import com.xiaowei.android.wht.R;
import com.xiaowei.android.wht.views.Html5WebView;

public class Html5Activity extends Activity {

  private String mUrl;

  private LinearLayout mLayout;
  private WebView mWebView;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    this.requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉标题栏
    setContentView(R.layout.activity_web);

    Bundle bundle = getIntent().getBundleExtra("bundle");
    //if (bundle != null) {
    //    mUrl = bundle.getString("url");
    //} else {
    //    mUrl = "https://wing-li.github.io/";
    //}
    mUrl =
        "http://m.youku.com/video/id_XMTMyNTMwOTk3Ng==.html?refer=pc-sns-1&x=&from=singlemessage&isappinstalled=0?id=f9a8fe6556c4d7850156d4c76b680009";
    //mUrl =
    //    "http://192.168.1.159:8080/wht/phone_getMeetSchedule.action?meetid=f9a8fe6557f608580157f6380e4e0001";
    mLayout = (LinearLayout) findViewById(R.id.web_layout);

    LinearLayout.LayoutParams params =
        new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams
            .MATCH_PARENT);
    mWebView = new Html5WebView(getApplicationContext());
    mWebView.setLayoutParams(params);
    mLayout.addView(mWebView);

    mWebView.loadUrl(mUrl);
  }

  private long mOldTime;

  @Override
  public boolean onKeyDown(int keyCode, KeyEvent event) {
    if (keyCode == KeyEvent.KEYCODE_BACK) {
      if (System.currentTimeMillis() - mOldTime < 1500) {
        mWebView.clearHistory();
        mWebView.loadUrl(mUrl);
      } else if (mWebView.canGoBack()) {
        mWebView.goBack();
      } else {
        Html5Activity.this.finish();
      }
      mOldTime = System.currentTimeMillis();
      return true;
    }

    return super.onKeyDown(keyCode, event);
  }

  @Override
  protected void onDestroy() {
    if (mWebView != null) {
      mWebView.loadDataWithBaseURL(null, "", "text/html", "utf-8", null);
      mWebView.clearHistory();

      ((ViewGroup) mWebView.getParent()).removeView(mWebView);
      mWebView.destroy();
      mWebView = null;
    }
    super.onDestroy();
  }
}