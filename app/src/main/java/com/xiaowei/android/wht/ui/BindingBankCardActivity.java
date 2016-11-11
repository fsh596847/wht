package com.xiaowei.android.wht.ui;

import com.alibaba.fastjson.JSON;
import com.xiaowei.android.wht.ApplicationTool;
import com.xiaowei.android.wht.R;
import com.xiaowei.android.wht.SpData;
import com.xiaowei.android.wht.beans.DoctorBankCardBindingInfo;
import com.xiaowei.android.wht.model.HttpResult;
import com.xiaowei.android.wht.service.DataService;
import com.xiaowei.android.wht.utils.mLog;
import com.xiaowei.android.wht.utis.HlpUtils;
import com.xiaowei.android.wht.utis.Utils;
import com.xiaowei.android.wht.views.SildingFinishLayout;
import com.xiaowei.android.wht.views.SildingFinishLayout.OnSildingFinishListener;

import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class BindingBankCardActivity extends Activity {
	
	private TextView tvBank,tvMobile,tv_;
	private EditText etName,etBankNumber,etBankBranch,etPwd,etPwd2,etCode;
	private Button btnGetCode,btnBinding;
	private RelativeLayout rlBank;
	
	private String bank;
	
	private String mobile;
	
	private DoctorBankCardBindingInfo info;
	
	private EventReceiver receiver;
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
						try {
							etCode.setText(content);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			}
		}
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE); 
		super.onCreate(savedInstanceState);
		ApplicationTool.getInstance().activitis.add(this);//把当前Activity放入集合中
		SpData sp = new SpData(getApplicationContext());
		mobile = sp.getStringValue(SpData.keyPhoneUser, null);
		/*setContentView(R.layout.activity_bank_card_binding);
		
		initViews();
		
		initListeners();*/
		
		queryBoundCard();
		
		receiver = new EventReceiver();
		registerReceiver(receiver, new IntentFilter("android.provider.Telephony.SMS_RECEIVED")); 
	}

	private void initListeners() {
		//返回
		findViewById(R.id.iv_bank_card_binding_back).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				finish();
				overridePendingTransition(0, R.anim.out_right);
			}
		});
		
		//开户行
		rlBank.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				startActivityForResult(new Intent(BindingBankCardActivity.this, BankSelectActivity.class)
				, BankSelectActivity.RESULTCODE_BankSelectActivity);
			}
		});
		
		//获取验证码
		btnGetCode.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(ifCanGetCode){
					
					getCode(mobile);
				}
			}
		});
		
		//绑定
		btnBinding.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(!isAddBoundCardING){
					
					String bankNum = etBankNumber.getText().toString().trim();
					String name = etName.getText().toString().trim();
					String bankBranch = etBankBranch.getText().toString().trim();
					String pwd = etPwd.getText().toString().trim();
					String pwd2 = etPwd2.getText().toString().trim();
					String code = etCode.getText().toString().trim();
					if(bankNum.isEmpty()){
						Toast.makeText(getApplicationContext(), "请输入银行卡（卡号）", Toast.LENGTH_SHORT).show();
						return;
					}
					else if(name.isEmpty()){
						Toast.makeText(getApplicationContext(), "请输入持卡人（开户人姓名）", Toast.LENGTH_SHORT).show();
						return;
					}
					else if(bank == null){
						Toast.makeText(getApplicationContext(), "请设置开户行（银行名称）", Toast.LENGTH_SHORT).show();
						return;
					}
					else if(bankBranch.isEmpty()){
						Toast.makeText(getApplicationContext(), "请输入开户网点（如：xxx支行）", Toast.LENGTH_SHORT).show();
						return;
					}
					else if(pwd.isEmpty() || pwd.length() != 6){
						Toast.makeText(getApplicationContext(), "请输入6位交易密码", Toast.LENGTH_SHORT).show();
						return;
					}
					else if(pwd2.isEmpty() || !pwd2.equals(pwd)){
						Toast.makeText(getApplicationContext(), "交易密码两次输入不一致", Toast.LENGTH_SHORT).show();
						return;
					}
					else if(code.isEmpty()){
						Toast.makeText(getApplicationContext(), "请输入验证码", Toast.LENGTH_SHORT).show();
						return;
					}
					else if(!isBindingBank){
						//新加
						addBoundCard(bankNum, name, bank, bankBranch, mobile, pwd, pwd2, code);
					}
					else if(isBindingBank){
						//修改
						updateBoundCard(bankNum, name, bank, bankBranch, mobile, pwd, code, info.getBoundcardid());
					}
				}
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
		tvBank = (TextView) findViewById(R.id.tv_bank_card_binding_bank);
		tvMobile = (TextView) findViewById(R.id.et_bank_card_binding_phone);
		tvMobile.setText(mobile);
		etName = (EditText) findViewById(R.id.et_bank_card_binding_name);
		etBankNumber = (EditText) findViewById(R.id.et_bank_card_binding_number);
		etBankBranch = (EditText) findViewById(R.id.et_bank_card_binding_branch);
		etPwd = (EditText) findViewById(R.id.et_bank_card_binding_pwd);
		etPwd2 = (EditText) findViewById(R.id.et_bank_card_binding_pwd2);
		etCode = (EditText) findViewById(R.id.et_bank_card_binding_code);
		btnGetCode = (Button) findViewById(R.id.btn_bank_card_binding);
		btnBinding = (Button)findViewById(R.id.btn_bank_card_binding_submit);
		rlBank = (RelativeLayout) findViewById(R.id.rl_bank_card_binding_bank);
		tv_ = (TextView) findViewById(R.id.tv_bank_card_binding_);
		
		//右滑退出
		SildingFinishLayout mSildingFinishLayout = (SildingFinishLayout) findViewById(R.id.sildingFinishLayout_bank_card_binding); 
		mSildingFinishLayout.setOnSildingFinishListener(new OnSildingFinishListener() {

			@Override
			public void onSildingFinish() {
				BindingBankCardActivity.this.finish();
			}
		});
		// touchView
		mSildingFinishLayout.setTouchView(mSildingFinishLayout);
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
		unregisterReceiver(receiver);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (resultCode) {
		case BankSelectActivity.RESULTCODE_BankSelectActivity:
			bank = data.getStringExtra("result");
			if(bank != null){
				tvBank.setText(bank);
			}
			break;
		}
	}
	
	private void queryBoundCard(){
		reload("正在努力加载……");
		new Thread(new Runnable() {

			@Override
			public void run() {

				try {
					String s = DataService.queryBoundCard(getApplicationContext(), mobile
							, new SpData(getApplicationContext()).getStringValue(SpData.keyId, null));
					mLog.d("http", "s:"+s);
					if (!isDestroy && !HlpUtils.isEmpty(s)){
						final HttpResult hr = JSON.parseObject(s,HttpResult.class);
						if (hr != null){
							runOnUiThread(new Runnable(){  
								@Override  
								public void run() {  
									setContentView(R.layout.activity_bank_card_binding);
									
									initViews();
									
									initListeners();
								}  
							});
							if  (hr.isSuccess()){
								info = JSON.parseArray(hr.getData().toString(), DoctorBankCardBindingInfo.class).get(0);
								if(info != null){
									final String bankNum = info.getAccountcode();
									final String name = info.getAccountname();
									final String bank = info.getBankname();
									final String bankBranch = info.getZbankname();
									final String moblie = info.getMobile();
									isBindingBank = true;
									runOnUiThread(new Runnable(){  
										@Override  
										public void run() { 
											
											if(bankNum != null){
												etBankNumber.setHint("****"+bankNum);
												etBankNumber.setEnabled(false);
											}
											
											if(name != null){
												etName.setHint("*"+name.substring(name.length()-1));
												etName.setEnabled(false);
											}
											
											if(bank != null){
												tvBank.setText(bank);
												rlBank.setEnabled(false);
											}
											
											if(bankBranch != null){
												etBankBranch.setText(bankBranch);
												etBankBranch.setEnabled(false);
											}
											
											if(moblie != null){
												tvMobile.setText(moblie);
											}
											
											//btnBinding.setText("修改绑定");
											btnBinding.setVisibility(View.INVISIBLE);
											RelativeLayout rlPwd = (RelativeLayout) findViewById(R.id.rl_bank_card_binding_pwd);
											RelativeLayout rlPwd2 = (RelativeLayout) findViewById(R.id.rl_bank_card_binding_pwd2);
											LinearLayout llCode = (LinearLayout) findViewById(R.id.ll_bank_card_binding_code);
											rlPwd.setVisibility(View.GONE);
											rlPwd2.setVisibility(View.GONE);
											llCode.setVisibility(View.GONE);
											tv_.setVisibility(View.INVISIBLE);
										}  
									});
								}
							}else{
								runOnUiThread(new Runnable(){  
									@Override  
									public void run() {  
										//Toast.makeText(getApplicationContext(), hr.getData().toString(), Toast.LENGTH_SHORT).show();
									}  
								});
							}
						}else{
							
						}
					}else{
						finish();
					}
				}catch (Exception he) {
					he.printStackTrace();
				}
				closeLoadingDialog();
			}
		}).start();
	}
	private boolean isBindingBank = false;
	
	private void updateBoundCard(final String bankNum, final String name, final String bank, final String bankBranch
			,final String mobile, final String password, final String code, final String boundcardid){
		isAddBoundCardING = true;
		reload("正在努力加载……");
		new Thread(new Runnable() {

			@Override
			public void run() {

				try {
					String s = DataService.updateBoundCard(getApplicationContext(), 2, name, bankNum, bank
							, bankBranch, mobile, password, code, boundcardid);
					//mLog.d("http", "s:"+s);
					if (!isDestroy && !HlpUtils.isEmpty(s)){
						final HttpResult hr = JSON.parseObject(s,HttpResult.class);
						if (hr != null){
							if  (hr.isSuccess()){
									runOnUiThread(new Runnable(){  
										@Override  
										public void run() {  
											Toast.makeText(getApplicationContext(), "修改绑定银行卡成功", Toast.LENGTH_SHORT).show();
											finish();
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
					}
				}catch (Exception he) {
					he.printStackTrace();
				}
				closeLoadingDialog();
				isAddBoundCardING = false;
			}
		}).start();
	}
	
	private void addBoundCard(final String bankNum, final String name, final String bank, final String bankBranch
			,final String mobile, final String password, final String confirmsafepwd, final String code){
		isAddBoundCardING = true;
		reload("正在努力加载……");
		new Thread(new Runnable() {

			@Override
			public void run() {

				try {
					String s = DataService.addBoundCard(getApplicationContext(), 2, name, bankNum, bank
							, bankBranch, mobile, password, confirmsafepwd, code);
					//mLog.d("http", "s:"+s);
					if (!isDestroy && !HlpUtils.isEmpty(s)){
						final HttpResult hr = JSON.parseObject(s,HttpResult.class);
						if (hr != null){
							if  (hr.isSuccess()){
								SpData sp = new SpData(getApplicationContext());
								sp.setStringValue(SpData.keyCode, code);
								runOnUiThread(new Runnable(){  
									@Override  
									public void run() {  
										Toast.makeText(getApplicationContext(), "绑定银行卡成功", Toast.LENGTH_SHORT).show();
										finish();
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
					}
				}catch (Exception he) {
					he.printStackTrace();
				}
				closeLoadingDialog();
				isAddBoundCardING = false;
			}
		}).start();
	}
	private boolean isAddBoundCardING = false;
	
	/**
	 * 获取后台图片信息
	 */
	private void getCode(final String mobile){
		reload("正在努力加载……");
		new Thread(new Runnable() {

			@Override
			public void run() {

				try {
					String s = DataService.getCode(BindingBankCardActivity.this, mobile);
					//mLog.d("http", "s:"+s);
					if (!isDestroy && !HlpUtils.isEmpty(s)){
						final HttpResult hr = JSON.parseObject(s,HttpResult.class);
						if (hr != null){
							if  (hr.isSuccess()){
									runOnUiThread(new Runnable(){  
										@Override  
										public void run() {  
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

}
