package com.xiaowei.android.wht.ui;

import com.alibaba.fastjson.JSON;
import com.xiaowei.android.wht.ApplicationTool;
import com.xiaowei.android.wht.R;
import com.xiaowei.android.wht.SpData;
import com.xiaowei.android.wht.model.HttpResult;
import com.xiaowei.android.wht.service.DataService;
import com.xiaowei.android.wht.utils.mLog;
import com.xiaowei.android.wht.utils.mToast;
import com.xiaowei.android.wht.utis.HlpUtils;
import com.xiaowei.android.wht.utis.Utils;
import com.xiaowei.android.wht.views.SildingFinishLayout;
import com.xiaowei.android.wht.views.SildingFinishLayout.OnSildingFinishListener;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.EditText;

public class CashPwdTestActivity extends Activity {

	private EditText etCard,etName,etMobile;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE); 
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_cashpwd_change_test);
		ApplicationTool.getInstance().activitis.add(this);//把当前Activity放入集合中
		
		initViews();
		
		initListeners();
		
		//右滑退出
		SildingFinishLayout mSildingFinishLayout = (SildingFinishLayout) findViewById(R.id.sildingFinishLayout_cashpwd); 
		mSildingFinishLayout.setOnSildingFinishListener(new OnSildingFinishListener() {

			@Override
			public void onSildingFinish() {
				CashPwdTestActivity.this.finish();
			}
		});
		// touchView
		mSildingFinishLayout.setTouchView(mSildingFinishLayout);
	}

	private void initListeners() {
		//返回
		findViewById(R.id.iv_cashpwd_back).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
				overridePendingTransition(0, R.anim.out_right);
			}
		});
		//下一步
		findViewById(R.id.btn_cashpwd_next).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String card = etCard.getText().toString().trim();
				if(card.isEmpty()){
					mToast.showToast(getApplicationContext(), "请输入银行卡号");
					return;
				}
				String name = etName.getText().toString().trim();
				if(name.isEmpty()){
					mToast.showToast(getApplicationContext(), "请输入姓名");
					return;
				}
				String mobile = etMobile.getText().toString().trim();
				if(mobile.isEmpty()){
					mToast.showToast(getApplicationContext(), "请输入手机号");
					return;
				}
				checkBoundCard(mobile, name, card, getIntent().getStringExtra("boundcardid"));
			}
		});
		
	}
	
	// Press the back button in mobile phone
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		overridePendingTransition(0, R.anim.out_right);
	}

	private void initViews() {
		etCard = (EditText) findViewById(R.id.et_cashpwd_number);
		etName = (EditText) findViewById(R.id.et_cashpwd_name);
		etMobile = (EditText) findViewById(R.id.et_cashpwd_phone);
		
		String card = getIntent().getStringExtra("bank");
		String name = getIntent().getStringExtra("name");
		if(card != null){
			etCard.setHint(card);
		}
		if(name != null){
			etName.setHint(name);
		}
	}
	
	private void checkBoundCard(final String mobile,final String accountname, final String accountcode, final String boundcardid){
		reload("正在努力加载……");
		new Thread(new Runnable() {

			@Override
			public void run() {

				try {
					String s = DataService.checkBoundCard(getApplicationContext(), 2, accountname, accountcode
							, new SpData(getApplicationContext()).getStringValue(SpData.keyId, null)
							, mobile, boundcardid);
					mLog.d("http", "s："+s);
					if (!isDestroy && !HlpUtils.isEmpty(s)){
						final HttpResult hr = JSON.parseObject(s,HttpResult.class);
						if (hr != null){
							if  (hr.isSuccess()){
								runOnUiThread(new Runnable(){  
									@Override  
									public void run() {
										startActivity(new Intent(CashPwdTestActivity.this, CashPwdChangeActivity.class));
										finish();
									}  
								});
							}else{
								runOnUiThread(new Runnable(){  
									@Override  
									public void run() { 
										mToast.showToast(getApplicationContext(), hr.getData().toString());
									}  
								});
							}
						}else{
							runOnUiThread(new Runnable(){  
								@Override  
								public void run() { 
									mToast.showToast(getApplicationContext(), "请重试");
								}  
							});
						}
					}else{
						
					}
				}catch (Exception he) {
					he.printStackTrace();
				}

				closeLoadingDialog();
			}
		}).start();

	}
	
	private void reload(String text){
		if (loadingDialog == null){
			loadingDialog = Utils.createLoadingDialog(CashPwdTestActivity.this, text);
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
	
}
