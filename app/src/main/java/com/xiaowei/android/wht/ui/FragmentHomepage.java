package com.xiaowei.android.wht.ui;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewParent;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.xiaowei.android.wht.R;
import com.xiaowei.android.wht.SpData;
import com.xiaowei.android.wht.beans.DoctorPerson;
import com.xiaowei.android.wht.beans.MeetingNoticeDoctor;
import com.xiaowei.android.wht.beans.RegisterInfo;
import com.xiaowei.android.wht.model.HttpResult;
import com.xiaowei.android.wht.service.DataService;
import com.xiaowei.android.wht.utils.Util;
import com.xiaowei.android.wht.utils.mLog;
import com.xiaowei.android.wht.utis.HlpUtils;
import com.xiaowei.android.wht.utis.SyncImageLoaderListview;
import com.xiaowei.android.wht.utis.Utils;
import com.xiaowei.android.wht.views.CircularImage;
import com.xiaowei.android.wht.views.ListViewInScrollView;

@SuppressLint("NewApi")
public class FragmentHomepage extends Fragment implements OnPageChangeListener {

	private TextView tvMeetname,tvHost/*,tvMeetingDetails*/;

	private List<MeetingNoticeDoctor> listHomeImg;

	//专家
	private ListViewInScrollView lvExpert;//专家ListView
	private List<DoctorPerson> listExpert;//专家List
	private MyExpertAdapter expertAdapter;

	private ScrollView scrollView;
	DisplayMetrics dm;

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
					String meetname = listHomeImg.get(selectItems % mImageViews.length).getMeetname();
					if(meetname != null){
						tvMeetname.setText(meetname);
					}
				}
				break;
				
			case 1:
				expertAdapter.setList(listExpert);
				Util.setListViewHeightBasedOnChildren(lvExpert);
				scrollView.smoothScrollTo(0, y);
				break;
				
			case 2:
				initPublicity(listHomeImg.size());
				break;
				
			case 3:
				//msg.arg1;
				mImageViews[msg.arg1].setBackground(imgIdArray[msg.arg1]);
				break;
			}

		};  
	}; 

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		dm = new DisplayMetrics();
		getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
		View view = inflater.inflate(R.layout.fragment_homepg, container, false); 
		init(view);
		return view;
	}

	private void init(View view) {
		//宣传栏初始化
		initMeeitng(view);

		//功能栏初始化
		initFunction(view);

		//搜索栏 初始化
		initSearch(view);

		initExpert(view);

		getImageInfo();
	}

	private void initExpert(View view) {
		listExpert = new ArrayList<DoctorPerson>();
		scrollView = (ScrollView) view.findViewById(R.id.scrollView_homepg);
		lvExpert = (ListViewInScrollView) view.findViewById(R.id.listview_home);
		expertAdapter = new MyExpertAdapter(getActivity(),lvExpert);
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
				startActivity(new Intent(getActivity(),ExpertInforActivity.class)
				.putExtra("DoctorPerson", listExpert.get(arg2)));
				getActivity().overridePendingTransition(R.anim.in_right,0);
			}
		});

		p = 1;
		page = 10;
		success = true;
		isQuery = true;
		isFinish = false;
		scrollView.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_MOVE:
					int scrollY=v.getScrollY();
					int height=v.getHeight();
					int scrollViewMeasuredHeight=scrollView.getChildAt(0).getMeasuredHeight();
					if(scrollY==0){
						//System.out.println("滑动到了顶端 view.getScrollY()="+scrollY);
					}
					y = scrollY+height;
					if(y==scrollViewMeasuredHeight){
						//System.out.println("滑动到了底部 scrollY="+scrollY);
						if(success && isQuery && !isFinish && curr==0){
							curr++;
							p+=1;
							getListExpert(null,null,p,page,true);
						}
					}else{
						curr = 0;
					}

					break;

				}
				return false;
			}
		});

		getListExpert(null, null,p,page,false);
	}
	//分页刷新
	private int p; 
	private int page;
	private boolean success;//是否更新成功
	private boolean isQuery;//是否正在更新
	private boolean isFinish;
	int curr = 0;
	int y = 0;

	/**
	 * get expert list
	 * @param detpname
	 * @param areaid
	 */
	private void getListExpert(final String detpname, final String areaid, final int p, final int pagesize,final boolean isP){
		isQuery = false;
		reload( "正在努力加载……");
		new Thread(new Runnable() {

			@Override
			public void run() {

				try {
					/*if(!isP){
						
						String ss = DataService.getDoctorHomeImg(getActivity());
						mLog.d("http", "ss："+ss);
						if (!HlpUtils.isEmpty(ss)){
							HttpResult hr = JSON.parseObject(ss,HttpResult.class);
							if (hr != null){
								if  (hr.isSuccess()){
									listHomeImg = JSON.parseArray(hr.getData().toString(), HomeImg.class);
									if(!isF && listHomeImg != null){
										//先显示默认图片
										handler.sendEmptyMessage(2);
										//获取后台图片
										for (int i = 0; i < listHomeImg.size(); i++) {
											//getImage(listHomeImg.get(i).getImgUrl(), i);
											for (int j = 0; j < 3; j++) {
												byte[] data = DataService.getImage(listHomeImg.get(i).getImgUrl());
												if(data!=null){ 
													j = 100;
													if(!isF){
														final Bitmap mBitmap = BitmapFactory.decodeByteArray(data, 0, data.length);// bitmap  
														imgIdArray[i] = new BitmapDrawable(getResources(), mBitmap);
														Message msg = new Message();
														msg.arg1 = i;
														msg.what = 3;
														handler.sendMessage(msg);
													}
												}
												else{
													Thread.sleep(400);
												}
											}
										}
									}
								}else{
								}
							}else{
							}
						}else{
						}
					}*/
					
					String s = DataService.queryDoctor(getActivity()
							, new SpData(getActivity()).getStringValue(SpData.keyPhoneUser, null)
							, null, detpname, areaid, null, null, p, pagesize);
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
									List<DoctorPerson> list = JSON.parseArray(hr.getData().toString(), DoctorPerson.class);
									listExpert.addAll(list);
									y = scrollView.getScrollY();
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

	public interface OnClickDetailsListener {   
		void onClickDetailsListener(int position);   
	}

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
			};};

		List<DoctorPerson> list = new ArrayList<DoctorPerson>();
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

		private void setList(List<DoctorPerson> list)
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
				convertView = mInflater.inflate(R.layout.item_expert_listview, null);
				holder.ivHead = (CircularImage) convertView.findViewById(R.id.iv_item_expert_headphoto);
				holder.tvName = (TextView) convertView.findViewById(R.id.tv_item_expert_name);
				holder.tvDescribe = (TextView) convertView.findViewById(R.id.tv_item_expert_describe);
				holder.tvArea = (TextView) convertView.findViewById(R.id.tv_item_expert_area);
				convertView.setTag(holder);
			}else
			{
				holder = (ViewHolder)convertView.getTag();
			}

			if(getCount()>0)
			{
				DoctorPerson expert = list.get(position);
				String name = expert.getDoctorname();
				if(name != null){
					holder.tvName.setText(name);
				}
				else{
					holder.tvName.setText("");
				}
				String describe = "";
				if(expert.getHospital() != null){
					describe += expert.getHospital();
				}
				if(expert.getDetp() != null){
					describe += expert.getDetp();
				}
				if(expert.getJobtitle() != null){
					describe += expert.getJobtitle();
				}
				holder.tvDescribe.setText(describe);

				String area = expert.getAddress();
				if(area != null){
					holder.tvArea.setText(area);
				}
				else{
					holder.tvArea.setText("");
				}

				Drawable d = drawables[position];
				if(d != null){
					holder.ivHead.setImageDrawable(d);
				}
				else{
					holder.ivHead.setImageResource(R.drawable.ic_head);
					syncImageLoader.loadImage(mContext,position,expert.getHeadimg(),imageLoadListener,holder.ivHead,0);
				}
			}

			return convertView;
		}



		SyncImageLoaderListview.OnImageLoadListener imageLoadListener = new SyncImageLoaderListview.OnImageLoadListener(){

			@Override
			public void onImageLoad(Integer t, Drawable drawable, CircularImage ivHead,Integer index) {
				//mLog.d("http", "imageLoadListener  onImageLoad ");
				//View view = mListView.getChildAt(t);
				if(ivHead != null){
					//CircularImage v = (CircularImage) view.findViewById(R.id.iv_item_expert_find_headphoto);
					//v.setImageDrawable(drawable);
					handler.sendEmptyMessage(0);
					//ivHead.setImageDrawable(drawable);
					//notifyDataSetChanged();
					drawables[t] = drawable;
					//iv.setImageDrawable(drawable);
					//mLog.d("http", "imageLoadListener  onImageLoad view != null");
				}
				mImageLoadListener.onImageLoad(t, drawable, ivHead,0);
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
			private CircularImage ivHead;
			private TextView tvName;
			private TextView tvDescribe;
			private TextView tvArea;
		}
	}

	private void initMeeitng(View view) {
		RelativeLayout ad_div = (RelativeLayout)view.findViewById(R.id.rl_homepage_meeting);
		//广告图的大小是16（长）:9（宽），所以做此调整
		LinearLayout.LayoutParams layout = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 9*dm.widthPixels/16);
		ad_div.setLayoutParams(layout);
		group = (ViewGroup)view.findViewById(R.id.viewGroup_homepage);
		viewPager = (ViewPager) view.findViewById(R.id.viewPager_homepage);
		tvMeetname = (TextView) view.findViewById(R.id.tv_homepage_meetname);
		tvHost = (TextView) view.findViewById(R.id.tv_homepage_host);
		//tvMeetingDetails = (TextView) view.findViewById(R.id.tv_homepage_meeting_details);

	}

	private void initSearch(View view) {
		// TODO Auto-generated method stub

	}

	private void initFunction(View view) {
		//在线转诊
		view.findViewById(R.id.btn_homepage_transfer_treatment).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				SpData spData = new SpData(getActivity());
				String mobile = spData.getStringValue(SpData.keyPhoneUser, null);
				String id = spData.getStringValue(SpData.keyId, null);
				if(isRegisterSucced() && mobile != null && id != null){
					startActivity(new Intent(getActivity(),PatientAddAtivity.class));
					getActivity().overridePendingTransition(R.anim.in_right,0);
				}
				else{
					if(mobile != null && id != null){
						queryDoctorNoAudit(mobile, id);
					}
					else{
						startActivity(new Intent(getActivity(), RegisterActivity.class));
					}
				}

				//startActivity(new Intent(getActivity(),PatientAddAtivity.class));
			}
		});

		//会议通知
		view.findViewById(R.id.btn_homepage_meeting_notice).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				getActivity().startActivityForResult(new Intent(getActivity(), MeetingNoticeActivity.class), MeetingApply.RESULTCODE_MeetingApply_ApplyOK);
				//getActivity().overridePendingTransition(R.anim.in_right,0);
			}
		});
		
		//知名专家
		view.findViewById(R.id.btn_homepage_expert_known).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				startActivity(new Intent(getActivity(), ExpertKnownActivity.class));
				getActivity().overridePendingTransition(R.anim.in_right,0);
			}
		});
		
		//课程资料
		view.findViewById(R.id.btn_homepage_meeting_course).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				startActivity(new Intent(getActivity(),CourseActivity.class));
				getActivity().overridePendingTransition(R.anim.in_right,0);
			}
		});
		//医生服务
		view.findViewById(R.id.btn_homepage_doctor_service).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				startActivity("医生服务", "http://www.baidu.com");
			}
		});


	}

	protected void startActivity(String title, String url) {
		startActivity(new Intent(getContext(),WebHealthActivity.class)
						.putExtra("title", title)
						.putExtra("url", url));
		getActivity().overridePendingTransition(R.anim.in_right,0);
	}



	
	private boolean isRegisterSucced(){
		SpData spData = new SpData(getActivity().getApplicationContext());
		int va = spData.getIntValue(SpData.KeyApprovestate, -1);
		mLog.e("http", "KeyApprovestate  isRegisterSucced:"+va);
		if(va != 1){
			mLog.e("http", "return false:"+va);
			return false;
		}
		mLog.e("http", "return true:"+va);
		return true;
	}

	@SuppressLint("NewApi")
	private void initPublicity(int length) {
		if(length <= 0 /*|| (imgIdArray != null && imgIdArray.length > 0)*/){
			return;
		}

		//imgIdArray = new Drawable[length];
		for (int i = 0; i < length; i++) {
			imgIdArray[i] = getResources().getDrawable(R.drawable.h_vp1);
		}
		tips = new ImageView[imgIdArray.length];

		//将点点加入到ViewGroup中  
		tips = new ImageView[imgIdArray.length];  
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
			group.addView(imageView, layoutParams);  
		}

		//将图片装载到数组中  
		mImageViews = new ImageView[imgIdArray.length];  
		for(int i=0; i<mImageViews.length; i++){  
			ImageView imageView = new ImageView(getActivity());  
			mImageViews[i] = imageView;  
			imageView.setBackground(imgIdArray[i]);  
		} 

		//设置Adapter  
		MyAdapter myAdapter = new MyAdapter();
		viewPager.setAdapter(myAdapter);  
		//设置监听，主要是设置点点的背景  
		viewPager.setOnPageChangeListener(this);  
		//设置ViewPager的默认项, 设置为长度的100倍，这样子开始就能往左滑动  
		viewPager.setCurrentItem(mImageViews.length * 100); 

		myAdapter.setMyOnClickListener(new MyOnClickListener() {

			@Override
			public void onClick(int position) {
				onClickMeetingDetails(selectItems % mImageViews.length);
			}
		});

		/*tvMeetingDetails.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				onClickMeetingDetails(selectItems % mImageViews.length);				
			}
		});*/
		String meetname = listHomeImg.get(selectItems % mImageViews.length).getMeetname();
		if(meetname != null){
			tvMeetname.setText(meetname);
		}
		String host = listHomeImg.get(selectItems % mImageViews.length).getHost();
		if(host != null){
			tvHost.setText("主办方    "+host);
		}
		tvMeetname.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				onClickMeetingDetails(selectItems % mImageViews.length);
			}
		});
		tvHost.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				onClickMeetingDetails(selectItems % mImageViews.length);
			}
		});
	}
	private ViewPager viewPager;
	private ImageView[] tips;
	private ImageView[] mImageViews;
	private Drawable[] imgIdArray ;
	private ViewGroup group;

	/**
	 * 查看会议详情
	 * @param position
	 */
	private void onClickMeetingDetails(int position){
		/*startActivity(new Intent(getActivity(), WebMeetingDetailsActivity.class)
		.putExtra("id", listHomeImg.get(position).getMeetid()));*/
		getActivity().startActivityForResult(new Intent(getActivity(), MeetingNoticeActivity2.class)
		.putExtra("mny", listHomeImg.get(position).getMny())
		.putExtra("data", listHomeImg.get(position)), MeetingApply.RESULTCODE_MeetingApply_ApplyOK);
	}

	public interface MyOnClickListener{
		void onClick(int position);
	}

	public class MyAdapter extends PagerAdapter{

		MyOnClickListener myOnClickListener;

		public void setMyOnClickListener(MyOnClickListener myOnClickListener){
			this.myOnClickListener = myOnClickListener;
		}

		@Override
		public int getCount() {
			return Integer.MAX_VALUE;
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
		String meetname = listHomeImg.get(selectItems % mImageViews.length).getMeetname();
		if(meetname != null){
			tvMeetname.setText(meetname);
		}
		Log.d("wht", "arg0:"+arg0);

	}

	private int selectItems = 0;
	private void setImageBackground(int selectItems){  
		Log.d("wht", "selectItems:"+selectItems);
		//this.selectItems = selectItems;
		for(int i=0; i<tips.length; i++){  
			if(i == selectItems){  
				tips[i].setBackgroundResource(R.drawable.round_hb);  
			}else{  
				tips[i].setBackgroundResource(R.drawable.round_hg);  
			}  
		}  
	}

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
		stopPlay();
		isDestroy = false;
	}

	@Override
	public void onStop() {
		super.onStop();
		isF = true;
	}

	boolean isDestroy = false;
	@Override
	public void onDestroy() {
		super.onDestroy();
		isDestroy = true;
	}

	private void queryDoctorNoAudit(final String mobile, final String id){
		if(isQueryDoctorNoAuditING){
			return;
		}
		isQueryDoctorNoAuditING = true;
		reload("正在努力加载……");
		new Thread(new Runnable() {

			@Override
			public void run() {

				try {
					String s = DataService.queryDoctorNoAudit(getActivity(), mobile, id);
					mLog.d("http", "s:"+s);
					if (!HlpUtils.isEmpty(s)){
						final HttpResult hr = JSON.parseObject(s,HttpResult.class);
						if (hr != null){
							if  (hr.isSuccess()){
								RegisterInfo info = JSON.parseObject(hr.getData().toString(), RegisterInfo.class);
								int a = info.getApprovestate();
								mLog.e("http", "KeyApprovestate  fragm:"+a);
								new SpData(getActivity().getApplicationContext())
								.setIntValue(SpData.KeyApprovestate, a);
								if(info.getApprovestate() == 1){
									startActivity(new Intent(getActivity(),PatientAddAtivity.class));
									getActivity().overridePendingTransition(R.anim.in_right,0);
								}
								else{
									startActivity(new Intent(getActivity(), RegisterInfoActivity.class));
								}
								RegisterInfo.setInstance(info);
							}else{
							}
						}else{
							getActivity().runOnUiThread(new Runnable(){  
								@Override  
								public void run() {  
									Toast.makeText(getActivity(), "请求失败，请重试！", Toast.LENGTH_SHORT).show();
								}  
							});
						}
					}else{
						getActivity().runOnUiThread(new Runnable(){  
							@Override  
							public void run() {  
								Toast.makeText(getActivity(), "请检查网络后重试！", Toast.LENGTH_SHORT).show();
							}  
						});
					}
				}catch (Exception he) {
					mLog.e("http", "Exception:"+he.toString());
					he.printStackTrace();
				}
				closeLoadingDialog();
				isQueryDoctorNoAuditING = false;
			}
		}).start();
	}
	boolean isQueryDoctorNoAuditING = false;

	/**
	 * 获取后台图片信息
	 */
	public void getImageInfo(){
		if(getImgSuccess){
			mLog.w("http", "getImageInfo   return");
			return;
		}
		getImgSuccess = true;
		//reload("正在努力加载……");
		new Thread(new Runnable() {

			@Override
			public void run() {

				try {
					String ss = DataService.getDoctorHomeImg(getActivity());
					mLog.e("http", "ss："+ss);
					if (!HlpUtils.isEmpty(ss)){
						HttpResult hr = JSON.parseObject(ss,HttpResult.class);
						if (hr != null){
							if  (hr.isSuccess()){
								listHomeImg = JSON.parseArray(hr.getData().toString(), MeetingNoticeDoctor.class);
								if(!isF && listHomeImg != null){
									//先显示默认图片
									imgIdArray = new Drawable[listHomeImg.size()];
									handler.sendEmptyMessage(2);
									//获取后台图片
									for (int i = 0; i < listHomeImg.size(); i++) {
										//getImage(listHomeImg.get(i).getImgUrl(), i);
										byte[] data;
										for (int j = 0; j < 3; j++) {
											try {
												data = DataService.getImage(listHomeImg.get(i).getImgUrl());
												if(data!=null){ 
													getImgSuccess = true;
													j = 100;
													if(!isF){
														final Bitmap mBitmap = BitmapFactory.decodeByteArray(data, 0, data.length);// bitmap  
														imgIdArray[i] = new BitmapDrawable(getResources(), mBitmap);
														Message msg = new Message();
														msg.arg1 = i;
														msg.what = 3;
														handler.sendMessage(msg);
													}
												}
												else{
													getImgSuccess = false;
													Thread.sleep(3000);
												}
											} catch (Exception e) {
												j = 0;
												e.printStackTrace();
											}
										}
									}
								}
							}else{
								getImgSuccess = false;
							}
						}else{
							getImgSuccess = false;
						}
					}else{
						getImgSuccess = false;
					}
					/*String s = DataService.getDoctorHomeImg(getActivity());
					if (!HlpUtils.isEmpty(s)){
						HttpResult hr = JSON.parseObject(s,HttpResult.class);
						if (hr != null){
							if  (hr.isSuccess()){
								listHomeImg = JSON.parseArray(hr.getData().toString(), HomeImg.class);
								if(!isF && listHomeImg != null){
									//获取后台图片
									for (int i = 0; i < listHomeImg.size(); i++) {
										getImage(listHomeImg.get(i).getImgUrl(), i);
									}
									//先显示默认图片
									getActivity().runOnUiThread(new Runnable(){  
										@Override  
										public void run() {  
											initPublicity(listHomeImg.size());
										}  
									});
								}
							}else{
							}
						}else{
						}
					}else{
					}*/
				}catch (Exception he) {
					mLog.w("http", "Exception"+he.getMessage());
					getImgSuccess = false;
					he.printStackTrace();
				}
				//closeLoadingDialog();

			}
		}).start();
	}
	boolean getImgSuccess = false;

	/**
	 * 获取后台图片
	 */
	/*private void getImage(final String path,final int mImageViewsIndex){

		new Thread(new Runnable() {

			@Override
			public void run() {

				byte[] data;
				try {

					data = DataService.getImage(path);
					if(data!=null && !isF){  
						final Bitmap mBitmap = BitmapFactory.decodeByteArray(data, 0, data.length);// bitmap  
						imgIdArray[mImageViewsIndex] = new BitmapDrawable(getResources(), mBitmap);
						getActivity().runOnUiThread(new Runnable(){  
							@Override  
							public void run() {  
								mImageViews[mImageViewsIndex].setBackground(imgIdArray[mImageViewsIndex]);
							}  
						});
					}else if(!isF){
						getActivity().runOnUiThread(new Runnable(){  
							@Override  
							public void run() { 
								//mLog.d("http", msg)
								//mToast.showToast(getActivity(), "Image error!");  
							}  
						});
					} 

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();

	}*/

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


}
