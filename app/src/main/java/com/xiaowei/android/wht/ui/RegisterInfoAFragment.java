package com.xiaowei.android.wht.ui;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import com.xiaowei.android.wht.R;
import com.xiaowei.android.wht.beans.Area;
import com.xiaowei.android.wht.beans.RegisterInfo;
import com.xiaowei.android.wht.service.DataService;
import com.xiaowei.android.wht.ui.mycenter.JobSelectActivity;
import com.xiaowei.android.wht.utils.Util;
import com.xiaowei.android.wht.utis.Utils;

public class RegisterInfoAFragment extends Fragment {
	
	private EditText etName,etHospital/*,etWorkPermit*/,etExecutePermit,etCertification;
	private TextView tvSex,tvJobTitle,tvPostTitle,tvArea,etAdministrativeOffice;
	
	private ImageView ivHeadphoto;
	
	private String job,post;
	private int sex = 1;//0；男；1女
	private String capturePhotoPath;//图片路径
	private Area area;
	
	private static final int RESULTCODE_PHOTO_SET = 510;
	private static final int RESULTCODE_JOB_TITLE = 511;
	private static final int RESULTCODE_POST_TITLE = 512;//职务
	private static final int RESULTCODE_AREA_CHOOSE = 515;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_register_info1, container, false); 
		
		initViews(view);
		
		initListeners(view);
		
		initInfo();
		
		return view;
	}

	private void initInfo() {
		RegisterInfo info = RegisterInfo.getInstance();
		String name = info.getDoctorname();//姓名
		sex = info.getSex();//0；男；1女
		String headPhoto = info.getHeadimg();//头像
		String hospital = info.getHospital();//所属医院
		String administrativeOffice = info.getDetp();//所属科室
		String address = info.getAddress();
		if(address != null && info.getAreaid() != null){
			area = new Area();
			area.setAreaname(address);
			area.setId(info.getAreaid());
		}
		job = info.getJobtitle();//职称
		post = info.getDuty();//职务
		//String workPermit = info.getWorkcard();//工作证
		String executePermit = info.getLicensecard();//执行证
		String certification = info.getQualifiedcard();//资格证
		if(name != null){
			etName.setText(name);
		}
		switch (sex) {
		case 0:
			tvSex.setText("男");
			break;

		case 1:
			tvSex.setText("女");
			break;
		}
		if(hospital != null){
			etHospital.setText(hospital);
		}
		if(administrativeOffice != null){
			etAdministrativeOffice.setText(administrativeOffice);
		}
		if(address != null){
			tvArea.setText(address);
		}
		if(job != null){
			tvJobTitle.setText(job);
		}
		if(post != null){
			tvPostTitle.setText(post);
		}
		/*if(workPermit != null){
			etWorkPermit.setText(workPermit);
		}*/
		if(executePermit != null){
			etExecutePermit.setText(executePermit);
		}
		if(certification != null){
			etCertification.setText(certification);
		}
		if(headPhoto != null){
			getHeadImage(headPhoto);
		}
	}

	private void initListeners(View view) {
		//性别切换
		view.findViewById(R.id.btn_register_info1_sex_cut).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				switch (sex) {
				case 0:
					sex = 1;
					tvSex.setText("女");
					break;

				case 1:
					sex = 0;
					tvSex.setText("男");
					break;
				}
			}
		});
		
		//设置头像
		ivHeadphoto.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				startActivityForResult(new Intent(getActivity(), PhotoSetActivity.class)
				.putExtra("title", "设置头像").putExtra("outputX", 400).putExtra("outputY", 400), 
						RESULTCODE_PHOTO_SET);
			}
		});
		
		//选择科室
		view.findViewById(R.id.rl_register_info1_administrative_office).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				startActivityForResult(new Intent(getActivity(), SectionOfficeChooseActivity.class), 513);
			}
		});
		
		//选择地址
		view.findViewById(R.id.rl_register_info1_area).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				
				startActivityForResult(new Intent(getActivity(), AreaChooseActivity.class), RESULTCODE_AREA_CHOOSE);
			}
		});
		
		//选择职称
		view.findViewById(R.id.rl_register_info1_job_title).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				
				startActivityForResult(new Intent(getActivity(), JobSelectActivity.class)
				.putExtra("RESULTCODE", RESULTCODE_JOB_TITLE), RESULTCODE_JOB_TITLE);
			}
		});
		
		//选择职务
		view.findViewById(R.id.rl_register_info1_job).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				startActivityForResult(new Intent(getActivity(), JobSelectActivity.class)
				.putExtra("RESULTCODE", RESULTCODE_POST_TITLE), RESULTCODE_POST_TITLE);
			}
		});
		
	}

	private void initViews(View view) {
		etName = (EditText) view.findViewById(R.id.et_register_info1_name);
		etHospital = (EditText) view.findViewById(R.id.et_register_info1_hospital);
		etAdministrativeOffice = (TextView) view.findViewById(R.id.et_register_info1_administrative_office);
		//etWorkPermit = (EditText) view.findViewById(R.id.et_register_info1_work_permit);
		etExecutePermit = (EditText) view.findViewById(R.id.et_register_info1_execute_permit);
		etCertification = (EditText) view.findViewById(R.id.et_register_info1_certification);
		tvSex = (TextView) view.findViewById(R.id.tv_register_info1_sex_value);
		tvJobTitle = (TextView) view.findViewById(R.id.tv_register_info1_job_title);
		tvPostTitle =  (TextView) view.findViewById(R.id.tv_register_info1_job);
		tvArea = (TextView) view.findViewById(R.id.tv_register_info1_area);
		ivHeadphoto = (ImageView) view.findViewById(R.id.iv_register_info1_headphoto);
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (resultCode) {
		case RESULTCODE_AREA_CHOOSE:
			area = (Area) data.getSerializableExtra("Area");
			if(area != null){
				tvArea.setText(area.getAreaname());
			}
			break;
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

		case RESULTCODE_JOB_TITLE:
			job = data.getStringExtra("result");
			if(job != null){
				tvJobTitle.setText(job);
			}
			break;
			
		case RESULTCODE_POST_TITLE:
			post = data.getStringExtra("result");
			if(post != null){
				tvPostTitle.setText(post);
			}
			break;
			
		case 513:
			String office = data.getStringExtra("result");
			if(office != null){
				etAdministrativeOffice.setText(office);
			}
			break;
		}
	}

	/**
	 * 
	 * @return 头像文件
	 */
	public String getHeadPhotoPath() {
		return capturePhotoPath;
	}
	/**
	 * 
	 * @return 姓名
	 */
	public String getName() {
		return etName.getText().toString().trim();
	}
	
	/**
	 * 
	 * @return 0；男；1女
	 */
	public int getSex() {
		return sex;
	}
	/**
	 * 
	 * @return 所属医院
	 */
	public String getHospital() {
		return etHospital.getText().toString().trim();
	}
	/**
	 * 
	 * @return 所属科室
	 */
	public String getAdministrativeOffice() {
		return etAdministrativeOffice.getText().toString().trim();
	}
	/**
	 * 
	 * @return 地址
	 */
	public Area getArea() {
		return area;
	}
	/**
	 * 
	 * @return 职务
	 */
	public String getPost() {
		return post;
	}
	/**
	 * 
	 * @return 职称
	 */
	public String getJob() {
		return job;
	}
	/**
	 * 
	 * @return 工作证
	 */
	/*public String getWorkPermit() {
		return etWorkPermit.getText().toString().trim();
	}*/
	/**
	 * 
	 * @return 执行证
	 */
	public String getExecutePermit() {
		return etExecutePermit.getText().toString().trim();
	}
	/**
	 * 
	 * @return 资格证
	 */
	public String getCertification() {
		return etCertification.getText().toString().trim();
	}
	
	
	private void getHeadImage(final String headimg) {
		reload("正在努力加载……");
		new Thread(new Runnable() {
			@Override
			public void run() {

				try {
					byte[] data = DataService.getImage(headimg);
					final Bitmap mBitmap = BitmapFactory.decodeByteArray(data, 0, data.length);// bitmap 
					Bitmap b = Util.rotaingImageView(mBitmap,2,ivHeadphoto.getHeight());
					if (b != null && !isDestroy) {
						//设置图片
						ivHeadphoto.setImageBitmap(b);
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
			loadingDialog = Utils.createLoadingDialog(getActivity(), text);
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
	}

}
