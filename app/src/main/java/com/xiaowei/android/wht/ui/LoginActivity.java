package com.xiaowei.android.wht.ui;

import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Dialog;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.alibaba.fastjson.JSON;
import com.xiaowei.android.wht.ApplicationTool;
import com.xiaowei.android.wht.R;
import com.xiaowei.android.wht.SpData;
import com.xiaowei.android.wht.model.HttpResult;
import com.xiaowei.android.wht.model.UserInfo;
import com.xiaowei.android.wht.service.DataService;
import com.xiaowei.android.wht.uibase.BaseNoTitleBarActivity;
import com.xiaowei.android.wht.utis.HlpUtils;
import com.xiaowei.android.wht.utis.Utils;

public class LoginActivity extends BaseNoTitleBarActivity implements View.OnClickListener{
	String phone, password;
//	HttpResult hr;
	Dialog progressDialog;
	EditText phoneEt, passwordEt;
	Button getVerifyNo_button,ok_button;
	ImageView back_button;
	String meetingId;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		ApplicationTool.getInstance().activitis.add(this);//把当前Activity放入集合中
		phoneEt = (EditText) findViewById(R.id.phone_et);
		passwordEt = (EditText) findViewById(R.id.password_et);
		getVerifyNo_button = (Button) findViewById(R.id.getVerifyNo_button);
		ok_button = (Button) findViewById(R.id.ok_button);
		getVerifyNo_button.setOnClickListener(this);
		ok_button.setOnClickListener(this);
		back_button = (ImageView) findViewById(R.id.back_button);
		back_button.setOnClickListener(this);
		meetingId = getIntent().getStringExtra("meetingId");
	}

	private void login(View v) {
		phone = phoneEt.getText().toString();
		password = passwordEt.getText().toString();
		if (password.equals("") || phone.equals("")) {
			Utils.showToast(this, "手机号、验证码都不能为空");
			return;
		}
		if (phone.length() != 11) {
			Utils.showToast(this, "请输入正确的手机号");
			return;
		}
		if (progressDialog != null) {
			progressDialog.dismiss();
		}
		ApplicationTool.getInstance().loginUserId = phone;
		ApplicationTool.getInstance().LoginPassword = password;
		progressDialog = Utils.createLoadingDialog(LoginActivity.this, "正在登录");
		progressDialog.show();
		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					SpData sp = new SpData(getApplicationContext());
					sp.setStringValue(SpData.keyPhoneUser, phone);
					sp.setStringValue(SpData.keyPassword, password);
					String s = DataService.regUser(getApplicationContext(), phone, password);
					if (!HlpUtils.isEmpty(s)){
						HttpResult hr = JSON.parseObject(s,HttpResult.class);
						if (hr != null){
							if  (hr.isSuccess()){
								UserInfo ui = JSON.parseObject(hr.getData().toString(),UserInfo.class);
								ApplicationTool.getInstance().setUserInfo(ui);
								ApplicationTool.getInstance().hasLogined = true;
								sp.setStringValue(SpData.keyUserInfo, hr.getData().toString());
								handler.sendEmptyMessage(msgRegOK);
							}else{
								handler.obtainMessage(msgRegFail,hr.getData()).sendToTarget();
							}
						}else{
							handler.obtainMessage(msgRegFail,"无法登录").sendToTarget();
						}
					}else{
						handler.obtainMessage(msgRegFail,"无法登录").sendToTarget();
					}
				}catch (Exception he) {
					handler.obtainMessage(msgRegFail,"无法登录").sendToTarget();
					he.printStackTrace();
				}

			}
		}).start();

	}

	final int msgRegOK = 1007, msgRegFail = 1201;
	final int msgVerifyOK = 1001, msgVerifyFail = 1002;
	final public static int msgDisplaySecond = 1003;
	Handler handler = new Handler() {

		public void dispatchMessage(android.os.Message msg) {
			switch (msg.what) {
			case msgVerifyOK:
				progressDialog.dismiss();
				sendDate = (new Date()).getTime();
				startCount();
				readSms();
				HlpUtils.showToast(getApplicationContext(), "获取验证码成功，请留意您的手机短信");
				break;
			case msgVerifyFail:
				getVerifyNo_button.setEnabled(true);
				progressDialog.dismiss();
				String httpresultF1 = (String)msg.obj;
				Utils.showToast(LoginActivity.this, httpresultF1);
				break;
			case msgRegOK:
				progressDialog.dismiss();
				getIntent().putExtra("meetingId", meetingId);
				setResult(RESULT_OK, getIntent());
				finish();
				break;
			case msgRegFail:
				progressDialog.dismiss();
				String httpresultF = (String) msg.obj;
				Utils.showToast(LoginActivity.this, httpresultF);
				break;
			case msgDisplaySecond:
				getVerifyNo_button.setText(theCout+ "S");
				if (theCout==0){
					getVerifyNo_button.setEnabled(true);
					getVerifyNo_button.setText("重新获取验证码");
				}
			default:
				break;
			}
		};
	};

	int theCout = 60;
	protected void startCount() {
		theCout = 60;
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				while (true){
					handler.sendEmptyMessage(msgDisplaySecond);
					if (theCout==0){
						break;
					}
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					theCout--;
				}
			}
		}).start();
	}
	protected void readSms() {
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				int i=0;
				while (i<600){
					boolean ret = getSmsFromPhone();
					if (ret){
						break;
					}else{
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					i++;
				}
			}
		}).start();
	}

	private void getVifityNo(View v) {
		phone = phoneEt.getText().toString();
		if (phone.length() != 11) {
			Utils.showToast(this, "请输入正确的手机号");
			return;
		}
		getVerifyNo_button.setEnabled(false);
		if (progressDialog != null) {
			progressDialog.dismiss();
		}
		progressDialog = Utils.createLoadingDialog(LoginActivity.this, "正在获取验证码");
		progressDialog.show();
		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					String s = DataService.getVefiyNo(getApplicationContext(), phone);
					if (!HlpUtils.isEmpty(s)){
						HttpResult hr = JSON.parseObject(s,HttpResult.class);
						if (hr != null){
							if  (hr.isSuccess()){
								handler.sendEmptyMessage(msgVerifyOK);
							}else{
								handler.obtainMessage(msgVerifyFail,hr.getData()).sendToTarget();
							}
						}else{
							handler.obtainMessage(msgVerifyFail,"获取验证码失败").sendToTarget();
						}
					}else{
						handler.obtainMessage(msgVerifyFail,"获取验证码失败").sendToTarget();
					}
				}catch (Exception he) {
					handler.obtainMessage(msgVerifyFail,"获取验证码失败").sendToTarget();
					he.printStackTrace();
				}

			}
		}).start();

	}

	public void finish(View v) {
		LoginActivity.this.finish();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		ApplicationTool.getInstance().activitis.remove(this);//把当前Activity移出集合中
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.getVerifyNo_button:
			getVifityNo(view);
			break;
		case R.id.ok_button:
			login(view);
			break;
		case R.id.back_button:
			finish();
			break;
		default:
			break;
		}
	}
	private Uri SMS_INBOX = Uri.parse("content://sms/");
	long sendDate = 0;
	private boolean getSmsFromPhone() {
		ContentResolver cr = getContentResolver();
		String[] projection = new String[] { "body" ,"_id", "address", "person", "date", "type"};//
		String where = " body like '%医生公会%' and date> "+sendDate;
		Cursor cur = cr.query(SMS_INBOX, projection, where, null, "date desc");
		if (null == cur)
			return false;
		if (cur.moveToNext()) {
//			String number = cur.getString(cur.getColumnIndex("address"));//手机号
//			String name = cur.getString(cur.getColumnIndex("person"));//联系人姓名列表
			String body = cur.getString(cur.getColumnIndex("body"));
			//这里我是要获取自己短信服务号码中的验证码~~
			Pattern pattern = Pattern.compile("[0-9]{4}");
			final Matcher matcher = pattern.matcher(body);
			if (matcher.find()){
				runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						 if ( passwordEt.getText().toString().trim().length() == 0) {
							String res = matcher.group();//.substring(1, 5);
							passwordEt.setText(res);
						 }
					}
				});
				return true;
			}
		}
		return false;
	}

}
