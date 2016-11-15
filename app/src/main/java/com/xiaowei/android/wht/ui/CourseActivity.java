package com.xiaowei.android.wht.ui;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.xiaowei.android.wht.R;
import com.xiaowei.android.wht.beans.HealthLore;
import com.xiaowei.android.wht.model.HttpResult;
import com.xiaowei.android.wht.service.DataService;
import com.xiaowei.android.wht.utils.mLog;
import com.xiaowei.android.wht.utis.HlpUtils;
import com.xiaowei.android.wht.utis.SyncImageLoaderListview;
import com.xiaowei.android.wht.utis.Utils;
import com.xiaowei.android.wht.views.CircularImage;
import com.xiaowei.android.wht.views.SildingFinishLayout;
import com.xiaowei.android.wht.views.SildingFinishLayout.OnSildingFinishListener;

/**
 * 课程资料
 */
public class CourseActivity extends Activity {
	
	private List<HealthLore> listExpert;//专家List
	private MyExpertAdapter expertAdapter;
	
	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {  
		public void handleMessage(Message msg) {  
			switch (msg.what) {
			case 1:
				expertAdapter.setList(listExpert);
				break;
			}
		}
	}; 
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_course);
		
		p = 1;
		page = 20;
		success = true;
		isQuery = true;
		isFinish = false;
		
		findViewById(R.id.iv_my_course_back).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
				overridePendingTransition(0, R.anim.out_right);
			}
		});
		
		listExpert = new ArrayList<HealthLore>();
		ListView lvExpert = (ListView) findViewById(R.id.listview_my_course);
		expertAdapter = new MyExpertAdapter(CourseActivity.this,lvExpert);
		lvExpert.setAdapter(expertAdapter);
		//专家头像
		expertAdapter.setOnImageLoadListener(new SyncImageLoaderListview.OnImageLoadListener(){

			@Override
			public void onImageLoad(Integer t, Drawable drawable, CircularImage ivHead,Integer index) {
				//listExpert.get(t).setDrawable(drawable);
			}
			@Override
			public void onError(Integer t) {
			}

		});
		lvExpert.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				startActivity(new Intent(CourseActivity.this,WebHealthActivity.class)
				.putExtra("title", "课程资料")
				.putExtra("url", listExpert.get(arg2).getDetailurl()+"?id="+listExpert.get(arg2).getId() ));
				overridePendingTransition(R.anim.in_right,0);
			}
		});
		lvExpert.setOnScrollListener(new OnScrollListener() {
			
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
			}
			
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				if (firstVisibleItem != 0 && !isFinish) {
		            //判断可视Item是否能在当前页面完全显示
		            if (visibleItemCount+firstVisibleItem == totalItemCount) {
		            	if(isQuery){
		            		if(success){
		            			p+=1;
		            		}
		            		getListExpert(p,page);
		            	}
		            }
		        }
			}
		});
		
		//右滑退出
		SildingFinishLayout mSildingFinishLayout = (SildingFinishLayout) findViewById(R.id.sildingFinishLayout_my_course); 
		mSildingFinishLayout.setOnSildingFinishListener(new OnSildingFinishListener() {

			@Override
			public void onSildingFinish() {
				CourseActivity.this.finish();
			}
		});
		// touchView
		mSildingFinishLayout.setTouchView(lvExpert);
		
		getListExpert(p,page);
	}
	
	private void getListExpert(final int p, final int pagesize){
		isQuery = false;
		reload( "正在努力加载……");
		new Thread(new Runnable() {

			@Override
			public void run() {

				try {
					String s = DataService.queryCourse(CourseActivity.this, p, pagesize);
					mLog.d("http", "s："+s);
					if (!HlpUtils.isEmpty(s)){
						HttpResult hr = JSON.parseObject(s,HttpResult.class);
						if (hr != null){
							success = true;
							if  (hr.isSuccess()){
								if(!isDestroy){
									if(hr.getTotalpage()==p){
										isFinish = true;
									}
									List<HealthLore> list = JSON.parseArray(hr.getData().toString(), HealthLore.class);
									listExpert.addAll(list);
									handler.sendEmptyMessage(1);
								}
							}else{
//								listExpert.clear();
//								handler.sendEmptyMessage(1);
							}
						}else{
						}
					}else{
						success = false;
					}
				}catch (Exception he) {
					he.printStackTrace();
				}
				closeLoadingDialog();
				isQuery = true;
			}
		}).start();
	}
	//分页刷新
	private int p; 
	private int page;
	private boolean success;//是否更新成功
	private boolean isQuery;//是否正在更新
	private boolean isFinish;
	
	private class MyExpertAdapter extends BaseAdapter {
		
		@SuppressLint("HandlerLeak")
		Handler handler = new Handler(){
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case 0:
					notifyDataSetChanged();
					break;

				default:
					break;
				}
			}
		};

		List<HealthLore> list = new ArrayList<HealthLore>();
		ListView mListView;
		private Drawable[] drawables;

		private LayoutInflater mInflater = null;
		private Context mContext;


		SyncImageLoaderListview.OnImageLoadListener mImageLoadListener;

		private MyExpertAdapter(Context context, ListView lvExpert)
		{
			this.mInflater = LayoutInflater.from(context);
			mContext = context;
			mListView = lvExpert;
			mListView.setOnScrollListener(onScrollListener);
		}

		private void setList(List<HealthLore> list)
		{
			if(list!=null){
				if(list.size()>0){
					this.drawables = new Drawable[list.size()];
				}
				this.list = list;
				notifyDataSetChanged();
			}
		}

		private void setOnImageLoadListener(SyncImageLoaderListview.OnImageLoadListener mImageLoadListener){
			this.mImageLoadListener = mImageLoadListener;
		}

		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public Object getItem(int arg0) {

			return list.get(arg0);
		}

		@Override
		public long getItemId(int position) {

			return position;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup arg2) {
			ViewHolder holder;
			if(convertView == null)
			{
				holder = new ViewHolder();
				convertView = mInflater.inflate(R.layout.item_health, null);
				holder.ivImg =  (ImageView) convertView.findViewById(R.id.imageView_item_health);
				holder.tvTitle = (TextView) convertView.findViewById(R.id.textView_item_health);
				convertView.setTag(holder);
			}else
			{
				holder = (ViewHolder)convertView.getTag();
			}

			if(getCount()>0)
			{
				HealthLore expert = list.get(position);
				String title = expert.getTitle();
				if(title != null){
					holder.tvTitle.setText(title);
				}

				Drawable d = drawables[position];
				if(d != null){
					holder.ivImg.setImageDrawable(d);
				}
				else{
					holder.ivImg.setImageResource(R.drawable.loading_img);
					syncImageLoader.loadImage(mContext,position,expert.getImg(),imageLoadListener,null,0);
				}
			}

			return convertView;
		}



		SyncImageLoaderListview.OnImageLoadListener imageLoadListener = new SyncImageLoaderListview.OnImageLoadListener(){

			@Override
			public void onImageLoad(Integer t, Drawable drawable, CircularImage ivHead,Integer index) {
				drawables[t] = drawable;
				handler.sendEmptyMessage(0);
				//mImageLoadListener.onImageLoad(t, drawable, ivHead,0);
			}
			@Override
			public void onError(Integer t) {
			}

		};

		SyncImageLoaderListview syncImageLoader = new SyncImageLoaderListview();
		public void loadImage(){
			int start = mListView.getFirstVisiblePosition();
			int end =mListView.getLastVisiblePosition();
			if(end >= getCount()){
				end = getCount() -1;
			}
			syncImageLoader.setLoadLimit(start, end);
			syncImageLoader.unlock();
		}

		AbsListView.OnScrollListener onScrollListener = new AbsListView.OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				switch (scrollState) {
				case AbsListView.OnScrollListener.SCROLL_STATE_FLING:
					syncImageLoader.lock();
					break;
				case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:
					loadImage();
					break;
				case AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
					syncImageLoader.lock();
					break;

				default:
					break;
				}

			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
			}
		};


		class ViewHolder
		{
			private ImageView ivImg;
			private TextView tvTitle;
		}
	}
	
	private void reload(String text){
		if (loadingDialog == null){
			loadingDialog = Utils.createLoadingDialog(CourseActivity.this, text);
		}
		if (!loadingDialog.isShowing()){
			loadingDialog.show();
		}
	}
	
	private Dialog loadingDialog = null;
	private void closeLoadingDialog() {
		if(null != loadingDialog) {	 
			loadingDialog.dismiss();
			loadingDialog = null;
		}
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		isDestroy = true;
	}
	boolean isDestroy = false;

}
