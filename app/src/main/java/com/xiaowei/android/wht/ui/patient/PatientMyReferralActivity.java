package com.xiaowei.android.wht.ui.patient;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
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
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageContainer;
import com.android.volley.toolbox.ImageLoader.ImageListener;
import com.android.volley.toolbox.Volley;
import com.hillpool.LruImageCache;
import com.xiaowei.android.wht.R;
import com.xiaowei.android.wht.R.color;
import com.xiaowei.android.wht.beans.PatientReferral;
import com.xiaowei.android.wht.model.HttpResult;
import com.xiaowei.android.wht.service.DataService4Patient;
import com.xiaowei.android.wht.uibase.BaseNoTitleBarActivity;
import com.xiaowei.android.wht.utils.mLog;
import com.xiaowei.android.wht.utis.HlpUtils;
import com.xiaowei.android.wht.views.CircularImage;
import com.xiaowei.android.wht.views.SildingFinishLayout;
import com.xiaowei.android.wht.views.SildingFinishLayout.OnSildingFinishListener;

public class PatientMyReferralActivity extends BaseNoTitleBarActivity implements View.OnClickListener  {
	ImageView back_imageView;
	ListView record_listView;
	MyAdapter adapter;
	

	RequestQueue mQueue = null;
	LruImageCache lruImageCache = null;
	ImageLoader imageLoader = null;
	
	//分页刷新
	private int p; 
	private int page;
	private boolean success;//是否更新成功
	private boolean isQuery;//是否正在更新
	private boolean isFinish;
	private List<PatientReferral> listR;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE); 
		super.onCreate(savedInstanceState);
		setContentView(R.layout.patient_act_my_referral);
		p = 1;
		page = 15;
		success = true;
		isQuery = true;
		isFinish = false;
		listR = new ArrayList<PatientReferral>();
		
		initCache();
		initView();
		
		queryData();
	}
	private void initView() {
		back_imageView = (ImageView)findViewById(R.id.back_imageView);
		back_imageView.setOnClickListener(this);
		record_listView = (ListView)findViewById(R.id.record_ref_listView);
		adapter = new MyAdapter(getApplicationContext());
		record_listView.setAdapter(adapter);
		record_listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int pos,
					long arg3) {
				if (pos >-1 && pos<adapter.getCount()){
					PatientReferral dp = adapter.getItem(pos);
					if (dp != null){
						if (dp.getState()==PatientReferral.StateToBooking){
							Intent it = new Intent(PatientMyReferralActivity.this,PatientMyReferralInfoActivity.class);
							it.putExtra("patientReferral", dp);
							startActivityForResult(it, requestCodeDetail);
							overridePendingTransition(R.anim.in_right,0);
						}else{
							Intent it = new Intent(PatientMyReferralActivity.this,PatientMyReferralInfoDoneActivity.class);
							it.putExtra("patientReferral", dp);
							startActivity(it);
							overridePendingTransition(R.anim.in_right,0);
						}
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
		            	if(success && isQuery){
		            		p+=1;
		            		queryData();
		            	}
		            }
		        }
			}
		});
		
		//右滑退出
		SildingFinishLayout mSildingFinishLayout = (SildingFinishLayout) findViewById(R.id.sildingFinishLayout_act_referral); 
		mSildingFinishLayout.setOnSildingFinishListener(new OnSildingFinishListener() {

			@Override
			public void onSildingFinish() {
				PatientMyReferralActivity.this.finish();
			}
		});
		// touchView
		mSildingFinishLayout.setTouchView(record_listView);
	}
	final int requestCodeDetail = 1001;
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == requestCodeDetail && resultCode == RESULT_OK){
			finish();
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	private void initCache() {

		mQueue = Volley.newRequestQueue(PatientMyReferralActivity.this);
		lruImageCache = LruImageCache.instance();
		imageLoader = new ImageLoader(mQueue, lruImageCache);
		
	}
	final int msgQueryOk = 1001;
	final int msgQueryFail = 1002;
	@SuppressLint("HandlerLeak")
	Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case msgQueryOk:
				@SuppressWarnings("unchecked")
				List<PatientReferral> list = (List<PatientReferral>)msg.obj;
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
					s = DataService4Patient.queryPatientReferral(getApplicationContext(), null, mobile, null, null,null,null,p,page);
					mLog.w("http", "  s："+s);
					if (!HlpUtils.isEmpty(s)){
						HttpResult hr = JSON.parseObject(s,HttpResult.class);
						if (hr != null && hr.isSuccess()){
							success = true;
							if(hr.getTotalpage()==p){
								isFinish = true;
							}
							List<PatientReferral> list = JSON.parseArray(hr.getData().toString(),PatientReferral.class);
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
		List<PatientReferral> items;
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

		public void setItems(List<PatientReferral> items) {
			this.items = items;
		}

		public void addPatientReferrals(List<PatientReferral> items) {
			if (this.items == null) {
				this.items = new ArrayList<PatientReferral>();
			}
			this.items.addAll(items);
		}

		public void clearData() {
			if (this.items != null) {
				this.items.clear();
			}
		}

		@Override
		public PatientReferral getItem(int arg0) {
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
						.inflate(R.layout.patient_act_my_referral_adapter, null);
				h.name_textView = (TextView) v
						.findViewById(R.id.name_textView);
				h.hospitalName_textView = (TextView) v
						.findViewById(R.id.hospitalName_textView);
				h.date_textView = (TextView) v
						.findViewById(R.id.date_textView);
				h.status_textView = (TextView) v
						.findViewById(R.id.status_textView);
				h.pic_imageView = (CircularImage) v
						.findViewById(R.id.pic_imageView);
				
				v.setTag(h);
			} else {
				h = (ViewHolder) v.getTag();
			}
			final PatientReferral psr = items.get(position);
			if (psr != null){
				h.name_textView.setText(HlpUtils.isEmpty(psr.getDoctorname())?"":psr.getDoctorname());
				h.hospitalName_textView.setText(psr.getHospital()==null?"":psr.getHospital()
						+psr.getDept()==null?"":psr.getDept()+psr.getJobtitle()==null?"":psr.getJobtitle());
				h.date_textView.setText(psr.getBooking() == null ? "2016" : psr.getBooking());
				h.status_textView.setText(psr.getStatusStr());
				if (psr.getState() == PatientReferral.StateWaitingDoctorAccept){
					h.status_textView.setTextColor(getResources().getColor(color.color_red));
					h.date_textView.setVisibility(View.GONE);
				}else if (psr.getState() == PatientReferral.StateToBooking){
					h.status_textView.setTextColor(getResources().getColor(color.color_red));
					h.date_textView.setVisibility(View.GONE);
				}else if (psr.getState() == PatientReferral.StateBeBooked){
					h.status_textView.setTextColor(getResources().getColor(color.color_major));
					//h.date_textView.setBackgroundColor(getResources().getColor(color.color_major));
					h.date_textView.setVisibility(View.VISIBLE);
				}else if (psr.getState() == PatientReferral.StateBeFinished){
					h.status_textView.setTextColor(getResources().getColor(color.color_red));
					//h.date_textView.setBackgroundColor(getResources().getColor(color.color_major));
					h.date_textView.setVisibility(View.VISIBLE);
				}else if(psr.getState() == PatientReferral.StateToFinishing){
					h.status_textView.setTextColor(getResources().getColor(color.color_major));
					h.date_textView.setVisibility(View.GONE);
				}else if(psr.getState() == PatientReferral.StateNoAccept){
					h.status_textView.setTextColor(getResources().getColor(color.color_major));
					h.date_textView.setVisibility(View.GONE);
				}
//				Uri uri = Uri.parse(psr.getHeadimg());
//				h.pic_imageView.setImageURI(uri);
				h.pic_imageView.setImageResource(R.drawable.ic_head);
				final CircularImage headImageView = h.pic_imageView;
				if (!HlpUtils.isEmpty(psr.getHeadimg())){
					ImageListener imageListener = new ImageListener() {
						
						@Override
						public void onErrorResponse(VolleyError arg0) {
//							System.out.print(arg0.getMessage());
						}
						
						@Override
						public void onResponse(ImageContainer arg0, boolean arg1) {
							Bitmap bmp = arg0.getBitmap();
							if (bmp != null){
	//							bmp = HlpUtils.getCroppedBitmap(bmp, HlpUtils.dip2px(PatientMyDoctorActivity.this, 60));
								headImageView.setImageBitmap(bmp);
							}
						}
					};
					imageLoader.get(psr.getHeadimg(), imageListener);
				}

			}
			return v;
		}
	}

	class ViewHolder {
		CircularImage pic_imageView;
		TextView name_textView, hospitalName_textView, status_textView,
		date_textView;
	}
}
