package com.xiaowei.android.wht.ui.patient;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.xiaowei.android.wht.R;
import com.xiaowei.android.wht.model.HttpResult;
import com.xiaowei.android.wht.model.PatientSickRecord;
import com.xiaowei.android.wht.service.DataService4Patient;
import com.xiaowei.android.wht.uibase.BaseNoTitleBarActivity;
import com.xiaowei.android.wht.utils.mLog;
import com.xiaowei.android.wht.utis.HlpUtils;
import com.xiaowei.android.wht.views.SildingFinishLayout;
import com.xiaowei.android.wht.views.SildingFinishLayout.OnSildingFinishListener;

public class PatientSickRecordActivity extends BaseNoTitleBarActivity implements View.OnClickListener  {
	ImageView back_imageView;
	ListView record_listView;
	MyAdapter adapter;
	
	//分页刷新
	private int p; 
	private int page;
	private boolean success;//是否更新成功
	private boolean isQuery;//是否正在更新
	private boolean isFinish;
	private List<PatientSickRecord> listR;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.patient_act_my_sick_record);
		p = 1;
		page = 15;
		success = true;
		isQuery = true;
		isFinish = false;
		listR = new ArrayList<PatientSickRecord>();
		
		initView();
		
		queryData();
	}
	private void initView() {
		back_imageView = (ImageView)findViewById(R.id.back_imageView);
		back_imageView.setOnClickListener(this);
		record_listView = (ListView)findViewById(R.id.record_listView);
		adapter = new MyAdapter(getApplicationContext());
		record_listView.setAdapter(adapter);
		record_listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int pos,
					long arg3) {
				if (pos >-1 && pos<adapter.getCount()){
					PatientSickRecord psr = adapter.getItem(pos);
					if (psr != null){
						Intent it = new Intent(PatientSickRecordActivity.this,PatientSickRecordInfoActivity.class);
						it.putExtra("patientSickRecord", psr);
						startActivity(it);
						overridePendingTransition(R.anim.in_right,0);
					}
				}
			}
		});
		
		record_listView.setOnScrollListener(new OnScrollListener() {
			
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
			}
			
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				if (firstVisibleItem != 0 && !isFinish) {
		            //判断可视Item是否能在当前页面完全显示
		            if (visibleItemCount+firstVisibleItem == totalItemCount) {
		            	mLog.w("http", "分页  p:"+p);
		            	if(success && isQuery){
		            		p+=1;
		            		queryData();
		            		mLog.w("http", "分页  p:"+p);
		            	}
		            }
		        }
			}
		});
		
		//右滑退出
		SildingFinishLayout mSildingFinishLayout = (SildingFinishLayout) findViewById(R.id.sildingFinishLayout_act_my_sick); 
		mSildingFinishLayout.setOnSildingFinishListener(new OnSildingFinishListener() {

			@Override
			public void onSildingFinish() {
				PatientSickRecordActivity.this.finish();
			}
		});
		// touchView
		mSildingFinishLayout.setTouchView(record_listView);
	}
	final int msgQueryOk = 1001;
	final int msgQueryFail = 1002;
	@SuppressLint("HandlerLeak")
	Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case msgQueryOk:
				@SuppressWarnings("unchecked")
				List<PatientSickRecord> list = (List<PatientSickRecord>)msg.obj;
				adapter.setItems(list);
				adapter.notifyDataSetChanged();
				break;
			case msgQueryFail:
				String s = (String)msg.obj;
				HlpUtils.showToast(getApplicationContext(), s);
			default:
				break;
			}
		};
	};
	
	private void queryData() {
		isQuery = false;
		new Thread(new Runnable() {
			
			@Override
			public void run() {
//				String mobile = "13325468526";
				String mobile = DataService4Patient.getMyMobile(getApplicationContext());
				String s = null;
				try{
					s = DataService4Patient.queryPatientRecord(getApplicationContext(), null, mobile, null, null,p,page);
					if (!HlpUtils.isEmpty(s)){
						HttpResult hr = JSON.parseObject(s,HttpResult.class);
						if (hr != null && hr.isSuccess()){
							success = true;
							if(hr.getTotalpage()==p){
								isFinish = true;
							}
							List<PatientSickRecord> list = JSON.parseArray(hr.getData().toString(),PatientSickRecord.class);
							listR.addAll(list);
							handler.obtainMessage(msgQueryOk,listR).sendToTarget();
						}else{
							success = false;
						}
					}
				}catch (Exception e){
					e.printStackTrace();
					handler.obtainMessage(msgQueryFail,e.getMessage()).sendToTarget();
				}
				isQuery = true;
			}
		}).start();
	}
	@Override
	protected void onStart() {
		super.onStart();
		isFinish = false;
	}
	@Override
	protected void onPause() {
		super.onPause();
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back_imageView:
			finish();
			overridePendingTransition(0, R.anim.out_right);
			break;

		default:
			break;
		}
	}
	
	// Press the back button in mobile phone
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		overridePendingTransition(0, R.anim.out_right);
	}
	
	class MyAdapter extends BaseAdapter {
		List<PatientSickRecord> items;
		private LayoutInflater mInflater = null;

		public MyAdapter(Context context) {
			super();
			mInflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		@Override
		public int getCount() {
			if (null != items) {
				return items.size();
			} else {
				return 0;
			}
		}

		public void setItems(List<PatientSickRecord> items) {
			this.items = items;
		}

		public void addPatientSickRecords(List<PatientSickRecord> items) {
			if (this.items == null) {
				this.items = new ArrayList<PatientSickRecord>();
			}
			this.items.addAll(items);
		}

		public void clearData() {
			if (this.items != null) {
				this.items.clear();
			}
		}

		@Override
		public PatientSickRecord getItem(int arg0) {
			if (items != null) {
				return items.get(arg0);
			} else {
				return null;
			}
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View v, ViewGroup arg2) {
			ViewHolder h;
			if (null == v) {
				h = new ViewHolder();
				v = mInflater
						.inflate(R.layout.patient_act_my_sick_record_adapter, null);
				h.name_textView = (TextView) v
						.findViewById(R.id.name_textView);
				h.doctorName_textView = (TextView) v
						.findViewById(R.id.doctorName_textView);
				h.date_textView = (TextView) v.findViewById(R.id.date_textView);
				h.status_textView = (TextView) v
						.findViewById(R.id.status_textView);
				h.doctor2Name_textView = (TextView) v
						.findViewById(R.id.doctor2Name_textView);
				v.setTag(h);
			} else {
				h = (ViewHolder) v.getTag();
			}
			final PatientSickRecord psr = items.get(position);
			if (psr != null){
				h.name_textView.setText(HlpUtils.isEmpty(psr.getIllnessname())?"":psr.getIllnessname());
				h.doctorName_textView.setText(HlpUtils.isEmpty(psr.getAcceptdoctorname())?"":psr.getAcceptdoctorname());
				h.doctor2Name_textView.setText(HlpUtils.isEmpty(psr.getReferdoctorname())?"":psr.getReferdoctorname());
				h.date_textView.setText(HlpUtils.isEmpty(psr.getBooking())?"":psr.getBooking());
				h.status_textView.setText(psr.getStateStr());
			}
			return v;
		}
	}

	class ViewHolder {
		TextView name_textView, doctorName_textView, status_textView,
		date_textView,doctor2Name_textView;
	}
}
