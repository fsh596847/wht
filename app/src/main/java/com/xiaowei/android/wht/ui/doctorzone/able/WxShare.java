package com.xiaowei.android.wht.ui.doctorzone.able;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXWebpageObject;
import com.xiaowei.android.wht.ApplicationTool;
import com.xiaowei.android.wht.R;
import com.xiaowei.android.wht.utils.mToast;

/**
 * Created by HIPAA on 2016/11/17.
 */
public class WxShare implements IWxShare {

  private static IWxShare ourInstance;
  private Context context;

  private WxShare(Context context) {
    this.context = context;
  }

  public static IWxShare getInstance(Context context) {
    if (ourInstance == null) {
      synchronized (WxShare.class) {
        if (ourInstance == null) {
          ourInstance = new WxShare(context);
        }
      }
    }

    return ourInstance;
  }

  @Override public void wxShare(int flag) {
    if (!ApplicationTool.wxApi.isWXAppInstalled()) {
      //提醒用户没有按照微信
      mToast.showToast(context, "没有安装微信");
      return;
    }
    ApplicationTool.isWxShare = true;
    WXWebpageObject webpage = new WXWebpageObject();
    webpage.webpageUrl = "http://fir.im/4d53";
    WXMediaMessage msg = new WXMediaMessage(webpage);
    msg.title = "华佗来了";
    msg.description = "您的私人医生！";
    //这里替换一张自己工程里的图片资源
    Bitmap thumb = BitmapFactory.decodeResource(context.getResources(), R.drawable.app_share);
    msg.setThumbImage(thumb);

    SendMessageToWX.Req req = new SendMessageToWX.Req();
    req.transaction = String.valueOf(System.currentTimeMillis());
    req.message = msg;
    req.scene =
        flag == 0 ? SendMessageToWX.Req.WXSceneSession : SendMessageToWX.Req.WXSceneTimeline;
    ApplicationTool.wxApi.sendReq(req);
  }
}
