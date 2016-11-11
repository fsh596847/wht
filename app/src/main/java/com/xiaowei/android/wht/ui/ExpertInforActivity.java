package com.xiaowei.android.wht.ui;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.ScrollView;
import android.widget.TextView;

import com.xiaowei.android.wht.ApplicationTool;
import com.xiaowei.android.wht.R;
import com.xiaowei.android.wht.beans.DoctorPerson;
import com.xiaowei.android.wht.service.DataService;
import com.xiaowei.android.wht.utils.mLog;
import com.xiaowei.android.wht.views.CircularImage;
import com.xiaowei.android.wht.views.SildingFinishLayout;
import com.xiaowei.android.wht.views.SildingFinishLayout.OnSildingFinishListener;

public class ExpertInforActivity extends Activity {
	
	private DoctorPerson expert;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_expert_infor);
		ApplicationTool.getInstance().activitis.add(this);//把当前Activity放入集合中
		expert = (DoctorPerson) getIntent().getSerializableExtra("DoctorPerson");
		
		if(expert == null){
			mLog.d("http", "expert == null");
		}
		else{
			
			initViews();
			
			initListeners();
			
			//右滑退出
			SildingFinishLayout mSildingFinishLayout = (SildingFinishLayout) findViewById(R.id.sildingFinishLayout_expert_infor); 
			mSildingFinishLayout.setOnSildingFinishListener(new OnSildingFinishListener() {

				@Override
				public void onSildingFinish() {
					ExpertInforActivity.this.finish();
				}
			});
			// touchView
			ScrollView view = (ScrollView) findViewById(R.id.ScrollView_expert_infor);
			mSildingFinishLayout.setTouchView(view);
		}
	}

	private void initListeners() {
		
		//返回
		findViewById(R.id.iv_expert_infor_back).setOnClickListener(new OnClickListener() {
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
		final CircularImage ivHead = (CircularImage) findViewById(R.id.iv_expert_infor_headphoto);
		/*Drawable drawable = expert.getDrawable();
		if(drawable != null){
			ivHead.setImageDrawable(drawable);
		}else */if(expert.getHeadimg() != null){
			//网络请求
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					try {
						byte[] data = DataService.getImage(expert.getHeadimg());
						if(data!=null){  
							Bitmap mBitmap = BitmapFactory.decodeByteArray(data, 0, data.length);// bitmap  
							final Drawable d = new BitmapDrawable(ExpertInforActivity.this.getResources(), mBitmap);
							runOnUiThread(new Runnable() {
								public void run() {
									ivHead.setImageDrawable(d);
								}
							});
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}).start();
		}
		
		TextView tvName = (TextView) findViewById(R.id.tv_expert_infor_name);
		String name = expert.getDoctorname();
		if(name != null){
			tvName.setText(name);
		}
		
		TextView tvSex = (TextView) findViewById(R.id.tv_expert_infor_sex);
		int sex = expert.getSex();
		switch (sex) {
		case 0:
			tvSex.setText("男");
			break;

		case 1:
			tvSex.setText("女");
			break;
		}
		
		TextView tvArea = (TextView) findViewById(R.id.tv_expert_infor_district);
		String area = expert.getAddress();
		if(area != null){
			tvArea.setText(area);
		}
		
		TextView tvHospital = (TextView) findViewById(R.id.tv_expert_infor_hospital);
		String hospital = expert.getHospital();
		if(hospital != null){
			tvHospital.setText(hospital);
		}
		
		TextView tvDetp = (TextView) findViewById(R.id.tv_expert_infor_detp);
		String detp = expert.getDetp();
		if(detp != null){
			tvDetp.setText(detp);
		}
		
		TextView tvJobtitle = (TextView) findViewById(R.id.tv_expert_infor_jobtitle);
		String jobtitle = expert.getJobtitle();
		if(jobtitle != null){
			tvJobtitle.setText(jobtitle);
		}
		
		TextView tvDuty = (TextView) findViewById(R.id.tv_expert_infor_duty);
		String duty = expert.getDuty();
		if(duty != null){
			tvDuty.setText(duty);
		}
		
		TextView tvPaper = (TextView) findViewById(R.id.tv_expert_infor_paper);
		String paper = expert.getJobresults();
		if(paper != null){
			tvPaper.setText(paper);
		}
		
		TextView tvJobresult = (TextView) findViewById(R.id.tv_expert_infor_jobresult);
		String jobresult = expert.getPaper();
		if(jobresult != null){
			tvJobresult.setText(jobresult);
		}
		
		TextView tvGoodfield = (TextView) findViewById(R.id.tv_expert_infor_goodfield);
		String goodfield = expert.getGoodfield();
		if(goodfield != null){
			tvGoodfield.setText(jobresult);
		}
		
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		ApplicationTool.getInstance().activitis.remove(this);//把当前Activity移出集合中
	}

}
