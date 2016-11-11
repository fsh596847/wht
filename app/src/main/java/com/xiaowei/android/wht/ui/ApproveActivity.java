package com.xiaowei.android.wht.ui;

import com.xiaowei.android.wht.ApplicationTool;
import com.xiaowei.android.wht.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * 
 * @author kevin
 * type = getIntent().getIntExtra("type", -1);//0，等待认证；1，认证通过
 */
public class ApproveActivity extends Activity {
	
	
	private TextView tvTitle,tvText;
	private ImageView ivTitle;
	private LinearLayout llWait;
	private Button btnSee;
	
	private int type;//1，等待认证；2，认证通过
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_approve);
		ApplicationTool.getInstance().activitis.add(this);//把当前Activity放入集合中
		type = getIntent().getIntExtra("type", -1);
		
		initViews();
		
		initListeners();
	}

	private void initListeners() {
		//返回
		findViewById(R.id.iv_approve_back).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				finish();
			}
		});
		
		//返回首页
		findViewById(R.id.btn_approve_back_homepage).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				finish();
			}
		});
		
		//查看我的资料
		btnSee.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				
			}
		});
		
	}

	private void initViews() {
		tvTitle = (TextView) findViewById(R.id.tv_approve_title);
		ivTitle = (ImageView) findViewById(R.id.iv_approve_title);
		tvText = (TextView) findViewById(R.id.tv_approve_text);
		llWait = (LinearLayout) findViewById(R.id.tv_approve_wait);
		btnSee = (Button) findViewById(R.id.btn_approve_see);
		
		switch (type) {
		case 1:
			tvTitle.setText("等待认证");
			ivTitle.setImageResource(R.drawable.tt_result_ask);
			tvText.setText("您好，您已成功提交资料，系统马上进行审核，1~2个工作日内给您反馈认证结果。");
			llWait.setVisibility(View.VISIBLE);
			btnSee.setVisibility(View.GONE);
			break;

		case 2:
			tvTitle.setText("专家认证");
			ivTitle.setImageResource(R.drawable.tt_result_sys);
			tvText.setText("欢迎您！平台服务已认证您的资料，开始您的旅程吧！");
			llWait.setVisibility(View.GONE);
			btnSee.setVisibility(View.VISIBLE);
			break;
		}
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		ApplicationTool.getInstance().activitis.remove(this);//把当前Activity移出集合中
	}

}
