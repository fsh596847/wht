package com.xiaowei.android.wht.ui.patient;

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
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.xiaowei.android.wht.ApplicationTool;
import com.xiaowei.android.wht.R;
import com.xiaowei.android.wht.SpData;
import com.xiaowei.android.wht.beans.RegisterInfo;
import com.xiaowei.android.wht.model.HttpResult;
import com.xiaowei.android.wht.service.DataService;
import com.xiaowei.android.wht.ui.RegisterActivity;
import com.xiaowei.android.wht.ui.RegisterInfoActivity;
import com.xiaowei.android.wht.utils.mLog;
import com.xiaowei.android.wht.utis.HlpUtils;
import com.xiaowei.android.wht.utis.Utils;

public class PatientMainActivity extends Activity {
	
	private FragmentPatientHomepage fHomepage;
	
	private FragmentPatientMsg fragMsg;
	
	private FragmentPatientMy fMy;
	
	private Button btnHomepage,btn_main_msg,btnMy;
	
	private TextView tvMy;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.patient_act_main);
		ApplicationTool.getInstance().activitis.add(this);//把当前Activity放入集合中
		btnHomepage = (Button) findViewById(R.id.btn_main_homepage);
		btn_main_msg = (Button) findViewById(R.id.btn_main_msg);
		btnMy = (Button) findViewById(R.id.btn_main_my);
		tvMy = (TextView) findViewById(R.id.tv_patient_main_my);
		
		fHomepage = new FragmentPatientHomepage();
		fragMsg = new FragmentPatientMsg();
		fMy = new FragmentPatientMy();
		setFragment(fHomepage);
		
		btnHomepage.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
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
		
		btn_main_msg.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				isMy = false;
				setFragment(fragMsg);
				clearTabView();
				Drawable drawable;
				drawable = getResources().getDrawable(R.drawable.tab_message_hightliang);
				drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight()); 
				btn_main_msg.setCompoundDrawables(null, drawable, null, null);
				btn_main_msg.setTextColor(Color.parseColor("#199BFC"));
			}
		});

		btnMy.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				SpData spData = new SpData(getApplicationContext());
				String mobile = spData.getStringValue(SpData.keyPhoneUser, null);
				String id = spData.getStringValue(SpData.keyId, null);
				if(!HlpUtils.isEmpty(mobile) && !HlpUtils.isEmpty(id)){
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
					if(mobile != null && id != null){
//						queryDoctorNoAudit(mobile, id);
					}
					else{
						Intent it = new Intent(PatientMainActivity.this, RegisterActivity.class);
						it.putExtra("type", RegisterActivity.clientTypePatient);
						startActivity(it);
					}
				}
			}
		});
		
	}
	boolean isMy = false;
	
	
	void clearTabView(){
		Drawable drawable;
		drawable = getResources().getDrawable(R.drawable.tab_main_zyg);
		drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight()); 
		btnHomepage.setCompoundDrawables(null, drawable, null, null);
		btnHomepage.setTextColor(Color.parseColor("#666666"));
		drawable = getResources().getDrawable(R.drawable.tab_message_normal);
		drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight()); 
		btn_main_msg.setCompoundDrawables(null, drawable, null, null);
		btn_main_msg.setTextColor(Color.parseColor("#666666"));
		drawable = getResources().getDrawable(R.drawable.tab_main_wdg);
		drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight()); 
		btnMy.setCompoundDrawables(null, drawable, null, null);
		btnMy.setTextColor(Color.parseColor("#666666"));
	}
	
	
	private void setFragment(Fragment setFragment)  
    {  
        FragmentManager fm = getFragmentManager();  
        FragmentTransaction transaction = fm.beginTransaction();  
        transaction.replace(R.id.frameLayout_main, setFragment);  
        transaction.commit();  
    }
	
	private void queryNotReadNoctice(){
		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					String s = DataService.queryNotReadNoctice(PatientMainActivity.this
							,new SpData(getApplicationContext()).getStringValue(SpData.keyPhoneUser, null)
							, "PATIENT");
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
	
	private void queryDoctorNoAudit(final String mobile, final String id){
		if(isQueryDoctorNoAuditING){
			return;
		}
		isQueryDoctorNoAuditING = true;
		reload("正在努力加载……");
		new Thread(new Runnable() {

			@Override
			public void run() {

				try {
					String s = DataService.queryDoctorNoAudit(PatientMainActivity.this, mobile, id);
					mLog.d("http", "mobile:"+mobile);
					mLog.d("http", "s:"+s);
					if (!HlpUtils.isEmpty(s)){
						final HttpResult hr = JSON.parseObject(s,HttpResult.class);
						if (hr != null){
							if  (hr.isSuccess()){
								RegisterInfo.setInstance(JSON.parseObject(hr.getData().toString(), RegisterInfo.class));
								new SpData(getApplicationContext())
								.setIntValue(SpData.KeyApprovestate, RegisterInfo.getInstance().getApprovestate());
								if(RegisterInfo.getInstance().getApprovestate() == 1){
									setFragment(fMy);
									clearTabView();
									Drawable drawable;
									drawable = getResources().getDrawable(R.drawable.tab_main_wdb);
									drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight()); 
									btnMy.setCompoundDrawables(null, drawable, null, null);
									btnMy.setTextColor(Color.parseColor("#199BFC"));
								}
								else{
									startActivity(new Intent(PatientMainActivity.this, RegisterInfoActivity.class));
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
				closeLoadingDialog();
				isQueryDoctorNoAuditING = false;
			}
		}).start();
	}
	boolean isQueryDoctorNoAuditING = false;
	
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
		queryNotReadNoctice();
		if(isMy){
			fMy.queryNotReadNoctice();
		}
	}

}
