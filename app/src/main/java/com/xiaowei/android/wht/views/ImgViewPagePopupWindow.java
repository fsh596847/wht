package com.xiaowei.android.wht.views;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
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
import android.widget.PopupWindow;

import com.xiaowei.android.wht.R;

public class ImgViewPagePopupWindow extends PopupWindow implements OnPageChangeListener {
	
	private ViewPager viewPager;
	private ViewGroup group;
	
	private ImageView[] mImageViews;
	private ImageView[] tips;
	private List<Bitmap> bitmapArray ;
	private int currentItem;
	
	private Activity context;
	
	public ImgViewPagePopupWindow(Context context){
		
	}

	public ImgViewPagePopupWindow(Activity context,List<Bitmap> bitmapArray, int currentItem){
		this.bitmapArray = bitmapArray;
		this.context = context;
		this.currentItem = currentItem;
		
		View customView = context.getLayoutInflater().inflate(R.layout.popup_img_viewpage, null, false); 

		setContentView(customView);

		setWidth(LayoutParams.MATCH_PARENT);
		setHeight(LayoutParams.MATCH_PARENT);
		setFocusable(true);
		//setAnimationStyle(R.style.AnimationFade);  

		inidDatas();
		initViews(customView);
		initListeners(customView);
	}

	private void inidDatas() {
	}

	private void initListeners(View customView) {
		// TODO Auto-generated method stub

	}

	private void initViews(View customView) {
		group = (ViewGroup)customView.findViewById(R.id.viewGroup_img_popup);
		viewPager = (ViewPager) customView.findViewById(R.id.viewPager_img_popup);
		
		//将点点加入到ViewGroup中  
		tips = new ImageView[bitmapArray.size()];  
		for(int i=0; i<tips.length; i++){  
			ImageView imageView = new ImageView(context);  
			imageView.setLayoutParams(new LayoutParams(10,10));  
			tips[i] = imageView;  
			if(i == currentItem){  
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
		
		mImageViews = new ImageView[bitmapArray.size()]; 
		for(int i=0; i<mImageViews.length; i++){  
			ImageView imageView = new ImageView(context);  
			mImageViews[i] = imageView;  
			imageView.setImageBitmap(bitmapArray.get(i));  
		}
		
		viewPager.setAdapter(new MyAdapter());  
		//设置监听，主要是设置点点的背景  
		viewPager.setOnPageChangeListener(this);  
		//设置ViewPager的默认项, 设置为长度的100倍，这样子开始就能往左滑动  
		viewPager.setCurrentItem(currentItem); 
	}
	
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
	
	public class MyAdapter extends PagerAdapter{

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
			view.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					dismiss();
				}
			});
			return view; 

			//			((ViewPager)container).addView(mImageViews[position % mImageViews.length], 0); 
			//			return mImageViews[position % mImageViews.length];
		}

	}

	@Override
	public void onPageScrollStateChanged(int arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
	}

	@Override
	public void onPageSelected(int arg0) {
		
		setImageBackground(arg0);
	}
}
