package com.xiaowei.android.wht.ui.doctorzone;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.ValueCallback;
import android.webkit.WebView;
import android.widget.LinearLayout;
import com.xiaowei.android.wht.R;
import com.xiaowei.android.wht.SpData;
import com.xiaowei.android.wht.utils.ReWebChomeClient;
import com.xiaowei.android.wht.views.Html5WebView;

public class Html5Activity extends Activity implements ReWebChomeClient.OpenFileChooserCallBack {

  private String mUrl;

  private LinearLayout mLayout;
  private WebView mWebView;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    this.requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉标题栏
    setContentView(R.layout.activity_web);

    Bundle bundle = getIntent().getBundleExtra("bundle");
    if (bundle != null) {
      mUrl = bundle.getString("url");
    } else {
      mUrl = "https://wing-li.github.io/";
    }
    //mUrl =
    //    "http://m.youku.com/video/id_XMTMyNTMwOTk3Ng==.html?refer=pc-sns-1&x=&from=singlemessage&isappinstalled=0?id=f9a8fe6556c4d7850156d4c76b680009";
    mUrl =
        " http://121.40.126.229:8082/wht/phone_releaseCaseIndex.action?userid=f9a8fe655846b399015846c603980002&caseclass=0";
    mLayout = (LinearLayout) findViewById(R.id.web_layout);
    String userid = new SpData(getApplicationContext()).getStringValue(SpData.keyId, null);
    LinearLayout.LayoutParams params =
        new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams
            .MATCH_PARENT);
    mWebView = new Html5WebView(Html5Activity.this);
    mWebView.setLayoutParams(params);
    mLayout.addView(mWebView);

    mWebView.loadUrl("file:///android_asset/index.html");
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

  @Override public void openFileChooserCallBack(ValueCallback<Uri> uploadMsg, String acceptType) {
    mUploadMsg = uploadMsg;
    showOptions();
  }

  public void showOptions() {
    AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
    alertDialog.setOnCancelListener(new ReOnCancelListener());
    alertDialog.setTitle(R.string.options);
    alertDialog.setItems(R.array.options, new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {

          }
        }
    );
    alertDialog.show();
  }

  private ValueCallback<Uri> mUploadMsg;

  private class ReOnCancelListener implements DialogInterface.OnCancelListener {
    @Override
    public void onCancel(DialogInterface dialogInterface) {
      if (mUploadMsg != null) {
        mUploadMsg.onReceiveValue(null);
        mUploadMsg = null;
      }
    }
  }
}