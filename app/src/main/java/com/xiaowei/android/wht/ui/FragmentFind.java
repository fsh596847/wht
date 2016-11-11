package com.xiaowei.android.wht.ui;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.xiaowei.android.wht.R;
import com.xiaowei.android.wht.SpData;
import com.xiaowei.android.wht.beans.DoctorPerson;
import com.xiaowei.android.wht.model.HttpResult;
import com.xiaowei.android.wht.service.DataService;
import com.xiaowei.android.wht.utils.mLog;
import com.xiaowei.android.wht.utis.HlpUtils;
import com.xiaowei.android.wht.utis.SyncImageLoaderListview;
import com.xiaowei.android.wht.utis.Utils;
import com.xiaowei.android.wht.views.CircularImage;

public class FragmentFind extends Fragment {
	
	//专家
	private ListView lvExpert;//专家ListView
	private List<DoctorPerson> listExpert;//专家List
	private MyExpertAdapter expertAdapter;
	
		@SuppressLint("HandlerLeak")
		private Handler handler = new Handler() {  
			public void handleMessage(Message msg) {  
				// 如果有更新就提示  
				switch (msg.what) {
				case 1:
					expertAdapter.setList(listExpert);
					break;
					
				case 2:
					break;
					
				case 3:
					//msg.arg1;
					break;
				}

			};  
		}; 
		
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_find, container, false); 
		initView(view);
		return view;
	}

	private void initView(View view) {
		listExpert = new ArrayList<DoctorPerson>();
		lvExpert = (ListView) view.findViewById(R.id.listview_find);
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
		page = 15;
		success = true;
		isQuery = true;
		isFinish = false;
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
		            	if(success && isQuery){
		            		p+=1;
							getListExpert(null,null,p,page);
		            	}
		            }
		        }
			}
		});
		
		getListExpert(null,null,p,page);
		
	}
	//分页刷新
	private int p; 
	private int page;
	private boolean success;//是否更新成功
	private boolean isQuery;//是否正在更新
	private boolean isFinish;
	

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
	
	@Override
	public void onStart() {
		super.onStart();
		isFinish = false;
		isDestroy = false;
	}

	boolean isDestroy = false;
	@Override
	public void onDestroy() {
		super.onDestroy();
		isFinish = true;
		isDestroy = true;
	}
	
	/**
	 * get expert list
	 * @param detpname
	 * @param areaid
	 */
	private void getListExpert(final String detpname, final String areaid, final int p, final int pagesize){
		isQuery = false;
		reload( "正在努力加载……");
		new Thread(new Runnable() {

			@Override
			public void run() {

				try {
					
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

}
