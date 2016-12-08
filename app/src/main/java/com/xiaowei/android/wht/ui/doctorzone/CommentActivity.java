package com.xiaowei.android.wht.ui.doctorzone;

import android.content.Context;
import android.content.Intent;
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
import com.xiaowei.android.wht.views.AlertDialog;
import com.xiaowei.android.wht.views.Html5WebView;
import com.xiaowei.android.wht.views.TextFont;

/**
 * Created by HIPAA on 2016/11/24.  评论/病例详情
 */

public class CommentActivity extends BaseActivity implements AlertDialog.CallPayType {
  private WebView mWebView;
  private LinearLayout mLayout;
  private String mUrl;
  private TextFont tvTitle;
  AlertDialog alertDialog;
  private static String INTENT_KEY_CASE_ID = "caseid";

  @Override protected void setContentView() {
    setContentView(R.layout.activity_brandcase);
  }

  @Override public void init(Bundle savedInstanceState) {
    mLayout = (LinearLayout) findViewById(R.id.web_layout);
    tvTitle = (TextFont) findViewById(R.id.tv_title);
    tvTitle.setText("评论详情");
    mWebView = new Html5WebView(activity);
    mWebView.addJavascriptInterface(new JavaScriptInterface(),
        "Android");
    String userid = new SpData(getApplicationContext()).getStringValue(SpData.keyId, null);
    LinearLayout.LayoutParams params =
        new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams
            .MATCH_PARENT);
    mWebView.setLayoutParams(params);
    mLayout.addView(mWebView);
    mUrl = Config.issueCaseDetaile.replace("{userid}", userid)
        .replace("{id}", getIntent().getStringExtra(INTENT_KEY_CASE_ID));
    Log.d(DoctorTalkFragment.class.getSimpleName(), mUrl);
    mWebView.loadUrl(mUrl);
  }

  @Override public void setListener() {

  }

  public static void getIntent(Context context, String id) {
    Intent intent = new Intent(context, CommentActivity.class);
    intent.putExtra(INTENT_KEY_CASE_ID, id);
    context.startActivity(intent);
  }

  public void backClick(View view) {
    finish();
  }

  public void onClick(View view) {
    showDialog();
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
        CommentActivity.this.finish();
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

  private final int REQUEST_CODE = 1;
  //微信支付
  public final static int WETCHAT_PAY = 2;
  //支付宝
  public final static int AILPAY = 3;
  public int PAY_TYPE;
  public static String KEY_INTENT_PAY_TYPE = "pay_type";

  @Override public void callback() {
    Intent intent = new Intent(this, PaySelectTyepActivity.class);
    intent.putExtra(KEY_INTENT_PAY_TYPE, PAY_TYPE);
    startActivityForResult(intent, REQUEST_CODE);
  }

  @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (data == null) {
      return;
    }
    if (resultCode == WETCHAT_PAY) {
      PAY_TYPE = WETCHAT_PAY;
      alertDialog.setMultiActionTextView(getString(R.string.select_pay_wetchat), 8, 10);
    }
    if (resultCode == AILPAY) {
      PAY_TYPE = AILPAY;
      alertDialog.setMultiActionTextView(getString(R.string.select_pay_ail), 9, 11);
    }
  }

  /**
   * 分享js调用的方法
   */
  public class JavaScriptInterface {
    @JavascriptInterface
    public void zoomImageIntent(String url) {
      Log.d(CommentActivity.class.getSimpleName(), url);
      RotationSampleActivity.getIntent(activity, url);
    }
  }

  public void showDialog() {
    alertDialog = new AlertDialog(CommentActivity.this).builder();
    alertDialog.setTitle("打赏");
    alertDialog.setMultiActionTextView(getString(R.string.select_pay_ail), 9, 11);
    alertDialog.setCallback(this);
    alertDialog.setPositiveButton(getString(R.string.text_custom_view_btn_positive),
        new View.OnClickListener() {
          @Override public void onClick(View v) {

          }
        });
    alertDialog.setNegativeButton(getString(R.string.text_custom_view_btn_negative)).show();
  }
}
