package com.xiaowei.android.wht.ui;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.xiaowei.android.wht.ApplicationTool;
import com.xiaowei.android.wht.R;
import com.xiaowei.android.wht.SpData;
import com.xiaowei.android.wht.beans.Area;
import com.xiaowei.android.wht.beans.DoctorPerson;
import com.xiaowei.android.wht.beans.Patient;
import com.xiaowei.android.wht.beans.SectionOffice;
import com.xiaowei.android.wht.model.HttpResult;
import com.xiaowei.android.wht.service.DataService;
import com.xiaowei.android.wht.utils.mLog;
import com.xiaowei.android.wht.utils.mToast;
import com.xiaowei.android.wht.utis.HlpUtils;
import com.xiaowei.android.wht.utis.SyncImageLoaderListview;
import com.xiaowei.android.wht.utis.Utils;
import com.xiaowei.android.wht.views.CircularImage;
import com.xiaowei.android.wht.views.MyCityAdapter;
import com.xiaowei.android.wht.views.MyCityAdapter.AreaChooseCallBack;
import com.xiaowei.android.wht.views.MyProvinceAdapter;
import com.xiaowei.android.wht.views.SildingFinishLayout;
import com.xiaowei.android.wht.views.SildingFinishLayout.OnSildingFinishListener;

public class ExpertFindActivity extends Activity {
	private static final int TransferTreatmentResult = 500;

	//科室
	private String detpname;
	private TextView tvSectionOffice;
	/*private ListView lvSectionOffice;//科室ListView
	private List<String> listSectionOffice;//科室list
	private MySectionOfficeAdapter sectionOfficeAdapter;*/
	private LinearLayout llSectionOffice;
	private ListView listViewOne;//一级科室
	private MyAdapter mAdapterOne;// 
	private List<SectionOffice> listOne;//一级List
	private ListView listViewTwo;//二级科室
	private MyAdapter mAdapterTwo;
	private List<SectionOffice> listTwo;//二级List
	
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
	//专家
	private ListView lvExpert;//专家ListView
	private List<DoctorPerson> listExpert;//专家List
	private MyExpertAdapter expertAdapter;
	private int choosePosition = -1;
	
	private Patient patient;
	
	private boolean isAgain;
	private String referid;
	public static final int resultCode_stateChange = 516;
	
	private int p; 
	private int page;
	private boolean success;//是否更新成功
	private boolean isQuery;//是否正在更新
	private boolean isFinish;
	
	SildingFinishLayout mSildingFinishLayout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_expert_find);
		ApplicationTool.getInstance().activitis.add(this);//把当前Activity放入集合中
		Intent data = getIntent();
		patient = (Patient) data.getSerializableExtra("Patient");
		isAgain = data.getBooleanExtra("again", false);
		referid = data.getStringExtra("referid");

		if(patient != null || isAgain){
			p = 1;
			page = 15;
			isFinish = false;
			success = true;
			isQuery = true;
			
			initViews();
			initListeners();
			getListExpert(null, null,p,page);
			
			//右滑退出
			mSildingFinishLayout = (SildingFinishLayout) findViewById(R.id.sildingFinishLayout_expert_find); 
			mSildingFinishLayout.setOnSildingFinishListener(new OnSildingFinishListener() {

				@Override
				public void onSildingFinish() {
					ExpertFindActivity.this.finish();
				}
			});
			// touchView
			mSildingFinishLayout.setTouchView(lvExpert);
		}

	}

	private void initListeners() {

		//科室列表展示
		findViewById(R.id.btn_expert_find_section_office).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//网络获取科室list
				//getListSectionOffice();
				queryDepartment(null);
				llSectionOffice.setVisibility(View.VISIBLE);
				mSildingFinishLayout.setTouchView(listViewOne);
			}
		});
		
		//一级科室
		listViewOne.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				if(arg2 == 0){
					detpname = null;
					Drawable drawable = getResources().getDrawable(R.drawable.down_g);
					drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
					tvSectionOffice.setCompoundDrawables(null, null, drawable, null);
					llSectionOffice.setVisibility(View.GONE);
					tvSectionOffice.setText(detpname == null ? "科室" : detpname);
					//查询专家
					p = 1;
					page = 15;
					isFinish = false;
					listExpert = null;
					getListExpert(detpname, chooseAreaIndex != -1 ? listDistrictArea.get(chooseAreaIndex).getId() : null,p,page);
					mSildingFinishLayout.setTouchView(lvExpert);
				}
				queryDepartment(listOne.get(arg2).getId());
			}
		});

		//选择科室
		listViewTwo.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				Drawable drawable;
				/*if(arg2 == 0){
					detpname = null;
					drawable = getResources().getDrawable(R.drawable.down_g);
				}
				else{*/
					
					detpname = listTwo.get(arg2).getDepartname();
					drawable = getResources().getDrawable(R.drawable.down_b);
				/*}*/
				drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight()); 
				tvSectionOffice.setCompoundDrawables(null, null, drawable, null);
				llSectionOffice.setVisibility(View.GONE);
				tvSectionOffice.setText(detpname == null ? "科室" : detpname);
				//查询专家
				p = 1;
				page = 15;
				isFinish = false;
				listExpert = null;
				getListExpert(detpname, chooseAreaIndex != -1 ? listDistrictArea.get(chooseAreaIndex).getId() : null,p,page);
				mSildingFinishLayout.setTouchView(lvExpert);
			}
		});

		//地区列表展示
		findViewById(R.id.btn_expert_find_district).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// 网络获取地区（省）
				getDistrict(null, null,"省", 0);
				llDistrict.setVisibility(View.VISIBLE);
				mSildingFinishLayout.setTouchView(lvDistrictProvince);
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
					//查询专家
					p = 1;
					page = 15;
					isFinish = false;
					listExpert = null;
					getListExpert(detpname, null,p,page);
					mSildingFinishLayout.setTouchView(lvExpert);
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

		//请求转诊
		findViewById(R.id.btn_expert_find_ask).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				if(choosePosition != -1){
					if(isAgain){
						againRefer(0, referid, listExpert.get(choosePosition).getUserid());
					}
					else{
						SpData data = new SpData(getApplicationContext());
						addReferral(0, data.getStringValue(SpData.keyPhoneUser, null)
								, data.getStringValue(SpData.keyId, null), listExpert.get(choosePosition).getUserid()
								, listExpert.get(choosePosition).getDoctorname());
					}
				}else{
					mToast.showToast(getApplicationContext(), "请选择接诊医生");
				}
			}
		});

		//系统转诊
		findViewById(R.id.btn_expert_find_system).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(isAgain){
					againRefer(1, referid, null);
				}
				else{
					SpData data = new SpData(getApplicationContext());
					addReferral(1, data.getStringValue(SpData.keyPhoneUser, null)
							, data.getStringValue(SpData.keyId, null), null, null);
				}
			}
		});

		//返回
		findViewById(R.id.iv_expert_find_back).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				if(llSectionOffice.getVisibility() == View.VISIBLE){
					//选择科室页面，关闭
					llSectionOffice.setVisibility(View.GONE);
					mSildingFinishLayout.setTouchView(lvExpert);
				}else if(llDistrict.getVisibility() == View.VISIBLE){
					//选择地区页面，关闭
					llDistrict.setVisibility(View.GONE);
					mSildingFinishLayout.setTouchView(lvExpert);
				}else{
					finish();
					overridePendingTransition(0, R.anim.out_right);
				}
			}
		});

		//搜索
		findViewById(R.id.iv_expert_find_search).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
			}
		});

		//选择专家
		lvExpert.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				if(choosePosition != arg2){
					choosePosition = arg2;
				}else{//已选择，取消
					choosePosition = -1;
				}
				expertAdapter.setChoosePosition(choosePosition);
			}
		});

		//专家详情
		expertAdapter.setOnClickDetailsListener(new OnClickDetailsListener() {

			@Override
			public void onClickDetailsListener(int position) {
				startActivity(new Intent(ExpertFindActivity.this,ExpertInforActivity.class)
				.putExtra("DoctorPerson", listExpert.get(position)));
			}
		});
		
		//专家头像
		expertAdapter.setOnImageLoadListener(new SyncImageLoaderListview.OnImageLoadListener(){

			@Override
			public void onImageLoad(Integer t, Drawable drawable, CircularImage ivHead,Integer index) {
				//listExpert.get(t).setDrawable(drawable);
			}
			@Override
			public void onError(Integer t) {
			}

		});
		//
		lvExpert.setOnScrollListener(new OnScrollListener() {
			
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
		            		getListExpert(detpname, chooseAreaIndex != -1 ? listDistrictArea.get(chooseAreaIndex).getId() : null,p,page);
		            	}
		            }
		        }
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
		tvSectionOffice = (TextView) findViewById(R.id.tv_expert_find_section_office);
		llSectionOffice = (LinearLayout) findViewById(R.id.ll_expert_find_section_office);
		listViewOne = (ListView) findViewById(R.id.listview_expert_find_section_office1);
		listViewTwo = (ListView) findViewById(R.id.listview_expert_find_section_office2);
		mAdapterOne = new MyAdapter(ExpertFindActivity.this);
		mAdapterTwo = new MyAdapter(ExpertFindActivity.this);
		listViewOne.setAdapter(mAdapterOne);
		listViewTwo.setAdapter(mAdapterTwo);
		llDistrict = (LinearLayout) findViewById(R.id.ll_expert_find_district);
		tvArea = (TextView) findViewById(R.id.tv_expert_find_district);
		lvDistrictProvince = (ListView) findViewById(R.id.listview_expert_find_district_province);
		provinceAdapter = new MyProvinceAdapter(this, listDistrictProvince);
		lvDistrictProvince.setAdapter(provinceAdapter);
		lvDistrictCity = (ListView) findViewById(R.id.listview_expert_find_district_city);
		cityAdapter = new MyCityAdapter(this, listDistrictCity);
		lvDistrictCity.setAdapter(cityAdapter);
		lvExpert = (ListView) findViewById(R.id.listview_expert_find_expert);
		expertAdapter = new MyExpertAdapter(getApplicationContext(),lvExpert);
		lvExpert.setAdapter(expertAdapter);
	}
	
	//拒绝
	private void againRefer(final int bustype, final String referid, final String acceptdoctor){
		reload("正在努力加载……");
		new Thread(new Runnable() {

			@Override
			public void run() {

				try {
					String s = DataService.againRefer(getApplicationContext()
							, new SpData(getApplicationContext()).getStringValue(SpData.keyPhoneUser, null)
							, acceptdoctor, bustype, referid);
					mLog.d("http", "s："+s);
					if (!isDestroy && !HlpUtils.isEmpty(s)){
						final HttpResult hr = JSON.parseObject(s,HttpResult.class);
						if (hr != null){
							if  (hr.isSuccess()){
								runOnUiThread(new Runnable(){  
									@Override  
									public void run() {
										mToast.showToast(getApplicationContext(), "转诊成功");
										setResult(resultCode_stateChange);
										finish();
									}  
								});
							}else{
								runOnUiThread(new Runnable(){  
									@Override  
									public void run() { 
										mToast.showToast(getApplicationContext(), hr.getData().toString());
									}  
								});
							}
						}else{
							runOnUiThread(new Runnable(){  
								@Override  
								public void run() { 
									mToast.showToast(getApplicationContext(), "操作失败，请重试");
								}  
							});
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
									SectionOffice o = new SectionOffice();
									o.setDepartname("全部");
									listOne.add(0, o);
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
	
	/**
	 * 
	 * @param bustype 0:人工转诊；1：；系统转诊
	 * @param 用户医生 
	 * @param mobile 手机
	 * @param referdoctor id
	 * @param 接诊医生 (人工转诊:必须)
	 * @param acceptdoctor id
	 * @param acceptdoctorname 姓名
	 */
	private void addReferral(final int bustype, final String mobile, final String referdoctor
			, final String acceptdoctor, final String acceptdoctorname){
		if(mobile == null || referdoctor == null){
			return;
		}
		switch (bustype) {
		case 0://人工转诊
			if(acceptdoctor == null || acceptdoctorname == null){
				mToast.showToast(getApplicationContext(), "请选择接诊医生");
				return;
			}
			break;
		}
		
		reload("正在请求转诊……");
		new Thread(new Runnable() {

			@Override
			public void run() {
				uploadHeadImg(Utils.getImageToBase64Str(patient.getHeadimg()));
				/*if(uploadHeadImg(Utils.getImageToBase64Str(patient.getHeadimg()))){//头像改为非必须要求*/
					if(patient.getPatientimgone() != null){
						if(uploadAttestationImg(1, Utils.getImageToBase64Str(patient.getPatientimgone()))){

							if(patient.getPatientimgtwo() != null){
								if(uploadAttestationImg(2, Utils.getImageToBase64Str(patient.getPatientimgtwo()))){

									if(patient.getPatientimgthree() != null){
										if(uploadAttestationImg(3, Utils.getImageToBase64Str(patient.getPatientimgthree()))){
											if(addReferralNetwork(bustype, mobile, referdoctor, acceptdoctor, acceptdoctorname)){
												startActivityForResult(new Intent(ExpertFindActivity.this,TransferTreatmentResultActivity.class)
												.putExtra("type", bustype), TransferTreatmentResult);
												overridePendingTransition(R.anim.in_right,0);
											}else{
												/*runOnUiThread(new Runnable(){  
													@Override  
													public void run() {  
														mToast.showToast(getApplicationContext(), "转诊失败，请重试");
													}  
												});*/
											}
										}else{
											/*runOnUiThread(new Runnable(){  
												@Override  
												public void run() {  
													mToast.showToast(getApplicationContext(), "影像资料3上传失败，请重试");
												}  
											});*/
										}
									}else{//没有影像资料3
										if(addReferralNetwork(bustype, mobile, referdoctor, acceptdoctor, acceptdoctorname)){
											startActivityForResult(new Intent(ExpertFindActivity.this,TransferTreatmentResultActivity.class)
											.putExtra("type", bustype), TransferTreatmentResult);
											overridePendingTransition(R.anim.in_right,0);
										}else{
											/*runOnUiThread(new Runnable(){  
												@Override  
												public void run() {  
													mToast.showToast(getApplicationContext(), "转诊失败，请重试");
												}  
											});*/
										}
									}

								}else{
									/*runOnUiThread(new Runnable(){  
										@Override  
										public void run() {  
											mToast.showToast(getApplicationContext(), "影像资料2上传失败，请重试");
										}  
									});*/
								}
							}else{//没有影像资料2
								if(addReferralNetwork(bustype, mobile, referdoctor, acceptdoctor, acceptdoctorname)){
									startActivityForResult(new Intent(ExpertFindActivity.this,TransferTreatmentResultActivity.class)
									.putExtra("type", bustype), TransferTreatmentResult);
									overridePendingTransition(R.anim.in_right,0);
								}else{
									/*runOnUiThread(new Runnable(){  
										@Override  
										public void run() {  
											mToast.showToast(getApplicationContext(), "转诊失败，请重试");
										}  
									});*/
								}
							}

						}else{
							/*runOnUiThread(new Runnable(){  
								@Override  
								public void run() {  
									mToast.showToast(getApplicationContext(), "影像资料1上传失败，请重试");
								}  
							});*/
						}
					}else{//没有影像资料1
						if(addReferralNetwork(bustype, mobile, referdoctor, acceptdoctor, acceptdoctorname)){
							startActivityForResult(new Intent(ExpertFindActivity.this,TransferTreatmentResultActivity.class)
							.putExtra("type", bustype), TransferTreatmentResult);
							overridePendingTransition(R.anim.in_right,0);
						}else{
							/*runOnUiThread(new Runnable(){  
								@Override  
								public void run() {  
									mToast.showToast(getApplicationContext(), "转诊失败，请重试");
								}  
							});*/
						}
					}
				/*}*/
					
					
				closeLoadingDialog();

			}
		}).start();
	}
	
	
	boolean addReferralNetwork(int bustype, String mobile, String referdoctor,String acceptdoctor, String acceptdoctorname){
		
		try {
			String s = DataService.addReferral(getApplicationContext(), String.valueOf(bustype), patient.getPatientname()
					, patient.getSex(), patient.getBirthdate(), patient.getAreaid(), patient.getAddress()
					, patient.getPatientmobile(), headimg, patient.getPatientdesc(), patientimg[0], patientimg[1], patientimg[2]
							, patient.getIllnessname(), mobile, referdoctor, acceptdoctor, acceptdoctorname);
			mLog.d("http", "s："+s);
			if (!HlpUtils.isEmpty(s)){
				final HttpResult hr = JSON.parseObject(s,HttpResult.class);
				if (hr != null){
					if  (hr.isSuccess()){
						return true;
					}else{
						runOnUiThread(new Runnable(){  
							@Override  
							public void run() {  
								mToast.showToast(getApplicationContext(), hr.getData().toString());
							}  
						});
					}
				}else{
					runOnUiThread(new Runnable(){  
						@Override  
						public void run() {  
							mToast.showToast(getApplicationContext(), "转诊失败，请重试");
						}  
					});
				}
			}else{
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return false;
	}
	
	/**
	 * 
	 * @param patientimg 1:患者映射1IMG 2:患者映射2IMG 3:患者映射3IMG 
	 * @param iosimg
	 * @return
	 */
	private boolean uploadAttestationImg(final int patientimg, final String iosimg){
		boolean re = false;
		if(iosimg == null){
			return re;
		}
		String img = null;
		switch (patientimg) {
		case 1:
			img = "patientimgone";
			break;

		case 2:
			img = "patientimgtwo";
			break;
			
		case 3:
			img = "patientimgthree";
			break;
		}
		try {
			String s = DataService.uploadUserImg(getApplicationContext(), 
					new SpData(getApplicationContext()).getStringValue(SpData.keyId, null), img, iosimg);
			if (!isDestroy && !HlpUtils.isEmpty(s)){
				final HttpResult hr = JSON.parseObject(s,HttpResult.class);
				if (hr != null){
					if  (hr.isSuccess()){
						String imgName = hr.getFilename();
						if(imgName != null){
							mLog.d("http", "imgName :  "+imgName);
							this.patientimg[patientimg-1] = imgName;
							re = true;
						}
					}else{
						runOnUiThread(new Runnable(){  
							@Override  
							public void run() {  
								Toast.makeText(getApplicationContext(), hr.getData().toString(), Toast.LENGTH_SHORT).show();
							}  
						});
					}
				}else{
					runOnUiThread(new Runnable(){  
						@Override  
						public void run() {  
							mToast.showToast(getApplicationContext(), "转诊失败，请重试");
						}  
					});
				}
			}else{
				runOnUiThread(new Runnable(){  
					@Override  
					public void run() {  
						Toast.makeText(getApplicationContext(), "请检查网络后重试！", Toast.LENGTH_SHORT).show();
					}  
				});
			}
		}catch (Exception he) {
			he.printStackTrace();
		}
		return re;
	}
	String[] patientimg = new String[3];
	
	/**
	 * 上传头像
	 * @param iosimg
	 */
	private boolean uploadHeadImg(final String iosimg){
		boolean re = false;
		if(iosimg == null){
			return re;
		}
		try {
			String s = DataService.uploadUserImg(getApplicationContext(), 
					new SpData(getApplicationContext()).getStringValue(SpData.keyId, null), "userlogoimg", iosimg);
			if (!isDestroy && !HlpUtils.isEmpty(s)){
				final HttpResult hr = JSON.parseObject(s,HttpResult.class);
				if (hr != null){
					if  (hr.isSuccess()){
						if(hr.getFilename() != null){
							headimg = hr.getFilename();
							re = true;
							mLog.d("http", "uploadHeadImg true");
						}
					}else{
						runOnUiThread(new Runnable(){  
							@Override  
							public void run() {  
								Toast.makeText(getApplicationContext(), hr.getData().toString(), Toast.LENGTH_SHORT).show();
							}  
						});
					}
				}else{
					runOnUiThread(new Runnable(){  
						@Override  
						public void run() {  
							Toast.makeText(getApplicationContext(), "上传失败，请重试！", Toast.LENGTH_SHORT).show();
						}  
					});
				}
			}else{
			}
		}catch (Exception he) {
			he.printStackTrace();
		}
		return re;
	}
	private String headimg = null;

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
													//查询专家
													p = 1;
													page = 15;
													isFinish = false;
													listExpert = null;
													getListExpert(detpname, listDistrictArea.get(position).getId(),p,page);
													mSildingFinishLayout.setTouchView(lvExpert);
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

	/*private void getListSectionOffice(){
		reload("正在努力加载……");
		new Thread(new Runnable() {

			@Override
			public void run() {

				try {
					String s = DataService.getDepart(getApplicationContext());
					mLog.d("http", "s："+s);
					if (!HlpUtils.isEmpty(s)){
						HttpResult hr = JSON.parseObject(s,HttpResult.class);
						if (hr != null){
							if  (hr.isSuccess()){
								//listSectionOffice = Arrays.asList(hr.getData().toString().split(","));
								listSectionOffice = new ArrayList<String>();
								com.alibaba.fastjson.JSONArray array = JSON.parseArray(hr.getData().toString());
								for (int i = 0; i < array.size(); i++) {
									listSectionOffice.add(array.getString(i));
								}
								listSectionOffice.add(0, "全部");
								runOnUiThread(new Runnable(){  
									@Override  
									public void run() { 
										sectionOfficeAdapter.setList(listSectionOffice);
									}  
								});
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
	}*/

	/**
	 * get expert list
	 * @param detpname
	 * @param areaid
	 */
	private void getListExpert(final String detpname, final String areaid, final int p, final int pagesize){
		isQuery = false;
		reload( "正在努力加载……");
		new Thread(new Runnable() {

			@Override
			public void run() {

				try {
					String s = DataService.queryDoctor(getApplicationContext()
							, new SpData(getApplicationContext()).getStringValue(SpData.keyPhoneUser, null)
							, null, detpname, areaid, null, null, p, pagesize);
					mLog.d("http", "s："+s);
					if (!HlpUtils.isEmpty(s)){
						HttpResult hr = JSON.parseObject(s,HttpResult.class);
						if (hr != null){
							success = true;
							if  (hr.isSuccess()){
								if(!isDestroy){
									if(hr.getTotalpage()==p){
										isFinish = true;
									}
									if(listExpert != null){
										List<DoctorPerson> list = JSON.parseArray(hr.getData().toString(), DoctorPerson.class);
										
										listExpert.addAll(list);
									}
									else{
										listExpert = JSON.parseArray(hr.getData().toString(), DoctorPerson.class);
									}
									runOnUiThread(new Runnable(){  
										@Override  
										public void run() {  
											choosePosition = -1;
											expertAdapter.setList(listExpert);
										}  
									});
								}
							}else{
								runOnUiThread(new Runnable(){  
									@Override  
									public void run() {  
										if(listExpert == null){
											listExpert = new ArrayList<DoctorPerson>();
										}else{
											listExpert.clear();
										}
										expertAdapter.setList(listExpert);
									}  
								});
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
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (resultCode) {
		case TransferTreatmentResult:

			setResult(TransferTreatmentResult, data);
			finish();
			overridePendingTransition(0, R.anim.out_right);
			break;
		}
	}
	

	public interface OnClickDetailsListener {   
		void onClickDetailsListener(int position);   
	}

	private class MyExpertAdapter extends BaseAdapter {
		
		@SuppressLint("HandlerLeak")
		Handler handler = new Handler(){
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case 0:
					notifyDataSetChanged();
					break;

				default:
					break;
				}
			};};

		List<DoctorPerson> list = new ArrayList<DoctorPerson>();
		int choosePosition = -1;
		ListView mListView;
		OnClickDetailsListener onClickDetailsListener;
		private Drawable[] drawables;

		private LayoutInflater mInflater = null;
		private Context mContext;
		
		//private boolean isChooseNotify;
		
		SyncImageLoaderListview.OnImageLoadListener mImageLoadListener;

		private MyExpertAdapter(Context context, ListView lvExpert)
		{
			this.mInflater = LayoutInflater.from(context);
			mContext = context;
			mListView = lvExpert;
			mListView.setOnScrollListener(onScrollListener);
			//isChooseNotify = false;
		}

		private void setList(List<DoctorPerson> list)
		{
			if(list!=null){
				if(list.size()>0){
					drawables = new Drawable[list.size()];
				}
				choosePosition = -1;
				this.list = list;
				//isChooseNotify = false;
				notifyDataSetChanged();
			}
		}

		private void setChoosePosition(int choosePosition)
		{
			this.choosePosition = choosePosition;
			//isChooseNotify = true;
			notifyDataSetChanged();
		}

		private void setOnClickDetailsListener(OnClickDetailsListener onClickDetailsListener){
			this.onClickDetailsListener = onClickDetailsListener;
		}
		
		private void setOnImageLoadListener(SyncImageLoaderListview.OnImageLoadListener mImageLoadListener){
			this.mImageLoadListener = mImageLoadListener;
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
				convertView = mInflater.inflate(R.layout.item_expert_find_listview, null);
				holder.ivHead = (CircularImage) convertView.findViewById(R.id.iv_item_expert_find_headphoto);
				holder.ivChoose = (ImageView) convertView.findViewById(R.id.iv_item_expert_find_choose);
				holder.tvName = (TextView) convertView.findViewById(R.id.tv_item_expert_find_name);
				holder.tvDescribe = (TextView) convertView.findViewById(R.id.tv_item_expert_find_describe);
				holder.tvArea = (TextView) convertView.findViewById(R.id.tv_item_expert_find_area);
				holder.btnDetails = (Button)convertView.findViewById(R.id.btn_item_expert_find_details);
				convertView.setTag(holder);
			}else
			{
				holder = (ViewHolder)convertView.getTag();
			}

			if(getCount()>0)
			{
				/*if(!isChooseNotify){*/
					DoctorPerson expert = list.get(position);
					String name = expert.getDoctorname();
					if(name != null){
						holder.tvName.setText(name);
					}
					else{
						holder.tvName.setText("");
					}
					String describe = "";
					if(expert.getHospital() != null){
						describe += expert.getHospital();
					}
					if(expert.getDetp() != null){
						describe += expert.getDetp();
					}
					if(expert.getJobtitle() != null){
						describe += expert.getJobtitle();
					}
					holder.tvDescribe.setText(describe);
					
					String area = expert.getAddress();
					if(area != null){
						holder.tvArea.setText(area);
					}
					else{
						holder.tvArea.setText("");
					}
					
					holder.btnDetails.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View v) {
							onClickDetailsListener.onClickDetailsListener(position);
						}
					});
					
					Drawable d = drawables[position];
					if(d != null){
						holder.ivHead.setImageDrawable(d);
					}
					else{
						holder.ivHead.setImageResource(R.drawable.ic_head);
						syncImageLoader.loadImage(mContext,position,expert.getHeadimg(),imageLoadListener,holder.ivHead,0);
					}
				/*}*/
				if(choosePosition == position){
					holder.ivChoose.setImageResource(R.drawable.expert_find_b);
				}else{
					holder.ivChoose.setImageResource(R.drawable.expert_find_g);
				}
			}

			return convertView;
		}



		SyncImageLoaderListview.OnImageLoadListener imageLoadListener = new SyncImageLoaderListview.OnImageLoadListener(){

			@Override
			public void onImageLoad(Integer t, Drawable drawable, CircularImage ivHead,Integer index) {
				//mLog.d("http", "imageLoadListener  onImageLoad ");
				//View view = mListView.getChildAt(t);
				if(ivHead != null){
					//CircularImage v = (CircularImage) view.findViewById(R.id.iv_item_expert_find_headphoto);
					//v.setImageDrawable(drawable);
					handler.sendEmptyMessage(0);
					//ivHead.setImageDrawable(drawable);
					drawables[t] = drawable;
					//iv.setImageDrawable(drawable);
					//mLog.d("http", "imageLoadListener  onImageLoad view != null");
				}
				mImageLoadListener.onImageLoad(t, drawable, ivHead,0);
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
			private CircularImage ivHead;
			private TextView tvName;
			private TextView tvDescribe;
			private TextView tvArea;
			private Button btnDetails;
			private ImageView ivChoose;
		}
	}
	
	private class MySectionOfficeAdapter extends BaseAdapter {
		List<String> list = new ArrayList<String>();
		
		private LayoutInflater mInflater = null;
		
		private MySectionOfficeAdapter(Context context)
	    {
	        this.mInflater = LayoutInflater.from(context);
	    }
		
		private void setList(List<String> list)
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
