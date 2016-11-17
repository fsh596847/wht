package com.xiaowei.android.wht.ui.doctorzone;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.LinearLayout;
import com.xiaowei.android.wht.Config;
import com.xiaowei.android.wht.R;
import com.xiaowei.android.wht.views.Html5WebView;

public class DoctorTalkFragment extends BaseFragment {

  private LinearLayout mLayout;
  private WebView mWebView;

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
    mWebView = new Html5WebView(getContext().getApplicationContext());
    mWebView.setLayoutParams(params);
    mLayout.addView(mWebView);
    mWebView.loadUrl(Config.getDoctorTalk);
  }

  @Override
  protected void setAdapter() {

  }

  @Override
  protected void setListener() {

  }
}
