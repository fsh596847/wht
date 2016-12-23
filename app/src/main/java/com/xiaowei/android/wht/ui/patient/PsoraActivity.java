package com.xiaowei.android.wht.ui.patient;

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
import com.xiaowei.android.wht.ui.doctorzone.DoctorTalkFragment;
import com.xiaowei.android.wht.views.Html5WebView;
import com.xiaowei.android.wht.views.TextFont;

/**
 * Created by HIPAA on 2016/11/24.  牛皮鲜
 */

public class PsoraActivity extends BaseActivity {
  private WebView mWebView;
  private LinearLayout mLayout;
  private String mUrl;
  private TextFont tvTitle;

  @Override protected void setContentView() {
    setContentView(R.layout.activity_psora);
  }

  @Override public void init(Bundle savedInstanceState) {
    mLayout = (LinearLayout) findViewById(R.id.web_layout);
    tvTitle = (TextFont) findViewById(R.id.tv_title);
    tvTitle.setText("牛皮癣吧");
    String userid = new SpData(activity.getApplicationContext()).getStringValue(SpData.keyId, null);
    mWebView = new Html5WebView(activity);
    LinearLayout.LayoutParams params =
        new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams
            .MATCH_PARENT);
    mWebView.setLayoutParams(params);
    mLayout.addView(mWebView);
    mWebView.addJavascriptInterface(new JavaScriptInterface(activity),
        "Android");
    mUrl = Config.psora.replace("{USID}", userid);
    Log.d(DoctorTalkFragment.class.getSimpleName(), mUrl);
    mWebView.loadUrl(mUrl);
  }

  public void addCaseClick(View view) {
    startActivity(IssueActivity.class);
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
        PsoraActivity.this.finish();
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
    public void patientHeadInfo(String patientid) {//评论  详情
      Log.d(DoctorTalkFragment.class.getSimpleName(), "patientHeadInfo" + patientid);
      PatientHeadInfoActivity.CallIntent(activity, patientid);
    }

    @JavascriptInterface
    public void commentntent(String id) {//评论  详情
      Log.d(PsoraActivity.class.getSimpleName(), "commentntent" + id);
      CommentActivity.getIntent(activity, id);
    }

    @JavascriptInterface
    public void reportIntent(String caseId) {
      Log.d(PsoraActivity.class.getSimpleName(), caseId);
      Bundle bundle = new Bundle();
      bundle.putString(ReportActivity.KEY_INTENT_CASEID, caseId);
      startActivityWithBundle(ReportActivity.class, bundle);
    }
  }
}
