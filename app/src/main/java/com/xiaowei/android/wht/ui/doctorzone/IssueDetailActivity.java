package com.xiaowei.android.wht.ui.doctorzone;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.xiaowei.android.wht.Config;
import com.xiaowei.android.wht.R;
import com.xiaowei.android.wht.SpData;
import com.xiaowei.android.wht.views.Html5WebView;

import static com.xiaowei.android.wht.ui.doctorzone.IssueActivity.INTENT_KEY_CASE;

/**
 * Created by HIPAA on 2016/11/29.
 */

public class IssueDetailActivity extends BaseActivity {
  private WebView mWebView;
  private LinearLayout mLayout;
  private String mUrl;
  private TextView tvTitle;

  @Override protected void setContentView() {
    setContentView(R.layout.activity_brandcase);
  }

  @Override public void init(Bundle savedInstanceState) {
    mLayout = (LinearLayout) findViewById(R.id.web_layout);
    tvTitle = (TextView) findViewById(R.id.tv_title);
    tvTitle.setText("发布详情");
    mWebView = new Html5WebView(activity);
    String userid = new SpData(getApplicationContext()).getStringValue(SpData.keyId, null);
    LinearLayout.LayoutParams params =
        new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams
            .MATCH_PARENT);
    mWebView.setLayoutParams(params);
    mLayout.addView(mWebView);
    //求助
    if (getIntent().getStringExtra(IssueActivity.INTENT_KEY_CASE)
        .equals(DoctorZoneActivity.INTENT_KEY_TYPE_ISSUE)) {
      mUrl = Config.issueCaseIss.replace("{USID}", userid);
      //分享
    } else if (getIntent().getStringExtra(IssueActivity.INTENT_KEY_CASE)
        .equals(DoctorZoneActivity.INTENT_KEY_TYPE_SHARE)) {
      mUrl = Config.issueCaseShare.replace("{USID}", userid);
    }

    Log.d(IssueDetailActivity.class.getSimpleName(), mUrl);
    mWebView.loadUrl(mUrl);
  }

  @Override public void setListener() {

  }

  public static void getIntent(Context context, String key) {
    Intent intent = new Intent(context, IssueDetailActivity.class);
    intent.putExtra(INTENT_KEY_CASE, key);
    context.startActivity(intent);
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
        IssueDetailActivity.this.finish();
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
