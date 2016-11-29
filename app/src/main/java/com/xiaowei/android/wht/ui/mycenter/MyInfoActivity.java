package com.xiaowei.android.wht.ui.mycenter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import com.alibaba.fastjson.JSON;
import com.xiaowei.android.wht.ApplicationTool;
import com.xiaowei.android.wht.Config;
import com.xiaowei.android.wht.R;
import com.xiaowei.android.wht.SpData;
import com.xiaowei.android.wht.beans.Area;
import com.xiaowei.android.wht.beans.DoctorPerson;
import com.xiaowei.android.wht.model.HttpResult;
import com.xiaowei.android.wht.service.DataService;
import com.xiaowei.android.wht.service.DataService4Patient;
import com.xiaowei.android.wht.ui.AreaChooseActivity;
import com.xiaowei.android.wht.ui.PhotoSetActivity;
import com.xiaowei.android.wht.ui.SectionOfficeChooseActivity;
import com.xiaowei.android.wht.ui.WebAgreementActivity;
import com.xiaowei.android.wht.utils.Util;
import com.xiaowei.android.wht.utils.mLog;
import com.xiaowei.android.wht.utils.mToast;
import com.xiaowei.android.wht.utis.HlpUtils;
import com.xiaowei.android.wht.utis.HttpUtil;
import com.xiaowei.android.wht.utis.Utils;
import com.xiaowei.android.wht.views.CircularImage;
import com.xiaowei.android.wht.views.SildingFinishLayout;
import com.xiaowei.android.wht.views.SildingFinishLayout.OnSildingFinishListener;
import java.util.HashMap;
import java.util.Map;

public class MyInfoActivity extends Activity {
	
	private DoctorPerson doctorPerson;
	
	private CircularImage ivPhoto;//头像
	private EditText et_my_info_name; //姓名
	private TextView tv_my_info_sex_value;//显示的性别
	private Button btn_my_info_sex_cut; //切换
	
	private EditText et_my_info_hospital;//所属医院
	private TextView et_my_info_administrative_office; //所属科室
	private RelativeLayout rl_my_info_administrative_office,rl_my_info_job_title,rl_my_info_job,rl_my_info_district;
	private RelativeLayout layout_info_circle;
	private TextView tv_job_title;//职称
	private TextView tv_job;//职务
	private TextView tv_area; //所属地区
	private EditText et_zgzbm; //医师资格证书编码
	private EditText et_zsbm; //执行医师证书编码
	private EditText et_goodfield;//擅长领域
	private EditText et_my_info_publish; //学术成果
	private EditText et_my_info_achievement; //社会职务
	private Button btn_operate;//编辑或保存
	
	private ToggleButton tBtnAgreement;
	
	private int RESULTCODE = 611;
	
	
	private static final int RESULTCODE_PHOTO_SET = 510;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my_info);
		ApplicationTool.getInstance().activitis.add(this);//把当前Activity放入集合中
		initViews();
		
		initListeners();
		
		initData();
		//disableAll();
		
		//右滑退出
		SildingFinishLayout mSildingFinishLayout = (SildingFinishLayout) findViewById(R.id.sildingFinishLayout_my_info); 
		mSildingFinishLayout.setOnSildingFinishListener(new OnSildingFinishListener() {

			@Override
			public void onSildingFinish() {
				MyInfoActivity.this.finish();
			}
		});
		// touchView
		ScrollView view = (ScrollView) findViewById(R.id.ScrollView_my_info);
		mSildingFinishLayout.setTouchView(view);
	}

	private void initData() {
		
		queryDoctorPerson();
	}

	/**
	 * 加载数据
	 */
	private void queryDoctorPerson() {
		Bundle extras = getIntent().getExtras();
		doctorPerson =  (DoctorPerson) extras.get("doctorPerson");
		reload("正在努力加载……");
		new Thread(new Runnable() {
			@Override
			public void run() {

				try {
					if(doctorPerson == null){
						String mobile = new SpData(getApplicationContext()).getStringValue(SpData.keyPhoneUser, null);
						//Map<String, String> map = new HashMap<String, String>();
						//map.put("mobile", mobile);
						String s = DataService4Patient.queryDoctorPerson(MyInfoActivity.this, mobile);
						mLog.d("http", "queryDoctorPerson  s:"+s);
						if (!HlpUtils.isEmpty(s)){
							final HttpResult hr = JSON.parseObject(s,HttpResult.class);
							if(hr != null){
								if(hr.isSuccess()){
									if(!isDestroy){
										doctorPerson = JSON.parseObject(hr.getData().toString(), DoctorPerson.class);
									}
								}else{
								}
							}
						}else{
						}
					}
					if(doctorPerson != null){
						if(!isDestroy){
							runOnUiThread(new Runnable(){  
								@Override  
								public void run() { 
									//姓名
									et_my_info_name.setText(doctorPerson.getDoctorname()==null?"":doctorPerson.getDoctorname());
									//设置性别
									tv_my_info_sex_value.setText(getSexByIndex(doctorPerson.getSex()+""));
									//所属医院
									et_my_info_hospital.setText(doctorPerson.getHospital()==null?"":doctorPerson.getHospital());
									//所属科室
									et_my_info_administrative_office.setText(doctorPerson.getDetp()==null?"":doctorPerson.getDetp());
									//职称
									tv_job_title.setText(doctorPerson.getJobtitle()==null?"":doctorPerson.getJobtitle());
									//职务
									tv_job.setText(doctorPerson.getDuty()==null?"":doctorPerson.getDuty());
									//所属地区
									tv_area.setText(doctorPerson.getAddress()==null?"":doctorPerson.getAddress());
									//学术成果
									et_my_info_publish.setText(doctorPerson.getJobresults()==null?"":doctorPerson.getJobresults());
									//擅长领域
									et_goodfield.setText(doctorPerson.getGoodfield()==null?"":doctorPerson.getGoodfield());
									//社会职务
									et_my_info_achievement.setText(doctorPerson.getPaper()==null?"":doctorPerson.getPaper());
									//医师资格证书编码
									et_zgzbm.setText(doctorPerson.getQualifiedcard()==null?"":doctorPerson.getQualifiedcard());
									//执行医师证书编码
									et_zsbm.setText(doctorPerson.getLicensecard()==null?"":doctorPerson.getLicensecard());

								}  
							});
							String headImg = doctorPerson.getHeadimg();
							if(headImg != null){
								byte[] data = DataService.getImage(doctorPerson.getHeadimg());
								final Bitmap mBitmap = BitmapFactory.decodeByteArray(data, 0, data.length);// bitmap 
								//mLog.d("http", "data:"+JSON.parseArray(hr.getData().toString()).toString());
								runOnUiThread(new Runnable(){  
									@Override  
									public void run() { 
										//设置图片
										ivPhoto.setImageBitmap(mBitmap);
									}  
								});
							}
						}
					}else{
						doctorPerson = new DoctorPerson();
					}
				}catch (Exception he) {
					he.printStackTrace();
				}
				
				closeLoadingDialog();

			}
		}).start();
	}
	
	public String getSexByIndex(String index){
		if("0".equals(index)){
			return "男";
		}else if("1".equals(index)){
			return "女";
		}
		return "男";
	}
	
	public String getIndexBySex(String sex){
		if("男".equals(sex)){
			return "0";
		}else if("女".equals(sex)){
			return "1";
		}
		return "0";
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
		ApplicationTool.getInstance().activitis.remove(this);//把当前Activity移出集合中
	}


	@Override
	public void onDestroy() {
		super.onDestroy();
		isDestroy = true;
	}

	private void initListeners() {
		
		findViewById(R.id.textView_my_info_agreement).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				startActivity(new Intent(MyInfoActivity.this, WebAgreementActivity.class).putExtra("type", 1));
				overridePendingTransition(R.anim.in_right,0);
			}
		});

		//返回
		findViewById(R.id.iv_my_info_back).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				finish();
				overridePendingTransition(0, R.anim.out_right);
			}
		});
		
		//设置头像
		ivPhoto.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				startActivityForResult(new Intent(MyInfoActivity.this, PhotoSetActivity.class)
				.putExtra("title", "设置头像").putExtra("outputX", 400).putExtra("outputY", 400), 
						RESULTCODE_PHOTO_SET);
				overridePendingTransition(R.anim.in_right,0);
			}
		});
		
		rl_my_info_administrative_office.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MyInfoActivity.this,SectionOfficeChooseActivity.class);
				startActivityForResult(intent, 513);
				overridePendingTransition(R.anim.in_right,0);
			}
		});
		
		rl_my_info_job_title.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MyInfoActivity.this,JobSelectActivity.class);
				intent.putExtra("RESULTCODE", 511);
				startActivityForResult(intent, 511);
				overridePendingTransition(R.anim.in_right,0);
			}
		});
		
		rl_my_info_job.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MyInfoActivity.this,JobSelectActivity.class);
				intent.putExtra("RESULTCODE", 512);
				startActivityForResult(intent, 512);
				overridePendingTransition(R.anim.in_right,0);
			}
		});

		layout_info_circle.setOnClickListener(new OnClickListener() {
			@Override public void onClick(View v) {
				Intent intent = new Intent(MyInfoActivity.this, JobSelectActivity.class);
				intent.putExtra("RESULTCODE", 512);
				startActivityForResult(intent, 512);
				overridePendingTransition(R.anim.in_right, 0);
			}
		});

		
		//男发切换
		btn_my_info_sex_cut.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String sex = tv_my_info_sex_value.getText().toString();
				if("男".equals(sex)){
					tv_my_info_sex_value.setText("女");
				}else{
					tv_my_info_sex_value.setText("男");
				}
			}
		});
		
		//所属地区
		rl_my_info_district.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MyInfoActivity.this,AreaChooseActivity.class);
				startActivityForResult(intent, 515);
				overridePendingTransition(R.anim.in_right,0);
			}
		});
		
		//编辑或完成
		btn_operate.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				/*if(!isEdit){ //点修改
					isEdit = true;
					btn_operate.setText("完成");
					enableAll();
				}else{*/ //点完成
				if(tBtnAgreement.isChecked()){
					
					updateDoctorPerson();
				}else{
					mToast.showToast(MyInfoActivity.this, "未同意《转诊协议》");
				}
				/*}*/
			}

		});
	}
	
	// Press the back button in mobile phone
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		overridePendingTransition(0, R.anim.out_right);
	}
	
	private void updateDoctorPerson() {
		reload("正在努力加载……");
		new Thread(new Runnable() {
			@Override
			public void run() {

				try {
					if(capturePhotoPath != null){
						if(uploadHeadImg((capturePhotoPath))){
							updata();
						}
						else{
							runOnUiThread(new Runnable(){  
								@Override  
								public void run() {  
									Toast.makeText(MyInfoActivity.this, "头像上传失败,请重试！", Toast.LENGTH_SHORT).show();
								}  
							});
						}
					}
					else{
						updata();
					}
					
					
				}catch (Exception he) {
					he.printStackTrace();
				}
				closeLoadingDialog();

			}
		}).start();
	}
	
	private void updata(){
		try {
			String mobile = new SpData(getApplicationContext()).getStringValue(SpData.keyPhoneUser, null);
			String userid = new SpData(getApplicationContext()).getStringValue(SpData.keyId, null);
			
			Map<String, String> map = new HashMap<String, String>();
			map.put("mobile", mobile);
			map.put("userid", userid);
			map.put("doctorid", doctorPerson.getDoctorid());
			map.put("doctorname", et_my_info_name.getText().toString());
			if(capturePhotoPath != null){
				map.put("headimg", doctorPerson.getHeadimg());//头像
			}
			map.put("hospital", et_my_info_hospital.getText().toString());
			map.put("address", tv_area.getText().toString());
			map.put("areaid", doctorPerson.getAreaid());
			map.put("detp", et_my_info_administrative_office.getText().toString());
			map.put("sex", getIndexBySex(tv_my_info_sex_value.getText().toString()));
//		map.put("birthday", userid); //生日期
//		map.put("workcard", userid); //工作证
			map.put("duty", tv_job.getText().toString()); //职务
			map.put("jobtitle", tv_job_title.getText().toString());//职称
//		map.put("licensecard", userid); //执业证
//		map.put("qualifiedcard", userid);//资格证
			map.put("goodfield", et_goodfield.getText().toString());//擅长领域
			map.put("paper", et_my_info_achievement.getText().toString()); //社会职务
			map.put("jobresults", et_my_info_publish.getText().toString());//学术成果
//		//医师资格证书编码
//		et_zgzbm.setText(doctorPerson.getQualifiedcard());
//		//执行医师证书编码
//		et_zsbm.setText(doctorPerson.getLicensecard());
			map.put("qualifiedcard", et_zgzbm.getText().toString());
			map.put("licensecard", et_zsbm.getText().toString());
			
			String s = HttpUtil.postUrl(MyInfoActivity.this, Config.updateDoctorPerson, map);
			if (!HlpUtils.isEmpty(s)){
				final HttpResult hr = JSON.parseObject(s,HttpResult.class);
				if(hr != null){
					if(hr.isSuccess()){
						if(!isDestroy){
							runOnUiThread(new Runnable(){  
								@Override  
								public void run() {
									setResult(RESULTCODE, new Intent().putExtra("isModify", true));
									//btn_operate.setText("修改");
									//disableAll();
									Toast.makeText(MyInfoActivity.this, "修改成功!", Toast.LENGTH_SHORT).show();
								}  
							});
						}
					}else{
						runOnUiThread(new Runnable(){  
							@Override  
							public void run() {  
								Toast.makeText(MyInfoActivity.this, hr.getData().toString(), Toast.LENGTH_SHORT).show();
							}  
						});
					}
				}
			}else{
				runOnUiThread(new Runnable(){  
					@Override  
					public void run() {  
						Toast.makeText(MyInfoActivity.this, "请检查网络后重试！", Toast.LENGTH_SHORT).show();
					}  
				});
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void disableAll(){
		//设置图片
		ivPhoto.setEnabled(false);
		//姓名
		et_my_info_name.setEnabled(false);
		//设置性别
		tv_my_info_sex_value.setEnabled(false);
		//所属医院
		et_my_info_hospital.setEnabled(false);
		//所属科室
		et_my_info_administrative_office.setEnabled(false);
		//职称
		rl_my_info_job_title.setEnabled(false);
		//职务
		rl_my_info_job.setEnabled(false);
		//所属地区
		rl_my_info_district.setEnabled(false);
		//出版物
		et_my_info_publish.setEnabled(false);
		
		et_goodfield.setEnabled(false);
		//学术成就
		et_my_info_achievement.setEnabled(false);
		btn_my_info_sex_cut.setEnabled(false);
		et_zgzbm.setEnabled(false);
		et_zsbm.setEnabled(false);
	}
	
	private void enableAll() {
		// 设置图片
		ivPhoto.setEnabled(true);
		// 姓名
		et_my_info_name.setEnabled(true);
		// 设置性别
		tv_my_info_sex_value.setEnabled(true);
		// 所属医院
		et_my_info_hospital.setEnabled(true);
		// 所属科室
		et_my_info_administrative_office.setEnabled(true);
		// 职称
		rl_my_info_job_title.setEnabled(true);
		// 职务
		rl_my_info_job.setEnabled(true);
		// 所属地区
		rl_my_info_district.setEnabled(true);
		// 出版物
		et_my_info_publish.setEnabled(true);
		
		et_goodfield.setEnabled(true);
		// 学术成就
		et_my_info_achievement.setEnabled(true);
		btn_my_info_sex_cut.setEnabled(true);
		et_zgzbm.setEnabled(true);
		et_zsbm.setEnabled(true);
	}

	private void initViews() {
		
		tBtnAgreement = (ToggleButton) findViewById(R.id.toggleButton_my_info_agreement);

		ivPhoto =  (CircularImage) findViewById(R.id.iv_my_info_headphoto);
		tv_job_title = (TextView) findViewById(R.id.tv_job_title);
		tv_job = (TextView) findViewById(R.id.tv_job);
		btn_my_info_sex_cut = (Button) findViewById(R.id.btn_my_info_sex_cut);
		tv_my_info_sex_value = (TextView) findViewById(R.id.tv_my_info_sex_value);
		et_my_info_name = (EditText) findViewById(R.id.et_my_info_name);
		et_my_info_hospital = (EditText) findViewById(R.id.et_my_info_hospital);
		et_my_info_administrative_office = (TextView) findViewById(R.id.tv_my_info_administrative_office);
		tv_area = (TextView) findViewById(R.id.tv_area);
		et_my_info_publish = (EditText) findViewById(R.id.et_my_info_publish);
		et_goodfield = (EditText) findViewById(R.id.et_goodfield);
		et_my_info_achievement = (EditText) findViewById(R.id.et_my_info_achievement);
		btn_operate = (Button) findViewById(R.id.btn_operate);
		
		et_zgzbm = (EditText) findViewById(R.id.et_zgzbm);
		et_zsbm = (EditText) findViewById(R.id.et_zsbm);
		
		rl_my_info_administrative_office = (RelativeLayout) findViewById(R.id.rl_my_info_administrative_office);
		rl_my_info_job_title = (RelativeLayout) findViewById(R.id.rl_my_info_job_title);
		rl_my_info_job = (RelativeLayout) findViewById(R.id.rl_my_info_job);
		rl_my_info_district = (RelativeLayout) findViewById(R.id.rl_my_info_district);
		layout_info_circle = (RelativeLayout) findViewById(R.id.layout_info_circle);
	}
	
	/**
	 * 上传头像
	 * @param iosimg
	 */
	private boolean uploadHeadImg(final String imgFile){
		
					try {
						String iosimg = Utils.getImageToBase64Str(imgFile);
						String s = DataService.uploadUserImg(MyInfoActivity.this, 
								new SpData(getApplicationContext()).getStringValue(SpData.keyId, null), "userlogoimg", iosimg);
						if (!HlpUtils.isEmpty(s)){
							final HttpResult hr = JSON.parseObject(s,HttpResult.class);
							if (hr != null){
								if  (hr.isSuccess()){
									if(!isDestroy && hr.getFilename() != null){
										doctorPerson.setHeadimg(hr.getFilename());
										return true;
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
					} catch (Exception e) {
						e.printStackTrace();
					}
					return false;
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (resultCode) {
		case RESULTCODE_PHOTO_SET:
			capturePhotoPath = data.getStringExtra("capturePhotoPath");
//			String s = DataService.uploadUserImg(RegisterInfoActivity.this, 
//					new SpData(getApplicationContext()).getStringValue(SpData.keyId, null), "userlogoimg", iosimg);
//			if (!HlpUtils.isEmpty(s)){
//				final HttpResult hr = JSON.parseObject(s,HttpResult.class);
//				if (hr != null){
//					if  (hr.isSuccess()){
//						if(!isDestroy && hr.getFilename() != null){
//							headPhoto = hr.getFilename();
			if(capturePhotoPath != null){
				//capturePhotoPath = capturePhotoPath.replaceAll("file://", "");
				BitmapFactory.Options optionsCapture = new BitmapFactory.Options();
				optionsCapture.inSampleSize = 1;
				Bitmap capturePhotoBitmap = BitmapFactory.decodeFile(capturePhotoPath, optionsCapture);
				int degree = Util.readPictureDegree(capturePhotoPath);
				capturePhotoBitmap = Util.rotaingImageView(degree, capturePhotoBitmap);
				if (capturePhotoBitmap != null) {
					//设置图片
					ivPhoto.setImageBitmap(capturePhotoBitmap);
				}
			}
			break;
		case 513: //科室
			String administrative_office =	data.getStringExtra("result");
			et_my_info_administrative_office.setText(administrative_office);
			break;
			
		case 511: //职称
			String jobTitle =	data.getStringExtra("result");
			tv_job_title.setText(jobTitle);
			break;
			
		case 512:	//职务
			String job = data.getStringExtra("result");
			tv_job.setText(job);
			break;
			
		case 515: //地区
			Area area = (Area) data.getSerializableExtra("Area");
			if(area != null){
				String addr = area.getAreaname();
				if(addr != null){
					tv_area.setText(addr);
					doctorPerson.setAreaid(area.getId());
				}
			}
			break;
		}
	}
	String capturePhotoPath;

}