package com.xiaowei.android.wht.ui;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.ValueCallback;
import android.webkit.WebView;
import android.widget.LinearLayout;
import com.xiaowei.android.wht.R;
import com.xiaowei.android.wht.SpData;
import com.xiaowei.android.wht.ui.doctorzone.BaseActivity;
import com.xiaowei.android.wht.ui.doctorzone.IssueActivity;
import com.xiaowei.android.wht.ui.doctorzone.IssueDetailActivity;
import com.xiaowei.android.wht.views.Html5WebView;
import com.xiaowei.android.wht.views.TextFont;

import static com.xiaowei.android.wht.ui.doctorzone.DoctorZoneActivity.INTENT_KEY_TYPE_ISSUE;
import static com.xiaowei.android.wht.ui.doctorzone.DoctorZoneActivity.INTENT_KEY_TYPE_SHARE;

/**
 * Created by HIPAA on 2016/11/29. 发布
 */
public class WebBrowserActivity1 extends BaseActivity implements Html5WebView.WebCall {
  private WebView mWebView;
  private LinearLayout mLayout;
  private String mUrl;
  private TextFont tvTitle;
  public static String INTENT_KEY_CASE = "casetype";
  private String intentKeyValue;

  @Override protected void setContentView() {
    setContentView(R.layout.activity_issue);
  }

  @Override public void init(Bundle savedInstanceState) {
    mLayout = (LinearLayout) findViewById(R.id.web_layout);
    tvTitle = (TextFont) findViewById(R.id.tv_title);
    tvTitle.setText("填写病例");
    mWebView = new Html5WebView(activity);
    String userid = new SpData(getApplicationContext()).getStringValue(SpData.keyId, null);
    LinearLayout.LayoutParams params =
        new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams
            .MATCH_PARENT);
    mWebView.setLayoutParams(params);
    mLayout.addView(mWebView);
    //intentKeyValue = getIntent().getStringExtra(INTENT_KEY_CASE);
    //mUrl = Config.issueCase.replace("{USID}", userid)
    //    .replace("{TYPE}", intentKeyValue);
    //Log.d(IssueActivity.class.getSimpleName(), mUrl);
    ((Html5WebView) mWebView).setCallBack(this);
    mWebView.loadUrl("http://www.91xiaowei.com/wht2q_case/wht2q_case_t");
    //mWebView.loadUrl("file:///android_asset/text2.html");

  }

  @Override public void setListener() {

  }

  public static void getIntent(Context context, String key) {
    Intent intent = new Intent(context, IssueActivity.class);
    intent.putExtra(INTENT_KEY_CASE, key);
    context.startActivity(intent);
  }

  @TargetApi(Build.VERSION_CODES.KITKAT)
  public void submitClick(View view) {

    try {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
        mWebView.evaluateJavascript("fsubmit()", new ValueCallback<String>() {
          @Override
          public void onReceiveValue(final String value) {
            mWebView.post(new Runnable() {
              @Override
              public void run() {
                Log.e(IssueActivity.class.getSimpleName(), "onReceiveValue value=" + value);
                if (value.equals("1")) {
                  if (getIntent().getStringExtra(INTENT_KEY_CASE).equals(INTENT_KEY_TYPE_ISSUE)) {
                    IssueDetailActivity.getIntent(activity, INTENT_KEY_TYPE_ISSUE);
                  } else {
                    IssueDetailActivity.getIntent(activity, INTENT_KEY_TYPE_SHARE);
                  }
                }
              }
            });
            //runOnUiThread(new Runnable() {
            //  @Override public void run() {
            //
            //  }
            //});
          }
        });
      } else {
        mWebView.post(new Runnable() {
          @Override
          public void run() {
            mWebView.loadUrl("javascript:fsubmit()");
            if (getIntent().getStringExtra(INTENT_KEY_CASE).equals(INTENT_KEY_TYPE_ISSUE)) {
              IssueDetailActivity.getIntent(activity, INTENT_KEY_TYPE_ISSUE);
            } else {
              IssueDetailActivity.getIntent(activity, INTENT_KEY_TYPE_SHARE);
            }
          }
        });
      }
    } catch (Exception e) {
      e.printStackTrace();
      Log.d(IssueActivity.class.getSimpleName(), e.getMessage());
    }
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
        WebBrowserActivity1.this.finish();
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

  private static final int FILE_SELECT_CODE = 0;
  private ValueCallback<Uri> mUploadMessage;//回调图片选择，4.4以下
  private ValueCallback<Uri[]> mUploadCallbackAboveL;//回调图片选择，5.0以上

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (resultCode != Activity.RESULT_OK) {
      if (Build.VERSION.SDK_INT >= 21) {//5.0以上版本处理
        mUploadCallbackAboveL.onReceiveValue(null);
        mUploadCallbackAboveL = null;
      } else {
        mUploadMessage.onReceiveValue(null);
        mUploadMessage = null;
      }
      mWebView.requestFocus();
      return;
    }

    switch (requestCode) {
      case FILE_SELECT_CODE: {
        if (Build.VERSION.SDK_INT >= 21) {//5.0以上版本处理
          Uri uri = data.getData();
          Uri[] uris = new Uri[] {uri};
                   /* ClipData clipData = data.getClipData();  //选择多张
                    if (clipData != null) {
                        for (int i = 0; i < clipData.getItemCount(); i++) {
                            ClipData.Item item = clipData.getItemAt(i);
                            Uri uri = item.getUri();
                            uris[i]=uri;
                        }
                    }*/
          mUploadCallbackAboveL.onReceiveValue(uris);//回调给js
          mUploadCallbackAboveL = null;
        } else {//4.4以下处理
          Uri uri = data.getData();
          if (mUploadMessage != null) {
            mUploadMessage.onReceiveValue(uri);
            mUploadMessage = null;
          }
        }
      }
      break;
    }
  }

  @Override public void fileChose3(ValueCallback<Uri> uploadMsg) {
    mUploadMessage = uploadMsg;
    Intent i = new Intent(Intent.ACTION_GET_CONTENT);
    i.addCategory(Intent.CATEGORY_OPENABLE);
    i.setType("image/*");
    startActivityForResult(Intent.createChooser(i, "File Chooser"), FILE_SELECT_CODE);
  }

  @Override public void fileChose3(ValueCallback<Uri> uploadMsg, String acceptType) {
    mUploadMessage = uploadMsg;
    Intent i = new Intent(Intent.ACTION_GET_CONTENT);
    i.addCategory(Intent.CATEGORY_OPENABLE);
    i.setType("*/*");
    startActivityForResult(Intent.createChooser(i, "File Chooser"), FILE_SELECT_CODE);
  }

  @Override public void fileChose4(ValueCallback<Uri> uploadMsg) {
    mUploadMessage = uploadMsg;
    Intent i = new Intent(Intent.ACTION_GET_CONTENT);
    i.addCategory(Intent.CATEGORY_OPENABLE);
    i.setType("image/*");
    startActivityForResult(Intent.createChooser(i, "File Chooser"), FILE_SELECT_CODE);
  }

  @Override public void fileChose5(ValueCallback<Uri[]> filePathCallback) {
    mUploadCallbackAboveL = filePathCallback;
    Intent i = new Intent(Intent.ACTION_GET_CONTENT);
    i.addCategory(Intent.CATEGORY_OPENABLE);
    i.setType("*/*");
    startActivityForResult(
        Intent.createChooser(i, "File Browser"),
        FILE_SELECT_CODE);
  }
}
