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

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;

public class CashPwdChangeActivity extends Activity {
	
	private EditText etPwd,etPwd2;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_cashpwd_change);
		ApplicationTool.getInstance().activitis.add(this);//把当前Activity放入集合中
		
		initViews();
		
		initListeners();
	}

	private void initListeners() {
		//返回
		findViewById(R.id.iv_cashpwd_change_back).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		//提交
		findViewById(R.id.btn_cashpwd_change_ok).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String pwd = etPwd.getText().toString().trim();
				if(pwd.isEmpty() || pwd.length() != 6){
					mToast.showToast(getApplicationContext(), "请输入6位密码");
					return;
				}
				String pwd2 = etPwd2.getText().toString().trim();
				if(!pwd2.equals(pwd)){
					mToast.showToast(getApplicationContext(), "两次输入密码不一致");
					return;
				}
				updateSafePwd(pwd, pwd2);
			}
		});
		
	}

	private void initViews() {
		etPwd = (EditText) findViewById(R.id.et_cashpwd_change_pwd);
		etPwd2 = (EditText) findViewById(R.id.et_cashpwd_change_pwd2);
	}
	
	private void updateSafePwd(final String safepwd,final String confirmsafepwd){
		reload("正在努力加载……");
		new Thread(new Runnable() {

			@Override
			public void run() {

				try {
					String s = DataService.updateSafePwd(getApplicationContext(), safepwd
							, new SpData(getApplicationContext()).getStringValue(SpData.keyId, null)
							, new SpData(getApplicationContext()).getStringValue(SpData.keyPhoneUser, null), confirmsafepwd);
					mLog.d("http", "s："+s);
					if (!isDestroy && !HlpUtils.isEmpty(s)){
						final HttpResult hr = JSON.parseObject(s,HttpResult.class);
						if (hr != null){
							if  (hr.isSuccess()){
								runOnUiThread(new Runnable(){  
									@Override  
									public void run() {
										
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
			loadingDialog = Utils.createLoadingDialog(CashPwdChangeActivity.this, text);
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
