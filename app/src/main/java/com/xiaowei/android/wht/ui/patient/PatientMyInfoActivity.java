package com.xiaowei.android.wht.ui.patient;

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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.hillpool.LruImageCache;
import com.xiaowei.android.wht.ApplicationTool;
import com.xiaowei.android.wht.R;
import com.xiaowei.android.wht.SpData;
import com.xiaowei.android.wht.beans.Area;
import com.xiaowei.android.wht.model.HttpResult;
import com.xiaowei.android.wht.model.PatientInfo;
import com.xiaowei.android.wht.service.DataService;
import com.xiaowei.android.wht.ui.AreaChooseActivity;
import com.xiaowei.android.wht.ui.PhotoSetActivity;
import com.xiaowei.android.wht.uibase.BaseNoTitleBarActivity;
import com.xiaowei.android.wht.utils.Util;
import com.xiaowei.android.wht.utis.HlpUtils;
import com.xiaowei.android.wht.utis.Utils;
import com.xiaowei.android.wht.views.BirthdayChoosePopupWindow;
import com.xiaowei.android.wht.views.BirthdayChoosePopupWindow.CallBack;
import com.xiaowei.android.wht.views.CircularImage;
import com.xiaowei.android.wht.views.SildingFinishLayout;
import com.xiaowei.android.wht.views.SildingFinishLayout.OnSildingFinishListener;

public class PatientMyInfoActivity extends BaseNoTitleBarActivity implements View.OnClickListener  {
	ImageView back_imageView;
	PatientInfo patientInfo;
	TextView title_textView,sex_textView,phone_textView,birthday_textView,tv_area;
	EditText name_textView;
	CircularImage ivHeadphoto;
	private String capturePhotoPath;//图片路径
	Button btnSex;
	RequestQueue mQueue = null;
	LruImageCache lruImageCache = null;
	ImageLoader imageLoader = null;
	private SildingFinishLayout viewParent;
	private BirthdayChoosePopupWindow birthdayChoosePopupWindow;
	private String headPhoto;//头像
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE); 
		super.onCreate(savedInstanceState);
		setContentView(R.layout.patient_act_my_info);
		ApplicationTool.getInstance().activitis.add(this);//把当前Activity放入集合中
		patientInfo = (PatientInfo)getIntent().getSerializableExtra("patientInfo");
		initView();
		initCache();
		displayInfo();
		if(patientInfo.getHeadimg() != null){
			queryDoctorPerson(patientInfo.getHeadimg());
		}
		
		//右滑退出
		viewParent.setOnSildingFinishListener(new OnSildingFinishListener() {

			@Override
			public void onSildingFinish() {
				PatientMyInfoActivity.this.finish();
			}
		});
		// touchView
		ScrollView view = (ScrollView) findViewById(R.id.ScrollView_patient_act_info_parent);
		viewParent.setTouchView(view);
	}
	private void initView() {
		tv_area = (TextView)findViewById(R.id.tv_patient_info_area);
		tv_area.setText(patientInfo.getAddress()==null?"":patientInfo.getAddress());
		title_textView = (TextView)findViewById(R.id.title_textView);
		back_imageView = (ImageView)findViewById(R.id.back_imageView);
		back_imageView.setOnClickListener(this);
		name_textView = (EditText)findViewById(R.id.name_textView);
		sex_textView = (TextView)findViewById(R.id.sex_textView);
		phone_textView = (TextView)findViewById(R.id.phone_textView);
		birthday_textView = (TextView)findViewById(R.id.birthday_textView);
		ivHeadphoto = (CircularImage)findViewById(R.id.iv_patient_info_headphoto);
		ivHeadphoto.setOnClickListener(this);
		btnSex = (Button) findViewById(R.id.btn_patient_info_sex_cut);
		btnSex.setOnClickListener(this);
		viewParent = (SildingFinishLayout) findViewById(R.id.sildingFinishLayout_mpatient_act_my_info);
		birthdayChoosePopupWindow = new BirthdayChoosePopupWindow(this);
		//弹出日期选择
		findViewById(R.id.rl_patient_info_birthday).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				birthdayChoosePopupWindow.showAtLocation(viewParent, Gravity.CENTER, 0, 0);
			}
		});
		
		//出生日期选择
		birthdayChoosePopupWindow.setCallBack(new CallBack() {
			
			@Override
			public void select(int yearSelect, int monthSelect, int daySelect) {
				//birthday = yearSelect + "-" + monthSelect + "-" + daySelect;
				birthday_textView.setText(yearSelect + "-" + monthSelect + "-" + daySelect);
			}
			
			@Override
			public void dismiss() {
				
			}
		});
		
		//修改
		findViewById(R.id.btn_patient_info_operate).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				uploadInfo();
			}
		});
		
		//地区选择
		findViewById(R.id.rl_patient_info_district).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				Intent intent = new Intent(PatientMyInfoActivity.this,AreaChooseActivity.class);
				startActivityForResult(intent, 515);
			}
		});
	}
	
	private void queryDoctorPerson(final String headImg) {
		reload("正在努力加载……");
		new Thread(new Runnable() {
			@Override
			public void run() {

				try {
					if(headImg != null){
						byte[] data = DataService.getImage(headImg);
						final Bitmap mBitmap = BitmapFactory.decodeByteArray(data, 0, data.length);// bitmap 
						if(!isDestroy){
							runOnUiThread(new Runnable(){  
								@Override  
								public void run() { 
									//设置图片
									ivHeadphoto.setImageBitmap(mBitmap);
									
								}  
							});
						}
					}
				}catch (Exception he) {
					he.printStackTrace();
				}
				
				closeLoadingDialog();

			}
		}).start();
	}
	
	/**
	 * 上传资料
	 */
	private void uploadInfo(){
		reload("正在上传资料……");
		new Thread(new Runnable() {
			@Override
			public void run() {

				if(capturePhotoPath != null){
					//头像图片上传失败
					uploadHeadImg(Utils.getImageToBase64Str(capturePhotoPath));
				}
				
				try {
					String s = DataService.updatePatientPerson(getApplicationContext(), 
							new SpData(getApplicationContext()).getStringValue(SpData.keyPhoneUser, null)
							, new SpData(getApplicationContext()).getStringValue(SpData.keyId, null)
							, name_textView.getText().toString().trim()
							, patientInfo.getSex()
							, birthday_textView.getText().toString().trim()
							, headPhoto, patientInfo.getAreaid(), patientInfo.getAddress() );
					if (!HlpUtils.isEmpty(s)){
						final HttpResult hr = JSON.parseObject(s,HttpResult.class);
						if (hr != null){
							if  (hr.isSuccess()){
								if(!isDestroy){
									runOnUiThread(new Runnable(){  
										@Override  
										public void run() { 
											setResult(200);
											Toast.makeText(getApplicationContext(), "修改成功！", Toast.LENGTH_SHORT).show();
										}  
									});
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
									Toast.makeText(getApplicationContext(), "上传失败，请重试！", Toast.LENGTH_SHORT).show();
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
			}
		}).start();
	}
	
	/**
	 * 上传头像
	 * @param iosimg
	 */
	private boolean uploadHeadImg(final String iosimg){
		boolean re = false;
		if(iosimg == null){
			return re;
		}
		try {
			String s = DataService.uploadUserImg(PatientMyInfoActivity.this, 
					new SpData(getApplicationContext()).getStringValue(SpData.keyId, null), "userlogoimg", iosimg);
			if (!HlpUtils.isEmpty(s)){
				final HttpResult hr = JSON.parseObject(s,HttpResult.class);
				if (hr != null){
					if  (hr.isSuccess()){
						if(!isDestroy && hr.getFilename() != null){
							headPhoto = hr.getFilename();
							re = true;
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
							Toast.makeText(getApplicationContext(), "上传失败，请重试！", Toast.LENGTH_SHORT).show();
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
		return re;
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
	
	private void initCache() {

		mQueue = Volley.newRequestQueue(PatientMyInfoActivity.this);
		lruImageCache = LruImageCache.instance();
		imageLoader = new ImageLoader(mQueue, lruImageCache);
		
	}
	private void displayInfo() {
		if (patientInfo != null){
			PatientInfo r = patientInfo;
			name_textView.setText(r.getUsername());
			sex_textView.setText(r.getSexStr(r.getSex()));
			phone_textView.setText(r.getMobile());
			birthday_textView.setText(r.getBirthday());
		}
	}
	/*private void loadImage(String url,final ImageView iv){
		ImageListener imageListener = new ImageListener() {
			
			@Override
			public void onErrorResponse(VolleyError arg0) {
//				System.out.print(arg0.getMessage());
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
	}*/
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
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back_imageView:
			finish();
			overridePendingTransition(0, R.anim.out_right);
			break;
		case R.id.iv_patient_info_headphoto:
			startActivityForResult(new Intent(PatientMyInfoActivity.this, PhotoSetActivity.class)
			.putExtra("title", "设置头像").putExtra("outputX", 400).putExtra("outputY", 400), 
					RESULTCODE_PHOTO_SET);
			break;
			
		case R.id.btn_patient_info_sex_cut:
			switch (patientInfo.getSex()) {
			case 0:

				patientInfo.setSex(1);
				sex_textView.setText("女");
				break;

			case 1:

				patientInfo.setSex(0);
				sex_textView.setText("男");
				break;
			}
			break;
		}
	}
	
	// Press the back button in mobile phone
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		overridePendingTransition(0, R.anim.out_right);
	}
	
	private static final int RESULTCODE_PHOTO_SET = 510;
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (resultCode) {
		case RESULTCODE_PHOTO_SET:
			capturePhotoPath = data.getStringExtra("capturePhotoPath");
			if(capturePhotoPath != null){
				//capturePhotoPath = capturePhotoPath.replaceAll("file://", "");
				BitmapFactory.Options optionsCapture = new BitmapFactory.Options();
				optionsCapture.inSampleSize = 1;
				Bitmap capturePhotoBitmap = BitmapFactory.decodeFile(capturePhotoPath, optionsCapture);
				int degree = Util.readPictureDegree(capturePhotoPath);
				capturePhotoBitmap = Util.rotaingImageView(degree, capturePhotoBitmap,2,ivHeadphoto.getHeight());
				if (capturePhotoBitmap != null) {
					//设置图片
					ivHeadphoto.setImageBitmap(capturePhotoBitmap);
				}
			}
			break;
		case 515: //地区
			Area area = (Area) data.getSerializableExtra("Area");
			if(area != null){
				String addr = area.getAreaname();
				if(addr != null){
					patientInfo.setAreaid(area.getId());
					patientInfo.setAddress(area.getAreaname());
					tv_area.setText(patientInfo.getAddress()==null?"":patientInfo.getAddress());
				}
			}
			break;
			
		}
	}
}
