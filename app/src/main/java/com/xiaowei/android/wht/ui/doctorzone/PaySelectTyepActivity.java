package com.xiaowei.android.wht.ui.doctorzone;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;
import com.alibaba.fastjson.JSON;
import com.alipay.sdk.app.PayTask;
import com.alipaysdk.PayResult;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.xiaowei.android.wht.Config;
import com.xiaowei.android.wht.R;
import com.xiaowei.android.wht.SpData;
import com.xiaowei.android.wht.model.HttpResult;
import com.xiaowei.android.wht.service.DataService;
import com.xiaowei.android.wht.ui.MeetingApply;
import com.xiaowei.android.wht.ui.enevnt.MessageEvent;
import com.xiaowei.android.wht.utils.mLog;
import com.xiaowei.android.wht.utis.HlpUtils;
import java.util.Map;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import static com.xiaowei.android.wht.ui.doctorzone.CommentActivity.AILPAY;
import static com.xiaowei.android.wht.ui.doctorzone.CommentActivity.WETCHAT_PAY;

/**
 * Created by HIPAA on 2016/12/7. 选择支付方式
 */

public class PaySelectTyepActivity extends BaseActivity {

  private RelativeLayout wechatRyt;
  private RelativeLayout alipayRyt;
  private ImageView wechatImage;
  private ImageView alipayImage;
  private int PAY_TYPE = 3;
  private EditText tvPrice;

  private String mUserid;
  private String mCaseId;
  private double mny;

  @Override protected void setContentView() {
    setContentView(R.layout.activity_payselect);
  }

  @Override public void init(Bundle savedInstanceState) {
    wechatRyt = (RelativeLayout) findViewById(R.id.wechat_div);
    alipayRyt = (RelativeLayout) findViewById(R.id.alipay_div);
    wechatImage = (ImageView) findViewById(R.id.wechatPicked_textView);
    alipayImage = (ImageView) findViewById(R.id.aliPicked_textView);
    tvPrice = (EditText) findViewById(R.id.price_textView);
    //tvPrice.setText(getIntent().getStringExtra(CommentActivity.KEY_INTENT_PAY_VALUE));
    mUserid = new SpData(getApplicationContext()).getStringValue(SpData.keyId, null);
    mCaseId = getIntent().getStringExtra("caseid");
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

  @Subscribe
  public void onMessageEvent(MessageEvent event) {
    Toast.makeText(PaySelectTyepActivity.this, "支付成功！", Toast.LENGTH_SHORT).show();
    finish();
    //EventBus.getDefault().post(new MessageEvent());
  }

  @Override public void setListener() {
    //PAY_TYPE = getIntent().getIntExtra(CommentActivity.KEY_INTENT_PAY_TYPE, 0);
    if (PAY_TYPE == WETCHAT_PAY) {
      wechatImage.setVisibility(View.VISIBLE);
      alipayImage.setVisibility(View.GONE);
    } else if (PAY_TYPE == AILPAY) {
      wechatImage.setVisibility(View.GONE);
      alipayImage.setVisibility(View.VISIBLE);
    }
  }

  public void wechatPayClick(View view) {
    wechatImage.setVisibility(View.VISIBLE);
    alipayImage.setVisibility(View.GONE);
    PAY_TYPE = WETCHAT_PAY;
  }

  public void alipayClick(View view) {
    wechatImage.setVisibility(View.GONE);
    alipayImage.setVisibility(View.VISIBLE);
    PAY_TYPE = AILPAY;
  }

  public void goPayClick(View view) {
    setIntentResult(PAY_TYPE);
  }

  private void setIntentResult(int resultcode) {
    if (PAY_TYPE == AILPAY) {

      mny = Double.parseDouble(tvPrice.getText().toString());
      if (mny > 0) {
        doAliPay();
      } else {
        Toast.makeText(PaySelectTyepActivity.this, "请输入有效金额", Toast.LENGTH_SHORT).show();
      }
    } else if (PAY_TYPE == WETCHAT_PAY) {
      mny = Double.parseDouble(tvPrice.getText().toString());
      if (mny > 0) {
        doWeixinPay();
      } else {
        Toast.makeText(PaySelectTyepActivity.this, "请输入有效金额", Toast.LENGTH_SHORT).show();
      }
    }
  }

  //========================================支付=========================================================
  final public static int MsgCodeXXX = 1001;
  final public static int msgQueryFail = 1002;
  final public static int msgAliPayOk = 1003;
  final public static int msgAlipayFail = 1004;
  ProgressDialog dialog = null;
  Handler handler = new Handler() {
    @Override
    public void dispatchMessage(Message msg) {
      HttpResult hr = null;

      if (dialog != null && dialog.isShowing()) {
        dialog.dismiss();
      }
      switch (msg.what) {
        case msgAliPayOk:
          PayResult payResult = new PayResult((String) msg.obj);

          // 支付宝返回此次支付结果及加签，建议对支付宝签名信息拿签约时支付宝提供的公钥做验签
          //String resultInfo = payResult.getResult();

          String resultStatus = payResult.getResultStatus();

          // 判断resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
          if (TextUtils.equals(resultStatus, "9000")) {
            //Toast.makeText(PayActivity.this, "支付成功", Toast.LENGTH_LONG).show();
            //WebBrowserActivity.payOk = true;
            setResult(MeetingApply.RESULTCODE_MeetingApply_ApplyOK);
            finish();
          } else {
            // 判断resultStatus 为非“9000”则代表可能支付失败
            // “8000”代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
            if (TextUtils.equals(resultStatus, "8000")) {
              Toast.makeText(PaySelectTyepActivity.this, "支付结果确认中",
                  Toast.LENGTH_LONG).show();
            } else if (TextUtils.equals(resultStatus, "4000")) {
              Toast.makeText(PaySelectTyepActivity.this, "为了方便您的支付【请安装支付宝客户端】",
                  Toast.LENGTH_LONG).show();
            } else {
              // 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
              Toast.makeText(PaySelectTyepActivity.this, payResult.getMemo(),
                  Toast.LENGTH_LONG).show();
            }
          }
          break;
        case msgQueryFail:
          hr = (HttpResult) msg.obj;
          if (hr != null) {
            HlpUtils.showToastLong(getApplicationContext(),
                hr.getData() == null ? "查询支付信息失败，请稍后再试" : hr.getData().toString());
          }
          break;
        case msgAlipayFail:
          hr = (HttpResult) msg.obj;
          if (hr != null) {
            HlpUtils.showToastLong(getApplicationContext(),
                hr.getData() == null ? "支付失败，请稍后再试" : hr.getData().toString());
          }
          break;
        default:
          break;
      }
      super.dispatchMessage(msg);
    }
  };

  private void doAliPay() {
    dialog = ProgressDialog.show(PaySelectTyepActivity.this, "提示信息", "正在调起支付宝支付...");
    new Thread(new Runnable() {

      @Override
      public void run() {
        //PayService ods = new PayService(getApplicationContext());
        int tryTimes = 0;
        while (tryTimes < 3) {
          try {
            //						HttpResult hr = ods.getAppAliPayInfo(userId, orderId, ""+mny, meetid);
            String s =
                DataService.getAliPayAreward(PaySelectTyepActivity.this, mUserid, mCaseId, mny);
            if (s != null && !s.isEmpty()) {
              final HttpResult hr = JSON.parseObject(s, HttpResult.class);
              if (hr != null && hr.isSuccess()) {
                //							final AlipayInfo ali = JSON.parseObject(hr.getData().toString(),AlipayInfo.class);
                final Map<String, Object> ali = JSON.parseObject(hr.getData().toString());
                if (ali != null) {
                  // 构造PayTask 对象
                  PayTask alipay = new PayTask(PaySelectTyepActivity.this);
                  //								String orderInfo;
                  try {
                    //{"status":1,"data":{"body":"74","private_key":"MIICeAIBADANBgkqhkiG9w0BAQEFAASCAmIwggJeAgEAAoGBAOxRRtbhLnA4xQu8Li2Nov0mrojNofx8aipDP6ewxbpks8ZOL5sNOTvd1Rh8Bp9mO08QGvxxps5vHN/5g5QTwdmH/HWDAIxOMJlxLLgOh6NN6fCWQ7avpHN+4uI/vxHsDx820v5+9xvoyBP5HVDxvBKkd3d9sBEWdRpPmgBMUHZXAgMBAAECgYEAkf9wvYanMwvPqP1cpiYQIUhJohkwz5Vp3axoYkiTECHw8z4QqUFVL8hQQ+00BjyZvbHcikQI4xbAhxtXEB/YrUUkOh1lti6wjhph1nMW2WqUxMxeXi8i6kgNT/8TmhIb/3IScsmm9pyHuwTjfwwtAMMkN69WXoYATMkx5UVKJeECQQD5luLocgd6lG0wTYBU6DqE51BCeWiRnmaDwi2D3G/cWK+zZ/y5duCJ5uRZ+j6a7Z0jXkBierKRPUQMrFPWVdCnAkEA8mMfySmXmJ7xionEczLR6poDxRrPA9wSViVKJk4mU+8nL/80p+5iaQNErB0/s0NY5KSuvbIA1aNBNpNroguy0QJBAKCs4Bqf/eyNzNUBr5H5hmK0vthsbEMCZCCCSubObDy/4yxtzyZePyLHv02ladQ3D6gLLwu2zWnutPYrojUjPZUCQQCeX2AUa0WvJiNCVvPyJUF9XTabYgHjTrb4bJL/Zk6qxvc+6Cw6kYrhfI4xO0c01QtPEb9PGPBspqfkhfxsu+IRAkANHuCk4fO0vCLaCR5NluMS9myAD7ddB/gsywsoJizBYzAvdb5PUW+Lc4jba6YmoeOKX/EW7b8yZ9CGsFggrIki","subject":"验证支付宝支付金额","sign_type":"RSA","notify_url":"http://liesheng.gicp.net:8080/wcar/alipay/phone_alipayPayNotify.action","out_trade_no":"Binding_ZFB_OD1440080240692_877","sign":null,"_input_charset":"utf-8","total_fee":"0.01","service":"mobile.securitypay.pay","partner":"2088021234709183","seller_id":"2088021234709183","payment_type":"1"}}

                    //									orderInfo = PayService.getAliPayOrderInfo(ali);
                    //									// 对订单做RSA 签名
                    //									String sign = aliSign(orderInfo,ali.getPrivate_key());
                    //									// 仅需对sign 做URL编码
                    //									sign = URLEncoder.encode(sign, "UTF-8");
                    //									String payInfo = orderInfo + "&sign=\"" + sign + "\"&"
                    //											+ getAliSignType();
                    String payInfo = (String) ali.get("payInfo");
                    // 调用支付接口，获取支付结果
                    String result = alipay.pay(payInfo, true);

                    Message msg = new Message();
                    msg.what = msgAliPayOk;
                    msg.obj = result;
                    handler.sendMessage(msg);
                    break;
                  } catch (Exception e1) {
                    e1.printStackTrace();
                    HttpResult hr2 = new HttpResult();
                    hr2.setData(e1.getMessage());
                    handler.obtainMessage(msgAlipayFail, hr2).sendToTarget();
                  }
                } else {
                  handler.obtainMessage(msgAlipayFail, hr).sendToTarget();
                }
              } else {
                handler.obtainMessage(msgAlipayFail, hr).sendToTarget();
              }
            } else {
              tryTimes = 3;
              handler.obtainMessage(msgAlipayFail, new HttpResult()).sendToTarget();
            }
          } catch (Exception e) {
            e.printStackTrace();
            mLog.e("http", "Exception:" + e.getMessage());
            HttpResult hr = new HttpResult();
            hr.setData((Config.debug ? e.getMessage() : ""));
            handler.obtainMessage(msgAlipayFail, hr).sendToTarget();
          }
          tryTimes++;
        }
      }
    }).start();
  }

  private void doWeixinPay() {
    /*final UserInfo ui = (UserInfo)((ApplicationTool)getApplication()).getUserInfo();
    if (ui == null){
			return;
		}*/
    dialog = ProgressDialog.show(PaySelectTyepActivity.this, "提示信息", "正在调起微信支付...");
    new Thread(new Runnable() {

      @Override
      public void run() {
        try {
          String s =
              DataService.getwetChatAreward(PaySelectTyepActivity.this, mUserid, mCaseId, mny);
          if (!TextUtils.isEmpty(s)) {
            final HttpResult hr = JSON.parseObject(s, HttpResult.class);
            if (hr != null && hr.isSuccess()) {
              runOnUiThread(new Runnable() {

                @Override
                public void run() {
                  final IWXAPI msgApi = WXAPIFactory.createWXAPI(PaySelectTyepActivity.this, null);
                  String s = hr.getData().toString();
                  s = s.replace("package", "packageValue");
                  PayReq req = JSON.parseObject(s, PayReq.class);
                  msgApi.registerApp(req.appId);
                  msgApi.sendReq(req);
                  if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                  }
                }
              });
            } else {
              handler.obtainMessage(msgQueryFail, hr).sendToTarget();
            }
          } else {
            runOnUiThread(new Runnable() {

              @Override
              public void run() {
                Toast.makeText(PaySelectTyepActivity.this, "接口返回数据异常", Toast.LENGTH_SHORT).show();
              }
            });
          }
        } catch (Exception e) {
          e.printStackTrace();
          HttpResult hr = new HttpResult();
          hr.setData((Config.debug ? e.getMessage() : ""));
          handler.obtainMessage(msgQueryFail, hr).sendToTarget();
        }
      }
    }).start();
  }
}
