package com.xiaowei.android.wht.ui;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXWebpageObject;
import com.xiaowei.android.wht.ApplicationTool;
import com.xiaowei.android.wht.R;
import com.xiaowei.android.wht.service.DataService;
import com.xiaowei.android.wht.utils.Util;
import com.xiaowei.android.wht.utils.mToast;
import com.xiaowei.android.wht.utis.Utils;
import com.xiaowei.android.wht.views.SharePopupwindow;
import com.xiaowei.android.wht.views.SildingFinishLayout;
import com.xiaowei.android.wht.views.SharePopupwindow.CallBack;
import com.xiaowei.android.wht.views.SildingFinishLayout.OnSildingFinishListener;

public class MyInviteActivity extends Activity {
	
	private ImageView imgCode;
	
	private LinearLayout viewParent;
	private SharePopupwindow popup;
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my_invite);
		ApplicationTool.getInstance().activitis.add(this);//把当前Activity放入集合中
		initViews();
		
		initListeners();
		
		//getImage(path);
		popup = new SharePopupwindow(this);
		popup.setOutsideTouchable(true);
		popup.setCallBack(new CallBack() {
			
			@Override
			public void group() {
				wxShare(1);
			}
			
			@Override
			public void friend() {
				wxShare(0);
			}
			
			@Override
			public void dismiss() {
			}
		});
	}
	
	// Press the back button in mobile phone
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		overridePendingTransition(0, R.anim.out_right);
	}
	
	/** 
	  * 微信分享 （这里仅提供一个分享网页的示例，其它请参看官网示例代码） 
	  * @param flag(0:分享到微信好友，1：分享到微信朋友圈) 
	  */  
	 private void wxShare(int flag){ 
		 if (!ApplicationTool.wxApi.isWXAppInstalled()) {
				//提醒用户没有按照微信
				mToast.showToast(this, "没有安装微信");
				return;
			}
		 ApplicationTool.isWxShare = true;
	     WXWebpageObject webpage = new WXWebpageObject();  
	     webpage.webpageUrl = "http://fir.im/4d53";  
	     WXMediaMessage msg = new WXMediaMessage(webpage);
		 msg.title = getResources().getString(R.string.app_name);
		 msg.description = "您的私人医生！";
	     //这里替换一张自己工程里的图片资源  
	     Bitmap thumb = BitmapFactory.decodeResource(getResources(), R.drawable.app_share);  
	     msg.setThumbImage(thumb);  
	       
	     SendMessageToWX.Req req = new SendMessageToWX.Req();  
	     req.transaction = String.valueOf(System.currentTimeMillis());  
	     req.message = msg;  
	     req.scene = flag==0?SendMessageToWX.Req.WXSceneSession:SendMessageToWX.Req.WXSceneTimeline;  
	     ApplicationTool.wxApi.sendReq(req); 
	 } 

	private void initListeners() {
		//返回
		findViewById(R.id.iv_my_invite_back).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				finish();
				overridePendingTransition(0, R.anim.out_right);
			}
		});
		//分享
		findViewById(R.id.rl_my_invite_share).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				popup.showAtLocation(viewParent, Gravity.BOTTOM, 0, 0);
			}
		});
		
	}

	private void initViews() {
		
		imgCode = (ImageView) findViewById(R.id.iv_my_invite_code);
		viewParent = (LinearLayout) findViewById(R.id.ll_my_invite);
		
		//右滑退出
		SildingFinishLayout mSildingFinishLayout = (SildingFinishLayout) findViewById(R.id.sildingFinishLayout_my_invite); 
		mSildingFinishLayout.setOnSildingFinishListener(new OnSildingFinishListener() {

			@Override
			public void onSildingFinish() {
				MyInviteActivity.this.finish();
			}
		});
		// touchView
		mSildingFinishLayout.setTouchView(viewParent);
	}
	
	boolean isDestroy = false;
	@Override
	public void onStart() {
		super.onStart();
		isDestroy = false;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		isDestroy = true;
		ApplicationTool.getInstance().activitis.remove(this);//把当前Activity移出集合中
	}
	
	private void getImage(final String path){
		reload("正在努力加载……");
		new Thread(new Runnable() {

			@Override
			public void run() {

				byte[] data;
				try {

					data = DataService.getImage(path);
					if(data!=null && !isDestroy){  
						Bitmap mBitmap = BitmapFactory.decodeByteArray(data, 0, data.length);// bitmap  
						int w = imgCode.getWidth();
						int h = imgCode.getHeight();
						int type = 1;
						int value = w;
						if(w > h){
							type = 2;
							value = h;
						}
						final Bitmap b = Util.rotaingImageView(mBitmap, type, value);
						runOnUiThread(new Runnable(){  
							@Override  
							public void run() {  
								imgCode.setImageBitmap(b);
							}  
						});
					}

				} catch (Exception e) {
					e.printStackTrace();
				}

				closeLoadingDialog();
			}
		}).start();

	}
	
	private void reload(String text){
		if (loadingDialog == null){
			loadingDialog = Utils.createLoadingDialog(MyInviteActivity.this, text);
		}
		if (!loadingDialog.isShowing()){
			loadingDialog.show();
		}
	}

	private Dialog loadingDialog = null;
	private void closeLoadingDialog() {
		if(null != loadingDialog) {	 
			loadingDialog.dismiss();
			loadingDialog = null;
		}
	}

}
