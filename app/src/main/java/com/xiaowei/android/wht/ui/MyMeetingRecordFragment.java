package com.xiaowei.android.wht.ui;

import java.util.ArrayList;
import java.util.List;

import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.xiaowei.android.wht.R;
import com.xiaowei.android.wht.SpData;
import com.xiaowei.android.wht.beans.MeetingNoticeDoctor;
import com.xiaowei.android.wht.model.HttpResult;
import com.xiaowei.android.wht.service.DataService;
import com.xiaowei.android.wht.utils.mLog;
import com.xiaowei.android.wht.utils.mToast;
import com.xiaowei.android.wht.utis.HlpUtils;
import com.xiaowei.android.wht.utis.Utils;

public class MyMeetingRecordFragment extends Fragment {
	private ListView listView;
	private List<MeetingNoticeDoctor> listMeeting;
	private MyAdapter myAdapter;
	
	private int type;
	
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
		queryMeetingNotice(0,false);
		return view;
	}

	private void init(View view) {
		listView = (ListView) view.findViewById(R.id.listview_doc_meeting_notice);
		myAdapter = new MyAdapter(getActivity(), new OnClickAdapterListener() {
			
			@Override
			public void onClickDetailsListener(int position) {
				startActivity(new Intent(getActivity(), WebMeetingDetailsActivity.class)
				.putExtra("id", listMeeting.get(position).getId()));
				getActivity().overridePendingTransition(R.anim.in_right,0);
			}
			
		});
		listView.setAdapter(myAdapter);
		
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
		            		queryMeetingNotice(0,false);
		            	}
		            }
		        }
			}
		});
	}
	
	public interface OnClickAdapterListener {   
		void onClickDetailsListener(int position); 
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
	
	/**
	 * 
	 * @param type 查询类型（必传）0：待参会；1：已参会
	 */
	public void queryMeetingNotice(final int type,final boolean isOut){
		if(isOut){
			p = 1;
			page = 15;
			isFinish = false;
			listMeeting.clear();
		}
		isQuery = false;
		reload("正在努力加载……");
		this.type = type;
		new Thread(new Runnable() {

			@Override
			public void run() {

				try {
					SpData spData = new SpData(getActivity());
					String s = DataService.queryMeetRecord(getActivity()
							, spData.getStringValue(SpData.keyPhoneUser, null), type, p, page);
					mLog.d("http", "s："+s);
					if (!isDestroy && !HlpUtils.isEmpty(s)){
						HttpResult hr = JSON.parseObject(s,HttpResult.class);
						if (hr != null){
							if  (hr.isSuccess()){
								success = true;
								if(hr.getTotalpage()==p){
									isFinish = true;
								}
								listMeeting.addAll(JSON.parseArray(hr.getData().toString(), MeetingNoticeDoctor.class));
//								if(listMeeting != null && listMeeting.size()>0){
//									for (int i = 0; i < listMeeting.size(); i++) {
//										MeetingNoticeDoctor meetingNotice = listMeeting.get(i);
//										if(meetingNotice.getImgdata() != null){
//											meetingNotice.setMeetingImgs(JSON.parseArray(meetingNotice.getImgdata(), MeetingImg.class));
//										}
//									}
//								}
								getActivity().runOnUiThread(new Runnable(){  
									@Override  
									public void run() { 
										myAdapter.setList(listMeeting);
									}  
								});
							}else{
								if(p == 1){
									getActivity().runOnUiThread(new Runnable(){  
										@Override  
										public void run() { 
											listMeeting.clear();
											myAdapter.setList(listMeeting);
											mToast.showToast(getActivity(), type==0?"没有待参会议":"没有已参会议");
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
	
	private class MyAdapter extends BaseAdapter {

		List<MeetingNoticeDoctor> listMeeting = new ArrayList<MeetingNoticeDoctor>();


		private LayoutInflater mInflater = null;

		private OnClickAdapterListener onClickListener;

		private MyAdapter(Context context,OnClickAdapterListener onClickListener)
		{
			this.mInflater = LayoutInflater.from(context);
			this.onClickListener = onClickListener;
		}

		public void setList(List<MeetingNoticeDoctor> list)
		{
			if(list!=null){
				this.listMeeting = list;
				notifyDataSetChanged();
			}
		}

		@Override
		public int getCount() {
			return listMeeting.size();
		}

		@Override
		public Object getItem(int arg0) {

			return listMeeting.get(arg0);
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
				convertView = mInflater.inflate(R.layout.item_meeting_ed, null);
				holder.tvMeetname = (TextView) convertView.findViewById(R.id.tv_meeting_ed_meetingname);
				holder.btnDetails = (Button) convertView.findViewById(R.id.tv_meeting_ed_details);
				holder.tvTime = (TextView) convertView.findViewById(R.id.tv_meeting_ed_time);
				holder.tvJoin = (TextView) convertView.findViewById(R.id.tv_meeting_ed_join);
				holder.tvAddress = (TextView) convertView.findViewById(R.id.tv_meeting_ed_address);
				holder.tvHost = (TextView) convertView.findViewById(R.id.tv_meeting_ed_host);
				
				convertView.setTag(holder);
			}else
			{
				holder = (ViewHolder)convertView.getTag();
			}

			if(getCount()>0)
			{
				MeetingNoticeDoctor meetingNoticeDoctor = listMeeting.get(position);
				
				String time = meetingNoticeDoctor.getStarttime();
				if(time != null){
					holder.tvTime.setText(time);
				}
				else {
					holder.tvTime.setVisibility(View.GONE);
				}
				
				switch (type) {
				case 0:
					holder.tvJoin.setText("已报名缴费");
					//holder.tvJoin.setTextColor(Color.parseColor("#cccccc"));
					break;

				case 1:
					holder.tvJoin.setText("已参会");
					//holder.tvJoin.setTextColor(Color.parseColor("#199BFC"));
					break;
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

			}

			return convertView;
		}


		class ViewHolder
		{
			private TextView tvMeetname;
			private Button btnDetails;
			private TextView tvTime;
			private TextView tvJoin;
			private TextView tvAddress;
			private TextView tvHost;
		}
	}

}
