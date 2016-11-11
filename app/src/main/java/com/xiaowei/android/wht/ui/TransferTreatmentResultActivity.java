package com.xiaowei.android.wht.ui;

import com.xiaowei.android.wht.ApplicationTool;
import com.xiaowei.android.wht.R;
import com.xiaowei.android.wht.views.SildingFinishLayout;
import com.xiaowei.android.wht.views.SildingFinishLayout.OnSildingFinishListener;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class TransferTreatmentResultActivity extends Activity {
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_transfer_treatment_result);
		ApplicationTool.getInstance().activitis.add(this);//把当前Activity放入集合中
		initViews();
		
		initListeners();
		
		//右滑退出
		SildingFinishLayout mSildingFinishLayout = (SildingFinishLayout) findViewById(R.id.sildingFinishLayout_transfer_treatment_result); 
		mSildingFinishLayout.setOnSildingFinishListener(new OnSildingFinishListener() {

			@Override
			public void onSildingFinish() {
				TransferTreatmentResultActivity.this.finish();
			}
		});
		// touchView
		LinearLayout view = (LinearLayout) findViewById(R.id.LinearLayout_transfer_treatment_result);
		mSildingFinishLayout.setTouchView(view);
	}

	private void initListeners() {

		//返回
		findViewById(R.id.iv_transfer_treatment_result_back).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				setResult(500, new Intent().putExtra("isBackHomepage", true));
				finish();
				overridePendingTransition(0, R.anim.out_right);
			}
		});
		
		//返回首页
		findViewById(R.id.btn_transfer_treatment_result_back_homepage).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				setResult(500, new Intent().putExtra("isBackHomepage", true));
				finish();
				overridePendingTransition(0, R.anim.out_right);
			}
		});

		//添加新患者
		findViewById(R.id.btn_transfer_treatment_result_add).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				setResult(500, new Intent().putExtra("isBackHomepage", false));
				finish();
				overridePendingTransition(0, R.anim.out_right);
			}
		});
		
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		if ((keyCode == KeyEvent.KEYCODE_BACK)) {//返回键
			setResult(500, new Intent().putExtra("isBackHomepage", true));
		} 
		return super.onKeyDown(keyCode, event);
	}
	
	// Press the back button in mobile phone
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		overridePendingTransition(0, R.anim.out_right);
	}

	private void initViews() {
		
		TextView tvtitle = (TextView) findViewById(R.id.tv_transfer_treatment_result_title);
		ImageView ivTitle = (ImageView) findViewById(R.id.iv_transfer_treatment_result);
		TextView text = (TextView) findViewById(R.id.tv_transfer_treatment_result);
		
		int type = getIntent().getIntExtra("type", -1);
		switch (type) {
		case 0:
			
			tvtitle.setText("转诊成功");
			ivTitle.setImageResource(R.drawable.tt_result_ask);
			text.setText("转诊请求已通知到专家，请耐心等待专家确认。专家反馈后会第一时间通知您。");
			break;

		case 1:
			
			tvtitle.setText("客服转诊");
			ivTitle.setImageResource(R.drawable.tt_result_sys);
			text.setText("已提交系统，客服将帮您进行转诊。请耐心等待反馈信息。");
			break;
		}
		
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		ApplicationTool.getInstance().activitis.remove(this);//把当前Activity移出集合中
	}

}
