package com.xiaowei.android.wht.ui.patient;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.xiaowei.android.wht.ApplicationTool;
import com.xiaowei.android.wht.R;
import com.xiaowei.android.wht.SpData;
import com.xiaowei.android.wht.beans.BalanceDetails;
import com.xiaowei.android.wht.model.HttpResult;
import com.xiaowei.android.wht.service.DataService4Patient;
import com.xiaowei.android.wht.utils.mLog;
import com.xiaowei.android.wht.utis.HlpUtils;
import com.xiaowei.android.wht.utis.Utils;
import com.xiaowei.android.wht.views.SildingFinishLayout;
import com.xiaowei.android.wht.views.SildingFinishLayout.OnSildingFinishListener;

public class PayDetailsActivity extends Activity {
	
	private ListView listView;
	private MyAdapter adapter;
	private List<BalanceDetails> list;
	
	//分页刷新
	private int p; 
	private int page;
	private boolean success;//是否更新成功
	private boolean isQuery;//是否正在更新
	private boolean isFinish;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pay_detail);
		ApplicationTool.getInstance().activitis.add(this);//把当前Activity放入集合中
		
		findViewById(R.id.iv_pay_detail_back).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
				overridePendingTransition(0, R.anim.out_right);
			}
		});
		p = 1;
		page = 15;
		success = true;
		isQuery = true;
		isFinish = false;
		
		list = new ArrayList<BalanceDetails>();
		listView = (ListView) findViewById(R.id.listView_pay_details);
		adapter = new MyAdapter(getApplicationContext());
		listView.setAdapter(adapter);
		
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
		            		queryPatientPay();
		            	}
		            }
		        }
			}
		});
		
		queryPatientPay();
		
		//右滑退出
		SildingFinishLayout mSildingFinishLayout = (SildingFinishLayout) findViewById(R.id.sildingFinishLayout_pay_detail); 
		mSildingFinishLayout.setOnSildingFinishListener(new OnSildingFinishListener() {

			@Override
			public void onSildingFinish() {
				PayDetailsActivity.this.finish();
			}
		});
		// touchView
		mSildingFinishLayout.setTouchView(listView);
	}
	
	// Press the back button in mobile phone
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		overridePendingTransition(0, R.anim.out_right);
	}
	
	private void reload(String text){
		if (loadingDialog == null){
			loadingDialog = Utils.createLoadingDialog(PayDetailsActivity.this, text);
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
	protected void onDestroy() {
		super.onDestroy();
		isFinish = true;
		isDestroy = true;
		ApplicationTool.getInstance().activitis.remove(this);//把当前Activity移出集合中
	}
	
	private void queryPatientPay() {
		reload("正在努力加载……");
		new Thread(new Runnable() {
			@Override
			public void run() {

				try {
					String s = DataService4Patient.queryPatientPay(getApplicationContext()
							,new SpData(PayDetailsActivity.this).getStringValue(SpData.keyId, null)
							,new SpData(PayDetailsActivity.this).getStringValue(SpData.keyPhoneUser, null)
							, p, page);
					mLog.d("http", "s:"+s);
					if (!HlpUtils.isEmpty(s)){
						final HttpResult hr = JSON.parseObject(s,HttpResult.class);
						if(hr != null){
							if(hr.isSuccess()){
								success = true;
								if(hr.getTotalpage()==p){
									isFinish = true;
								}
								list.addAll(JSON.parseArray(hr.getData().toString(), BalanceDetails.class));
								if(!isDestroy && list != null && list.size()>0){
									runOnUiThread(new Runnable(){  
										@Override  
										public void run() { 
											adapter.notifyData();
										}  
									});
								}
							}else{
								if(p == 1){
									runOnUiThread(new Runnable(){  
										@Override  
										public void run() {  
											Toast.makeText(PayDetailsActivity.this, hr.getData().toString(), Toast.LENGTH_SHORT).show();
										}  
									});
								}
							}
						}else{
							success = false;
						}
					}else{
						runOnUiThread(new Runnable(){  
							@Override  
							public void run() {  
								Toast.makeText(PayDetailsActivity.this, "请检查网络后重试！", Toast.LENGTH_SHORT).show();
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
	
	public class MyAdapter extends BaseAdapter {
		
		private LayoutInflater mInflater = null;
		
	    public MyAdapter(Context context)
	    {
	        this.mInflater = LayoutInflater.from(context);
	    }
	    
	    public void  notifyData(){
	    	notifyDataSetChanged();
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
		        convertView = mInflater.inflate(R.layout.item_pay_record, null);
		        holder.ivPaytype = (ImageView) convertView.findViewById(R.id.iv_item_pay_record);
		        holder.tvIncometype = (TextView)convertView.findViewById(R.id.tv_item_pay_record_incometype);
		        holder.tvBustype = (TextView)convertView.findViewById(R.id.tv_item_pay_record_paytype);
		        holder.tvTime = (TextView)convertView.findViewById(R.id.tv_item_pay_record_time);
		        holder.tvMny = (TextView)convertView.findViewById(R.id.tv_item_pay_record_mny);
		        convertView.setTag(holder);
		    }else
		    {
		        holder = (ViewHolder)convertView.getTag();
		    }
		    
		    if(getCount()>0)
		    {
		    	BalanceDetails detail = list.get(position);
		    	holder.ivPaytype.setImageResource(detail.getPaytypeImgResourcesId());
		    	holder.tvIncometype.setText(detail.getAcceptdoctorname()==null?"":detail.getAcceptdoctorname()+"专家接诊");
		    	holder.tvBustype.setText(detail.getPaytypeStr());
		    	holder.tvTime.setText(detail.getCreatetimeMMddHHmm()==null?"":detail.getCreatetimeMMddHHmm());
		    	holder.tvMny.setText("-"+detail.getMny());
		    }
		                                                                                                 
		    return convertView;
		}
		
		class ViewHolder
		{
			public TextView tvIncometype;
			public TextView tvBustype;
			public TextView tvTime;
			public TextView tvMny;
			public ImageView ivPaytype;
		}

	}

}
