package com.xiaowei.android.wht.ui.doctorzone;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.LinearLayout;
import com.xiaowei.android.wht.Config;
import com.xiaowei.android.wht.R;
import com.xiaowei.android.wht.ui.CourseActivity;
import com.xiaowei.android.wht.ui.doctorzone.able.IWxShare;
import com.xiaowei.android.wht.ui.doctorzone.able.WxShare;
import com.xiaowei.android.wht.views.Html5WebView;
import com.xiaowei.android.wht.views.SharePopupwindow;

public class DoctorTalkFragment extends BaseFragment {

  private LinearLayout mLayout;
  private WebView mWebView;
  private SharePopupwindow popup;
  /**
   * 分享病例的url
   */
  private String mUrl;
  public static DoctorTalkFragment newInstance() {
    DoctorTalkFragment f = new DoctorTalkFragment();

    return f;
  }

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
    mWebView.addJavascriptInterface(new DoctorTalkFragment.JavaScriptInterface(mActivity),
        "Android");
    mWebView.setLayoutParams(params);
    mLayout.addView(mWebView);
    mWebView.loadUrl(DoctorZoneActivity.tabUrl);
    mUrl = Config.issueCaseDetaile.replace("{userid}", ((DoctorZoneActivity) mActivity).userid);
  }

  @Override
  protected void setAdapter() {

  }

  @Override
  protected void setListener() {
    popup = new SharePopupwindow(mActivity);
    popup.setOutsideTouchable(true);
    popup.setCallBack(new SharePopupwindow.CallBack() {

      @Override
      public void group() {
        IWxShare iWxShare = WxShare.getInstance(mActivity);
        iWxShare.wxShare(1, mUrl);
        //wxShare(1);
      }

      @Override
      public void friend() {
        IWxShare iWxShare = WxShare.getInstance(mActivity);
        iWxShare.wxShare(0, mUrl);
        //wxShare(0);
      }

      @Override
      public void dismiss() {
      }
    });
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
    public void showSharePopuJs(String caseUrl) {
      mUrl = mUrl.replace("{id}", caseUrl);
      mActivity.runOnUiThread(new Runnable() {
        @Override
        public void run() {
          popup.showAtLocation(mLayout, Gravity.BOTTOM, 0, 0);
        }
      });
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
      startActivity(DoctorHeadInfoActivity.class);
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
