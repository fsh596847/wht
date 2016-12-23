package com.xiaowei.android.wht.ui.doctorzone;

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
import com.xiaowei.android.wht.views.Html5WebView;
import com.xiaowei.android.wht.views.TextFont;

/**
 * Created by fsh on 2016/11/12.   病例详情
 */

public class CaseDetailActivity extends BaseActivity {
    private WebView mWebView;
    private LinearLayout mLayout;
    private String mUrl;
    private TextFont tvTitle;
    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_case);
    }

    @Override
    public void init(Bundle savedInstanceState) {
        mLayout = (LinearLayout) findViewById(R.id.web_layout);
        mWebView = new Html5WebView(activity);

        String userid = new SpData(getApplicationContext()).getStringValue(SpData.keyId, null);
        LinearLayout.LayoutParams params =
            new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams
                    .MATCH_PARENT);
        mWebView.setLayoutParams(params);
        mLayout.addView(mWebView);
        mWebView.addJavascriptInterface(new JavaScriptInterface(activity),
            "Android");
        mUrl = Config.shareCase.replace("{USID}", userid).replace("{TYPE}", "0");
        Log.d(DoctorTalkFragment.class.getSimpleName(), mUrl);
        mWebView.loadUrl(mUrl);
    }

    @Override
    public void setListener() {

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
                finish();
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
            Log.d(DoctorTalkFragment.class.getSimpleName(), "commentntent" + id);
            //CommentActivity.getIntent(mActivity, id);
            startActivity(CaseDetailActivity.class);
        }

        @JavascriptInterface
        public void doctorHeadInfo(String doctorid) {//评论  详情
            Log.d(DoctorTalkFragment.class.getSimpleName(), "doctorHeadInfo" + doctorid);
            DoctorHeadInfoActivity.CallIntent(activity, doctorid);
        }
    }
}
