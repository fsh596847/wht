package com.xiaowei.android.wht.ui.patient;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ToggleButton;
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
import com.xiaowei.android.wht.SpData;
import com.xiaowei.android.wht.beans.DoctorScheduleInfo;
import com.xiaowei.android.wht.beans.PatientBooking;
import com.xiaowei.android.wht.beans.PatientReferral;
import com.xiaowei.android.wht.model.HttpResult;
import com.xiaowei.android.wht.service.DataService4Patient;
import com.xiaowei.android.wht.ui.WebAgreementActivity;
import com.xiaowei.android.wht.uibase.BaseNoTitleBarActivity;
import com.xiaowei.android.wht.utils.mLog;
import com.xiaowei.android.wht.utils.mToast;
import com.xiaowei.android.wht.utis.HlpUtils;
import com.xiaowei.android.wht.views.CircularImage;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PatientMyReferralBookingActivity extends BaseNoTitleBarActivity implements View.OnClickListener  {
	ImageView back_imageView;
	CircularImage pic_imageView;
	TextView name_textView,hospitalName_textView,doctorInfo_textView,city_textView;
//	TextView dateDuration_textView;
	ImageView left_imageView,right_imageView;
	LinearLayout datePick_div,dateDuaration_div;
	TextView timeSelect_textView,pickResult_textView,submit_textView;
	TextView date1_textView,date2_textView,date3_textView,date4_textView,date5_textView,date6_textView,date7_textView;
	ListView time_listView;
	MyAdapter adapter;
	PatientReferral patientReferral;
	TextView tvChooseTime;
	private ToggleButton tBtnAgreement;

	RequestQueue mQueue = null;
	LruImageCache lruImageCache = null;
	ImageLoader imageLoader = null;
	List<DoctorScheduleInfo> bookingList;
	List<TextView> dateViewList;
	List<Date> dateList;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.patient_act_my_booking);
		patientReferral = (PatientReferral)getIntent().getSerializableExtra("patientReferral");
		initCache();
		initView();
		displayInfo();
	}
	private void initView() {
		tBtnAgreement = (ToggleButton) findViewById(R.id.toggleButton_patient_act_my_booking_agreement);
		back_imageView = (ImageView)findViewById(R.id.patient_act_my_booking_back_imageView);
		back_imageView.setOnClickListener(this);
		pic_imageView = (CircularImage)findViewById(R.id.patient_act_my_booking_pic_imageView);

		tvChooseTime = (TextView) findViewById(R.id.tv_patient_act_my_booking_choosetime);
		name_textView = (TextView)findViewById(R.id.patient_act_my_booking_name_textView);
		hospitalName_textView = (TextView)findViewById(R.id.patient_act_my_booking_hospitalName_textView);
		doctorInfo_textView = (TextView)findViewById(R.id.patient_act_my_booking_doctorInfo_textView);
		doctorInfo_textView.setOnClickListener(this);
		city_textView = (TextView)findViewById(R.id.patient_act_my_booking_city_textView);
//		dateDuration_textView = (TextView)findViewById(R.id.dateDuration_textView);
		left_imageView = (ImageView)findViewById(R.id.patient_act_my_booking_left_imageView);
		right_imageView = (ImageView)findViewById(R.id.patient_act_my_booking_right_imageView);
		datePick_div = (LinearLayout)findViewById(R.id.patient_act_my_booking_datePick_div);
		dateDuaration_div = (LinearLayout)findViewById(R.id.patient_act_my_booking_dateDuaration_div);
		timeSelect_textView = (TextView)findViewById(R.id.patient_act_my_booking_timeSelect_textView);
		pickResult_textView = (TextView)findViewById(R.id.patient_act_my_booking_pickResult_textView);
		submit_textView = (TextView)findViewById(R.id.patient_act_my_booking_submit_textView);
		submit_textView.setOnClickListener(this);
		timeSelect_textView.setOnClickListener(this);
		left_imageView.setOnClickListener(this);
		right_imageView.setOnClickListener(this);
		date1_textView = (TextView)findViewById(R.id.patient_act_my_booking_date1_textView);
		date2_textView = (TextView)findViewById(R.id.patient_act_my_booking_date2_textView);
		date3_textView = (TextView)findViewById(R.id.patient_act_my_booking_date3_textView);
		date4_textView = (TextView)findViewById(R.id.patient_act_my_booking_date4_textView);
		date5_textView = (TextView)findViewById(R.id.patient_act_my_booking_date5_textView);
		date6_textView = (TextView)findViewById(R.id.patient_act_my_booking_date6_textView);
		date7_textView = (TextView)findViewById(R.id.patient_act_my_booking_date7_textView);
		dateViewList = new ArrayList<TextView>();
		dateViewList.add(date1_textView);
		dateViewList.add(date2_textView);
		dateViewList.add(date3_textView);
		dateViewList.add(date4_textView);
		dateViewList.add(date5_textView);
		dateViewList.add(date6_textView);
		dateViewList.add(date7_textView);
		for (TextView tv:dateViewList){
			tv.setOnClickListener(this);
		}
		dateList = new ArrayList<Date>();
		adapter =  new MyAdapter(getApplicationContext());
		time_listView = (ListView)findViewById(R.id.patient_act_my_booking_time_listView);
		time_listView.setAdapter(adapter);
		time_listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int pos,
					long arg3) {
				if (pos >-1 && pos<adapter.getCount()){
					DoctorScheduleInfo pb = adapter.getItem(pos);
					if (pb != null && pb.getBookingnum()<pb.getMaximum()){
						currentPickSchedule = pb;
						adapter.notifyDataSetChanged();
						tvChooseTime.setText(pb.getStartdate()+"   "+pb.getTimes());
					}else{
						HlpUtils.showToast(getApplicationContext(), "该时段已约满，请选择日期或者其他时段");
					}
				}
			}
		});
		
		findViewById(R.id.textView_patient_act_my_booking_agreement).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				startActivity(new Intent(PatientMyReferralBookingActivity.this, WebAgreementActivity.class).putExtra("type", 2));
				overridePendingTransition(R.anim.in_right,0);
			}
		});
	}
	DoctorScheduleInfo currentPickSchedule = null;
	private void initCache() {

		mQueue = Volley.newRequestQueue(PatientMyReferralBookingActivity.this);
		lruImageCache = LruImageCache.instance();
		imageLoader = new ImageLoader(mQueue, lruImageCache);
		
	}

	private void displayInfo() {
		if (patientReferral != null){
			loadLogo(pic_imageView, patientReferral.getHeadimg(), R.drawable.icon_logo);
			name_textView.setText(patientReferral.getDoctorname());
			hospitalName_textView.setText(patientReferral.getHospital()+patientReferral.getJobtitle());
			city_textView.setText(patientReferral.getAddress());
			queryDoctorSchedule();
		}
	}
	private void loadLogo(ImageView iv,String url,int defaultImageId) {
		if (defaultImageId != 0){
			iv.setImageResource(defaultImageId);
		}
		imageLoader.get(url, imageListener);
	}
	ImageListener imageListener = new ImageListener() {
		
		@Override
		public void onErrorResponse(VolleyError arg0) {
			System.out.print(arg0.getMessage());
		}
		
		@Override
		public void onResponse(ImageContainer arg0, boolean arg1) {
			Bitmap bmp = arg0.getBitmap();
			if (bmp != null){
//				bmp = HlpUtils.getCroppedBitmap(bmp, HlpUtils.dip2px(PatientDoctorInfoActivity.this, 60));
				pic_imageView.setImageBitmap(bmp);
			}
		}
	};
	PatientBooking currentPatientBooking;
	final int msgQueryOk = 1001;
	final int msgQueryFail = 1002;
	final int msgAddBookingOk = 1003;
	final int msgAddBookingFail = 1004;
	final int msgGetWeixinPayOk = 1005;
	final int msgGetWeixinPayFail = 1006;
	@SuppressLint("HandlerLeak")
	Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case msgQueryOk:
//				List<PatientReferral> list = (List<PatientReferral>)msg.obj;
//				adapter.setItems(list);
//				adapter.notifyDataSetChanged();
				updateBookingUi();
				break;
			case msgQueryFail:
//				updateDayDiv4None();
				updateBookingUi();
				//HlpUtils.showToast(getApplicationContext(), s1);
				break;
			case msgAddBookingFail:
				String s = (String)msg.obj;
				HlpUtils.showToast(getApplicationContext(), s);
				break;
			case msgAddBookingOk:
				currentPatientBooking= (PatientBooking)msg.obj;
				showPaySelect();
				break;
			case msgGetWeixinPayOk:
				break;
			default:
				break;
			}
		}
	};
	String queryDate;
	
	private void queryDoctorSchedule() {
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				String mobile = patientReferral.getAcceptmobile();// "13325468526";
				String s = null;
				try{
					s = DataService4Patient.queryPatientBooking(getApplicationContext(), null, mobile,isFirstQuery?"0":"1", queryDate);
					mLog.d("http", "s:"+s);
					if (!HlpUtils.isEmpty(s)){
						HttpResult hr = JSON.parseObject(s,HttpResult.class);
						if (hr != null && hr.isSuccess()){
							bookingList = JSON.parseArray(hr.getData().toString(),DoctorScheduleInfo.class);
							handler.obtainMessage(msgQueryOk).sendToTarget();
						}else{
							bookingList = new ArrayList<DoctorScheduleInfo>();
							handler.obtainMessage(msgQueryFail,"没有可预约的时间").sendToTarget();
						}
					}
				}catch (Exception e){
					e.printStackTrace();
					bookingList = new ArrayList<DoctorScheduleInfo>();
					handler.obtainMessage(msgQueryFail,e.getMessage()).sendToTarget();
				}
				
			}
		}).start();
	}
	protected void showPaySelect() {
		if (currentPatientBooking != null){
			Intent it = new Intent(PatientMyReferralBookingActivity.this,BookingPayActivity.class);
			it.putExtra("patientBooking", currentPatientBooking);
			startActivityForResult(it, requestCodePay);
		}
	}
	final int requestCodePay = 1001;
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == requestCodePay){
			if (resultCode == RESULT_OK){
				Intent it = getIntent();
				setResult(RESULT_OK, it);
				finish();
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	boolean isFirstQuery = true;
	Date today;//今天
	Date firstDay;//第一天
	protected void updateBookingUi() {
		if (bookingList != null && bookingList.size()>0){
			if (isFirstQuery){
				String date = bookingList.get(0).getStartdate();
				try {
					today = HlpUtils.dateFormatterYyyy_MM_dd.parse(date);
					firstDay = HlpUtils.getNextDate(today, -7);
					updateDayDivNext();
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}
			for (int i=0;i<bookingList.size();i++){
				if (i>dateViewList.size()){
					dateViewList.get(i).setText(bookingList.get(i).getStartdate());
				}
			}
			dateDuaration_div.setVisibility(View.VISIBLE);
			pickResult_textView.setText("就诊时间");
			pickResult_textView.setVisibility(View.GONE);
			adapter.setItems(bookingList);
			//adapter.notifyDataSetChanged();
		}else{
			if (isFirstQuery){
				try {
					today = HlpUtils.dateFormatterYyyy_MM_dd.parse(
							HlpUtils.dateFormatterYyyy_MM_dd.format(new Date(new Date().getTime()+60000*60*24)));
					firstDay = HlpUtils.getNextDate(today, -7);
					updateDayDivNext();
				} catch (ParseException e) {
					e.printStackTrace();
				}
				dateDuaration_div.setVisibility(View.VISIBLE);
			}
			//dateDuaration_div.setVisibility(View.INVISIBLE);
			pickResult_textView.setText("没有可预约时间");
			pickResult_textView.setVisibility(View.VISIBLE);
			adapter.clearData();
			//adapter.notifyDataSetChanged();
		}
		adapter.notifyDataSetChanged();
	}
	private void updateDayDiv(Date fd) {
		firstDay = fd;
		dateList.clear();
		for (int i=0;i<7;i++){
			dateViewList.get(i).setVisibility(View.VISIBLE);
			Date d = HlpUtils.getNextDate(fd, i);
			dateList.add(d);
			setDateText(i,dateViewList.get(i),d);
		}
		queryDoctorSchedule4OneDay(dateViewList.get(0));
	}
	
	@Override
	protected void onStart() {
		super.onStart();
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
		case R.id.patient_act_my_booking_back_imageView:
			finish();
			break;
		case R.id.patient_act_my_booking_submit_textView:
			if(tBtnAgreement.isChecked()){
				
				submitBooking();
			}else{
				mToast.showToast(PatientMyReferralBookingActivity.this, "未同意《转诊协议》");
			}
			break;
		case R.id.patient_act_my_booking_left_imageView:
			updateDayDivprev();
			break;
		case R.id.patient_act_my_booking_right_imageView:
			updateDayDivNext();
			break;
		case R.id.patient_act_my_booking_date1_textView:
		case R.id.patient_act_my_booking_date2_textView:
		case R.id.patient_act_my_booking_date3_textView:
		case R.id.patient_act_my_booking_date4_textView:
		case R.id.patient_act_my_booking_date5_textView:
		case R.id.patient_act_my_booking_date6_textView:
		case R.id.patient_act_my_booking_date7_textView:
			queryDoctorSchedule4OneDay(v);
			break;
		case R.id.patient_act_my_booking_doctorInfo_textView:
			showDoctorInfo();
			break;
		default:
			break;
		}
	}
	
	private void showDoctorInfo() {
		Intent it = new Intent(PatientMyReferralBookingActivity.this,PatientDoctorInfoActivity.class);
		it.putExtra("doctorMobile", patientReferral.getAcceptmobile());
		startActivity(it);
	}
	private void queryDoctorSchedule4OneDay(View v) {
		int i = dateViewList.indexOf(v);
		if (i<dateList.size() && i>-1){
			Date d = dateList.get(i);
			for (TextView tv:dateViewList){
				tv.setBackgroundColor(Color.TRANSPARENT);
			}
			v.setBackgroundResource(R.drawable.btn_round);
			queryDate = HlpUtils.dateFormatterYyyy_MM_dd.format(d);
			isFirstQuery = false;
			adapter.clearData();
			adapter.notifyDataSetChanged();
			queryDoctorSchedule();
		}
	}
	private void updateDayDivprev() {
		if (firstDay != null && today != null){
			if (firstDay.getTime()>today.getTime()){
				Date fd = HlpUtils.getNextDate(firstDay, -7);
				updateDayDiv(fd);
			}
		}
	}
	private void setDateText(int index,TextView tv,Date d){
		String txt = HlpUtils.getWeekOfDateShort(d)+"\n"+HlpUtils.datetimeFormatterM_d.format(d);
		if (index==0){
			tv.setText(txt);
		}else{
			String ds = HlpUtils.datetimeFormatterd.format(d);
			if ("1".equals(ds)){
				tv.setText(HlpUtils.getWeekOfDateShort(d)+"\n"+HlpUtils.datetimeFormatterM_d.format(d));
			}else{
				tv.setText(HlpUtils.getWeekOfDateShort(d)+"\n"+HlpUtils.datetimeFormatterd.format(d));
			}
		}
	}
	private void updateDayDivNext() {
		if (firstDay != null ){
			Date fd = HlpUtils.getNextDate(firstDay, 7);
			updateDayDiv(fd);
		}
	}
	private void submitBooking() {
		if (currentPickSchedule != null){
			new Thread(new Runnable() {
				
				@Override
				public void run() {
//					String mobile = DataService4Patient.getMyMobile(getApplicationContext());
					try {
						String s = DataService4Patient.addPatientBooking(getApplicationContext()
								, new SpData(getApplicationContext()).getStringValue(SpData.keyPhoneUser, null)
								, patientReferral.getAcceptdoctor(),
								patientReferral.getAcceptid(), ""+currentPickSchedule.getDoctorscheduleid());
						if (!HlpUtils.isEmpty(s)){
							HttpResult hr = JSON.parseObject(s,HttpResult.class);
							if (hr != null ){
								if (hr.isSuccess()){
									PatientBooking pb = JSON.parseObject(hr.getData().toString(),PatientBooking.class);
									handler.obtainMessage(msgAddBookingOk,pb).sendToTarget();
								}else{
									handler.obtainMessage(msgAddBookingFail,hr.getData().toString()).sendToTarget();
								}
							}else{
								handler.obtainMessage(msgAddBookingFail,"提交失败").sendToTarget();
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
						handler.obtainMessage(msgAddBookingFail,"提交失败:"+e.getMessage()).sendToTarget();
					}
				}
			}).start();
		}else{
			HlpUtils.showToast(getApplicationContext(), "请选择预约时间");
		}
	}

	class MyAdapter extends BaseAdapter {
		List<DoctorScheduleInfo> items;
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

		public void setItems(List<DoctorScheduleInfo> items) {
			this.items = items;
		}

		public void addPatientReferrals(List<DoctorScheduleInfo> items) {
			if (this.items == null) {
				this.items = new ArrayList<DoctorScheduleInfo>();
			}
			this.items.addAll(items);
		}

		public void clearData() {
			if (this.items != null) {
				this.items.clear();
			}
		}

		@Override
		public DoctorScheduleInfo getItem(int arg0) {
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
						.inflate(R.layout.patient_act_my_booking_adapter_time, null);
				h.time_textView = (TextView) v
						.findViewById(R.id.time_textView);
				
				v.setTag(h);
			} else {
				h = (ViewHolder) v.getTag();
			}
			final DoctorScheduleInfo psr = items.get(position);
			if (psr != null){
				if (currentPickSchedule != null && currentPickSchedule == psr){
					h.time_textView.setBackgroundColor(Color.parseColor("#199BFC"));
					h.time_textView.setTextColor(Color.parseColor("#FFFFFF"));
				}else{
					h.time_textView.setBackgroundColor(Color.TRANSPARENT);
					h.time_textView.setTextColor(color.color_333);
				}
				h.time_textView.setText(psr.getTimes());
				if (psr.getMaximum()<=psr.getBookingnum()){
					h.time_textView.setTextColor(color.gray_light);
				}else{
					h.time_textView.setTextColor(color.color_333);
				}
			}
			return v;
		}
	}

	class ViewHolder {
		TextView time_textView;
	}
}
