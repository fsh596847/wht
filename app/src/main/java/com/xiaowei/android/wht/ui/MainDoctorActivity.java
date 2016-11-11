package com.xiaowei.android.wht.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.xiaowei.android.wht.ApplicationTool;
import com.xiaowei.android.wht.R;
import com.xiaowei.android.wht.SpData;
import com.xiaowei.android.wht.beans.RegisterInfo;
import com.xiaowei.android.wht.model.HttpResult;
import com.xiaowei.android.wht.service.DataService;
import com.xiaowei.android.wht.ui.FragmentMy.CallBack;
import com.xiaowei.android.wht.utils.mLog;
import com.xiaowei.android.wht.utis.HlpUtils;
import com.xiaowei.android.wht.utis.Utils;

public class MainDoctorActivity extends Activity implements OnClickListener {
	
	private FragmentHomepage fHomepage;
	
	private FragmentFind fFind;
	
	private FragmentMy fMy;
	
	private Button btnHomepage,btnFind,btnMy;
	
	private TextView tvMy;
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (resultCode) {
		case MeetingApply.RESULTCODE_MeetingApply_ApplyOK:
			onClick(btnMy);
			break;

		}
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_doctor);
		ApplicationTool.getInstance().activitis.add(this);//把当前Activity放入集合中
		btnHomepage = (Button) findViewById(R.id.btn_main_homepage);
		btnFind = (Button) findViewById(R.id.btn_main_find);
		btnMy = (Button) findViewById(R.id.btn_main_my);
		tvMy = (TextView) findViewById(R.id.tv_main_my);
		
		initFragment();
		
		btnHomepage.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				fHomepage.getImageInfo();
				isMy = false;
				setFragment(fHomepage);
				clearTabView();
				Drawable drawable;
				drawable = getResources().getDrawable(R.drawable.tab_main_zyb);
				drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight()); 
				btnHomepage.setCompoundDrawables(null, drawable, null, null);
				btnHomepage.setTextColor(Color.parseColor("#199BFC"));
			}
		});
		
		btnFind.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				isMy = false;
				setFragment(fFind);
				clearTabView();
				Drawable drawable;
				drawable = getResources().getDrawable(R.drawable.tab_main_fxb);
				drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight()); 
				btnFind.setCompoundDrawables(null, drawable, null, null);
				btnFind.setTextColor(Color.parseColor("#199BFC"));
			}
		});

		btnMy.setOnClickListener(this);
		
		fMy.setCallBack(new CallBack() {
			
			@Override
			public void finish() {
				
				MainDoctorActivity.this.finish();
			}
		});
		
		SpData spData = new SpData(MainDoctorActivity.this);
		mobile = spData.getStringValue(SpData.keyPhoneUser, null);
		String id = spData.getStringValue(SpData.keyId, null);
		if(mobile != null && id != null){
			queryDoctorNoAuditA(mobile, id);
		}
	}
	String mobile;
	boolean isMy = false;
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_main_my:
			SpData spData = new SpData(getApplicationContext());
			String mobile = spData.getStringValue(SpData.keyPhoneUser, null);
			String id = spData.getStringValue(SpData.keyId, null);
			//没登录需登录，登录可直接进入
			if(/*Util.isRegisterSucced(getApplicationContext()) && */mobile != null && id != null){
				isMy = true;
				setFragment(fMy);
				clearTabView();
				Drawable drawable;
				drawable = getResources().getDrawable(R.drawable.tab_main_wdb);
				drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight()); 
				btnMy.setCompoundDrawables(null, drawable, null, null);
				btnMy.setTextColor(Color.parseColor("#199BFC"));
			}
			else{
				/*if(mobile != null && id != null){
					queryDoctorNoAudit(mobile, id);
				}
				else{*/
					startActivity(new Intent(MainDoctorActivity.this, RegisterActivity.class));
				/*}*/
			}
			break;

		}
		
	}
	
	void clearTabView(){
		Drawable drawable;
		drawable = getResources().getDrawable(R.drawable.tab_main_zyg);
		drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight()); 
		btnHomepage.setCompoundDrawables(null, drawable, null, null);
		btnHomepage.setTextColor(Color.parseColor("#666666"));
		drawable = getResources().getDrawable(R.drawable.tab_main_fxg);
		drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight()); 
		btnFind.setCompoundDrawables(null, drawable, null, null);
		btnFind.setTextColor(Color.parseColor("#666666"));
		drawable = getResources().getDrawable(R.drawable.tab_main_wdg);
		drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight()); 
		btnMy.setCompoundDrawables(null, drawable, null, null);
		btnMy.setTextColor(Color.parseColor("#666666"));
	}
	
	
	private void setFragment(Fragment setFragment)  
    {  
        FragmentManager fm = getFragmentManager();  
        FragmentTransaction transaction = fm.beginTransaction();  
        //transaction.replace(R.id.frameLayout_main, setFragment);  
        hideFfragment(transaction);//隐藏所有的
        transaction.show(setFragment);//显示要显示的
        transaction.commit();  
    }
	
	/**
	 * 隐藏加载的多个fragment
	 * @param transaction
	 */
	@SuppressLint("NewApi")
	void hideFfragment(FragmentTransaction transaction){
		
		if(transaction != null){
			
			if(fHomepage != null){
	        	transaction.hide(fHomepage);
	        }

	        if(fFind != null){
	        	transaction.hide(fFind);
	        }

	        if(fMy != null){
	        	transaction.hide(fMy);
	        }

		}
	}
	
	/**
	 * 初始化fragment
	 */
	@SuppressLint("NewApi")
	void initFragment(){
		FragmentManager fm = getFragmentManager();  
        FragmentTransaction transaction = fm.beginTransaction(); 
        
        if(fHomepage == null){
        	fHomepage = new FragmentHomepage();
        	transaction.add(R.id.frameLayout_main,fHomepage);
        }

        if(fFind == null){
        	fFind = new FragmentFind();
        	transaction.add(R.id.frameLayout_main,fFind);
        }

        if(fMy == null){
        	fMy = new FragmentMy();
        	transaction.add(R.id.frameLayout_main,fMy);
        }

        hideFfragment(transaction);
        transaction.show(fHomepage);
		transaction.commit();  
		
	}
	
	private void queryDoctorNoAuditA(final String mobile, final String id){
		reload("正在努力加载……");
		new Thread(new Runnable() {

			@Override
			public void run() {
				int state = 0;
				try {
					String s = DataService.queryDoctorNoAudit(MainDoctorActivity.this, mobile, id);
					mLog.d("http", "s:"+s);
					if (!HlpUtils.isEmpty(s)){
						final HttpResult hr = JSON.parseObject(s,HttpResult.class);
						if (hr != null){
							if  (hr.isSuccess()){
								state = RegisterInfo.getInstance().getApprovestate();
								mLog.e("http", "KeyApprovestate  MainDoctorActivity:"+state);
							}
						}
					}
				}catch (Exception he) {
					he.printStackTrace();
				}
				closeLoadingDialog();
				/*new SpData(getApplicationContext())
				.setIntValue(SpData.KeyApprovestate, state);*/
			}
		}).start();
	}
	
	
	private void queryNotReadNoctice(final String mobile){
		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					String s = DataService.queryNotReadNoctice(MainDoctorActivity.this, mobile, "DOCTOR");
					mLog.d("http", "s:"+s);
					if (!HlpUtils.isEmpty(s)){
						final HttpResult hr = JSON.parseObject(s,HttpResult.class);
						if (hr != null){
								final int totalRows = hr.getTotalRows();
								if(totalRows > 0){
									runOnUiThread(new Runnable(){  
										@Override  
										public void run() {  
											tvMy.setVisibility(View.VISIBLE);
											tvMy.setText(totalRows > 99 ? "99+" : totalRows+"");
										}  
									});
								}else{
									runOnUiThread(new Runnable(){  
										@Override  
										public void run() {  
											tvMy.setVisibility(View.GONE);
										}  
									});
								}
						}
					}
				}catch (Exception he) {
					he.printStackTrace();
				}
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
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		ApplicationTool.getInstance().activitis.remove(this);//把当前Activity移出集合中
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		if(mobile != null){
			queryNotReadNoctice(mobile);
		}
		if(isMy){
			fMy.queryNotReadNoctice();
		}
	}

}
