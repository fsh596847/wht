package com.xiaowei.android.wht.ui.doctorzone;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.LinearLayout;
import com.xiaowei.android.wht.Config;
import com.xiaowei.android.wht.R;
import com.xiaowei.android.wht.ui.CourseActivity;
import com.xiaowei.android.wht.views.Html5WebView;

public class DoctorZoneFragment extends BaseFragment {

  private LinearLayout mLayout;
  private WebView mWebView;
  private String mUrl;

  @Override
  protected View createView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_doctor, container, false);
    return view;
  }

  @Override
  protected void init(View container, Bundle savedInstanceState) {
    mLayout = (LinearLayout) container.findViewById(R.id.web_layout);
    LinearLayout.LayoutParams params =
        new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams
            .MATCH_PARENT);
    mWebView = new Html5WebView(mActivity);
    mWebView.addJavascriptInterface(new JavaScriptInterface(mActivity),
        "Android");
    mWebView.setLayoutParams(params);
    mLayout.addView(mWebView);
    mUrl = Config.overSeasCase.replace("{userid}", ((DoctorZoneActivity) mActivity).userid);
    mWebView.loadUrl(mUrl);
  }

  @Override
  protected void setAdapter() {

  }

  @Override
  protected void setListener() {

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
    public void brandCaseIntent() {
      startActivity(BrandCaseActivity.class);
    }

    @JavascriptInterface
    public void selectCaseIntent() {
      startActivity(SelectCaseActivity.class);
    }

    @JavascriptInterface
    public void videoIntent() {
      startActivity(CourseActivity.class);
    }

    @JavascriptInterface
    public void classifyntent() {
      startActivity(ClassifyActivity.class);
    }

    @JavascriptInterface
    public void commentntent(String id) {//评论  详情
      Log.d(DoctorTalkFragment.class.getSimpleName(), "commentntent" + id);
      CommentActivity.getIntent(mActivity, id);
    }

    @JavascriptInterface
    public void doctorHeadInfo(String doctorid) {//评论  详情
      Log.d(DoctorTalkFragment.class.getSimpleName(), "doctorHeadInfo" + doctorid);
      DoctorHeadInfoActivity.CallIntent(mActivity, doctorid);
    }

    @JavascriptInterface
    public void reportIntent(String caseId) {
      Log.d(CommentActivity.class.getSimpleName(), caseId);
      Bundle bundle = new Bundle();
      bundle.putString(ReportActivity.KEY_INTENT_CASEID, caseId);
      startActivityWithBundle(ReportActivity.class, bundle);
    }
  }
}
