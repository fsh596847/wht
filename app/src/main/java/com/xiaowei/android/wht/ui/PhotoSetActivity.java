package com.xiaowei.android.wht.ui;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.xiaowei.android.wht.ApplicationTool;
import com.xiaowei.android.wht.R;
import com.xiaowei.android.wht.utils.Util;
import com.xiaowei.android.wht.utils.mLog;
import com.xiaowei.android.wht.utis.Utils;
import com.xiaowei.android.wht.views.SildingFinishLayout;
import com.xiaowei.android.wht.views.SildingFinishLayout.OnSildingFinishListener;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * 
 * @author kevin
 *
 * resultCode = 510;
 * setResult(resultCode, new Intent().putExtra("capturePhotoPath", capturePhotoPath));
 * 
 * 界面标题
 *     title = getIntent().getStringExtra("title")
 * 图片宽
 *     outputX = data.getIntExtra("outputX", 400);
 * 图片高
 *     outputY = data.getIntExtra("outputY", 400);
 * 
 */
@SuppressLint("SimpleDateFormat")
public class PhotoSetActivity extends Activity {
	
	private TextView tvTitle;
	private ImageView ivPhoto;
	
	
	private static Uri mOutPutFileUri = null;//图片url
	private static Uri mCameraUri = null;//拍照url
	
	private int outputX;
	private int outputY;
	
	private static final int RESULTCODE_OK = 510;
	/**
	 * 相机拍照返回
	 */
	private final int RESULT_IMAGE_CAPTURE = 1;
	/**
	 * 裁剪返回
	 */
	private final int RESULT_IMAGE_CROP = 2;
	/**
	 * 相册选择返回
	 */
	private final int RESULT_IMAGE_SELECT = 3;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE); 
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_photo_set);
		ApplicationTool.getInstance().activitis.add(this);//把当前Activity放入集合中
		initViews();
		
		initListeners();
		
		//右滑退出
		SildingFinishLayout mSildingFinishLayout = (SildingFinishLayout) findViewById(R.id.sildingFinishLayout_photo_set); 
		mSildingFinishLayout.setOnSildingFinishListener(new OnSildingFinishListener() {

			@Override
			public void onSildingFinish() {
				PhotoSetActivity.this.finish();
			}
		});
		// touchView
		LinearLayout view = (LinearLayout) findViewById(R.id.ll_photo_set);
		mSildingFinishLayout.setTouchView(view);
	}

	private void initListeners() {
		//从相册选择
		findViewById(R.id.rl_photo_set_photo_album).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {

				getCutPictureFromPhotoAlbum();
				
			}
		});
		
		//拍一张照片
		findViewById(R.id.rl_photo_set_camera).setOnClickListener(new OnClickListener() {

			@SuppressLint("SimpleDateFormat")
			@Override
			public void onClick(View v) {
				
				getCutPictureFromCamera();

			}
		});
		
		//返回
		findViewById(R.id.iv_photo_set_back).setOnClickListener(new OnClickListener() {

			@SuppressLint("SimpleDateFormat")
			@Override
			public void onClick(View v) {

				finish();
				overridePendingTransition(0, R.anim.out_right);
			}
		});
		
	}
	
	// Press the back button in mobile phone
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		overridePendingTransition(0, R.anim.out_right);
	}

	private void initViews() {
		Intent data = getIntent();
		
		tvTitle = (TextView) findViewById(R.id.tv_photo_set_title);
		String title = data.getStringExtra("title");
		if(!title.isEmpty()){
			tvTitle.setText(title);
		}
		
		ivPhoto = (ImageView) findViewById(R.id.iv_photo_set_photo);
		outputX = data.getIntExtra("outputX", 400);
		outputY = data.getIntExtra("outputY", 400);
	}
	
	/**
	 * 相册选择
	 */
	void getCutPictureFromPhotoAlbum(){
		Intent getImage = new Intent( Intent.ACTION_GET_CONTENT);
		getImage.addCategory(Intent.CATEGORY_OPENABLE);
		getImage.setType("image/*");
		startActivityForResult(getImage,RESULT_IMAGE_SELECT);
	}
	
	/**
	 * 拍照
	 */
	void getCutPictureFromCamera(){
		File photoFilePath = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/DCIM/Camera");
		if (!photoFilePath.exists()) {
			photoFilePath.mkdirs();
		}
		File photoFile = new File(photoFilePath, new SimpleDateFormat("yyyyMMdd_hhmmss").format(new Date()) + ".jpg");
		mCameraUri = Uri.fromFile(photoFile);
		
		// 调用系统的照相机
		Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		camera.putExtra(MediaStore.EXTRA_OUTPUT, mCameraUri);
		startActivityForResult(camera, RESULT_IMAGE_CAPTURE);
	}
	
	/**
	 * 
	 * @return 图片存储Uri
	 */
	private Uri newOutPutFileUri(){
		File savefile = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/DCIM/Camera");
		if (!savefile.exists()) {
			savefile.mkdirs();
		}
		File file = new File(savefile, new SimpleDateFormat("yyyyMMdd_hhmmss").format(new Date()) + ".jpg");
		
		return Uri.fromFile(file);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case RESULT_IMAGE_CAPTURE:
			
			if (Activity.RESULT_CANCELED == resultCode) {
				mCameraUri = null;
	        	return;
	        }
	        if(null == mCameraUri) {
	        	return;
	        }
	        
	        mOutPutFileUri = newOutPutFileUri();
	        mLog.d("PhotoSetActivity", "mOutPutFileUri:"+mOutPutFileUri.toString());
            Utils.cropImageUri(PhotoSetActivity.this, true,mOutPutFileUri,mCameraUri, RESULT_IMAGE_CROP, outputX, outputY);
			break;

		case RESULT_IMAGE_CROP:
			
			if (Activity.RESULT_CANCELED == resultCode) {
	        	mOutPutFileUri = null;
	        	return;
	        }
	        if(null == mOutPutFileUri) {
	        	return;
	        }
			String capturePhotoPath = mOutPutFileUri.toString().replaceAll("file://", "");
			BitmapFactory.Options optionsCapture = new BitmapFactory.Options();
			optionsCapture.inSampleSize = 1;
			Bitmap capturePhotoBitmap = BitmapFactory.decodeFile(capturePhotoPath, optionsCapture);
			int degree = Util.readPictureDegree(capturePhotoPath);
			capturePhotoBitmap = Util.rotaingImageView(degree, capturePhotoBitmap);
			if (capturePhotoBitmap != null) {
				//设置图片
				ivPhoto.setImageBitmap(capturePhotoBitmap);
				setResult(RESULTCODE_OK, new Intent().putExtra("capturePhotoPath", capturePhotoPath));
			}
			break;
			
		case RESULT_IMAGE_SELECT:
			
			if (Activity.RESULT_CANCELED == resultCode) {
				return;
			}
			
	        if (null == data) {
				return;
			} 
			Uri selectedImage = data.getData();
			if (null == selectedImage) {
				return;
			}
			
			mOutPutFileUri = newOutPutFileUri();
			mLog.d("PhotoSetActivity", "selectedImage:"+selectedImage.toString());
			Utils.cropImageUri(PhotoSetActivity.this, false,mOutPutFileUri, selectedImage,RESULT_IMAGE_CROP, outputX, outputY);
			break;
			
		}
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		ApplicationTool.getInstance().activitis.remove(this);//把当前Activity移出集合中
	}
	
}
