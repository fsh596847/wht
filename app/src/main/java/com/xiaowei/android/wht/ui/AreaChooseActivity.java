package com.xiaowei.android.wht.ui;

import java.util.List;

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
import com.xiaowei.android.wht.views.MyProvinceAdapter;
import com.xiaowei.android.wht.views.SildingFinishLayout;
import com.xiaowei.android.wht.views.MyCityAdapter.AreaChooseCallBack;
import com.xiaowei.android.wht.views.SildingFinishLayout.OnSildingFinishListener;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

/**
 * 
 * @author kevin
 *
 * {@code} RESULTCODE_AREA_CHOOSE = 515;
 * 
 * setResult(RESULTCODE_AREA_CHOOSE, new Intent().putExtra("Area", "Area类"));
 */
public class AreaChooseActivity extends Activity {
	
	private static final int RESULTCODE_AREA_CHOOSE = 515;
	
	private ListView lvProvince;//省ListView
	private MyProvinceAdapter provinceAdapter;//省Adapter 
	private List<Area> listProvince;//省List
	private int positionProvince;
	private ListView lvCity;//市ListView
	private MyCityAdapter cityAdapter;
	private List<Area> listCity;//市List
	private int positionCity;
	private List<Area> listArea;//区List
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_area_choose);
		ApplicationTool.getInstance().activitis.add(this);//把当前Activity放入集合中
		initViews();
		
		initListeners();
		
		getDistrict(null, null,"省", 0);
		
		//右滑退出
		SildingFinishLayout mSildingFinishLayout = (SildingFinishLayout) findViewById(R.id.sildingFinishLayout_area_choose); 
		mSildingFinishLayout.setOnSildingFinishListener(new OnSildingFinishListener() {

			@Override
			public void onSildingFinish() {
				AreaChooseActivity.this.finish();
			}
		});
		// touchView
		mSildingFinishLayout.setTouchView(lvProvince);
	}


	private void initListeners() {
		//地区选择（省）
		lvProvince.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				//网络获取相应省下级地区
				getDistrict(listProvince.get(arg2).getId(),null,"市",0);
				positionProvince = arg2;
			}
		});

		//地区选择（市）
		lvCity.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				getDistrict(listCity.get(arg2).getId(),null,"区",arg2);
				positionCity = arg2;
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

	// Press the back button in mobile phone
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		overridePendingTransition(0, R.anim.out_right);
	}

	private void initViews() {
		lvProvince = (ListView) findViewById(R.id.listview_area_choose_province);
		provinceAdapter = new MyProvinceAdapter(this, listProvince);
		lvProvince.setAdapter(provinceAdapter);
		lvCity = (ListView) findViewById(R.id.listview_area_choose_city);
		cityAdapter = new MyCityAdapter(this, listCity);
		lvCity.setAdapter(cityAdapter);
		
	}
	
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
									listProvince = JSON.parseArray(hr.getData().toString(), Area.class);
									//显示lvDistrictProvince
									runOnUiThread(new Runnable(){  
										@Override  
										public void run() {  
											provinceAdapter.setList(listProvince);
										}  
									});
								}
								else if("市".equals(level)){
									//解析数据
									listCity = JSON.parseArray(hr.getData().toString(), Area.class);
									//显示lvDistrictCity
									runOnUiThread(new Runnable(){  
										@Override  
										public void run() {  
											cityAdapter.setList(listCity);
										}  
									});
									
								}
								else if("区".equals(level)){
									//解析数据
									listArea = JSON.parseArray(hr.getData().toString(), Area.class);
									//显示lvDistrictCity
									runOnUiThread(new Runnable(){  
										@Override  
										public void run() {  
											cityAdapter.setListItem(listArea, position);
											lvCity.setSelection(position);
											cityAdapter.setAreaChooseCallBack(new AreaChooseCallBack() {
												
												@Override
												public void areaChooseCallBack(int position) {
													String s = listProvince.get(positionProvince).getAreaname();
													setResult(RESULTCODE_AREA_CHOOSE, new Intent()
													.putExtra("Area", listArea.get(position)
															.setAreaname(("北京".equals(s) || "天津".equals(s)|| "上海".equals(s)|| "重庆".equals(s)) 
																	? (listCity.get(positionCity).getAreaname()+listArea.get(position).getAreaname()) 
																	: (s + listCity.get(positionCity).getAreaname()+listArea.get(position).getAreaname()))));
													finish();
													overridePendingTransition(0, R.anim.out_right);
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
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		ApplicationTool.getInstance().activitis.remove(this);//把当前Activity移出集合中
	}

}
