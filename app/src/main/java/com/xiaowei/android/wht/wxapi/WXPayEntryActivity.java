package com.xiaowei.android.wht.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;
import com.tencent.mm.sdk.constants.ConstantsAPI;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.xiaowei.android.wht.R;
import com.xiaowei.android.wht.ui.WebBrowserActivity;
import com.xiaowei.android.wht.ui.enevnt.MessageEvent;
import com.xiaowei.android.wht.utis.HlpUtils;
import org.greenrobot.eventbus.EventBus;

public class WXPayEntryActivity extends Activity implements IWXAPIEventHandler {

  private static final String TAG = "MicroMsg.SDKSample.WXPayEntryActivity";

  private IWXAPI api;
  TextView tip_textView;

  public static final String ACTION_PAY_SUCCESS =
      "com.xiaowei.android.WXPayEntryActivity.ACTION_PAY_SUCCESS";

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.pay_result);
    tip_textView = (TextView) findViewById(R.id.tip_textView);
    api = WXAPIFactory.createWXAPI(this, Constants.APP_ID);
    api.handleIntent(getIntent(), this);
  }

  @Override
  public void onStart() {
    super.onStart();
    EventBus.getDefault().register(this);
  }

  @Override
  public void onStop() {
    EventBus.getDefault().unregister(this);
    super.onStop();
  }

  @Override
  protected void onNewIntent(Intent intent) {
    super.onNewIntent(intent);
    setIntent(intent);
    api.handleIntent(intent, this);
  }

  @Override
  public void onReq(BaseReq req) {
  }

  Handler handler = new Handler() {
    public void handleMessage(android.os.Message msg) {

    }
  };

  @Override
  public void onResp(BaseResp resp) {
    Log.d(TAG, "onPayFinish, errCode = " + resp.errCode);
    //wx20160624074128b3e96532a70728586787
    if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
      if (resp.errCode == 0) {
        Toast.makeText(WXPayEntryActivity.this, "支付成功！正在返回应用App", Toast.LENGTH_SHORT).show();
        //HlpUtils.showToastLong(getApplicationContext(), "微信支付成功");
        WebBrowserActivity.payOk = true;//设置以便显示支付成功的结果
        sendBroadcast(new Intent(ACTION_PAY_SUCCESS));
        finish();
        EventBus.getDefault().post(new MessageEvent("Hello everyone!"));
        //handler.postDelayed(new Runnable() {
        //
        //  @Override
        //  public void run() {
        //
        //  }
        //}, 1000);
      } else if (resp.errCode == -2) {
        tip_textView.setText(
            "支付失败：\n" + resp.errCode + "\n" + (resp.errStr == null ? "" : resp.errStr) + "\n");
        if (!HlpUtils.isEmpty(resp.errStr)) {
          HlpUtils.showToastLong(getApplicationContext(), resp.errStr);
        }
        finish();
      } else {
        tip_textView.setText(
            "支付失败：\n" + resp.errCode + "\n" + (resp.errStr == null ? "" : resp.errStr));
        if (!HlpUtils.isEmpty(resp.errStr)) {
          HlpUtils.showToastLong(getApplicationContext(), resp.errStr);
        }
      }
    }
  }
}