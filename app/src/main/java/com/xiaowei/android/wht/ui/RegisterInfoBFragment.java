package com.xiaowei.android.wht.ui;

import com.xiaowei.android.wht.R;
import com.xiaowei.android.wht.beans.RegisterInfo;
import com.xiaowei.android.wht.utils.Util;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;

public class RegisterInfoBFragment extends Fragment {
	
	private static final int RESULTCODE_PHOTO_SET = 510;
	
	private ImageView imageView;
	private EditText etGoodAt;
	private EditText etAchievement;
	private EditText etAcceptsThat;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_register_info2, container, false); 
		
		init(view);
		
		initInfo();
		
		return view;
	}
	
	private void initInfo() {
		RegisterInfo info = RegisterInfo.getInstance();
		String goodAt = info.getGoodfield();//擅长领域
		String achievement = info.getJobresults();//学术成就
		String acceptsThat = info.getAcceptdesc();//接诊说明
		String attestationPhoto = info.getQualifiedimg();//认证图片
		if(goodAt != null){
			etGoodAt.setText(goodAt);
		}
		if(achievement != null){
			etAchievement.setText(achievement);
		}
		if(acceptsThat != null){
			etAcceptsThat.setText(acceptsThat);
		}
	}

	private void init(View view) {
		imageView = (ImageView) view.findViewById(R.id.iv_register_info2);
		etGoodAt = (EditText) view.findViewById(R.id.et_register_info2_good_at);
		etAchievement = (EditText) view.findViewById(R.id.et_register_info2_achievement);
		etAcceptsThat = (EditText) view.findViewById(R.id.et_register_info2_accepts_that);
		
		//设置资格认证信息图片
		imageView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				startActivityForResult(new Intent(getActivity(), PhotoSetActivity.class)
				.putExtra("title", "认证信息").putExtra("outputX", 550).putExtra("outputY", 240), 
						RESULTCODE_PHOTO_SET);
			}
		});
	}
	
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
				capturePhotoBitmap = Util.rotaingImageView(degree, capturePhotoBitmap,2,imageView.getHeight());
				if (capturePhotoBitmap != null) {
					//设置图片
					imageView.setImageBitmap(capturePhotoBitmap);
				}
			}
			break;

		}
	}
	private String capturePhotoPath;//图片路径

	/**
	 * 
	 * @return 图片
	 */
	public String getPhotoPath() {
		return capturePhotoPath;
	}
	/**
	 * 
	 * @return 擅长领域
	 */
	public String getGoodAt(){
		return etGoodAt.getText().toString();
	}
	/**
	 * 
	 * @return 学术成就
	 */
	public String getAchievement(){
		return etAchievement.getText().toString();
	}
	/**
	 * 
	 * @return 接诊说明
	 */
	public String getAcceptsThat(){
		return etAcceptsThat.getText().toString();
	}

}
