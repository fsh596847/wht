package com.xiaowei.android.wht.ui.mycenter;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.LinearLayout;
import com.xiaowei.android.wht.Config;
import com.xiaowei.android.wht.R;
import com.xiaowei.android.wht.SpData;
import com.xiaowei.android.wht.ui.doctorzone.BaseActivity;
import com.xiaowei.android.wht.ui.doctorzone.DoctorHeadInfoActivity;
import com.xiaowei.android.wht.ui.doctorzone.DoctorTalkFragment;
import com.xiaowei.android.wht.ui.doctorzone.CommentActivity;
import com.xiaowei.android.wht.views.Html5WebView;
import com.xiaowei.android.wht.views.TextFont;

/**
 * Created by HIPAA on 2016/11/24.  邀请评论
 */

public class PayCaseActivity extends BaseActivity {
  private WebView mWebView;
  private LinearLayout mLayout;
  private String mUrl;
  private TextFont tvTitle;

  @Override protected void setContentView() {
    setContentView(R.layout.activity_brandcase);
  }

  @Override public void init(Bundle savedInstanceState) {
    mLayout = (LinearLayout) findViewById(R.id.web_layout);
    mWebView = new Html5WebView(activity);
    tvTitle = (TextFont) findViewById(R.id.tv_title);
    tvTitle.setText("打赏");
    String userid = new SpData(getApplicationContext()).getStringValue(SpData.keyId, null);
    LinearLayout.LayoutParams params =
        new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams
            .MATCH_PARENT);
    mWebView.setLayoutParams(params);
    mLayout.addView(mWebView);
    mWebView.addJavascriptInterface(new JavaScriptInterface(activity),
        "Android");
    mUrl = Config.tip.replace("{USID}", userid);
    Log.d(DoctorTalkFragment.class.getSimpleName(), mUrl);
    mWebView.loadUrl(mUrl);
  }

  @Override public void setListener() {

  }

  public void backClick(View view) {
    finish();
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
        PayCaseActivity.this.finish();
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

  /**
   * 分享js调用的方法
   */
  public class JavaScriptInterface {

    Context context;

    JavaScriptInterface(Context context) {
      this.context = context;
    }

    @JavascriptInterface
    public void commentntent(String id) {//评论  详情
      Log.d(PayCaseActivity.class.getSimpleName(), "commentntent" + id);
      CommentActivity.getIntent(activity, id);
    }

    @JavascriptInterface
    public void doctorHeadInfo(String doctorid) {//评论  详情
      Log.d(DoctorTalkFragment.class.getSimpleName(), "doctorHeadInfo" + doctorid);
      DoctorHeadInfoActivity.CallIntent(activity, doctorid);
    }
  }
}
