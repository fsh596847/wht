package com.xiaowei.android.wht.ui;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewParent;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AbsListView.OnScrollListener;

import com.alibaba.fastjson.JSON;
import com.xiaowei.android.wht.R;
import com.xiaowei.android.wht.SpData;
import com.xiaowei.android.wht.beans.MeetingImg;
import com.xiaowei.android.wht.beans.MeetingNoticeDoctor;
import com.xiaowei.android.wht.model.HttpResult;
import com.xiaowei.android.wht.service.DataService;
import com.xiaowei.android.wht.utils.mLog;
import com.xiaowei.android.wht.utis.HlpUtils;
import com.xiaowei.android.wht.utis.SyncImageLoaderListview;
import com.xiaowei.android.wht.utis.Utils;
import com.xiaowei.android.wht.views.CircularImage;

public class MeetingNoticeINGFragment extends Fragment {

	private ListView listView;
	private List<MeetingNoticeDoctor> listMeeting;
	private MyMeetingAdapter mAdapter;
	
	//分页刷新
	private int p; 
	private int page;
	private boolean success;//是否更新成功
	private boolean isQuery;//是否正在更新
	private boolean isFinish;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_doc_meeting_notice_ing, container, false); 
		p = 1;
		page = 15;
		success = true;
		isQuery = true;
		isFinish = false;
		listMeeting = new ArrayList<MeetingNoticeDoctor>();
		
		init(view);
		queryMeetingNotice(1,false);
		return view;
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (resultCode) {
		case MeetingApply.RESULTCODE_MeetingApply_ApplyOK:
			if(data.getBooleanExtra("personage", false)){
				getActivity().setResult(MeetingApply.RESULTCODE_MeetingApply_ApplyOK);
				getActivity().finish();
			}
			else{
				queryMeetingNotice(1,true);
			}
			break;

		}
	}

	private void init(View view) {
		listView = (ListView) view.findViewById(R.id.listview_doc_meeting_notice);
		mAdapter = new MyMeetingAdapter(getActivity(), listView, new OnClickAdapterListener() {

			@Override
			public void onClickDetailsListener(int position) {
				/*startActivityForResult(new Intent(getActivity(), WebMeetingDetailsActivity.class)
				.putExtra("id", listMeeting.get(position).getId())
				.putExtra("mny", listMeeting.get(position).getMny())
				, MeetingApply.RESULTCODE_MeetingApply_ApplyOK);
				getActivity().overridePendingTransition(R.anim.in_right,0);*/
				startActivityForResult(new Intent(getActivity(), MeetingNoticeActivity2.class)
				.putExtra("data", listMeeting.get(position))
				.putExtra("mny", listMeeting.get(position).getMny())
				, MeetingApply.RESULTCODE_MeetingApply_ApplyOK);
			}

			@Override
			public void onClickApplyListener(int position) {
				startActivityForResult(new Intent(getActivity(), MeetingApply.class)
				.putExtra("meetid", listMeeting.get(position).getId())
				.putExtra("mny", listMeeting.get(position).getMny())
				, MeetingApply.RESULTCODE_MeetingApply_ApplyOK);
				getActivity().overridePendingTransition(R.anim.in_right,0);
			}
		});
		listView.setAdapter(mAdapter);
		
		listView.setOnScrollListener(new OnScrollListener() {
			
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
			}
			
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				if (firstVisibleItem != 0 && !isFinish) {
		            //判断可视Item是否能在当前页面完全显示
		            if (visibleItemCount+firstVisibleItem == totalItemCount) {
		            	if(success && isQuery){
		            		p+=1;
		            		queryMeetingNotice(1,false);
		            	}
		            }
		        }
			}
		});
	}

	public interface OnClickAdapterListener {   
		void onClickDetailsListener(int position); 
		void onClickApplyListener(int position); 
	}

	private class MyMeetingAdapter extends BaseAdapter {

		List<MeetingNoticeDoctor> listPatient = new ArrayList<MeetingNoticeDoctor>();
		ListView mListView;

		private List<Drawable[]> drawableList;

		private LayoutInflater mInflater = null;
		private Context mContext;

		private OnClickAdapterListener onClickListener;

		private MyMeetingAdapter(Context context, ListView lvPatient,OnClickAdapterListener onClickListener)
		{
			this.mInflater = LayoutInflater.from(context);
			this.onClickListener = onClickListener;
			mContext = context;
			mListView = lvPatient;
			mListView.setOnScrollListener(onScrollListener);
		}

		private void setList(List<MeetingNoticeDoctor> list)
		{
			if(list!=null){
				if(list.size()>0){
					drawableList = new ArrayList<Drawable[]>();
					for (int i = 0; i < list.size(); i++) {
						Drawable[] dr = new Drawable[list.get(i).getMeetingImgs().size()];
//						for (int j = 0; j < dr.length; j++) {
//							dr[j] = getResources().getDrawable(R.drawable.h_vp1);
//						}
						drawableList.add(dr);
					}
				}
				this.listPatient = list;
				notifyDataSetChanged();
			}
		}

		@Override
		public int getCount() {
			return listPatient.size();
		}

		@Override
		public Object getItem(int arg0) {

			return listPatient.get(arg0);
		}

		@Override
		public long getItemId(int position) {

			return position;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup arg2) {
			final ViewHolder holder;
			if(convertView == null)
			{
				holder = new ViewHolder();
				convertView = mInflater.inflate(R.layout.item_meeting_ing2, null);
				holder.tvTime = (TextView) convertView.findViewById(R.id.tv_item_meeting_time);
				holder.tvAddress = (TextView) convertView.findViewById(R.id.tv_item_meeting_address);
				holder.tvHost = (TextView) convertView.findViewById(R.id.tv_item_meeting_host);
				holder.btnDetails = (Button) convertView.findViewById(R.id.btn_item_meeting_details);
				holder.btnApply = (Button) convertView.findViewById(R.id.btn_item_meeting_apply);
				holder.group = (ViewGroup)convertView.findViewById(R.id.viewGroup_item_meeting);
				holder.viewPager = (ViewPager) convertView.findViewById(R.id.viewPager_item_meeting);
				holder.tvMeetname = (TextView) convertView.findViewById(R.id.tv_item_meeting_meetname);
				convertView.setTag(holder);
			}else
			{
				holder = (ViewHolder)convertView.getTag();
			}

			if(getCount()>0)
			{
				MeetingNoticeDoctor meetingNoticeDoctor = listPatient.get(position);
				String time = meetingNoticeDoctor.getStarttime();
				if(time != null){
					holder.tvTime.setText(time);
				}
				else {
					holder.tvTime.setVisibility(View.GONE);
				}

				String addres = meetingNoticeDoctor.getAddress();
				if(addres != null){
					holder.tvAddress.setText(addres);
				}
				else {
					holder.tvAddress.setVisibility(View.GONE);
				}

				String host = meetingNoticeDoctor.getHost();
				if(host != null){
					holder.tvHost.setText(host);
				}
				else {
					holder.tvHost.setVisibility(View.GONE);
				}
				
				String meetingName = meetingNoticeDoctor.getMeetname();
				if(meetingName != null){
					holder.tvMeetname.setText(meetingName);
				}
				else {
					holder.tvMeetname.setVisibility(View.GONE);
				}

				holder.btnDetails.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						onClickListener.onClickDetailsListener(position);
					}
				});
				holder.tvMeetname.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						onClickListener.onClickDetailsListener(position);
					}
				});
				holder.tvTime.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						onClickListener.onClickDetailsListener(position);
					}
				});
				holder.tvAddress.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						onClickListener.onClickDetailsListener(position);
					}
				});
				switch (meetingNoticeDoctor.getOrderstate()) {
				case 0:
					holder.btnApply.setText("报名缴费");
					holder.btnApply.setTextColor(Color.parseColor("#199BFC"));
					holder.btnApply.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							onClickListener.onClickApplyListener(position);
						}
					});
					break;

				case 1:
					holder.btnApply.setText("已报名缴费");
					holder.btnApply.setTextColor(Color.parseColor("#cccccc"));
					break;
				}

				List<MeetingImg> meetingImgs = meetingNoticeDoctor.getMeetingImgs();
				if(meetingImgs != null && meetingImgs.size()>0){
					//initPublicity(holder, position);
					boolean hasNull = false;
					for (int i = 0; i < meetingImgs.size(); i++) {
						if(drawableList.get(position)[i] == null){
							hasNull = true;
							syncImageLoader.loadImage(mContext,position,meetingImgs.get(i).getImg()
									,new SyncImageLoaderListview.OnImageLoadListener(){

								@SuppressLint("NewApi")
								@Override
								public void onImageLoad(final Integer position, Drawable drawable,CircularImage ivHead, Integer index) {
									//View view = mListView.getChildAt(t);
									try {
										drawableList.get(position)[index] = drawable;
										ImageView[] mImageViews = new ImageView[drawableList.get(position).length];  
										boolean hasNull = false;
										for(int i=0; i<mImageViews.length; i++){  
										   ImageView imageView = new ImageView(getActivity());  
										   mImageViews[i] = imageView;  
										   if(drawableList.get(position)[i] == null){
											   hasNull = true;
										   }
										   imageView.setBackground(drawableList.get(position)[i] == null ? 
												   getResources().getDrawable(R.drawable.h_vp1) : drawableList.get(position)[i]);  
										} 
										if(!hasNull){
											getActivity().runOnUiThread(new Runnable() {
												
												@Override
												public void run() {
													initPublicity(holder, position);
												}
											});
										}
									} catch (Exception e) {
										e.printStackTrace();
									}
								}
								@Override
								public void onError(Integer t) {
								}

							},null,i);
						}
						if(!hasNull){
							initPublicity(holder, position);
						}
					}
				}
				
				//Drawable d = syncImageLoader.getSoftReferenceDrawable(expert.getHeadimg());
				//					Drawable d = drawables[position];
				//					if(d != null){
				//						holder.ivHead.setImageDrawable(d);
				//					}
				//					else{
				//						holder.ivHead.setImageResource(R.drawable.ic_head);
				//						syncImageLoader.loadImage(mContext,position,meetingNoticeDoctor.getHeadimg(),imageLoadListener,holder.ivHead);
				//					}
			}

			return convertView;
		}

		@SuppressLint("NewApi")
		private void initPublicity(ViewHolder holder,int position) {
			final ImageView[] tips;
			final ImageView[] mImageViews;
			Drawable[] imgIdArray = new Drawable[drawableList.get(position).length];
			for (int i = 0; i < drawableList.get(position).length; i++) {
				imgIdArray[i] = getResources().getDrawable(R.drawable.h_vp1);
			}

			tips = new ImageView[drawableList.get(position).length];
			//将点点加入到ViewGroup中  
			holder.group.removeAllViews();
			for(int i=0; i<tips.length; i++){  
				ImageView imageView = new ImageView(getActivity());  
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
				holder.group.addView(imageView, layoutParams);  
			}
			
			//将图片装载到数组中  
			mImageViews = new ImageView[drawableList.get(position).length];  
			for(int i=0; i<mImageViews.length; i++){  
			   ImageView imageView = new ImageView(getActivity());  
			   mImageViews[i] = imageView;  
			   imageView.setBackground(drawableList.get(position)[i] == null ? imgIdArray[i] :drawableList.get(position)[i]);  
			} 

			holder.viewPager.setBackground(drawableList.get(position)[0]);
			//设置Adapter  
			MyAdapter myAdapter = new MyAdapter(tips, mImageViews);
			holder.viewPager.setAdapter(myAdapter); 
			//设置ViewPager的默认项, 设置为长度的100倍，这样子开始就能往左滑动  
			holder.viewPager.setCurrentItem(mImageViews.length > 2 ? (mImageViews.length) * 100 : 0); 
			//设置监听，主要是设置点点的背景  
			holder.viewPager.setOnPageChangeListener(new OnPageChangeListener() {
				
				@Override
				public void onPageSelected(int arg0) {
					//setImageBackground(arg0 % mImageViews.length);
					for(int i=0; i<tips.length; i++){  
						if(i == arg0 % mImageViews.length){  
							tips[i].setBackgroundResource(R.drawable.round_hb);  
						}else{  
							tips[i].setBackgroundResource(R.drawable.round_hg);  
						}  
					} 
					//selectItems = arg0;
					Log.d("wht", "arg0:"+arg0);
				}
				
				@Override
				public void onPageScrolled(int arg0, float arg1, int arg2) {
					
				}
				
				@Override
				public void onPageScrollStateChanged(int arg0) {
					
				}
			});  
			
		}
		//private int selectItems = 0;
		
		public class MyAdapter extends PagerAdapter{
			//ImageView[] tips;
			ImageView[] mImageViews;
			public MyAdapter(ImageView[] tips, ImageView[] mImageViews){
				//this.tips = tips;
				this.mImageViews = mImageViews;
			}
			
			@Override
			public int getCount() {
				if(mImageViews.length > 0 && mImageViews.length <= 2){
					return mImageViews.length;
				}
				return Integer.MAX_VALUE;
			}

			@Override
			public boolean isViewFromObject(View arg0, Object arg1) {
				return arg0 == arg1;
			}
			
			@Override
			public void destroyItem(ViewGroup container, int position, Object object) {
				//Warning：不要在这里调用removeView 
//				((ViewPager)container).removeView(mImageViews[position % mImageViews.length]);
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
				return view; 

//				((ViewPager)container).addView(mImageViews[position % mImageViews.length], 0); 
//				return mImageViews[position % mImageViews.length];
			}
		}


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
			private TextView tvTime;
			private TextView tvAddress;
			private TextView tvHost;
			private Button btnDetails;
			private Button btnApply;
			private ViewGroup group;
			private ViewPager viewPager;
			private TextView tvMeetname;
		}
	}

	/**
	 * 
	 * @param type 1，正在进行；2，会议预报；3，往期回顾
	 */
	private void queryMeetingNotice(final int type,final boolean isOut){
		if(isOut){
			p = 1;
			page = 15;
			isFinish = false;
			listMeeting.clear();
		}
		isQuery = false;
		reload("正在努力加载……");
		new Thread(new Runnable() {

			@Override
			public void run() {

				try {
					SpData spData = new SpData(getActivity());
					String s = DataService.queryMeetingNotice(getActivity(), type, spData.getStringValue(SpData.keyId, null)
							, spData.getStringValue(SpData.keyPhoneUser, null), p, page);
					mLog.w("http", "s："+s);
					if (!isDestroy && !HlpUtils.isEmpty(s)){
						HttpResult hr = JSON.parseObject(s,HttpResult.class);
						if (hr != null){
							if  (hr.isSuccess()){
								success = true;
								if(hr.getTotalpage()==p){
									isFinish = true;
								}
								listMeeting.addAll(JSON.parseArray(hr.getData().toString(), MeetingNoticeDoctor.class));
								if(listMeeting != null && listMeeting.size()>0){
									for (int i = 0; i < listMeeting.size(); i++) {
										MeetingNoticeDoctor meetingNotice = listMeeting.get(i);
										if(meetingNotice.getImgdata() != null){
											meetingNotice.setMeetingImgs(JSON.parseArray(meetingNotice.getImgdata(), MeetingImg.class));
										}
									}
								}
								getActivity().runOnUiThread(new Runnable(){  
									@Override  
									public void run() { 
										mAdapter.setList(listMeeting);
									}  
								});
							}else{
								getActivity().runOnUiThread(new Runnable(){  
									@Override  
									public void run() { 
									}  
								});
							}
						}else{
							success = false;
						}
					}else{
					}
				}catch (Exception he) {
					he.printStackTrace();
				}
				closeLoadingDialog();
				isQuery = true;
			}
		}).start();
	}

	private void reload(String text){
		if (loadingDialog == null){
			loadingDialog = Utils.createLoadingDialog(getActivity(), text);
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

	boolean isDestroy = false;
	@Override
	public void onStart() {
		super.onStart();
		isFinish = false;
		isDestroy = false;
	}


	@Override
	public void onDestroy() {
		super.onDestroy();
		isFinish = true;
		isDestroy = true;
	}

}
