package com.xiaowei.android.wht.ui.patient;



import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.xiaowei.android.wht.ApplicationTool;
import com.xiaowei.android.wht.R;
import com.xiaowei.android.wht.uibase.BaseNoTitleBarActivity;

public class BookingPayOkActivity extends BaseNoTitleBarActivity implements 
	View.OnClickListener{
	ImageView back_button;
	TextView return_textView;
//	String userId;
//	String orderId;
//	Double mny;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.patient_activity_pay_ok);
		ApplicationTool.getInstance().activitis.add(this);//把当前Activity放入集合中
		initView();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}


	@Override
	protected void onResume() {
		super.onResume();
	}
	
	@Override
	protected void onPause() {
		super.onPause();
	}

	private void initView() {
		back_button = (ImageView)findViewById(R.id.back_button);
		back_button.setOnClickListener(this);
		return_textView = (TextView)findViewById(R.id.return_textView);
		return_textView.setOnClickListener(this);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		ApplicationTool.getInstance().activitis.remove(this);//把当前Activity移出集合中
	}


	View currentPayView = null;
	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.back_button:
		case R.id.return_textView:
			Intent it = getIntent();
			setResult(RESULT_OK, it);
			finish();
			break;
		default:
			break;
		}
	}

	
}
