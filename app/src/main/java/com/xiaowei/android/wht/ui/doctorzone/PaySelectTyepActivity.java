package com.xiaowei.android.wht.ui.doctorzone;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import com.xiaowei.android.wht.R;

/**
 * Created by HIPAA on 2016/12/7. 选择支付方式
 */

public class PaySelectTyepActivity extends BaseActivity {

  private RelativeLayout wechatRyt;
  private RelativeLayout alipayRyt;
  private ImageView wechatImage;
  private ImageView alipayImage;
  private int PAY_TYPE;

  @Override protected void setContentView() {
    setContentView(R.layout.activity_payselect);
  }

  @Override public void init(Bundle savedInstanceState) {
    wechatRyt = (RelativeLayout) findViewById(R.id.wechat_div);
    alipayRyt = (RelativeLayout) findViewById(R.id.alipay_div);
    wechatImage = (ImageView) findViewById(R.id.wechatPicked_textView);
    alipayImage = (ImageView) findViewById(R.id.aliPicked_textView);
  }

  @Override public void setListener() {
    PAY_TYPE = getIntent().getIntExtra(CommentActivity.KEY_INTENT_PAY_TYPE, 0);
    if (PAY_TYPE == CommentActivity.WETCHAT_PAY) {
      wechatImage.setVisibility(View.VISIBLE);
      alipayImage.setVisibility(View.GONE);
    } else if (PAY_TYPE == CommentActivity.AILPAY) {
      wechatImage.setVisibility(View.GONE);
      alipayImage.setVisibility(View.VISIBLE);
    }
  }

  public void wechatPayClick(View view) {
    wechatImage.setVisibility(View.VISIBLE);
    alipayImage.setVisibility(View.GONE);
    PAY_TYPE = CommentActivity.WETCHAT_PAY;
  }

  public void alipayClick(View view) {
    wechatImage.setVisibility(View.GONE);
    alipayImage.setVisibility(View.VISIBLE);
    PAY_TYPE = CommentActivity.AILPAY;
  }

  public void goPayClick(View view) {
    setIntentResult(PAY_TYPE);
  }

  private void setIntentResult(int resultcode) {
    Intent intent = new Intent();
    setResult(resultcode, intent);
    finish();
  }
}
