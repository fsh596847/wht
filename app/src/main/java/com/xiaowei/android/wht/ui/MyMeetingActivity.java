package com.xiaowei.android.wht.ui;

import com.xiaowei.android.wht.ApplicationTool;
import com.xiaowei.android.wht.R;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class MyMeetingActivity extends Activity {
	
	private Button btnMeetingWill,btnMeetingRecord;
	private TextView ivMeetingWill,ivMeetingRecord;
	
	//private MyMeetingWillFragment meetingWillF;
	private MyMeetingRecordFragment meetingRecordF;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my_meeting);
		ApplicationTool.getInstance().activitis.add(this);//把当前Activity放入集合中
		initViews();
		
		initListeners();
		
		//初始化Fragment
		//meetingWillF = new MyMeetingWillFragment();
		meetingRecordF = new MyMeetingRecordFragment();
		setFragment(meetingRecordF);
	}

	private void initListeners() {
		//返回
		findViewById(R.id.iv_my_conference_back).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				finish();
			}
		});
		//待参会议
		btnMeetingWill.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				btnMeetingRecord.setTextColor(Color.parseColor("#666666"));
				ivMeetingRecord.setVisibility(View.GONE);
				btnMeetingWill.setTextColor(Color.parseColor("#199BFC"));
				ivMeetingWill.setVisibility(View.VISIBLE);
				//setFragment(meetingWillF);
				meetingRecordF.queryMeetingNotice(0,true);
			}
		});
		//参会记录
		btnMeetingRecord.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				btnMeetingWill.setTextColor(Color.parseColor("#666666"));
				ivMeetingWill.setVisibility(View.GONE);
				btnMeetingRecord.setTextColor(Color.parseColor("#199BFC"));
				ivMeetingRecord.setVisibility(View.VISIBLE);
				//setFragment(meetingRecordF);
				meetingRecordF.queryMeetingNotice(1,true);
			}
		});
		
	}

	private void initViews() {
		btnMeetingWill = (Button) findViewById(R.id.btn_my_meeting_will);
		ivMeetingWill = (TextView) findViewById(R.id.tv_my_meeting_will);
		btnMeetingRecord = (Button) findViewById(R.id.btn_my_meeting_record);
		ivMeetingRecord = (TextView) findViewById(R.id.tv_my_meeting_record);
	}
	
	private void setFragment(Fragment setFragment)  
    {  
        FragmentManager fm = getFragmentManager();  
        FragmentTransaction transaction = fm.beginTransaction();  
        transaction.replace(R.id.frameLayout_my_meeting, setFragment);  
        transaction.commit();  
    }
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		ApplicationTool.getInstance().activitis.remove(this);//把当前Activity移出集合中
	}

}
