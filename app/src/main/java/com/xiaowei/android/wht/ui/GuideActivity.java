package com.xiaowei.android.wht.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewParent;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xiaowei.android.wht.R;
import com.xiaowei.android.wht.ui.FragmentHomepage.MyOnClickListener;

public class GuideActivity extends Activity implements OnPageChangeListener {
	
	private ViewPager viewPager;
	private ViewGroup group;
	private ImageView[] mImageViews;
	private ImageView[] tips;
	private Drawable[] imgIdArray ;
	private TextView btnGuide;
	
	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {  
		public void handleMessage(Message msg) {  
			// 如果有更新就提示  
			switch (msg.what) {
			case 0:
				if(!isF){
					selectItems += 1;
					viewPager.setCurrentItem(selectItems);
					setImageBackground(selectItems % mImageViews.length);
					if(selectItems % mImageViews.length == mImageViews.length-1){
						btnGuide.setVisibility(View.VISIBLE);
					}else{
						btnGuide.setVisibility(View.GONE);
					}
				}
				break;
				
			}

		};  
	}; 
	
	boolean isF = false;
	private void stopPlay(){
		new Thread(new Runnable() {

			@Override
			public void run() {

				while (!isF) {
					try {
						Thread.sleep(4000);
						handler.sendEmptyMessage(0);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}

			}
		}).start();
	}

	@Override
	public void onStart() {
		super.onStart();
		isF = false;
		//stopPlay();//轮播
	}

	@Override
	public void onStop() {
		super.onStop();
		isF = true;
	}
	
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_guide);
		
		group = (ViewGroup)findViewById(R.id.viewGroup_guide);
		btnGuide = (TextView) findViewById(R.id.btn_guide);
		
		imgIdArray = new Drawable[4];
		imgIdArray[0] = getResources().getDrawable(R.drawable.ydy1);
		imgIdArray[1] = getResources().getDrawable(R.drawable.ydy3);
		imgIdArray[2] = getResources().getDrawable(R.drawable.ydy2);
		imgIdArray[3] = getResources().getDrawable(R.drawable.ydy4);
		
		//将点点加入到ViewGroup中  
		tips = new ImageView[imgIdArray.length];  
		for(int i=0; i<tips.length; i++){  
			ImageView imageView = new ImageView(GuideActivity.this);  
			imageView.setLayoutParams(new LayoutParams(10,10));  
			tips[i] = imageView;  
			if(i == 0){  
				tips[i].setBackgroundResource(R.drawable.round_hb);  
			}else{  
				tips[i].setBackgroundResource(R.drawable.round_hg);  
			}  

			LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(new ViewGroup.LayoutParams(LayoutParams.WRAP_CONTENT,    
					LayoutParams.WRAP_CONTENT));  
			layoutParams.leftMargin = 5;  
			layoutParams.rightMargin = 5;  
			group.addView(imageView, layoutParams);  
		}
		
		mImageViews = new ImageView[imgIdArray.length]; 
		for(int i=0; i<mImageViews.length; i++){  
			ImageView imageView = new ImageView(GuideActivity.this);  
			mImageViews[i] = imageView;  
			imageView.setBackground(imgIdArray[i]);  
		}
		
		viewPager = (ViewPager) findViewById(R.id.viewPager_guide);
		viewPager.setAdapter(new MyAdapter());  
		//设置监听，主要是设置点点的背景  
		viewPager.setOnPageChangeListener(this);  
		//设置ViewPager的默认项, 设置为长度的100倍，这样子开始就能往左滑动  
		viewPager.setCurrentItem(0); 
		
		//立即体验
		btnGuide.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				startActivity(new Intent(GuideActivity.this, ChooseWindowActivity.class)
				.putExtra("comeGuide", true));
				finish();
			}
		});
		
		//stopPlay();
	}
	
	
	public class MyAdapter extends PagerAdapter{

		MyOnClickListener myOnClickListener;

		public void setMyOnClickListener(MyOnClickListener myOnClickListener){
			this.myOnClickListener = myOnClickListener;
		}

		@Override
		public int getCount() {
			return mImageViews.length;
			//return Integer.MAX_VALUE;//循环轮播时设置
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			//Warning：不要在这里调用removeView 
			//			((ViewPager)container).removeView(mImageViews[position % mImageViews.length]);
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			//对ViewPager页号求模取出View列表中要显示的项  
			position %= mImageViews.length;  
			if (position<0){  
				position = mImageViews.length+position;  
			}  
			ImageView view = mImageViews[position];  
			//如果View已经在之前添加到了一个父组件，则必须先remove，否则会抛出IllegalStateException。  
			ViewParent vp =view.getParent();  
			if (vp!=null){  
				ViewGroup parent = (ViewGroup)vp;  
				parent.removeView(view);  
			}  
			container.addView(view);    
			//add listeners here if necessary  
			final int p = position;
			view.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if(myOnClickListener != null){
						myOnClickListener.onClick(p);
					}
				}
			});
			return view; 

			//			((ViewPager)container).addView(mImageViews[position % mImageViews.length], 0); 
			//			return mImageViews[position % mImageViews.length];
		}

	}

	@Override
	public void onPageScrollStateChanged(int arg0) {

	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {

	}

	@Override
	public void onPageSelected(int arg0) {
		selectItems = arg0;
		setImageBackground(selectItems % mImageViews.length);
		if(selectItems % mImageViews.length == mImageViews.length-1){
			btnGuide.setVisibility(View.VISIBLE);
		}else{
			btnGuide.setVisibility(View.GONE);
		}
	}

	private int selectItems = 0;
	private void setImageBackground(int selectItems){  
		//this.selectItems = selectItems;
		for(int i=0; i<tips.length; i++){  
			if(i == selectItems){  
				tips[i].setBackgroundResource(R.drawable.round_hb);  
			}else{  
				tips[i].setBackgroundResource(R.drawable.round_hg);  
			}  
		}  
	}

}
