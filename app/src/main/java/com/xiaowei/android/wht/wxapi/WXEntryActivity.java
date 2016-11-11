package com.xiaowei.android.wht.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.xiaowei.android.wht.ApplicationTool;
import com.xiaowei.android.wht.ui.WebBrowserActivity;

public class WXEntryActivity extends Activity implements IWXAPIEventHandler {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ApplicationTool.wxApi.handleIntent(getIntent(), this);
	}

	@Override
	public void onReq(BaseReq req) {
		finish();
	}

	@Override
	public void onResp(BaseResp resp) {
		switch(resp.errCode) {
			case BaseResp.ErrCode.ERR_OK:
				if(ApplicationTool.isWxShare){
					//分享成功
				}
				else if(ApplicationTool.isWXLogin){
					SendAuth.Resp sendResp = (SendAuth.Resp) resp;
					WebBrowserActivity.WX_CODE = sendResp.code;
				}else{
//					Toast.makeText(this, "成功!", Toast.LENGTH_LONG).show();
				}
				break;
			case BaseResp.ErrCode.ERR_USER_CANCEL:
//				Toast.makeText(this, "取消!", Toast.LENGTH_LONG).show();
				break;
			case BaseResp.ErrCode.ERR_AUTH_DENIED:
//				Toast.makeText(this, "被拒绝", Toast.LENGTH_LONG).show();
				break;
			default:
				break;
		}
		ApplicationTool.isWxShare = false;
		ApplicationTool.isWXLogin=false;
		finish();
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
		ApplicationTool.wxApi.handleIntent(intent, this);
		finish();
	}
}
