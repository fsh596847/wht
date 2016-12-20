package com.xiaowei.android.wht.ui.patient;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.LinearLayout;
import com.xiaowei.android.wht.Config;
import com.xiaowei.android.wht.R;
import com.xiaowei.android.wht.ui.doctorzone.BaseActivity;
import com.xiaowei.android.wht.ui.doctorzone.DoctorTalkFragment;
import com.xiaowei.android.wht.views.Html5WebView;
import com.xiaowei.android.wht.views.TextFont;

/**
 * Created by HIPAA on 2016/11/24.  患者详情
 */

public class PatientHeadInfoActivity extends BaseActivity {
  private WebView mWebView;
  private LinearLayout mLayout;
  private String mUrl;
  private TextFont tvTitle;
  private static String INTENT_KEY_ID = "id";

  @Override protected void setContentView() {
    setContentView(R.layout.activity_brandcase);
  }

  @Override public void init(Bundle savedInstanceState) {
    mLayout = (LinearLayout) findViewById(R.id.web_layout);
    tvTitle = (TextFont) findViewById(R.id.tv_title);
    tvTitle.setText("患者详情");
    mWebView = new Html5WebView(activity);
    LinearLayout.LayoutParams params =
        new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams
            .MATCH_PARENT);
    mWebView.setLayoutParams(params);
    mLayout.addView(mWebView);
    mUrl =
        Config.issueCaseHeadDetaile.replace("{userid}", getIntent().getStringExtra(INTENT_KEY_ID));
    Log.d(DoctorTalkFragment.class.getSimpleName(), mUrl);
    mWebView.loadUrl(mUrl);
  }

  public static void CallIntent(Context context, String id) {
    Intent intent = new Intent(context, PatientHeadInfoActivity.class);
    intent.putExtra(INTENT_KEY_ID, id);
    context.startActivity(intent);
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
        PatientHeadInfoActivity.this.finish();
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
