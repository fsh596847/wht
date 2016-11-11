package com.xiaowei.android.wht.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.xiaowei.android.wht.ApplicationTool;
import com.xiaowei.android.wht.R;
import com.xiaowei.android.wht.beans.Area;
import com.xiaowei.android.wht.beans.Patient;
import com.xiaowei.android.wht.utils.Util;
import com.xiaowei.android.wht.utils.mLog;
import com.xiaowei.android.wht.utils.mToast;
import com.xiaowei.android.wht.utis.Utils;
import com.xiaowei.android.wht.views.BirthdayChoosePopupWindow;
import com.xiaowei.android.wht.views.SildingFinishLayout;
import com.xiaowei.android.wht.views.BirthdayChoosePopupWindow.CallBack;
import com.xiaowei.android.wht.views.SildingFinishLayout.OnSildingFinishListener;
import com.xiaowei.android.wht.views.CircularImage;

public class PatientAddAtivity extends Activity {
	private static final String tag = "PatientAddAtivity";

	private static final int TransferTreatmentResult = 500;
	private static final int RESULTCODE_AREA_CHOOSE = 515;

	private LinearLayout viewParent;
	private Button btnSex;
	private CircularImage ivHead;
	private ImageView ivAdd1,ivAdd2,ivAdd3;
	private TextView tvSex,tvArea,tvBirthday;
	private EditText etName,etPhone,etDescribe,etIllnessname;

	private String[] imageUrl;//影像资料
	private String headImage;//头像
	private int imageIndex;

	private final int MAN = 0;
	private final int LADY = 1;
	private int SEX = LADY;
	private static final int RESULTCODE_PHOTO_SET = 510;
	/**
	 * 选择的新图片uri
	 */
	private String capturePicturePath;

	private Area area;
	
	private BirthdayChoosePopupWindow birthdayChoosePopupWindow;
	//private String birthday;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_patient_add);
		ApplicationTool.getInstance().activitis.add(this);//把当前Activity放入集合中
		initViews();

		initListeners();
		
		//右滑退出
		SildingFinishLayout mSildingFinishLayout = (SildingFinishLayout) findViewById(R.id.sildingFinishLayout_patient_add_parent); 
		mSildingFinishLayout.setOnSildingFinishListener(new OnSildingFinishListener() {

			@Override
			public void onSildingFinish() {
				PatientAddAtivity.this.finish();
			}
		});
		// touchView
		ScrollView view = (ScrollView) findViewById(R.id.ScrollView_patient_add_parent);
		mSildingFinishLayout.setTouchView(view);
	}

	private void initListeners() {

		//选择地址
		findViewById(R.id.rl_patient_site).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				startActivityForResult(new Intent(getApplicationContext(), AreaChooseActivity.class), RESULTCODE_AREA_CHOOSE);
				overridePendingTransition(R.anim.in_right,0);
			}
		});

		//切换（男/女）
		btnSex.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				switch (SEX) {
				case MAN:

					SEX = LADY;
					tvSex.setText("女");
					break;

				case LADY:

					SEX = MAN;
					tvSex.setText("男");
					break;
				}
			}
		});
		//头像
		ivHead.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				getPhoto(400,400);
				imageIndex = 4;
			}
		});

		//添加影像1
		ivAdd1.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				getPhoto(600,400);
				imageIndex = 1;
			}
		});

		//添加影像2
		ivAdd2.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				getPhoto(600,400);
				imageIndex = 2;
			}
		});

		//添加影像3
		ivAdd3.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				getPhoto(600,400);
				imageIndex = 3;
			}
		});

		//下一步
		findViewById(R.id.btn_patient_next).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Patient patient = isFullyInfo();
				if(patient != null){
					//信息完整，跳转
					startActivityForResult(new Intent(PatientAddAtivity.this,ExpertFindActivity.class)
					.putExtra("Patient", patient), TransferTreatmentResult);
					overridePendingTransition(R.anim.in_right,0);
				}

			}
		});

		//返回
		findViewById(R.id.iv_patient_back).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				finish();
				overridePendingTransition(0, R.anim.out_right);
			}
		});
		
		//弹出日期选择
		findViewById(R.id.rl_patient_birthday).setOnClickListener(new OnClickListener() {

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
				tvBirthday.setText(yearSelect + "-" + monthSelect + "-" + daySelect);
			}
			
			@Override
			public void dismiss() {
				
			}
		});

	}
	
	// Press the back button in mobile phone
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		overridePendingTransition(0, R.anim.out_right);
	}
	
	private Patient isFullyInfo(){
		Patient patient = new Patient();
		
		String name = etName.getText().toString().trim();
		if(name.isEmpty()){
			mToast.showToast(getApplicationContext(), "请输入姓名");
			return null;
		}else{
			patient.setPatientname(name);
		}
		
		patient.setSex(SEX);
		
		String phone = etPhone.getText().toString().trim();
		if(phone.isEmpty() || !Utils.checkPhone(phone)){
			Toast.makeText(getApplicationContext(), "请输入正确的11位手机号码", Toast.LENGTH_SHORT).show();
			return null;
		}else{
			patient.setPatientmobile(phone);
		}
		
		if(area != null){
			patient.setAddress(area.getAreaname());
			patient.setAreaid(area.getId());
		}else{
			mToast.showToast(getApplicationContext(), "请选择地区");
			return null;
		}
		
		String birthday = tvBirthday.getText().toString().trim();
		if(birthday.isEmpty()){
			mToast.showToast(getApplicationContext(), "请选择出生日期");
			return null;
		}else{
			patient.setBirthdate(birthday);
		}
		
		String illnessname = etIllnessname.getText().toString().trim();
		if(illnessname.isEmpty()){
			mToast.showToast(getApplicationContext(), "请填写病情名称");
			return null;
		}else{
			patient.setIllnessname(illnessname);
		}
		
		String patientdesc = etDescribe.getText().toString().trim();
		if(patientdesc.isEmpty()){
			mToast.showToast(getApplicationContext(), "请填写病情描述");
			return null;
		}else{
			patient.setPatientdesc(patientdesc);
		}
		
		if(headImage == null){
			/*mToast.showToast(getApplicationContext(), "请设置头像");
			return null;*/
		}else{
			patient.setHeadimg(headImage);
		}
		
		patient.setPatientimgone(imageUrl[0]);
		patient.setPatientimgtwo(imageUrl[1]);
		patient.setPatientimgthree(imageUrl[2]);
		
		return patient;
	}

	private void initViews() {
		viewParent = (LinearLayout) findViewById(R.id.ll_patient_add_parent);
		birthdayChoosePopupWindow = new BirthdayChoosePopupWindow(this);
		imageUrl = new String[3];

		tvSex = (TextView) findViewById(R.id.tv_patient_sex_value);
		tvArea = (TextView) findViewById(R.id.tv_patient_area);
		tvBirthday = (TextView) findViewById(R.id.tv_patient_birthday_val);
		
		etName = (EditText) findViewById(R.id.et_patient_name);
		etPhone = (EditText) findViewById(R.id.et_patient_phone);
		etDescribe = (EditText) findViewById(R.id.et_patient_describe);
		etIllnessname = (EditText) findViewById(R.id.et_patient_illnessname);

		btnSex = (Button) findViewById(R.id.btn_patient_sex_cut);
		ivAdd1 = (ImageView) findViewById(R.id.iv_patient_image1);
		ivAdd2 = (ImageView) findViewById(R.id.iv_patient_image2);
		ivAdd3 = (ImageView) findViewById(R.id.iv_patient_image3);
		ivHead = (CircularImage) findViewById(R.id.iv_patient_headphoto);

	}

	void getPhoto(int outputX, int outputY){
		startActivityForResult(new Intent(PatientAddAtivity.this, PhotoSetActivity.class)
		.putExtra("title", "影像资料").putExtra("outputX", outputX).putExtra("outputY", outputY), 
		RESULTCODE_PHOTO_SET);
		overridePendingTransition(R.anim.in_right,0);
	}

	void setPhoto(Bitmap capturePhotoBitmap){
		switch (imageIndex) {
		case 1:
			ivAdd1.setImageBitmap(capturePhotoBitmap);
			ivAdd2.setVisibility(View.VISIBLE);
			imageUrl[0] = capturePicturePath;
			mLog.d(tag, "ivAdd1.getWidth():"+ivAdd1.getWidth());
			break;

		case 2:
			ivAdd2.setImageBitmap(capturePhotoBitmap);
			ivAdd3.setVisibility(View.VISIBLE);
			imageUrl[1] = capturePicturePath;
			break;

		case 3:
			ivAdd3.setImageBitmap(capturePhotoBitmap);
			imageUrl[2] = capturePicturePath;
			break;

		case 4:
			ivHead.setImageBitmap(capturePhotoBitmap);
			headImage = capturePicturePath;
			break;
		}
	}

	@SuppressLint("SimpleDateFormat")
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (resultCode) {
		case RESULTCODE_AREA_CHOOSE:
			area = (Area) data.getSerializableExtra("Area");
			if(area != null){
				tvArea.setText(area.getAreaname());
			}
			break;
		case TransferTreatmentResult:
			if(data.getBooleanExtra("isBackHomepage", false)){
				finish();
			}else{
				etName.setText("");
				etPhone.setText("");
				area = null;
				tvArea.setText("");
				tvBirthday.setText("");
				etIllnessname.setText("");
				etDescribe.setText("");
				ivAdd1.setImageResource(R.drawable.patient_add);
				ivAdd2.setVisibility(View.GONE);
				ivAdd3.setVisibility(View.GONE);
				ivHead.setImageResource(R.drawable.ic_head);
				imageUrl = new String[3];
				headImage = null;
			}
			break;
		case RESULTCODE_PHOTO_SET:
			capturePicturePath = data.getStringExtra("capturePhotoPath");
			if(capturePicturePath != null){
				//capturePicturePath = capturePicturePath.replaceAll("file://", "");
				BitmapFactory.Options optionsCapture = new BitmapFactory.Options();
				//optionsCapture.inSampleSize = 2;
				Bitmap capturePhotoBitmap = BitmapFactory.decodeFile(capturePicturePath, optionsCapture);
				int degree = Util.readPictureDegree(capturePicturePath);
				capturePhotoBitmap = Util.rotaingImageView(degree, capturePhotoBitmap, 1, ivAdd1.getWidth());
				if (capturePhotoBitmap != null) {
					//设置图片
					setPhoto(capturePhotoBitmap);
				}
			}
			break;
		}

	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		ApplicationTool.getInstance().activitis.remove(this);//把当前Activity移出集合中
	}

}
