package com.xiaowei.android.wht.ui;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.xiaowei.android.wht.ApplicationTool;
import com.xiaowei.android.wht.R;
import com.xiaowei.android.wht.SpData;
import com.xiaowei.android.wht.beans.Patient;
import com.xiaowei.android.wht.model.HttpResult;
import com.xiaowei.android.wht.service.DataService;
import com.xiaowei.android.wht.utils.Util;
import com.xiaowei.android.wht.utils.mLog;
import com.xiaowei.android.wht.utils.mToast;
import com.xiaowei.android.wht.utis.HlpUtils;
import com.xiaowei.android.wht.utis.Utils;
import com.xiaowei.android.wht.views.ImgViewPagePopupWindow;
import com.xiaowei.android.wht.views.SildingFinishLayout;
import com.xiaowei.android.wht.views.SildingFinishLayout.OnSildingFinishListener;

public class PatientStateActivity extends Activity {


	private Patient patient;

	private ImageView imageOne,imageTwo,imageThree;
	
	
	public static final int resultCode_stateChange = 516;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE); 
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_patient_state);
		ApplicationTool.getInstance().activitis.add(this);//把当前Activity放入集合中
		Intent date = getIntent();
		patient = (Patient) date.getSerializableExtra("Patient");

		if(patient != null){
			initViews();//来自通知，查询到Patient数据！！！别忘记
		}
		else if(getIntent().getBooleanExtra("isComNotifi", false)){
			//来自通知，查询Patient数据
			if(date.getIntExtra("type", -1) == 2){
				queryDoctorAccept(date.getStringExtra("msgType"),date.getStringExtra("id"));
			}
			else{
				queryDoctorReferral(date.getStringExtra("msgType"),date.getStringExtra("id"));
			}
		}

		findViewById(R.id.iv_patient_state_back).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if(getIntent().getBooleanExtra("isComNotifi", false) 
						&& ApplicationTool.getInstance().activitis.size() == 1){
					//APP只启动当前页面，回退到主界面
					startActivity(new Intent(getApplication(),MainDoctorActivity.class));
				}
				finish();
				overridePendingTransition(0, R.anim.out_right);
			}
		});
		
		//右滑退出
		mSildingFinishLayout = (SildingFinishLayout) findViewById(R.id.sildingFinishLayout_patient_state); 
		mSildingFinishLayout.setOnSildingFinishListener(new OnSildingFinishListener() {

			@Override
			public void onSildingFinish() {
				PatientStateActivity.this.finish();
			}
		});
		// touchView
		mSildingFinishLayout.setTouchView(mSildingFinishLayout);

	}
	SildingFinishLayout mSildingFinishLayout;
	
	// Press the back button in mobile phone
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		overridePendingTransition(0, R.anim.out_right);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode==KeyEvent.KEYCODE_BACK){
			if(getIntent().getBooleanExtra("isComNotifi", false) 
					&& ApplicationTool.getInstance().activitis.size() == 1){
				//APP只启动当前页面，回退到主界面
				startActivity(new Intent(getApplication(),MainDoctorActivity.class));
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	private void initViews() {

		String name = patient.getPatientname();
		if(name != null){
			TextView tvName = (TextView) findViewById(R.id.tv_patient_state_name);
			tvName.setText(name);
		}

		TextView tvSex = (TextView) findViewById(R.id.tv_patient_state_sex);
		tvSex.setText(patient.getSex() == 0 ? "男" : "女");

		String address = patient.getAddress();
		if(address != null){
			TextView tvArea = (TextView) findViewById(R.id.tv_patient_state_area);
			tvArea.setText(address);
		}

		String brithday = patient.getBirthdate();
		if(brithday != null){
			TextView tvBrithday = (TextView) findViewById(R.id.tv_patient_state_brithday);
			tvBrithday.setText(brithday);
		}

		String describe = patient.getPatientdesc();
		if(describe != null){
			TextView tvDescribe = (TextView) findViewById(R.id.tv_patient_state_describe);
			tvDescribe.setText(describe);
		}

		String referdoctorname = patient.getReferdoctorname();
		if(referdoctorname != null){
			TextView tvReferdoctorname = (TextView) findViewById(R.id.tv_patient_state_referdoctorname);
			tvReferdoctorname.setText(referdoctorname);
		}

		LinearLayout llAcceptdoctorname = (LinearLayout) findViewById(R.id.ll_patient_state_acceptdoctorname);
		String acceptdoctorname = patient.getAcceptdoctorname();
		if(acceptdoctorname != null){
			TextView tvAcceptdoctorname = (TextView) findViewById(R.id.tv_patient_state_acceptdoctorname);
			tvAcceptdoctorname.setText(acceptdoctorname);
		}else{
			llAcceptdoctorname.setVisibility(View.GONE);
		}
		TextView tvState = (TextView) findViewById(R.id.tv_patient_state_state);
		ImageView ivState = (ImageView) findViewById(R.id.iv_patient_state_state);
		TextView tvTime = (TextView) findViewById(R.id.tv_patient_state_time);
		TextView tvTitle = (TextView) findViewById(R.id.tv_patient_state_title);
		switch (patient.getState()) {
		case 5:
		case 4:
			tvState.setText(patient.getStateStr());
			tvState.setTextColor(Color.parseColor("#f44e06"));
			ivState.setImageResource(R.drawable.watch);
			tvTitle.setText(patient.getStateStr());
			TextView tvAgain = (TextView) findViewById(R.id.tv_patient_state_re);
			if(getIntent().getIntExtra("type", -1) != 2) {
				tvAgain.setVisibility(View.VISIBLE);
				//转诊
				tvAgain.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						String referralid = patient.getReferralid();
						if(referralid != null){
							
							startActivityForResult(new Intent(PatientStateActivity.this, ExpertFindActivity.class)
							.putExtra("again", true).putExtra("referid", referralid), resultCode_stateChange);
						}
					}
				});
			}
			break;
			
		case 0:
			tvState.setText(patient.getStateStr());
			tvState.setTextColor(Color.parseColor("#f44e06"));
			ivState.setImageResource(R.drawable.watch);
			tvTitle.setText(patient.getStateStr());
			if(getIntent().getIntExtra("type", -1) == 2) {
				findViewById(R.id.ll_patient_state_state).setVisibility(View.GONE);
				findViewById(R.id.ll_patient_state_accept).setVisibility(View.VISIBLE);
				//接受
				findViewById(R.id.tv_patient_state_accept).setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						String referralid = patient.getReferralid();
						if(referralid != null){
							updateDoctorAccept(referralid,patient.getAcceptid());
						}else{
							mToast.showToast(getApplicationContext(), "网络故障，请退出重试");
						}
					}
				});
				//拒绝
				findViewById(R.id.tv_patient_state_refuse).setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						
						String referralid = patient.getReferralid();
						if(referralid != null){
							doctorRejec(referralid);
						}
					}
				});
				//转诊
				findViewById(R.id.tv_patient_state_again).setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						String referralid = patient.getReferralid();
						if(referralid != null){
							
							startActivityForResult(new Intent(PatientStateActivity.this, ExpertFindActivity.class)
							.putExtra("again", true).putExtra("referid", referralid), resultCode_stateChange);
						}
					}
				});
			}
			break;

		case 1:
			ivState.setImageResource(R.drawable.alarm_clock_s);
			tvState.setText(patient.getStateStr());
			tvTitle.setText(patient.getStateStr());
			break;

		case 2:
			ivState.setImageResource(R.drawable.alarm_clock_s);
			String booking = patient.getBooking();
			if(booking != null){
				tvTime.setText(booking);
			}
			tvTitle.setText(patient.getStateStr());
			if(getIntent().getIntExtra("type", -1) == 2) {
				TextView tvOK = (TextView) findViewById(R.id.tv_patient_state_ok);
				//tvOK.setVisibility(View.VISIBLE);
				//完成
				tvOK.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						
					}
				});
			}
			break;

		case 3:
			tvState.setText(patient.getStateStr());
			tvTitle.setText(patient.getStateStr());
			break;
		}

		bitmapArray = new ArrayList<Bitmap>();
		String imagePathOne = patient.getPatientimgone();
		if(imagePathOne != null){
			imageOne = (ImageView) findViewById(R.id.iv_patient_state_image1);
			imageOne.setVisibility(View.VISIBLE);
			getImage(imagePathOne, 1);
			imageOne.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					showImgPopupWindow(0);
				}
			});
		}

	}
	
	private void showImgPopupWindow(int position){
		imgViewPagePopupWindow = new ImgViewPagePopupWindow(PatientStateActivity.this, bitmapArray, position);
		imgViewPagePopupWindow.showAtLocation(mSildingFinishLayout, Gravity.CENTER, 0, 0);
	}
	private ImgViewPagePopupWindow imgViewPagePopupWindow;
	private List<Bitmap> bitmapArray;

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
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (resultCode) {
		case resultCode_stateChange:
			setResult(resultCode_stateChange);
			if(getIntent().getBooleanExtra("isComNotifi", false) 
					&& ApplicationTool.getInstance().activitis.size() == 1){
				//APP只启动当前页面，回退到主界面
				startActivity(new Intent(getApplication(),MainDoctorActivity.class));
			}
			finish();
			break;

		}
	}
	
	public void queryDoctorReferral(final String msgType, final String id){
		reload("正在努力加载……");
		new Thread(new Runnable() {

			@Override
			public void run() {

				try {
					SpData spData = new SpData(PatientStateActivity.this);
					String s = DataService.queryDoctorReferral(PatientStateActivity.this, -1, spData.getStringValue(SpData.keyId, null)
							, spData.getStringValue(SpData.keyPhoneUser, null), null,msgType,id,1,10);
					mLog.d("http", "s："+s);
					if (!isDestroy && !HlpUtils.isEmpty(s)){
						HttpResult hr = JSON.parseObject(s,HttpResult.class);
						if (hr != null){
							if  (hr.isSuccess()){
								List<Patient> listPatient = JSON.parseArray(hr.getData().toString(), Patient.class);
								if(listPatient != null && listPatient.size() > 0){
									
									patient = listPatient.get(0);
									runOnUiThread(new Runnable(){  
										@Override  
										public void run() { 
											initViews();
										}  
									});
								}
							}else{
								runOnUiThread(new Runnable(){  
									@Override  
									public void run() { 
										mToast.showToast(getApplicationContext(), "消息已过期");
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
	
	public void queryDoctorAccept(final String msgType, final String id){
		reload("正在努力加载……");
		new Thread(new Runnable() {

			@Override
			public void run() {

				try {
					SpData spData = new SpData(PatientStateActivity.this);
					String s = DataService.queryDoctorAccept(PatientStateActivity.this, -1, spData.getStringValue(SpData.keyId, null)
							, spData.getStringValue(SpData.keyPhoneUser, null), null,msgType,id,1,10);
					mLog.d("http", "s："+s);
					if (!isDestroy && !HlpUtils.isEmpty(s)){
						HttpResult hr = JSON.parseObject(s,HttpResult.class);
						if (hr != null){
							if  (hr.isSuccess()){
								List<Patient> listPatient = JSON.parseArray(hr.getData().toString(), Patient.class);
								if(listPatient != null && listPatient.size() > 0){
									
									patient = listPatient.get(0);
									runOnUiThread(new Runnable(){  
										@Override  
										public void run() { 
											initViews();
										}  
									});
								}
							}else{
								runOnUiThread(new Runnable(){  
									@Override  
									public void run() { 
										mToast.showToast(getApplicationContext(), "消息已过期");
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
	
	//拒绝
	private void doctorRejec(final String referralid){
		reload("正在努力加载……");
		new Thread(new Runnable() {

			@Override
			public void run() {

				try {
					String s = DataService.doctorRejec(getApplicationContext()
							, new SpData(getApplicationContext()).getStringValue(SpData.keyPhoneUser, null), referralid);
					mLog.d("http", "s："+s);
					if (!isDestroy && !HlpUtils.isEmpty(s)){
						HttpResult hr = JSON.parseObject(s,HttpResult.class);
						if (hr != null){
							if  (hr.isSuccess()){
								runOnUiThread(new Runnable(){  
									@Override  
									public void run() {
										setResult(resultCode_stateChange);
										finish();
									}  
								});
							}else{
								runOnUiThread(new Runnable(){  
									@Override  
									public void run() { 
										mToast.showToast(getApplicationContext(), "操作失败，请重试");
									}  
								});
							}
						}else{
							runOnUiThread(new Runnable(){  
								@Override  
								public void run() { 
									mToast.showToast(getApplicationContext(), "操作失败，请重试");
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
	
	//接诊
	private void updateDoctorAccept(final String referralid, final String acceptid){
		reload("正在努力加载……");
		new Thread(new Runnable() {

			@Override
			public void run() {

				try {
					String s = DataService.updateDoctorAccept(getApplicationContext(), referralid
							, new SpData(getApplicationContext()).getStringValue(SpData.keyPhoneUser, null)
							, acceptid);
					mLog.d("http", "s："+s);
					if (!isDestroy && !HlpUtils.isEmpty(s)){
						HttpResult hr = JSON.parseObject(s,HttpResult.class);
						if (hr != null){
							if  (hr.isSuccess()){
								runOnUiThread(new Runnable(){  
									@Override  
									public void run() {
										setResult(resultCode_stateChange);
										finish();
									}  
								});
							}else{
								runOnUiThread(new Runnable(){  
									@Override  
									public void run() { 
										mToast.showToast(getApplicationContext(), "操作失败，请重试");
									}  
								});
							}
						}else{
							runOnUiThread(new Runnable(){  
								@Override  
								public void run() { 
									mToast.showToast(getApplicationContext(), "操作失败，请重试");
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

	private void getImage(final String path, final int position){
		reload("正在努力加载……");
		new Thread(new Runnable() {

			@Override
			public void run() {

				byte[] data;
				try {

					data = DataService.getImage(path);
					if(data!=null && !isDestroy){  
						Bitmap mBitmap = BitmapFactory.decodeByteArray(data, 0, data.length);// bitmap  
						if(mBitmap != null){
							bitmapArray.add(mBitmap);
						}
						switch (position) {
						case 1:

							final Bitmap b1 = Util.rotaingImageView(mBitmap, 2, imageOne.getHeight());
							if(b1 != null){
								runOnUiThread(new Runnable(){  
									@Override  
									public void run() {  
										imageOne.setImageBitmap(b1);
									}  
								});
							}
							runOnUiThread(new Runnable(){  
								@Override  
								public void run() {  
									String imagePathTwo = patient.getPatientimgtwo();
									if(imagePathTwo != null){
										imageTwo = (ImageView) findViewById(R.id.iv_patient_state_image2);
										imageTwo.setVisibility(View.VISIBLE);
										getImage(imagePathTwo, 2);
										imageTwo.setOnClickListener(new OnClickListener() {
											
											@Override
											public void onClick(View v) {
												showImgPopupWindow(1);
											}
										});
									}
								}  
							});
							break;

						case 2:

							final Bitmap b2 = Util.rotaingImageView(mBitmap, 2, imageTwo.getHeight());
							if(b2 != null){
								runOnUiThread(new Runnable(){  
									@Override  
									public void run() {  
										imageTwo.setImageBitmap(b2);
									}  
								});
							}
							runOnUiThread(new Runnable(){  
								@Override  
								public void run() {  
									String imagePathThree = patient.getPatientimgthree();
									if(imagePathThree != null){
										imageThree = (ImageView) findViewById(R.id.iv_patient_state_image3);
										imageThree.setVisibility(View.VISIBLE);
										getImage(imagePathThree, 3);
										imageThree.setOnClickListener(new OnClickListener() {
											
											@Override
											public void onClick(View v) {
												showImgPopupWindow(2);
											}
										});
									}
								}  
							});
							break;

						case 3:

							final Bitmap b3 = Util.rotaingImageView(mBitmap, 2, imageThree.getHeight());
							if(b3 != null){
								runOnUiThread(new Runnable(){  
									@Override  
									public void run() {  
										imageThree.setImageBitmap(b3);
									}  
								});
							}
							break;
						}
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
			loadingDialog = Utils.createLoadingDialog(PatientStateActivity.this, text);
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
