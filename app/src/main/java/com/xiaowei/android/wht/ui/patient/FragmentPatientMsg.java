package com.xiaowei.android.wht.ui.patient;

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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.fortysevendeg.swipelistview.BaseSwipeListViewListener;
import com.fortysevendeg.swipelistview.SwipeListView;
import com.xiaowei.android.wht.R;
import com.xiaowei.android.wht.SpData;
import com.xiaowei.android.wht.beans.Message;
import com.xiaowei.android.wht.model.HttpResult;
import com.xiaowei.android.wht.service.DataService;
import com.xiaowei.android.wht.ui.PatientStateActivity;
import com.xiaowei.android.wht.utils.mLog;
import com.xiaowei.android.wht.utis.HlpUtils;
import com.xiaowei.android.wht.utis.Utils;

public class FragmentPatientMsg extends Fragment {
	
	private SwipeListView listView;
	private List<Message> listMessage;
	private MyAdapter adapter;
	//分页刷新
	private int p; 
	private int page;
	private boolean success;//是否更新成功
	private boolean isQuery;//是否正在更新
	private boolean isFinish;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.patient_fragment_friend, container, false); 
		
		/*p = 1;
		page = 20;
		success = true;
		isQuery = true;
		isFinish = false;
		listMessage = new ArrayList<Message>();
		
		initViews(view);
		
		getMyNotice(p,page);*/
		
		return view;
	}

	private void initViews(View v) {
		listView = (SwipeListView) v.findViewById(R.id.listview_patient_message);
		adapter = new MyAdapter(getActivity(), listView);
		listView.setAdapter(adapter);
		listView.setSwipeListViewListener(new SwipeListViewListener());
		listView.setOffsetLeft(getResources().getDisplayMetrics().widthPixels);
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
		            		getMyNotice(p,page);
		            	}
		            }
		        }
				if(isOpenLvRecord){
					listView.closeOpenedItems();
					isOpenLvRecord = false;
				}
			}
		});
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
		isDestroy = false;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		isDestroy = true;
	}
	
	private void updateMyNotice(final int position,final String state) {
		reload("正在努力加载……");
		new Thread(new Runnable() {
			@Override
			public void run() {

				try {
					String s = DataService.updateMyNotice(getActivity()
							,new SpData(getActivity()).getStringValue(SpData.keyPhoneUser, null)
							, listMessage.get(position).getId(),state);
					mLog.d("http", "s:"+s);
					if (!HlpUtils.isEmpty(s)){
						final HttpResult hr = JSON.parseObject(s,HttpResult.class);
						if(hr != null){
							if(hr.isSuccess()){
								if(!isDestroy){
									listMessage.get(position).setState(1);
									getActivity().runOnUiThread(new Runnable(){  
										@Override  
										public void run() { 
											adapter.setList(listMessage);
										}  
									});
								}
							}else{
								getActivity().runOnUiThread(new Runnable(){  
									@Override  
									public void run() {  
										Toast.makeText(getActivity(), hr.getData().toString(), Toast.LENGTH_SHORT).show();
									}  
								});
							}
						}else{
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
					he.printStackTrace();
				}
				
				closeLoadingDialog();

			}
		}).start();
	}
	
	private void getMyNotice(final int p,final int pagesize) {
		isQuery = false;
		reload("正在努力加载……");
		new Thread(new Runnable() {
			@Override
			public void run() {

				try {
					String s = DataService.getMyNotice(getActivity()
							,new SpData(getActivity()).getStringValue(SpData.keyPhoneUser, null)
							, pagesize, p, "PATIENT");
					mLog.d("http", "s:"+s);
					if (!HlpUtils.isEmpty(s)){
						final Message hr = JSON.parseObject(s,Message.class);
						if(hr != null){
							success = true;
							if(hr.isSuccess()){
								if(!isDestroy){
									if(hr.getTotalpage()==p){
										isFinish = true;
									}
									final List<Message> list = JSON.parseArray(hr.getData().toString(), Message.class);
									if(list != null && list.size()>0){
										listMessage.addAll(list);
										getActivity().runOnUiThread(new Runnable(){  
											@Override  
											public void run() { 
												adapter.setList(listMessage);
											}  
										});
									}
								}
							}else{
								getActivity().runOnUiThread(new Runnable(){  
									@Override  
									public void run() {  
										Toast.makeText(getActivity(), hr.getData().toString(), Toast.LENGTH_SHORT).show();
									}  
								});
							}
						}else{
							success = false;
						}
					}else{
						success = false;
						getActivity().runOnUiThread(new Runnable(){  
							@Override  
							public void run() {  
								Toast.makeText(getActivity(), "请检查网络后重试！", Toast.LENGTH_SHORT).show();
							}  
						});
					}
				}catch (Exception he) {
					he.printStackTrace();
				}
				
				closeLoadingDialog();
				isQuery = true;

			}
		}).start();
	}
	
	class SwipeListViewListener extends BaseSwipeListViewListener{
		@Override
		public void onClickFrontView(int position) {
			super.onClickFrontView(position);
			if(position >= listMessage.size()){
				return;
			}
			if(isOpenLvRecord){
				listView.closeOpenedItems();
				isOpenLvRecord = false;
				return;
			}
			
			Message message = listMessage.get(position);
			Intent intent = null;
			//医生，我的转诊
			if (message.getOptye().equals("OPEN_DOCTOR_REFER")){
				intent = new Intent(getActivity(), PatientStateActivity.class)
				.putExtra("isComNotifi", true)
				.putExtra("msgType", message.getMsgtype())
				.putExtra("id", message.getDataid());
			}
			//医生，我的接诊
			else if(message.getOptye().equals("OPEN_DOCTOR_ACCEPT")){
				intent = new Intent(getActivity(), PatientStateActivity.class)
				.putExtra("isComNotifi", true)
				.putExtra("msgType", message.getMsgtype())
				.putExtra("id", message.getDataid())
				.putExtra("type", 2);
			}
			//患者，我的转诊
			else if(message.getOptye().equals("OPEN_PATIENT_REFER")){
				intent = new Intent(getActivity(), PatientMyReferralInfoActivity.class)
				.putExtra("msgType", message.getMsgtype())
				.putExtra("id", message.getDataid())
				.putExtra("isComNotifi", true);
			}
			if(intent != null){
				startActivity(intent);
				updateMyNotice(position,"1");
			}
			
		}

		@Override
		public void onDismiss(int[] reverseSortedPositions) {
			for (int position : reverseSortedPositions) {
				//删除后台数据
				//删除item
				listMessage.remove(position);
			}
			
			adapter.notifyDataSetChanged();
		}

		@Override
		public void onStartOpen(int position, int action, boolean right) {
			super.onStartOpen(position, action, right);
			//lvRecord.closeOpenedItems();
		}
		
		@Override
		public void onOpened(int position, boolean toRight) {
			super.onOpened(position, toRight);
			if(isOpenLvRecord){
				listView.closeOpenedItems(position);
			}
			isOpenLvRecord = true;
		}
	}
	boolean isOpenLvRecord = false;
	
	public class MyAdapter extends BaseAdapter {
		private static final String tag = "GoodsRecordSlideListViewAdapter";
		
		List<Message> list = new ArrayList<Message>();
		
		private LayoutInflater mInflater = null;
		SwipeListView swipeListView;
		
		
	    public MyAdapter(Context context, SwipeListView swipeListView)
	    {
	    	this.swipeListView = swipeListView;
	        this.mInflater = LayoutInflater.from(context);
	    }
	    
	    public void setList(List<Message> list){
	    	if(list!=null){
	    		this.list = list;
	    		notifyDataSetChanged();
	    	}
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
		        convertView = mInflater.inflate(R.layout.item_message, null);
		        holder.ivPoint = (ImageView) convertView.findViewById(R.id.iv_item_message_point);
		        holder.tvTitle = (TextView)convertView.findViewById(R.id.tv_item_message_title);
		        holder.tvContent = (TextView)convertView.findViewById(R.id.tv_item_message_content);
		        holder.tvTime = (TextView)convertView.findViewById(R.id.tv_item_message_time);
		        holder.btnDel = (Button) convertView.findViewById(R.id.btn_item_message_delete);
		        convertView.setTag(holder);
		    }else
		    {
		        holder = (ViewHolder)convertView.getTag();
		    }
		    
		    if(getCount()>0)
		    {
		    	Message detail = list.get(position);
		    	holder.ivPoint.setVisibility(detail.getState()==0?View.VISIBLE:View.GONE);
		    	holder.tvTitle.setText(detail.getNtitle()==null?"":detail.getNtitle());
		    	holder.tvContent.setText(detail.getNcontent()==null?"":detail.getNcontent());
		    	holder.tvTime.setText(detail.getCreatetimeMMddHH()==null?"":detail.getCreatetimeMMddHH());
		    	holder.btnDel.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						mLog.d(tag, "btnDel onClick");
						swipeListView.closeOpenedItems();  
						swipeListView.closeAnimate(position);
						swipeListView.dismiss(position);
					}
				});
		    }
		                                                                                                 
		    return convertView;
		}
		
		class ViewHolder
		{
			public ImageView ivPoint;
			public TextView tvTitle;
			public TextView tvContent;
			public TextView tvTime;
			public Button btnDel;
		}

	}

}
