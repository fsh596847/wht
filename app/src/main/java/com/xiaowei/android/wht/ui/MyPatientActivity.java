package com.xiaowei.android.wht.ui;

import java.util.ArrayList;
import java.util.List;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.xiaowei.android.wht.ApplicationTool;
import com.xiaowei.android.wht.R;
import com.xiaowei.android.wht.beans.Area;
import com.xiaowei.android.wht.model.HttpResult;
import com.xiaowei.android.wht.service.DataService;
import com.xiaowei.android.wht.utils.mLog;
import com.xiaowei.android.wht.utis.HlpUtils;
import com.xiaowei.android.wht.utis.Utils;
import com.xiaowei.android.wht.views.MyCityAdapter;
import com.xiaowei.android.wht.views.MyCityAdapter.AreaChooseCallBack;
import com.xiaowei.android.wht.views.MyProvinceAdapter;

public class MyPatientActivity extends FragmentActivity {
	
	
	private Button btnTransferTreatment,btnClinicalReception;
	private ViewPager viewPager;
	private int viewPagerCurrentItem = 1;
	
	//状态
	private TextView tvState;
	private ListView lvState;//ListView
	private List<String> listState;//
	private MySectionOfficeAdapter stateAdapter;
	private int stateIndex;
	private List<String> listState1;//
	private List<String> listState0;//
	
	//地区
	private LinearLayout llDistrict;//地区列表容器（省、市……）
	private TextView tvArea;

	private ListView lvDistrictProvince;//省ListView
	private MyProvinceAdapter provinceAdapter;//省Adapter 
	private List<Area> listDistrictProvince;//省List
	private ListView lvDistrictCity;//市ListView
	private MyCityAdapter cityAdapter;
	private List<Area> listDistrictCity;//市List
	private List<Area> listDistrictArea;//区List
	private int chooseAreaIndex = -1;
	
	private ClinicalReceptionFragment clinicalReceptionFragment;
	private TransferTreatmentFragment transferTreatmentFragment;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my_patient);
		ApplicationTool.getInstance().activitis.add(this);//把当前Activity放入集合中
		listState1 = new ArrayList<String>();
		listState1.add("待接诊");
		listState1.add("待预约");
		listState1.add("已预约");
		listState1.add("已完成");
		listState1.add("已拒绝");
		listState1.add("未响应");
		listState1.add("全部");
		
		listState0 = new ArrayList<String>();
		listState0.add("待接诊");
		listState0.add("待预约");
		listState0.add("已预约");
		listState0.add("已完成");
		listState0.add("已拒绝");
		listState0.add("未响应");
		listState0.add("全部");
		
		listState = viewPagerCurrentItem==1 ? listState1 : listState0;
		
		initViews();
		
		initListeners();
		
		initFragments();
	}

	private void initFragments() {
		transferTreatmentFragment = new TransferTreatmentFragment();
		clinicalReceptionFragment = new ClinicalReceptionFragment();
		ArrayList<Fragment> fList = new ArrayList<Fragment>();
		fList.add(transferTreatmentFragment);
		fList.add(clinicalReceptionFragment);
		viewPager.setAdapter(new MyFragmentPagerAdapter(getSupportFragmentManager(), fList));
		viewPager.setCurrentItem(viewPagerCurrentItem);
		viewPager.setOnPageChangeListener(new OnPageChangeListener() {
			
			@Override
			public void onPageSelected(int arg0) {
				switch (arg0) {
				case 0:
					btnTransferTreatment.setBackgroundResource(R.drawable.btn_left_g);
					btnTransferTreatment.setTextColor(Color.parseColor("#ffffff"));
					btnClinicalReception.setBackgroundResource(R.drawable.btn_right_w);
					btnClinicalReception.setTextColor(Color.parseColor("#666666"));
					break;

				case 1:
					btnTransferTreatment.setBackgroundResource(R.drawable.btn_left_w);
					btnTransferTreatment.setTextColor(Color.parseColor("#666666"));
					btnClinicalReception.setBackgroundResource(R.drawable.btn_right_g);
					btnClinicalReception.setTextColor(Color.parseColor("#ffffff"));
					break;
				}
				viewPagerCurrentItem = arg0;
				initCondition();
				qurey();
				
			}
			
			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				
			}
			
			@Override
			public void onPageScrollStateChanged(int arg0) {
				
			}
		});
	}

	private void initListeners() {
		//返回
		findViewById(R.id.iv_my_patient_back).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				if(lvState.getVisibility() == View.VISIBLE){
					//选择科室页面，关闭
					lvState.setVisibility(View.GONE);
				}
				else if(llDistrict.getVisibility() == View.VISIBLE){
					//选择地区页面，关闭
					llDistrict.setVisibility(View.GONE);
				}
				else{
					finish();
				}
			}
		});
		
		//新增患者
		findViewById(R.id.btn_my_patient_add).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				startActivity(new Intent(MyPatientActivity.this,PatientAddAtivity.class));
			}
		});
		
		//我的转诊
		btnTransferTreatment.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				viewPagerCurrentItem = 0;
				viewPager.setCurrentItem(viewPagerCurrentItem);
				initCondition();
				//qurey();
			}
		});
		
		//我的接诊
		btnClinicalReception.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				viewPagerCurrentItem = 1;
				viewPager.setCurrentItem(viewPagerCurrentItem);
				initCondition();
				//qurey();
			}
		});
		
		//科室列表展示
		findViewById(R.id.btn_my_patient_section_office).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(lvState.getVisibility() == View.VISIBLE){
					//选择科室页面，关闭
					lvState.setVisibility(View.GONE);
				}
				else{
					if(llDistrict.getVisibility() == View.VISIBLE){
						//选择地区页面，关闭
						llDistrict.setVisibility(View.GONE);
					}
					//网络获取科室list
					//getListSectionOffice();
					lvState.setVisibility(View.VISIBLE);
				}
			}
		});

		//选择科室
		lvState.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				stateIndex = arg2;
				lvState.setVisibility(View.GONE);
				Drawable drawable = getResources().getDrawable(R.drawable.down_b);
				drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight()); 
				tvState.setCompoundDrawables(null, null, drawable, null);
				tvState.setText(listState.get(arg2));
				//查询
				qurey();
			}
		});
		
		//地区列表展示
		findViewById(R.id.btn_my_patient_district).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(llDistrict.getVisibility() == View.VISIBLE){
					//选择地区页面，关闭
					llDistrict.setVisibility(View.GONE);
				}
				else{
					if(lvState.getVisibility() == View.VISIBLE){
						//选择科室页面，关闭
						lvState.setVisibility(View.GONE);
					}
					// 网络获取地区（省）
					getDistrict(null, null,"省", 0);
					llDistrict.setVisibility(View.VISIBLE);
				}
			}
		});

		//地区选择（省）
		lvDistrictProvince.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				if(arg2 == 0){
					chooseAreaIndex = -1;
					tvArea.setText("地区");
					llDistrict.setVisibility(View.GONE);
					Drawable drawable = getResources().getDrawable(R.drawable.down_g);
					drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight()); 
					tvArea.setCompoundDrawables(null, null, drawable, null);
					//查询
					qurey();
				}
				else{
					//网络获取相应省下级地区
					getDistrict(listDistrictProvince.get(arg2).getId(),null,"市",0);
				}
			}
		});

		//地区选择（市）
		lvDistrictCity.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				getDistrict(listDistrictCity.get(arg2).getId(),null,"区",arg2);

			}
		});
		
	}

	protected void qurey() {
		switch (viewPagerCurrentItem) {
		case 0:
			transferTreatmentFragment.queryDoctorReferral(stateIndex
					, chooseAreaIndex == -1 ? null : listDistrictArea.get(chooseAreaIndex).getId(),true);
			break;

		case 1:
			clinicalReceptionFragment.queryDoctorReferral(stateIndex
					, chooseAreaIndex == -1 ? null : listDistrictArea.get(chooseAreaIndex).getId(),true);
			break;
		}
	}

	private void initViews() {
		btnTransferTreatment = (Button) findViewById(R.id.btn_my_patient_transfer_treatment);
		btnClinicalReception = (Button) findViewById(R.id.btn_my_patient_clinical_reception);
		viewPager = (ViewPager) findViewById(R.id.viewpager_my_patient);
		
		tvState = (TextView) findViewById(R.id.tv_my_patient_section_office);
		lvState = (ListView) findViewById(R.id.listview_my_patient_section_office);
		stateAdapter = new MySectionOfficeAdapter(getApplicationContext(), listState);
		lvState.setAdapter(stateAdapter);
		
		llDistrict = (LinearLayout) findViewById(R.id.ll_my_patient_district);
		tvArea = (TextView) findViewById(R.id.tv_my_patient_district);
		lvDistrictProvince = (ListView) findViewById(R.id.listview_my_patient_province);
		provinceAdapter = new MyProvinceAdapter(this, listDistrictProvince);
		lvDistrictProvince.setAdapter(provinceAdapter);
		lvDistrictCity = (ListView) findViewById(R.id.listview_my_patient_city);
		cityAdapter = new MyCityAdapter(this, listDistrictCity);
		lvDistrictCity.setAdapter(cityAdapter);
		
	}
	
	private void initCondition(){
		
		listState = viewPagerCurrentItem==1 ? listState1 : listState0;
		stateAdapter.setList(listState);
		
		chooseAreaIndex = -1;
		tvArea.setText("地区");
		llDistrict.setVisibility(View.GONE);
		Drawable drawable = getResources().getDrawable(R.drawable.down_g);
		drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight()); 
		tvArea.setCompoundDrawables(null, null, drawable, null);

		stateIndex = -1;
		tvState.setText("状态");
		lvState.setVisibility(View.GONE);
		drawable = getResources().getDrawable(R.drawable.down_g);
		drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight()); 
		tvState.setCompoundDrawables(null, null, drawable, null);

	}
	
	private class MyFragmentPagerAdapter extends FragmentStatePagerAdapter{
		
		private ArrayList<Fragment> list;

		public MyFragmentPagerAdapter(FragmentManager fm,ArrayList<Fragment> fList) {
			super(fm);
			list = fList;
		}

		@Override
		public Fragment getItem(int arg0) {
			return list.get(arg0);
		}

		@Override
		public int getCount() {
			return list.size();
		}
		
	}
	
	/**
	 * get area
	 * @param uppk
	 * @param areaid
	 * @param level
	 * @param position
	 */
	private void getDistrict(final String uppk,final String areaid,final String level, final int position){
		reload("正在努力加载……");
		new Thread(new Runnable() {

			@Override
			public void run() {

				try {
					String s = DataService.getArea(getApplicationContext(), uppk, areaid, level);
					mLog.d("http", "s:"+s);
					if (!HlpUtils.isEmpty(s)){
						HttpResult hr = JSON.parseObject(s,HttpResult.class);
						if (hr != null){
							if  (hr.isSuccess()){
								if("省".equals(level)){
									//解析数据
									listDistrictProvince = JSON.parseArray(hr.getData().toString(), Area.class);
									Area area = new Area();
									area.setAreaname("不限");
									listDistrictProvince.add(0, area);
									//显示lvDistrictProvince
									runOnUiThread(new Runnable(){  
										@Override  
										public void run() {  
											provinceAdapter.setList(listDistrictProvince);
										}  
									});
								}
								else if("市".equals(level)){
									//解析数据
									listDistrictCity = JSON.parseArray(hr.getData().toString(), Area.class);
									//显示lvDistrictCity
									runOnUiThread(new Runnable(){  
										@Override  
										public void run() {  
											cityAdapter.setList(listDistrictCity);
										}  
									});

								}
								else if("区".equals(level)){
									//解析数据
									listDistrictArea = JSON.parseArray(hr.getData().toString(), Area.class);
									//显示lvDistrictCity
									runOnUiThread(new Runnable(){  
										@Override  
										public void run() {  
											cityAdapter.setListItem(listDistrictArea, position);
											lvDistrictCity.setSelection(position);
											cityAdapter.setAreaChooseCallBack(new AreaChooseCallBack() {

												@Override
												public void areaChooseCallBack(int position) {
													chooseAreaIndex = position;
													tvArea.setText(listDistrictArea.get(position).getAreaname());
													llDistrict.setVisibility(View.GONE);
													Drawable drawable = getResources().getDrawable(R.drawable.down_b);
													drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight()); 
													tvArea.setCompoundDrawables(null, null, drawable, null);
													//查询
													qurey();
												}
											});
										}  
									});
								}

							}else{
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
	
	private class MySectionOfficeAdapter extends BaseAdapter {
		List<String> list = new ArrayList<String>();
		
		private LayoutInflater mInflater = null;
		
		private MySectionOfficeAdapter(Context context, List<String> list)
	    {
	        this.mInflater = LayoutInflater.from(context);
	        this.list = list;
	    }
		
		public void setList(List<String> list){
			this.list = list;
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
		public View getView(int position, View convertView, ViewGroup arg2) {
			ViewHolder holder;
		    if(convertView == null)
		    {
		        holder = new ViewHolder();
		        convertView = mInflater.inflate(R.layout.item_job_listview, null);
		        holder.tvText = (TextView)convertView.findViewById(R.id.tv_item_job_listview);
		        
		        convertView.setTag(holder);
		    }else
		    {
		        holder = (ViewHolder)convertView.getTag();
		    }
		    
		    if(getCount()>0)
		    {
		    	String detail = list.get(position);
		    	holder.tvText.setText(detail);
		    }
		                                                                                                 
		    return convertView;
		}
		
		class ViewHolder
		{
			public TextView tvText;
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
		ApplicationTool.getInstance().activitis.remove(this);//把当前Activity移出集合中
	}

}
