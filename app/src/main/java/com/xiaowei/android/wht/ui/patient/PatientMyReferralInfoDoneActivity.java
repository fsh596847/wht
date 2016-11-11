package com.xiaowei.android.wht.ui.patient;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.hillpool.LruImageCache;
import com.xiaowei.android.wht.R;
import com.xiaowei.android.wht.R.color;
import com.xiaowei.android.wht.beans.PatientReferral;
import com.xiaowei.android.wht.model.HttpResult;
import com.xiaowei.android.wht.service.DataService;
import com.xiaowei.android.wht.service.DataService4Patient;
import com.xiaowei.android.wht.uibase.BaseNoTitleBarActivity;
import com.xiaowei.android.wht.utils.Util;
import com.xiaowei.android.wht.utils.mLog;
import com.xiaowei.android.wht.utils.mToast;
import com.xiaowei.android.wht.utis.HlpUtils;
import com.xiaowei.android.wht.utis.Utils;
import com.xiaowei.android.wht.views.ImgViewPagePopupWindow;
import com.xiaowei.android.wht.views.SildingFinishLayout;
import com.xiaowei.android.wht.views.SildingFinishLayout.OnSildingFinishListener;

public class PatientMyReferralInfoDoneActivity extends BaseNoTitleBarActivity implements View.OnClickListener  {
	ImageView back_imageView;
	PatientReferral patientReferral;
	TextView title_textView,name_textView,sex_textView,city_textViw,birthday_textView,sick_textView;
	ImageView pic1_imageView,pic2_imageView,pic3_imageView;
	TextView status_textView,acceptDoctorName_textView,referralDoctorName_textView/*,finish_textView*/;
	
	RequestQueue mQueue = null;
	LruImageCache lruImageCache = null;
	ImageLoader imageLoader = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE); 
		super.onCreate(savedInstanceState);
		setContentView(R.layout.patient_act_my_referralinfo_done);
		patientReferral = (PatientReferral)getIntent().getSerializableExtra("patientReferral");
		if(getIntent().getBooleanExtra("isComNotifi", false)){
			//来自通知，网络查询PatientReferral数据
			Intent data = getIntent();
			queryData(data.getStringExtra("msgType"),data.getStringExtra("id"));
		}
		else{
			initView();
			initCache();
			displayInfo();
		}
		
		//右滑退出
		mSildingFinishLayout = (SildingFinishLayout) findViewById(R.id.sildingFinishLayout_patientMyReferralInfo_done); 
		mSildingFinishLayout.setOnSildingFinishListener(new OnSildingFinishListener() {

			@Override
			public void onSildingFinish() {
				PatientMyReferralInfoDoneActivity.this.finish();
			}
		});
		// touchView
		ScrollView view = (ScrollView) findViewById(R.id.ScrollView_patientMyReferralInfo_done);
		mSildingFinishLayout.setTouchView(view);
	}
	SildingFinishLayout mSildingFinishLayout;
	
	private void initView() {
		title_textView = (TextView)findViewById(R.id.title_textView);
		back_imageView = (ImageView)findViewById(R.id.back_imageView);
		back_imageView.setOnClickListener(this);
		name_textView = (TextView)findViewById(R.id.name_textView);
		sex_textView = (TextView)findViewById(R.id.sex_textView);
		city_textViw = (TextView)findViewById(R.id.city_textViw);
		birthday_textView = (TextView)findViewById(R.id.birthday_textView);
		sick_textView = (TextView)findViewById(R.id.sick_textView);
		pic1_imageView = (ImageView)findViewById(R.id.pic1_imageView);
		pic2_imageView = (ImageView)findViewById(R.id.pic2_imageView);
		pic3_imageView = (ImageView)findViewById(R.id.pic3_imageView);
		status_textView = (TextView)findViewById(R.id.status_textView);
		acceptDoctorName_textView = (TextView)findViewById(R.id.acceptDoctorName_textView);
		referralDoctorName_textView = (TextView)findViewById(R.id.referralDoctorName_textView);
		/*finish_textView = (TextView)findViewById(R.id.finish_textView);
		finish_textView.setOnClickListener(this);
		finish_textView.setVisibility(View.GONE);*/
	}
	private void initCache() {

		mQueue = Volley.newRequestQueue(PatientMyReferralInfoDoneActivity.this);
		lruImageCache = LruImageCache.instance();
		imageLoader = new ImageLoader(mQueue, lruImageCache);
		
	}
	
	private void displayInfo() {
		if (patientReferral != null){
			PatientReferral r = patientReferral;
			name_textView.setText(r.getPatientname()==null?"":r.getPatientname());
			sex_textView.setText(r.getSexStr(r.getSex()));
			city_textViw.setText(r.getPatientaddress()==null?"":r.getPatientaddress());
			birthday_textView.setText(r.getPatientbirthday()==null?"":r.getPatientbirthday());
			sick_textView.setText(r.getPatientdesc()==null?"":r.getPatientdesc());
			status_textView.setText(r.getState()==2?r.getBooking():r.getStatusStr());
			title_textView.setText(r.getStatusStr());
			acceptDoctorName_textView.setText(r.getAcceptdoctorname()==null?"":r.getAcceptdoctorname());
			referralDoctorName_textView.setText(r.getReferdoctorname()==null?"":r.getReferdoctorname());
			findViewById(R.id.name_div).setVisibility(r.getAcceptdoctorname()==null?View.GONE:View.VISIBLE);
			
			if (r.getState() == PatientReferral.StateToFinishing){
				status_textView.setTextColor(color.color_major);
				//finish_textView.setVisibility(View.VISIBLE);
			}
			
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
		imgViewPagePopupWindow = new ImgViewPagePopupWindow(PatientMyReferralInfoDoneActivity.this, bitmapArray, position);
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
								if (!HlpUtils.isEmpty(patientReferral.getPatientimgtwo())){
									pic2_imageView.setVisibility(View.VISIBLE);
									loadImage(patientReferral.getPatientimgtwo(), pic2_imageView,2,1);
									pic2_imageView.setOnClickListener(new OnClickListener() {
										
										@Override
										public void onClick(View v) {
											showImgPopupWindow(1);
										}
									});
								}
								break;

							case 1:
								if (!HlpUtils.isEmpty(patientReferral.getPatientimgthree())){
									pic3_imageView.setVisibility(View.VISIBLE);
									loadImage(patientReferral.getPatientimgthree(), pic3_imageView,2,2);
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
						}
						else if(!hr.isSuccess()) {
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
			loadingDialog = Utils.createLoadingDialog(PatientMyReferralInfoDoneActivity.this, text);
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
		case R.id.back_imageView:
			finish();
			overridePendingTransition(0, R.anim.out_right);
			break;
		/*case R.id.finish_textView:
			doComplete();
			break;*/
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
