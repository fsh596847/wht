package com.xiaowei.android.wht.ui;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import com.xiaowei.android.wht.beans.Patient;
import com.xiaowei.android.wht.model.HttpResult;
import com.xiaowei.android.wht.service.DataService;
import com.xiaowei.android.wht.utils.mLog;
import com.xiaowei.android.wht.utis.HlpUtils;
import com.xiaowei.android.wht.utis.SyncImageLoaderListview;
import com.xiaowei.android.wht.utis.SyncImageLoaderListview.OnImageLoadListener;
import com.xiaowei.android.wht.utis.Utils;
import com.xiaowei.android.wht.views.CircularImage;

public class ClinicalReceptionFragment extends Fragment {
	
	private ListView listView;
	private List<Patient> listPatient;
	private MyPatientAdapter patientAdapter;
	
	private int state;
	private String areaid;
	
	//分页刷新
	private int p; 
	private int page;
	private boolean success;//是否更新成功
	private boolean isQuery;//是否正在更新
	private boolean isFinish;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_clinical_reception, container, false); 
		
		p = 1;
		page = 15;
		success = true;
		isQuery = true;
		isFinish = false;
		listPatient = new ArrayList<Patient>();
		
		init(view);
		
		return view;
	}

	private void init(View view) {
		listView = (ListView) view.findViewById(R.id.listview_clinical_reception);
		patientAdapter = new MyPatientAdapter(getActivity(), listView);
		listView.setAdapter(patientAdapter);
		
		queryDoctorReferral(-1, null, false);
		
		patientAdapter.setOnImageLoadListener(new OnImageLoadListener() {
			
			@Override
			public void onImageLoad(Integer t, Drawable drawable, CircularImage ivHead,Integer index) {
				listPatient.get(t).setDrawable(drawable);
				
			}
			@Override
			public void onError(Integer t) {
			}
		});
		
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				
				startActivityForResult(new Intent(getActivity(), PatientStateActivity.class)
				.putExtra("Patient", listPatient.get(arg2))
				.putExtra("type", 2), PatientStateActivity.resultCode_stateChange);
				getActivity().overridePendingTransition(R.anim.in_right,0);
			}
		});

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
		            		queryDoctorReferral(state, areaid, false);
		            	}
		            }
		        }
			}
		});
		
	}
	
	public void queryDoctorReferral(final int state, final String areaid,boolean isOut){
		if(isOut){
			p = 1;
			page = 15;
			isFinish = false;
			listPatient.clear();
		}
		this.state = state;
		this.areaid = areaid;
		isQuery = false;
		reload("正在努力加载……");
		new Thread(new Runnable() {

			@Override
			public void run() {

				try {
					SpData spData = new SpData(getActivity());
					String s = DataService.queryDoctorAccept(getActivity(), state, spData.getStringValue(SpData.keyId, null)
							, spData.getStringValue(SpData.keyPhoneUser, null), areaid,null,null,p,page);
					mLog.d("http", "ss："+s);
					if (!isDestroy && !HlpUtils.isEmpty(s)){
						HttpResult hr = JSON.parseObject(s,HttpResult.class);
						if (hr != null){
							if  (hr.isSuccess()){
								success = true;
								if(!isDestroy){
									if(hr.getTotalpage()==p){
										isFinish = true;
									}
									/*if(listPatient == null){
										listPatient = JSON.parseArray(hr.getData().toString(), Patient.class);
									}
									else{*/
										List<Patient> list = JSON.parseArray(hr.getData().toString(), Patient.class);
										listPatient.addAll(list);
									/*}*/
									getActivity().runOnUiThread(new Runnable(){  
										@Override  
										public void run() { 
											patientAdapter.setList(listPatient);
										}  
									});
								}
							}else{
								if(p==1){
									if(listPatient != null){
										listPatient.clear();
									}
									getActivity().runOnUiThread(new Runnable(){  
										@Override  
										public void run() { 
											patientAdapter.setList(listPatient);
										}  
									});
								}
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
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (resultCode) {
		case PatientStateActivity.resultCode_stateChange:
			
			queryDoctorReferral(state, areaid, true);
			break;

		}
	}
	
	private class MyPatientAdapter extends BaseAdapter {

		List<Patient> listPatient = new ArrayList<Patient>();
		ListView mListView;

		private LayoutInflater mInflater = null;
		//private Context mContext;
		
		SyncImageLoaderListview.OnImageLoadListener mImageLoadListener;

		private MyPatientAdapter(Context context, ListView lvPatient)
		{
			this.mInflater = LayoutInflater.from(context);
			//mContext = context;
			mListView = lvPatient;
			mListView.setOnScrollListener(onScrollListener);
		}

		private void setList(List<Patient> list)
		{
			if(list!=null){
				this.listPatient = list;
				notifyDataSetChanged();
			}
		}

		private void setOnImageLoadListener(SyncImageLoaderListview.OnImageLoadListener mImageLoadListener){
			this.mImageLoadListener = mImageLoadListener;
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
			ViewHolder holder;
			if(convertView == null)
			{
				holder = new ViewHolder();
				convertView = mInflater.inflate(R.layout.item_my_patient2, null);
				//holder.ivHead = (CircularImage) convertView.findViewById(R.id.iv_item_my_patient2_headphoto);
				holder.tvName = (TextView) convertView.findViewById(R.id.tv_my_patient2_name);
				holder.tvIllnessname = (TextView) convertView.findViewById(R.id.tv_my_patient2_illnessname);
				holder.tvPatientdesc = (TextView) convertView.findViewById(R.id.tv_my_patient2_patientdesc);
				holder.tvInfo = (TextView) convertView.findViewById(R.id.tv_my_patient2_info);
				holder.tvState = (TextView) convertView.findViewById(R.id.tv_my_patient2_state);
				holder.tvDate = (TextView) convertView.findViewById(R.id.tv_my_patient2_date);
				convertView.setTag(holder);
			}else
			{
				holder = (ViewHolder)convertView.getTag();
			}

			if(getCount()>0)
			{
					Patient expert = listPatient.get(position);
					String name = expert.getPatientname();
					if(name != null){
						holder.tvName.setText(name);
					}
					else {
						holder.tvName.setText("");
					}
					
					String illnessname = expert.getIllnessname();
					if(illnessname != null){
						holder.tvIllnessname.setText("疾病名称："+illnessname);
					}
					else {
						holder.tvIllnessname.setText("");
					}
					
					String patientdesc = expert.getPatientdesc();
					if(patientdesc != null){
						holder.tvPatientdesc.setText("病情描述："+patientdesc);
					}
					else {
						holder.tvPatientdesc.setText("");
					}
					
					int year = 0;
					String bir = expert.getBirthdate();
					if(bir != null && bir.length()>=4){
						Calendar c = Calendar.getInstance();
						bir = bir.substring(0, 4);
						year = c.get(Calendar.YEAR) - Integer.parseInt(bir);
					}
					switch (expert.getSex()) {
					case 0:
						holder.tvInfo.setText("男/"+year);
						break;

					default:
						
						holder.tvInfo.setText("女/"+year);
						break;
					}
					
					String booking = expert.getBookingDayMonth();
					String state = "";
					switch (expert.getState()) {
					case 0:
						state = "待接诊";
						holder.tvState.setTextColor(Color.parseColor("#ee5775"));
						holder.tvDate.setVisibility(View.GONE);
						break;

					case 1:
						state = "待预约";
						holder.tvState.setTextColor(Color.parseColor("#199BFC"));
						holder.tvDate.setVisibility(View.GONE);
						break;
						
					case 2:
						state = "已预约";
						holder.tvState.setTextColor(Color.parseColor("#199BFC"));
						if(booking != null){
							holder.tvDate.setText(booking);
							holder.tvDate.setTextColor(Color.WHITE);
							holder.tvDate.setBackgroundColor(Color.parseColor("#199BFC"));
							holder.tvDate.setVisibility(View.VISIBLE);
						}else{
							holder.tvDate.setVisibility(View.GONE);
						}
						break;

					case 3:
						state = "已完成";
						holder.tvState.setTextColor(Color.GRAY);
						if(booking != null){
							holder.tvDate.setText(booking);
							holder.tvDate.setTextColor(Color.parseColor("#cccccc"));
							holder.tvDate.setBackgroundColor(Color.parseColor("#f7f7f7"));
							holder.tvDate.setVisibility(View.VISIBLE);
						}else{
							holder.tvDate.setVisibility(View.GONE);
						}
						break;
						
					case 4:
						state = "已拒绝";
						holder.tvState.setTextColor(Color.parseColor("#f44e06"));
						holder.tvDate.setVisibility(View.GONE);
						break;
						
					case 5:
						state = "未响应";
						holder.tvState.setTextColor(Color.parseColor("#f44e06"));
						holder.tvDate.setVisibility(View.GONE);
						break;
					}
					holder.tvState.setText(state);
//					Drawable d = syncImageLoader.getSoftReferenceDrawable(expert.getHeadimg());
//					if(d != null){
//						holder.ivHead.setImageDrawable(d);
//					}
//					else{
//						holder.ivHead.setImageResource(R.drawable.ic_head);
//						syncImageLoader.loadImage(mContext,position,expert.getHeadimg(),imageLoadListener,holder.ivHead);
//					}
			}

			return convertView;
		}



		SyncImageLoaderListview.OnImageLoadListener imageLoadListener = new SyncImageLoaderListview.OnImageLoadListener(){

			@Override
			public void onImageLoad(Integer t, Drawable drawable,CircularImage view,Integer index) {
				//View view = mListView.getChildAt(t);
				if(view != null){
					//CircularImage v = (CircularImage) view.findViewById(R.id.iv_item_my_patient_headphoto);
					//v.setImageDrawable(drawable);
					view.setImageDrawable(drawable);
					//mLog.d("http", "imageLoadListener  onImageLoad view != null");
				}
				if(mImageLoadListener != null){
					mImageLoadListener.onImageLoad(t, drawable, view,0);
				}
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
			//private CircularImage ivHead;
			private TextView tvName;
			private TextView tvIllnessname;
			private TextView tvPatientdesc;
			private TextView tvInfo;
			private TextView tvState;
			private TextView tvDate;
		}
	}

}
