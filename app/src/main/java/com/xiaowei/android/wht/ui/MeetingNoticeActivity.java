package com.xiaowei.android.wht.ui;

import java.util.ArrayList;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.xiaowei.android.wht.ApplicationTool;
import com.xiaowei.android.wht.R;

public class MeetingNoticeActivity extends FragmentActivity {
	
	private Button btnMeetingIng,btnMeetingWill,btnMeetingEd;
	private TextView ivMeetingIng,ivMeetingWill,ivMeetingEd;
	
	private MeetingNoticeINGFragment meetingIngF;
	private MeetingNoticeWILLFragment meetingWillF;
	private MeetingNoticeEDFragment meetingEdF;
	private ViewPager viewPager;
	private int viewPagerCurrentItem = 1;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE); 
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_doctor_meeting_notice);
		ApplicationTool.getInstance().activitis.add(this);//把当前Activity放入集合中
		btnMeetingIng = (Button) findViewById(R.id.btn_doc_meeting_notice_ing);
		ivMeetingIng = (TextView) findViewById(R.id.tv_doc_meeting_notice_ing);
		btnMeetingWill = (Button) findViewById(R.id.btn_doc_meeting_notice_will);
		ivMeetingWill = (TextView) findViewById(R.id.tv_doc_meeting_notice_will);
		btnMeetingEd = (Button) findViewById(R.id.btn_doc_meeting_notice_ed);
		ivMeetingEd = (TextView) findViewById(R.id.tv_doc_meeting_notice_ed);
		
		//初始化Fragment
		meetingWillF = new MeetingNoticeWILLFragment();
		meetingIngF = new MeetingNoticeINGFragment();
		meetingEdF = new MeetingNoticeEDFragment();
		viewPager = (ViewPager) findViewById(R.id.viewpager_doc_meeting_notice);
		ArrayList<Fragment> fList = new ArrayList<Fragment>();
		fList.add(meetingWillF);
		fList.add(meetingIngF);
		fList.add(meetingEdF);
		viewPager.setAdapter(new MyFragmentPagerAdapter(getSupportFragmentManager(), fList));
		viewPager.setCurrentItem(viewPagerCurrentItem);
		viewPager.setOnPageChangeListener(new OnPageChangeListener() {
			
			@Override
			public void onPageSelected(int arg0) {
				switch (arg0) {
				case 0:
					clearTabView();
					btnMeetingWill.setTextColor(Color.parseColor("#199BFC"));
					ivMeetingWill.setVisibility(View.VISIBLE);
					break;

				case 1:
					clearTabView();
					btnMeetingIng.setTextColor(Color.parseColor("#199BFC"));
					ivMeetingIng.setVisibility(View.VISIBLE);
					break;
					
				case 2:
					clearTabView();
					btnMeetingEd.setTextColor(Color.parseColor("#199BFC"));
					ivMeetingEd.setVisibility(View.VISIBLE);
					break;
				}
				viewPagerCurrentItem = arg0;
			}
			
			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				
			}
			
			@Override
			public void onPageScrollStateChanged(int arg0) {
				
			}
		});
		
		//正在进行
		btnMeetingIng.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				clearTabView();
				btnMeetingIng.setTextColor(Color.parseColor("#199BFC"));
				ivMeetingIng.setVisibility(View.VISIBLE);
				viewPagerCurrentItem = 1;
				viewPager.setCurrentItem(viewPagerCurrentItem);
			}
		});
		//会议预报
		btnMeetingWill.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				clearTabView();
				btnMeetingWill.setTextColor(Color.parseColor("#199BFC"));
				ivMeetingWill.setVisibility(View.VISIBLE);
				viewPagerCurrentItem = 0;
				viewPager.setCurrentItem(viewPagerCurrentItem);
			}
		});
		//往前回顾
		btnMeetingEd.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				clearTabView();
				btnMeetingEd.setTextColor(Color.parseColor("#199BFC"));
				ivMeetingEd.setVisibility(View.VISIBLE);
				viewPagerCurrentItem = 2;
				viewPager.setCurrentItem(viewPagerCurrentItem);
			}
		});
		//返回
		findViewById(R.id.iv_doc_meeting_notice_back).setOnClickListener(new OnClickListener() {

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
	
	/*@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (resultCode) {
		case MeetingApply.RESULTCODE_MeetingApply_ApplyOK:
			//meetingIngF.onActivityResult(requestCode, resultCode, data);
			break;

		}
	}*/
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		ApplicationTool.getInstance().activitis.remove(this);//把当前Activity移出集合中
	}
	
	
	protected void clearTabView() {
		btnMeetingIng.setTextColor(Color.parseColor("#666666"));
		ivMeetingIng.setVisibility(View.GONE);
		btnMeetingWill.setTextColor(Color.parseColor("#666666"));
		ivMeetingWill.setVisibility(View.GONE);
		btnMeetingEd.setTextColor(Color.parseColor("#666666"));
		ivMeetingEd.setVisibility(View.GONE);
	}


	/*private void setFragment(Fragment setFragment)  
    {  
        FragmentManager fm = getFragmentManager();  
        FragmentTransaction transaction = fm.beginTransaction();  
        transaction.replace(R.id.frameLayout_doc_meeting_notice, setFragment);  
        transaction.commit();  
    }*/
	
	private class MyFragmentPagerAdapter extends FragmentStatePagerAdapter{
		
		private ArrayList<Fragment> list;

		public MyFragmentPagerAdapter(FragmentManager fm,ArrayList<Fragment> fList) {
			super(fm);
			list = fList;
		}

		@Override
		public Fragment getItem(int arg0) {
			return list.get(arg0);
		}

		@Override
		public int getCount() {
			return list.size();
		}
		
	}


}
