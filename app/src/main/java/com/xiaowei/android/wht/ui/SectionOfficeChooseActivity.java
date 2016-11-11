package com.xiaowei.android.wht.ui;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.xiaowei.android.wht.ApplicationTool;
import com.xiaowei.android.wht.R;
import com.xiaowei.android.wht.beans.SectionOffice;
import com.xiaowei.android.wht.model.HttpResult;
import com.xiaowei.android.wht.service.DataService;
import com.xiaowei.android.wht.utils.mLog;
import com.xiaowei.android.wht.utis.HlpUtils;
import com.xiaowei.android.wht.utis.Utils;
import com.xiaowei.android.wht.views.SildingFinishLayout;
import com.xiaowei.android.wht.views.SildingFinishLayout.OnSildingFinishListener;

public class SectionOfficeChooseActivity extends Activity {
	
	private ListView listViewOne;//一级科室
	private MyAdapter mAdapterOne;// 
	private List<SectionOffice> listOne;//一级List
	private ListView listViewTwo;//二级科室
	private MyAdapter mAdapterTwo;
	private List<SectionOffice> listTwo;//二级List
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_area_choose);
		ApplicationTool.getInstance().activitis.add(this);//把当前Activity放入集合中
		
		initViews();
		
		initListeners();
		
		//右滑退出
		SildingFinishLayout mSildingFinishLayout = (SildingFinishLayout) findViewById(R.id.sildingFinishLayout_area_choose); 
		mSildingFinishLayout.setOnSildingFinishListener(new OnSildingFinishListener() {

			@Override
			public void onSildingFinish() {
				SectionOfficeChooseActivity.this.finish();
			}
		});
		// touchView
		mSildingFinishLayout.setTouchView(listViewOne);
		
		queryDepartment(null);
	}

	private void initViews() {
		TextView tvTitle = (TextView) findViewById(R.id.tv_area_choose_title);
		tvTitle.setText("科室");
		
		listViewOne = (ListView) findViewById(R.id.listview_area_choose_province);
		mAdapterOne = new MyAdapter(this);
		listViewOne.setAdapter(mAdapterOne);
		listViewTwo = (ListView) findViewById(R.id.listview_area_choose_city);
		mAdapterTwo = new MyAdapter(this);
		listViewTwo.setAdapter(mAdapterTwo);
	}

	private void initListeners() {
		//一级科室
		listViewOne.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				queryDepartment(listOne.get(arg2).getId());
			}
		});

		//二级科室
		listViewTwo.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				setResult(513, new Intent().putExtra("result", listTwo.get(arg2).getDepartname()));
				finish();
				overridePendingTransition(0, R.anim.out_right);
			}
		});
		
		//返回
		findViewById(R.id.iv_area_choose_back).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				finish();
				overridePendingTransition(0, R.anim.out_right);
			}
		});
		
	}
	
	private void queryDepartment(final String id){
		reload("正在努力加载……");
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				
				try {
					String s = DataService.queryDepartment(getApplicationContext(), id);
					mLog.d("http", "科室  s:"+s);
					if (!HlpUtils.isEmpty(s)){
						HttpResult hr = JSON.parseObject(s,HttpResult.class);
						if (hr != null){
							if  (hr.isSuccess()){
								if(id == null){
									//解析数据
									listOne = JSON.parseArray(hr.getData().toString(), SectionOffice.class);
									//显示lvDistrictProvince
									runOnUiThread(new Runnable(){  
										@Override  
										public void run() {  
											mAdapterOne.setList(listOne);
										}  
									});
								}
								else {
									//解析数据
									listTwo = JSON.parseArray(hr.getData().toString(), SectionOffice.class);
									//显示lvDistrictCity
									runOnUiThread(new Runnable(){  
										@Override  
										public void run() {  
											mAdapterTwo.setList(listTwo);
										}  
									});
									
								}
							}else{
								if(id != null){
									//解析数据
									listTwo = new ArrayList<SectionOffice>();
									//显示lvDistrictCity
									runOnUiThread(new Runnable(){  
										@Override  
										public void run() {  
											mAdapterTwo.setList(listTwo);
										}  
									});
									
								}
							}
						}else{
						}
					}else{
					}
				}catch (Exception he) {
					he.printStackTrace();
				}
				closeLoadingDialog();
				
			}
		}).start();
	}
	
	private void reload(String text){
		if (loadingDialog == null){
			loadingDialog = Utils.createLoadingDialog(this, text);
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
	protected void onDestroy() {
		super.onDestroy();
		ApplicationTool.getInstance().activitis.remove(this);//把当前Activity移出集合中
	}
	
	public class MyAdapter extends BaseAdapter {

		List<SectionOffice> list = new ArrayList<SectionOffice>();

		private LayoutInflater mInflater = null;

		public MyAdapter(Context context)
		{
			this.mInflater = LayoutInflater.from(context);
		}

		public void setList(List<SectionOffice> list)
		{
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
		public View getView(int position, View convertView, ViewGroup arg2) {
			ViewHolder holder;
			if(convertView == null)
			{
				holder = new ViewHolder();
				convertView = mInflater.inflate(R.layout.item_area_province_listview, null);
				holder.tvText = (TextView)convertView.findViewById(R.id.tv_item_area_province_listview);

				convertView.setTag(holder);
			}else
			{
				holder = (ViewHolder)convertView.getTag();
			}

			if(getCount()>0)
			{
				String detail = list.get(position).getDepartname();
				holder.tvText.setText(detail);
			}

			return convertView;
		}

		class ViewHolder
		{
			public TextView tvText;
		}


	}
	
}
