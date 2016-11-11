package com.xiaowei.android.wht.ui;

import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.xiaowei.android.wht.ApplicationTool;
import com.xiaowei.android.wht.R;
import com.xiaowei.android.wht.SpData;
import com.xiaowei.android.wht.beans.RegisterInfo;
import com.xiaowei.android.wht.beans.User;
import com.xiaowei.android.wht.model.HttpResult;
import com.xiaowei.android.wht.service.DataService;
import com.xiaowei.android.wht.ui.patient.PatientMainActivity;
import com.xiaowei.android.wht.utils.mLog;
import com.xiaowei.android.wht.utis.HlpUtils;
import com.xiaowei.android.wht.utis.Utils;

public class RegisterActivity extends Activity {
	
	private EditText etMobile;
	private EditText etCode;
	private Button btnGetCode;
	
	private int clientType;
	public static final int clientTypeDoctor = 0;
	public static final int clientTypePatient = 1;
	
	public static final int resultCodeRegisterSucces = 10000;
	
	//private EventReceiver receiver;
	public class EventReceiver extends BroadcastReceiver {

	    @Override
	    public void onReceive(Context context, Intent intent) {
	    	Object [] pdus= (Object[]) intent.getExtras().get("pdus");
		     for(Object pdu:pdus){
		    	SmsMessage smsMessage=SmsMessage.createFromPdu((byte [])pdu);
		    	//String sender=smsMessage.getDisplayOriginatingAddress();
		    	String content=smsMessage.getMessageBody();
		    	//如果短信来自10690365737609129173,获取验证码
		    	if(content.contains("华佗来了")){
		    		
		    		if(content.contains("：")){
		    			content = content.substring(content.indexOf("：")+1, content.indexOf("：")+5);
		    			etCode.setText(content);
		    		}
		    	}
		     }
	    }
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);
		ApplicationTool.getInstance().activitis.add(this);//把当前Activity放入集合中
		clientType = getIntent().getIntExtra("type", clientTypeDoctor);
		
		initViews();
		
		initListeners();
		
		//receiver = new EventReceiver();
		//registerReceiver(receiver, new IntentFilter("android.provider.Telephony.SMS_RECEIVED")); 
	}

	private void initListeners() {
		//返回
//		findViewById(R.id.iv_register_back).setOnClickListener(new OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
//				
//				finish();
//			}
//		});
		
		//确定
		findViewById(R.id.btn_register_ok).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				String mcode = etCode.getText().toString().trim();
				String mobile = etMobile.getText().toString().trim();
				if(!Utils.checkPhone(mobile)){
					Toast.makeText(getApplicationContext(), "请输入正确的11位手机号码", Toast.LENGTH_SHORT).show();
				}
				else if(mcode == null || mcode.length() != 4){
					Toast.makeText(getApplicationContext(), "请输入正确的4位验证码", Toast.LENGTH_SHORT).show();
				}
				else{
					register(mcode, mobile);
				}
			}
		});
		
		//获取验证码
		btnGetCode.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				if(ifCanGetCode){
					
					String mobile = etMobile.getText().toString().trim();
					if(Utils.checkPhone(mobile)){
						getCode(mobile);
					}else{
						Toast.makeText(getApplicationContext(), "请输入正确的11位手机号码", Toast.LENGTH_SHORT).show();
					}
				}
			}
		});
		
	}

	private void initViews() {
		etMobile = (EditText) findViewById(R.id.et_register_phone);
		etCode = (EditText) findViewById(R.id.et_register_code);
		btnGetCode = (Button) findViewById(R.id.btn_register_code);
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
		//unregisterReceiver(receiver);
	}
	
	/*private void queryDoctorNoAudit(final String mobile, final String id){

				try {
					String s = DataService.queryDoctorNoAudit(RegisterActivity.this, mobile, id);
					//mLog.d("http", "s:"+s);
					if (!HlpUtils.isEmpty(s)){
						final HttpResult hr = JSON.parseObject(s,HttpResult.class);
						if (hr != null){
							if  (hr.isSuccess()){
								RegisterInfo.setInstance(JSON.parseObject(hr.getData().toString(), RegisterInfo.class));
								new SpData(getApplicationContext())
								.setIntValue(SpData.KeyApprovestate, RegisterInfo.getInstance().getApprovestate());
								if(RegisterInfo.getInstance().getApprovestate() == 1){
									finish();
								}
								else{
									startActivity(new Intent(RegisterActivity.this, RegisterInfoActivity.class));
									finish();
								}
							}else{
								runOnUiThread(new Runnable(){  
									@Override  
									public void run() {  
										Toast.makeText(getApplicationContext(), hr.getData().toString(), Toast.LENGTH_SHORT).show();
									}  
								});
							}
						}else{
							runOnUiThread(new Runnable(){  
								@Override  
								public void run() {  
									Toast.makeText(getApplicationContext(), "请求失败，请重试！", Toast.LENGTH_SHORT).show();
								}  
							});
						}
					}else{
						runOnUiThread(new Runnable(){  
							@Override  
							public void run() {  
								Toast.makeText(getApplicationContext(), "请检查网络后重试！", Toast.LENGTH_SHORT).show();
							}  
						});
					}
				}catch (Exception he) {
					he.printStackTrace();
				}
	}*/
	
	private void register(final String mcode, final String mobile){
		if(isRegisterING){
			return;
		}
		isRegisterING = true;
		reload( "正在努力加载……");
		new Thread(new Runnable() {

			@Override
			public void run() {

				try {
					String s = DataService.getRegister(RegisterActivity.this,""+clientType,mcode,mobile);
					mLog.d("http", "s:"+s);
					if (!HlpUtils.isEmpty(s)){
						final HttpResult hr = JSON.parseObject(s,HttpResult.class);
						if (hr != null){
							if  (hr.isSuccess()){
								//启动信鸽推送
								ApplicationTool.getInstance().startPushService(mobile);
								User user = JSON.parseObject(hr.getData().toString(), User.class);
								SpData sp = new SpData(getApplicationContext());
								sp.setStringValue(SpData.keyCode, mcode);
								sp.setStringValue(SpData.keyId, user.getId());
								sp.setStringValue(SpData.KeyClientType, ""+clientType);
								queryDoctorNoAudit(mobile, user.getId());
								setResult(resultCodeRegisterSucces);
								switch (clientType) {
								case clientTypeDoctor:
									//进入医生端
									sp.setStringValue(SpData.keyPhoneUser, mobile);
									startActivity(new Intent(RegisterActivity.this, MainDoctorActivity.class));
									break;

								case clientTypePatient:
									//进入患者端
									sp.setStringValue(SpData.keyPhoneUser, mobile);
									startActivity(new Intent(RegisterActivity.this, PatientMainActivity.class));
									break;
								}
								finish();
								
								/*if(!isDestroy && user.getId() != null && user.getMobile() != null){
									queryDoctorNoAudit(user.getMobile(), user.getId());
								}*/
							}else{
								new SpData(getApplicationContext()).setIntValue(SpData.KeyApprovestate, 0);
								runOnUiThread(new Runnable(){  
									@Override  
									public void run() {  
										Toast.makeText(getApplicationContext(), hr.getData().toString(), Toast.LENGTH_SHORT).show();
									}  
								});
							}
						}else{
							new SpData(getApplicationContext()).setIntValue(SpData.KeyApprovestate, 0);
							runOnUiThread(new Runnable(){  
								@Override  
								public void run() {  
									Toast.makeText(getApplicationContext(), "请求失败，请重试！", Toast.LENGTH_SHORT).show();
								}  
							});
						}
					}else{
						new SpData(getApplicationContext()).setIntValue(SpData.KeyApprovestate, 0);
						runOnUiThread(new Runnable(){  
							@Override  
							public void run() {  
								Toast.makeText(getApplicationContext(), "请检查网络后重试！", Toast.LENGTH_SHORT).show();
							}  
						});
					}
				}catch (Exception he) {
					new SpData(getApplicationContext()).setIntValue(SpData.KeyApprovestate, 0);
					he.printStackTrace();
				}
				closeLoadingDialog();
				isRegisterING = false;
			}
		}).start();
	}
	boolean isRegisterING = false;
	
	private void queryDoctorNoAudit(final String mobile, final String id){

		try {
			String s = DataService.queryDoctorNoAudit(RegisterActivity.this, mobile, id);
			mLog.d("http", "医生信息  s:"+s);
			if (!HlpUtils.isEmpty(s)){
				final HttpResult hr = JSON.parseObject(s,HttpResult.class);
				if (hr != null){
					if  (hr.isSuccess()){
						RegisterInfo info = JSON.parseObject(hr.getData().toString(), RegisterInfo.class);
						int a = info.getApprovestate();
						mLog.e("http", "KeyApprovestate  register:"+a);
						new SpData(getApplicationContext())
						.setIntValue(SpData.KeyApprovestate, a);
					}else{
						new SpData(getApplicationContext()).setIntValue(SpData.KeyApprovestate, 0);
					}
				}else{
					new SpData(getApplicationContext()).setIntValue(SpData.KeyApprovestate, 0);
				}
			}else{
				new SpData(getApplicationContext()).setIntValue(SpData.KeyApprovestate, 0);
			}
		}catch (Exception he) {
			mLog.e("http", "Exception:"+he.toString());
			he.printStackTrace();
		}
	}
	
	/**
	 * 获取后台图片信息
	 */
	private void getCode(final String mobile){
		if(isGetCodeING){
			return;
		}
		isGetCodeING = true;
		reload("正在努力加载……");
		new Thread(new Runnable() {

			@Override
			public void run() {

				try {
					String s = DataService.getCode(RegisterActivity.this, mobile);
					//mLog.d("http", "s:"+s);
					if (!HlpUtils.isEmpty(s)){
						final HttpResult hr = JSON.parseObject(s,HttpResult.class);
						if (hr != null){
							if  (hr.isSuccess()){
									runOnUiThread(new Runnable(){  
										@Override  
										public void run() {  
											readSms();
											Toast.makeText(getApplicationContext(), "获取验证码成功！", Toast.LENGTH_SHORT).show();
											getCodeUIThread();
										}  
									});
							}else{
								runOnUiThread(new Runnable(){  
									@Override  
									public void run() {  
										Toast.makeText(getApplicationContext(), hr.getData().toString(), Toast.LENGTH_SHORT).show();
									}  
								});
							}
						}else{
							runOnUiThread(new Runnable(){  
								@Override  
								public void run() {  
									Toast.makeText(getApplicationContext(), "请求失败，请重试！", Toast.LENGTH_SHORT).show();
								}  
							});
						}
					}else{
						runOnUiThread(new Runnable(){  
							@Override  
							public void run() {  
								Toast.makeText(getApplicationContext(), "请检查网络后重试！", Toast.LENGTH_SHORT).show();
							}  
						});
					}
				}catch (Exception he) {
					he.printStackTrace();
				}
				closeLoadingDialog();
				isGetCodeING = false;
			}
		}).start();
	}
	boolean isGetCodeING = false;
	
	/**
	 * 倒计时60秒，60秒内不允许获取验证码,按钮上显示倒计时
	 */
	private void getCodeUIThread(){
		new Thread() {  
            public void run() {  
                while(!isDestroy && --time>0){
                	
                	ifCanGetCode = false;
                	
                	runOnUiThread(new Runnable(){  
                        @Override  
                        public void run() {  
                        	btnGetCode.setText(""+time);  
                        }  
                    });
                	
                	try {
                		Thread.sleep(1000);
                	} catch (InterruptedException e) {
                		e.printStackTrace();
                	}
                }
                
                ifCanGetCode = true;
            	time = 60;
            	
            	runOnUiThread(new Runnable(){  
                    @Override  
                    public void run() {  
                    	btnGetCode.setText("获取验证码");
                    }  
                });
                
            }  
        }.start();
	}
	private boolean ifCanGetCode = true;
	int time = 60;
	
	private void reload(String text){
		if (loadingDialog == null){
			loadingDialog = Utils.createLoadingDialog(this, text);
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
	protected void readSms() {
		sendDate = new Date();
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

	private Uri SMS_INBOX = Uri.parse("content://sms/");
	Date sendDate = null;
	private boolean getSmsFromPhone() {
		ContentResolver cr = getContentResolver();
		String[] projection = new String[] { "body" ,"_id", "address", "person", "date", "type"};//
		String where = " body like '%华佗来了%' ";
		Cursor cur = cr.query(SMS_INBOX, projection, where, null, "date desc");
		if (null == cur)
			return false;
		if (cur.moveToNext()) {
//			String number = cur.getString(cur.getColumnIndex("address"));//手机号
			long dateStr = cur.getLong(cur.getColumnIndex("date"));
			Date date = new Date(dateStr);
			if (date.getTime()>sendDate.getTime()){
				String body = cur.getString(cur.getColumnIndex("body"));
				//这里我是要获取自己短信服务号码中的验证码~~
				Pattern pattern = Pattern.compile("[0-9]{4}");
				final Matcher matcher = pattern.matcher(body);
				if (matcher.find()){
					runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
							 if ( etCode.getText().toString().trim().length() == 0) {
								String res = matcher.group();//.substring(1, 5);
								etCode.setText(res);
							 }
						}
					});
					return true;
				}
			}
			
		}
		return false;
	}
	
	
}
