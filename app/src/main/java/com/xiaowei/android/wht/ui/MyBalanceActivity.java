package com.xiaowei.android.wht.ui;

import com.alibaba.fastjson.JSON;
import com.xiaowei.android.wht.ApplicationTool;
import com.xiaowei.android.wht.R;
import com.xiaowei.android.wht.SpData;
import com.xiaowei.android.wht.beans.DoctorMoney;
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
import android.widget.TextView;
import android.widget.Toast;

public class MyBalanceActivity extends Activity {
	
	private TextView tvBalance;
	private TextView tvTakemny;
	private TextView tvAccountmny;
	private TextView tvTadymny;
	
	private DoctorMoney doctorMoney;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my_balance);
		ApplicationTool.getInstance().activitis.add(this);//把当前Activity放入集合中
		doctorMoney = (DoctorMoney) getIntent().getSerializableExtra("DoctorMoney");
		
		initViews();
		
		initListeners();
		
		//右滑退出
		SildingFinishLayout mSildingFinishLayout = (SildingFinishLayout) findViewById(R.id.sildingFinishLayout_my_balance); 
		mSildingFinishLayout.setOnSildingFinishListener(new OnSildingFinishListener() {

			@Override
			public void onSildingFinish() {
				MyBalanceActivity.this.finish();
			}
		});
		// touchView
		mSildingFinishLayout.setTouchView(mSildingFinishLayout);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		mLog.d("http", "onActivityResult  resultCode:"+resultCode);
		switch (resultCode) {
		case 100:
			queryUserMoney();
			setResult(100);
			break;
		}
	}
	
	private void initListeners() {
		//返回
		findViewById(R.id.iv_balance_my_back).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				finish();
				overridePendingTransition(0, R.anim.out_right);
			}
		});
		
		//余额提现
		findViewById(R.id.rl_balance_my_cash).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				if(doctorMoney != null){
					String accountmny = doctorMoney.getWithdrawmny();
					if(accountmny != null && Float.parseFloat(accountmny) >= 0.1){
						
						startActivityForResult(new Intent(MyBalanceActivity.this, BalanceCashActivity.class)
						.putExtra("money", accountmny),100);
						overridePendingTransition(R.anim.in_right,0);
					}else{
						mToast.showToast(getApplicationContext(), "没有可提现余额");
					}
				}
					
			}
		});
		
		//绑定银行卡
		findViewById(R.id.rl_balance_my_bank_card).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				startActivity(new Intent(MyBalanceActivity.this, BindingBankCardActivity.class));
				overridePendingTransition(R.anim.in_right,0);
			}
		});
		//余额明细
		tvBalance.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {

				startActivity(new Intent(MyBalanceActivity.this, BalanceDetailsActivity.class));
				overridePendingTransition(R.anim.in_right,0);
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
		tvBalance = (TextView) findViewById(R.id.tv_my_balance_balance);
		tvTakemny = (TextView) findViewById(R.id.tv_my_balance_takemny);
		tvAccountmny = (TextView) findViewById(R.id.tv_my_balance_accountmny);
		tvTadymny = (TextView) findViewById(R.id.tv_my_balance_tadaymny);
		
		if(doctorMoney != null){
			tvBalance.setText(doctorMoney.getAccountmny()==null ? "0元" : doctorMoney.getAccountmny()+"元");
			tvTakemny.setText(doctorMoney.getTakemny()==null ? "0元" : doctorMoney.getTakemny()+"元");
			tvAccountmny.setText(doctorMoney.getWithdrawmny()==null ? "0元" : doctorMoney.getWithdrawmny()+"元");
			tvTadymny.setText(doctorMoney.getTadymny()==null ? "0元" : doctorMoney.getTadymny()+"元");
		}
	}
	
	private void queryUserMoney() {
		reload("正在努力加载……");
		new Thread(new Runnable() {
			@Override
			public void run() {

				try {
					String s = DataService.queryUserMny(MyBalanceActivity.this
							, new SpData(MyBalanceActivity.this).getStringValue(SpData.keyId, null));
					mLog.d("http", "s:"+s);
					if (!HlpUtils.isEmpty(s)){
						final HttpResult hr = JSON.parseObject(s,HttpResult.class);
						if(hr != null){
							if(hr.isSuccess()){
								if(!isDestroy){
									doctorMoney = JSON.parseObject(hr.getData().toString(), DoctorMoney.class);
									runOnUiThread(new Runnable(){  
										@Override  
										public void run() { 
											if(doctorMoney != null){
												tvBalance.setText(doctorMoney.getAccountmny()==null ? "0元" : doctorMoney.getAccountmny()+"元");
												tvTakemny.setText(doctorMoney.getTakemny()==null ? "0元" : doctorMoney.getTakemny()+"元");
												tvAccountmny.setText(doctorMoney.getWithdrawmny()==null ? "0元" : doctorMoney.getWithdrawmny()+"元");
												tvTadymny.setText(doctorMoney.getTadymny()==null ? "0元" : doctorMoney.getTadymny()+"元");
											}
										}  
									});
								}
							}else{
								runOnUiThread(new Runnable(){  
									@Override  
									public void run() {  
										Toast.makeText(MyBalanceActivity.this, hr.getData().toString(), Toast.LENGTH_SHORT).show();
									}  
								});
							}
						}
					}else{
						runOnUiThread(new Runnable(){  
							@Override  
							public void run() {  
								Toast.makeText(MyBalanceActivity.this, "请检查网络后重试！", Toast.LENGTH_SHORT).show();
							}  
						});
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
			loadingDialog = Utils.createLoadingDialog(MyBalanceActivity.this, text);
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
	protected void onDestroy() {
		super.onDestroy();
		isDestroy = true;
		ApplicationTool.getInstance().activitis.remove(this);//把当前Activity移出集合中
	}
}
