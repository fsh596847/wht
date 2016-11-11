package com.xiaowei.android.wht.ui.patient;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.hillpool.LruImageCache;
import com.xiaowei.android.wht.R;
import com.xiaowei.android.wht.model.PatientSickRecord;
import com.xiaowei.android.wht.service.DataService;
import com.xiaowei.android.wht.uibase.BaseNoTitleBarActivity;
import com.xiaowei.android.wht.utils.Util;
import com.xiaowei.android.wht.utis.HlpUtils;
import com.xiaowei.android.wht.views.ImgViewPagePopupWindow;
import com.xiaowei.android.wht.views.SildingFinishLayout;
import com.xiaowei.android.wht.views.SildingFinishLayout.OnSildingFinishListener;

public class PatientSickRecordInfoActivity extends BaseNoTitleBarActivity implements View.OnClickListener  {
	ImageView back_imageView;
	PatientSickRecord patientSickRecord;
	TextView name_textView,sex_textView,city_textViw,birthday_textView,sick_textView;
	ImageView pic1_imageView,pic2_imageView,pic3_imageView;

	RequestQueue mQueue = null;
	LruImageCache lruImageCache = null;
	ImageLoader imageLoader = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.patient_act_my_sick_recordinfo);
		patientSickRecord = (PatientSickRecord)getIntent().getSerializableExtra("patientSickRecord");
		initView();
		initCache();
		displayInfo();
		
		//右滑退出
		mSildingFinishLayout = (SildingFinishLayout) findViewById(R.id.sildingFinishLayout_patient_act_my_sich_info); 
		mSildingFinishLayout.setOnSildingFinishListener(new OnSildingFinishListener() {

			@Override
			public void onSildingFinish() {
				PatientSickRecordInfoActivity.this.finish();
			}
		});
		// touchView
		ScrollView view = (ScrollView) findViewById(R.id.scrollview_patient_act_my_sich_info);
		mSildingFinishLayout.setTouchView(view);
	}
	SildingFinishLayout mSildingFinishLayout;
	
	private void initView() {
		back_imageView = (ImageView)findViewById(R.id.back_patient_act_my_sich_info_imageView);
		back_imageView.setOnClickListener(this);
		name_textView = (TextView)findViewById(R.id.name_patient_act_my_sich_info_textView);
		sex_textView = (TextView)findViewById(R.id.sex_patient_act_my_sich_info_textView);
		city_textViw = (TextView)findViewById(R.id.city_patient_act_my_sich_info_textViw);
		birthday_textView = (TextView)findViewById(R.id.birthday_patient_act_my_sich_info_textView);
		sick_textView = (TextView)findViewById(R.id.sick_patient_act_my_sich_info_textView);
		pic1_imageView = (ImageView)findViewById(R.id.pic1_patient_act_my_sich_info_imageView);
		pic2_imageView = (ImageView)findViewById(R.id.pic2_patient_act_my_sich_info_imageView);
		pic3_imageView = (ImageView)findViewById(R.id.pic3_patient_act_my_sich_info_imageView);
	}
	private void initCache() {

		mQueue = Volley.newRequestQueue(PatientSickRecordInfoActivity.this);
		lruImageCache = LruImageCache.instance();
		imageLoader = new ImageLoader(mQueue, lruImageCache);
		
	}
	private void displayInfo() {
		if (patientSickRecord != null){
			PatientSickRecord r = patientSickRecord;
			name_textView.setText(r.getPatientname());
			sex_textView.setText(r.getSexStr());
			city_textViw.setText(r.getAddress());
			birthday_textView.setText(r.getBirthdate());
			sick_textView.setText(r.getPatientdesc());
			
			bitmapArray = new ArrayList<Bitmap>();
			if (!HlpUtils.isEmpty(r.getPatientimgone())){
				pic1_imageView.setVisibility(View.VISIBLE);
				loadImage(r.getPatientimgone(), pic1_imageView,2,0);
				pic1_imageView.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						showImgPopupWindow(0);
					}
				});
			}
		}
	}
	
	private void showImgPopupWindow(int position){
		imgViewPagePopupWindow = new ImgViewPagePopupWindow(PatientSickRecordInfoActivity.this, bitmapArray, position);
		imgViewPagePopupWindow.showAtLocation(mSildingFinishLayout, Gravity.CENTER, 0, 0);
	}
	private ImgViewPagePopupWindow imgViewPagePopupWindow;
	private List<Bitmap> bitmapArray;
	
	/**
	 * 
	 * @param url 图片地址
	 * @param iv ImageView
	 * @param type  根据iv宽（1）/高（2）缩放图片
	 */
	private void loadImage(final String url,final ImageView iv, final int type,final int p) {
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				try {
					byte[] data = DataService.getImage(url);
					if(data!=null){  
						Bitmap b = BitmapFactory.decodeByteArray(data, 0, data.length);
						final Bitmap mBitmap = Util.rotaingImageView(b, type, type == 2 ? iv.getHeight():iv.getWidth());
						if(mBitmap != null){
							bitmapArray.add(b);
							runOnUiThread(new Runnable() {
								public void run() {
									iv.setImageBitmap(mBitmap);
								}
							});
						}
					}
					runOnUiThread(new Runnable() {
						public void run() {
							switch (p) {
							case 0:
								if (!HlpUtils.isEmpty(patientSickRecord.getPatientimgtwo())){
									pic2_imageView.setVisibility(View.VISIBLE);
									loadImage(patientSickRecord.getPatientimgtwo(), pic2_imageView,2,1);
									pic2_imageView.setOnClickListener(new OnClickListener() {
										
										@Override
										public void onClick(View v) {
											showImgPopupWindow(1);
										}
									});
								}
								break;

							case 1:
								if (!HlpUtils.isEmpty(patientSickRecord.getPatientimgthree())){
									pic3_imageView.setVisibility(View.VISIBLE);
									loadImage(patientSickRecord.getPatientimgthree(), pic3_imageView,2,2);
									pic3_imageView.setOnClickListener(new OnClickListener() {
										
										@Override
										public void onClick(View v) {
											showImgPopupWindow(2);
										}
									});
								}
								break;
							}
						}
					});
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
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
	}
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back_patient_act_my_sich_info_imageView:
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
