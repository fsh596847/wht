package com.xiaowei.android.wht.ui.patient;

import java.util.List;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageContainer;
import com.android.volley.toolbox.ImageLoader.ImageListener;
import com.android.volley.toolbox.Volley;
import com.hillpool.LruImageCache;
import com.xiaowei.android.wht.ApplicationTool;
import com.xiaowei.android.wht.R;
import com.xiaowei.android.wht.beans.PatientReferral;
import com.xiaowei.android.wht.model.HttpResult;
import com.xiaowei.android.wht.service.DataService4Patient;
import com.xiaowei.android.wht.uibase.BaseNoTitleBarActivity;
import com.xiaowei.android.wht.utils.mLog;
import com.xiaowei.android.wht.utils.mToast;
import com.xiaowei.android.wht.utis.HlpUtils;
import com.xiaowei.android.wht.utis.Utils;
import com.xiaowei.android.wht.views.CircularImage;
import com.xiaowei.android.wht.views.SildingFinishLayout;
import com.xiaowei.android.wht.views.SildingFinishLayout.OnSildingFinishListener;

public class PatientMyReferralInfoActivity extends BaseNoTitleBarActivity implements View.OnClickListener  {
	ImageView back_imageView;
	PatientReferral patientReferral;
	TextView title_textView,doctorName_textView,sex_textView,hospital_textView,
	dept_textView,techTitle_textView,position_textView,city_textView,publish_textView,jobresult_textView,
	doctor2_textView,patientName_textView,booking_textView;
	CircularImage pic_imageView;

	RequestQueue mQueue = null;
	LruImageCache lruImageCache = null;
	ImageLoader imageLoader = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE); 
		super.onCreate(savedInstanceState);
		setContentView(R.layout.patient_act_my_referralinfo);
		ApplicationTool.getInstance().activitis.add(this);//把当前Activity放入集合中
		patientReferral = (PatientReferral)getIntent().getSerializableExtra("patientReferral");
		if(patientReferral != null){
			initView();//来自通知查询到PatientReferral！！！不要忘记
			initCache();//来自通知查询到PatientReferral！！！不要忘记
			displayInfo();//来自通知查询到PatientReferral！！！不要忘记
		}
		else if(getIntent().getBooleanExtra("isComNotifi", false)){
			//来自通知，网络查询PatientReferral数据
			Intent data = getIntent();
			queryData(data.getStringExtra("msgType"),data.getStringExtra("id"));
		}
		back_imageView = (ImageView)findViewById(R.id.back_imageView);
		back_imageView.setOnClickListener(this);
		
		//右滑退出
		SildingFinishLayout mSildingFinishLayout = (SildingFinishLayout) findViewById(R.id.sildingFinishLayout_patientMyReferralInfo); 
		mSildingFinishLayout.setOnSildingFinishListener(new OnSildingFinishListener() {

			@Override
			public void onSildingFinish() {
				PatientMyReferralInfoActivity.this.finish();
			}
		});
		// touchView
		ScrollView view = (ScrollView) findViewById(R.id.ScrollView_patientMyReferralInfo);
		mSildingFinishLayout.setTouchView(view);
	}
	private void initView() {
		title_textView= (TextView)findViewById(R.id.title_textView);
		doctorName_textView= (TextView)findViewById(R.id.doctorName_textView);
		sex_textView= (TextView)findViewById(R.id.sex_textView);
		hospital_textView= (TextView)findViewById(R.id.hospital_textView);
		dept_textView= (TextView)findViewById(R.id.dept_textView);
		techTitle_textView= (TextView)findViewById(R.id.techTitle_textView);
		position_textView= (TextView)findViewById(R.id.position_textView);
		city_textView= (TextView)findViewById(R.id.city_textView);
		publish_textView= (TextView)findViewById(R.id.publish_textView);
		jobresult_textView= (TextView)findViewById(R.id.jobresult_textView);
		doctor2_textView= (TextView)findViewById(R.id.doctor2_textView);
		patientName_textView= (TextView)findViewById(R.id.patientName_textView);
		pic_imageView = (CircularImage)findViewById(R.id.pic_imageView);
		booking_textView= (TextView)findViewById(R.id.booking_textView);
		booking_textView.setOnClickListener(this);
	}
	private void initCache() {

		mQueue = Volley.newRequestQueue(PatientMyReferralInfoActivity.this);
		lruImageCache = LruImageCache.instance();
		imageLoader = new ImageLoader(mQueue, lruImageCache);
		
	}
	private void displayInfo() {
		if (patientReferral != null){
			PatientReferral r = patientReferral;
			doctorName_textView.setText(r.getAcceptdoctorname());
			sex_textView.setText(r.getSexStr(r.getSex()));
			hospital_textView.setText(r.getHospital());
			dept_textView.setText(r.getDept());
			techTitle_textView.setText(r.getJobtitle());
			position_textView.setText(r.getDuty());
			city_textView.setText(r.getAddress());
			publish_textView.setText(r.getPaper());
			jobresult_textView.setText(r.getJobresults());
			doctor2_textView.setText(r.getReferdoctorname());
			patientName_textView.setText(r.getPatientname());
			if(r.getState() != PatientReferral.StateToBooking){
				booking_textView.setBackgroundColor(getResources().getColor(R.color.color_divider_gray));
				booking_textView.setEnabled(false);
				booking_textView.setText("已预约");
			}
			loadImage(r.getHeadimg(), pic_imageView);
		}
	}
	private void loadImage(String url,final ImageView iv){
		if(url == null){
			return;
		}
		ImageListener imageListener = new ImageListener() {
			
			@Override
			public void onErrorResponse(VolleyError arg0) {
			}
			
			@Override
			public void onResponse(ImageContainer arg0, boolean arg1) {
				Bitmap bmp = arg0.getBitmap();
				if (bmp != null){
					iv.setImageBitmap(bmp);
				}
			}
		};
		imageLoader.get(url, imageListener);
	}
	final int msgQueryOk = 1001;
	final int msgQueryFail = 1002;
	@SuppressLint("HandlerLeak")
	Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			default:
				break;
			}
		};
	};

	@Override
	protected void onStart() {
		super.onStart();
	}
	@Override
	protected void onPause() {
		super.onPause();
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
		ApplicationTool.getInstance().activitis.remove(this);//把当前Activity移出集合中
	}
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back_imageView:
			if(getIntent().getBooleanExtra("isComNotifi", false) 
					&& ApplicationTool.getInstance().activitis.size() == 1){
				//APP只启动当前页面，回退到主界面
				startActivity(new Intent(getApplication(),PatientMainActivity.class));
			}
			finish();
			overridePendingTransition(0, R.anim.out_right);
			break;
		case R.id.booking_textView:
			gotoBooking();
			break;
		default:
			break;
		}
	}
	
	// Press the back button in mobile phone
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		overridePendingTransition(0, R.anim.out_right);
	}
	
	private void gotoBooking() {
		Intent it = new Intent(PatientMyReferralInfoActivity.this,PatientMyReferralBookingActivity.class);
		it.putExtra("patientReferral", patientReferral);
		startActivityForResult(it, requestCodeBooking);
	}
	
	private void queryData(final String msgType, final String id) {
		reload("正在努力加载……");
		new Thread(new Runnable() {
			
			@Override
			public void run() {
//				String mobile = "13325468526";
				String mobile = DataService4Patient.getMyMobile(getApplicationContext());
				String s = null;
				try{
					s = DataService4Patient.queryPatientReferral(getApplicationContext(), null, mobile, null, null,msgType,id,1,10);
					mLog.d("http", "queryPatientReferral  s:"+s);
					if (!HlpUtils.isEmpty(s)){
						HttpResult hr = JSON.parseObject(s,HttpResult.class);
						if (hr != null && hr.isSuccess()){
							List<PatientReferral> list = JSON.parseArray(hr.getData().toString(),PatientReferral.class);
							if(list!=null && list.size()>0){
								
								patientReferral = list.get(0);
								if(patientReferral != null){
									runOnUiThread(new Runnable(){  
										@Override  
										public void run() { 
											initView();
											initCache();
											displayInfo();
										}  
									});
								}
							}
						}else{
							runOnUiThread(new Runnable(){  
								@Override  
								public void run() { 
									mToast.showToast(getApplicationContext(), "消息已过期");
								}  
							});
						}
					}
				}catch (Exception e){
					e.printStackTrace();
					handler.obtainMessage(msgQueryFail,e.getMessage()).sendToTarget();
				}
				closeLoadingDialog();
			}
		}).start();
	}
	
	private void reload(String text){
		if (loadingDialog == null){
			loadingDialog = Utils.createLoadingDialog(PatientMyReferralInfoActivity.this, text);
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
	
	final int requestCodeBooking = 1001;
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == requestCodeBooking /*&& resultCode == RESULT_OK*/){
			if(getIntent().getBooleanExtra("isComNotifi", false) 
					&& ApplicationTool.getInstance().activitis.size() == 1){
				//APP只启动当前页面，回退到主界面
				startActivity(new Intent(getApplication(),PatientMainActivity.class));
			}
			Intent it = getIntent();
			setResult(RESULT_OK, it);
			finish();
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode==KeyEvent.KEYCODE_BACK){
			if(getIntent().getBooleanExtra("isComNotifi", false) 
					&& ApplicationTool.getInstance().activitis.size() == 1){
				//APP只启动当前页面，回退到主界面
				startActivity(new Intent(getApplication(),PatientMainActivity.class));
			}
		}
		return super.onKeyDown(keyCode, event);
	}
}
