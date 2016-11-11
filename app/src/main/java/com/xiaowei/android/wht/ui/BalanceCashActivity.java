package com.xiaowei.android.wht.ui;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.xiaowei.android.wht.ApplicationTool;
import com.xiaowei.android.wht.R;
import com.xiaowei.android.wht.SpData;
import com.xiaowei.android.wht.beans.DoctorBankCardBindingInfo;
import com.xiaowei.android.wht.model.HttpResult;
import com.xiaowei.android.wht.service.DataService;
import com.xiaowei.android.wht.utils.mLog;
import com.xiaowei.android.wht.utils.mToast;
import com.xiaowei.android.wht.utis.HlpUtils;
import com.xiaowei.android.wht.utis.Utils;
import com.xiaowei.android.wht.views.SildingFinishLayout;
import com.xiaowei.android.wht.views.SildingFinishLayout.OnSildingFinishListener;

public class BalanceCashActivity extends Activity {
	
	private TextView tvBankNumber,tvName,tvBank,tvBankBranch,tvMobile,tvBalance;
	private EditText etMoney,etPwd;
	
	private String mobile;
	private String balance;
	private DoctorBankCardBindingInfo info;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE); 
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_balance_cash);
		ApplicationTool.getInstance().activitis.add(this);//把当前Activity放入集合中
		
		balance = getIntent().getStringExtra("money");
		SpData sp = new SpData(getApplicationContext());
		mobile = sp.getStringValue(SpData.keyPhoneUser, null);
		
		initViews();
		
		initListeners();
		
		queryBoundCard();
		
		//右滑退出
		SildingFinishLayout mSildingFinishLayout = (SildingFinishLayout) findViewById(R.id.sildingFinishLayout_balance_cash); 
		mSildingFinishLayout.setOnSildingFinishListener(new OnSildingFinishListener() {

			@Override
			public void onSildingFinish() {
				BalanceCashActivity.this.finish();
			}
		});
		// touchView
		mSildingFinishLayout.setTouchView(mSildingFinishLayout);
	}

	private void initListeners() {
		//返回
		findViewById(R.id.iv_balance_cash_back).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				finish();
				overridePendingTransition(0, R.anim.out_right);
			}
		});
		
		//提现
		findViewById(R.id.btn_balance_cash_submit).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				if(info != null){
					String money = etMoney.getText().toString().trim();
					String pwd = etPwd.getText().toString().trim();
					if(money.isEmpty() || Double.parseDouble(money)<=0){
						mToast.showToast(getApplicationContext(), "请输入提现金额");
						return;
					}
					else if(Double.parseDouble(money) > Double.parseDouble(balance)){
						mToast.showToast(getApplicationContext(), "余额不足");
					}
					else if(pwd.isEmpty() || pwd.length() != 6){
						mToast.showToast(getApplicationContext(), "请输入6位交易密码");
						return;
					}
					
					//提现
					cash(money, pwd, info);
				}else{
					queryBoundCard();
				}
			}
		});
		//忘记密码
		findViewById(R.id.btn_balance_cash_forgetpwd).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if(info != null && info.getBoundcardid() != null){
					
					startActivity(new Intent(BalanceCashActivity.this, CashPwdTestActivity.class)
					.putExtra("boundcardid", info.getBoundcardid())
					.putExtra("bank", tvBankNumber.getText().toString().trim())
					.putExtra("name", tvName.getText().toString().trim()));
					overridePendingTransition(R.anim.in_right,0);
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
		tvBankNumber = (TextView) findViewById(R.id.et_balance_cash_number);
		tvName = (TextView) findViewById(R.id.et_balance_cash_name);
		tvBank = (TextView) findViewById(R.id.tv_balance_cash_bank);
		tvBankBranch = (TextView) findViewById(R.id.et_balance_cash_branch);
		tvMobile = (TextView) findViewById(R.id.et_balance_cash_phone);
		tvBalance = (TextView) findViewById(R.id.et_balance_cash_balance);
		if(balance != null){
			tvBalance.setText(balance);
		}
		
		etMoney = (EditText) findViewById(R.id.et_balance_cash_money);
		etPwd = (EditText) findViewById(R.id.et_balance_cash_pwd);
	}
	
	/**
	 * 提现
	 * @param mny 提现金额
	 * @param password 交易密码
	 * @param banIinfo
	 */
	private void cash(final String mny, final String password, final DoctorBankCardBindingInfo banIinfo){
		reload("正在努力加载……");
		new Thread(new Runnable() {

			@Override
			public void run() {

				try {
					String s = DataService.addTakeMny(getApplicationContext(), 2, banIinfo.getAccountname(), banIinfo.getAccountcode()
							, banIinfo.getBankname(), balance, banIinfo.getZbankname(), mobile, password, banIinfo.getBoundcardid(),mny);
					mLog.d("http", "s:"+s);
					if (!isDestroy && !HlpUtils.isEmpty(s)){
						final HttpResult hr = JSON.parseObject(s,HttpResult.class);
						if (hr != null){
							if  (hr.isSuccess()){
								runOnUiThread(new Runnable(){  
									@Override  
									public void run() {  
										mToast.showToast(getApplicationContext(), "提现成功");
										setResult(100);
										finish();
										overridePendingTransition(0, R.anim.out_right);
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
	
	/**
	 * 查询银行卡信息
	 */
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
							if  (hr.isSuccess()){
								info = JSON.parseArray(hr.getData().toString(), DoctorBankCardBindingInfo.class).get(0);
								if(info != null){
									final String bankNum = info.getAccountcode();
									final String name = info.getAccountname();
									final String bank = info.getBankname();
									final String bankBranch = info.getZbankname();
									final String moblie = info.getMobile();
									runOnUiThread(new Runnable(){  
										@Override  
										public void run() { 
											
											if(bankNum != null){
												tvBankNumber.setText("****"+bankNum);
											}
											
											if(name != null){
												tvName.setText("*"+name.substring(name.length()-1));
											}
											
											if(bank != null){
												tvBank.setText(bank);
											}
											
											if(bankBranch != null){
												tvBankBranch.setText(bankBranch);
											}
											
											if(moblie != null){
												tvMobile.setText(moblie);
											}
										}  
									});
								}
							}else{
								runOnUiThread(new Runnable(){  
									@Override  
									public void run() {  
										mToast.showToast(getApplicationContext(), "请先绑定银行卡");
										finish();
									}  
								});
							}
						}else{
							
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
