package com.xiaowei.android.wht.ui.patient;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
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
import com.xiaowei.android.wht.R;
import com.xiaowei.android.wht.beans.DoctorPerson;
import com.xiaowei.android.wht.model.HttpResult;
import com.xiaowei.android.wht.service.DataService4Patient;
import com.xiaowei.android.wht.utils.mLog;
import com.xiaowei.android.wht.utis.HlpUtils;
import com.xiaowei.android.wht.views.CircularImage;
import com.xiaowei.android.wht.views.SildingFinishLayout;
import com.xiaowei.android.wht.views.SildingFinishLayout.OnSildingFinishListener;

public class PatientDoctorInfoActivity extends Activity implements View.OnClickListener {
	
	private DoctorPerson doctor;
	
	RequestQueue mQueue = null;
	LruImageCache lruImageCache = null;
	ImageLoader imageLoader = null;
	
	CircularImage headPic_imageView;
	ImageView back_imageView;
	TextView title_textView,name_textView,expertTag_textView,tv_expert_infor_sex,tv_expert_infor_district;
	TextView tv_expert_infor_hospital,tv_expert_infor_detp,tv_expert_infor_jobtitle;
	TextView tv_expert_infor_duty,tv_expert_infor_paper,tv_expert_infor_jobresult,tv_expert_infor_goodfield;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE); 
		super.onCreate(savedInstanceState);
		setContentView(R.layout.patient_act_my_doctor_info);
		initCache();
		doctor = (DoctorPerson) getIntent().getSerializableExtra("doctor");
		if (HlpUtils.isEmpty(doctor)){
			String doctorMobile = getIntent().getStringExtra("doctorMobile");
			getDoctor(doctorMobile);
		}
		initViews();
		displayInfo();
		
		//右滑退出
		SildingFinishLayout mSildingFinishLayout = (SildingFinishLayout) findViewById(R.id.sildingFinishLayout_patient_act_my_doctor_info); 
		mSildingFinishLayout.setOnSildingFinishListener(new OnSildingFinishListener() {

			@Override
			public void onSildingFinish() {
				PatientDoctorInfoActivity.this.finish();
			}
		});
		ScrollView view = (ScrollView) findViewById(R.id.scrollview_patient_act_my_doctor_info);
		// touchView
		mSildingFinishLayout.setTouchView(view);
	}
	private void getDoctor(final String doctorMobile) {
		new Thread(new Runnable(){
			@Override
			public void run() {
				try {
					String s = DataService4Patient.queryDoctorPerson(getApplicationContext(), 
							 doctorMobile);
					mLog.d("http", "s:"+s);
					if (!HlpUtils.isEmpty(s)){
						HttpResult hr = JSON.parseObject(s,HttpResult.class);
						if (hr != null && hr.isSuccess()){
							doctor = JSON.parseObject(hr.getData().toString(),DoctorPerson.class);
							runOnUiThread(new Runnable() {
								
								@Override
								public void run() {
									displayInfo();
								}
							});
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
	}
	private void initCache() {

		mQueue = Volley.newRequestQueue(PatientDoctorInfoActivity.this);
		lruImageCache = LruImageCache.instance();
		imageLoader = new ImageLoader(mQueue, lruImageCache);
		
	}
	ImageListener imageListener = new ImageListener() {
		
		@Override
		public void onErrorResponse(VolleyError arg0) {
			System.out.print(arg0.getMessage());
		}
		
		@Override
		public void onResponse(ImageContainer arg0, boolean arg1) {
			Bitmap bmp = arg0.getBitmap();
			if (bmp != null){
				bmp = HlpUtils.getCroppedBitmap(bmp, HlpUtils.dip2px(PatientDoctorInfoActivity.this, 60));
				headPic_imageView.setImageBitmap(bmp);
			}
		}
	};
	private void loadLogo(CircularImage iv,String url,int defaultImageId) {
		if (defaultImageId != 0){
			iv.setImageResource(defaultImageId);
		}
		imageLoader.get(url, imageListener);
	}

	private void initViews() {
		headPic_imageView = (CircularImage) findViewById(R.id.headPic_imageView);
		back_imageView = (ImageView)findViewById(R.id.back_imageView);
		back_imageView.setOnClickListener(this);
		name_textView = (TextView) findViewById(R.id.name_textView);
		title_textView = (TextView) findViewById(R.id.title_textView);
		expertTag_textView = (TextView) findViewById(R.id.expertTag_textView);
		tv_expert_infor_sex = (TextView) findViewById(R.id.tv_expert_infor_sex);
		tv_expert_infor_district = (TextView) findViewById(R.id.tv_expert_infor_district);
		tv_expert_infor_hospital = (TextView) findViewById(R.id.tv_expert_infor_hospital);
		tv_expert_infor_detp = (TextView) findViewById(R.id.tv_expert_infor_detp);
		tv_expert_infor_jobtitle = (TextView) findViewById(R.id.tv_expert_infor_jobtitle);
		tv_expert_infor_duty = (TextView) findViewById(R.id.tv_expert_infor_duty);
		tv_expert_infor_paper = (TextView) findViewById(R.id.tv_expert_infor_paper);
		tv_expert_infor_jobresult = (TextView) findViewById(R.id.tv_expert_infor_jobresult);
		tv_expert_infor_goodfield = (TextView) findViewById(R.id.tv_expert_infor_goodfield);
	}
	private void displayInfo() {
		if (doctor == null){
			return;
		}
		loadLogo(headPic_imageView, doctor.getHeadimg(), R.drawable.tab_main_wdb);
		name_textView.setText(HlpUtils.isEmpty(doctor.getDoctorname())?"":doctor.getDoctorname());
		if (doctor.getUserclass()==2){
			expertTag_textView.setVisibility(View.VISIBLE);
		}else{
			expertTag_textView.setVisibility(View.GONE);
		}
//		int sex = doctor.getSex();
//		switch (sex) {
//		case 0:
//			tv_expert_infor_sex.setText("男");
//			break;
//
//		case 1:
//			tv_expert_infor_sex.setText("女");
//			break;
//		}
		
		String area = doctor.getAddress();
		tv_expert_infor_district.setText(HlpUtils.isEmpty(area)?"":area);
		
		String hospital = doctor.getHospital();
		tv_expert_infor_hospital.setText(HlpUtils.isEmpty(hospital)?"":hospital);
		
		String detp = doctor.getDetp();
		tv_expert_infor_detp.setText(HlpUtils.isEmpty(detp)?"":detp);
		
		String jobtitle = doctor.getJobtitle();
		tv_expert_infor_jobtitle.setText(HlpUtils.isEmpty(jobtitle)?"":jobtitle);
		
		String duty = doctor.getDuty();
		tv_expert_infor_duty.setText(HlpUtils.isEmpty(duty)?"":duty);
		
		String paper = doctor.getPaper();
		tv_expert_infor_paper.setText(HlpUtils.isEmpty(paper)?"":paper);
		
		String jobresult = doctor.getJobresults();
		tv_expert_infor_jobresult.setText(HlpUtils.isEmpty(jobresult)?"":jobresult);
		
		String goodfield = doctor.getGoodfield();
		tv_expert_infor_goodfield.setText(HlpUtils.isEmpty(goodfield)?"":goodfield);
	}
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back_imageView:
			finish();
			overridePendingTransition(0, R.anim.out_right);
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

}
