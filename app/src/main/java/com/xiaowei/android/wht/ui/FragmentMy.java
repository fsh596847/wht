package com.xiaowei.android.wht.ui;

import android.app.Dialog;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.xiaowei.android.wht.ApplicationTool;
import com.xiaowei.android.wht.R;
import com.xiaowei.android.wht.SpData;
import com.xiaowei.android.wht.beans.DoctorMoney;
import com.xiaowei.android.wht.beans.DoctorPerson;
import com.xiaowei.android.wht.model.HttpResult;
import com.xiaowei.android.wht.service.DataService;
import com.xiaowei.android.wht.service.DataService4Patient;
import com.xiaowei.android.wht.utils.mLog;
import com.xiaowei.android.wht.utis.HlpUtils;
import com.xiaowei.android.wht.utis.Utils;
import com.xiaowei.android.wht.views.CircularImage;

public class FragmentMy extends Fragment {
	
	private CircularImage iv_my_headphoto;//头像
	private TextView tv_my_name;//医生姓名
	private TextView tvMoney;
	private TextView tvVersion;
	private TextView tvMsg;
	
	private DoctorPerson doctorPerson;
	private DoctorMoney doctorMoney;
	
	private CallBack callBack;
	
	private static final int REQUEST_MYINFO = 610;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_my, container, false); 
		
		initViews(view);
		
		initListeners(view);
		initData();
		return view;
	}

	private void initListeners(View view) {

		//我的资料
		view.findViewById(R.id.rl_my_info).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getActivity(),MyInfoActivity.class);
				intent.putExtra("doctorPerson", doctorPerson);
				startActivityForResult(intent, REQUEST_MYINFO);
				getActivity().overridePendingTransition(R.anim.in_right,0);
			}
		});
		
		//我的余额
		view.findViewById(R.id.rl_my_balance).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				startActivityForResult(new Intent(getActivity(),MyBalanceActivity.class)
				.putExtra("DoctorMoney", doctorMoney),100);
				getActivity().overridePendingTransition(R.anim.in_right,0);
			}
		});
		
		//我的患者
		view.findViewById(R.id.rl_my_patient).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				startActivity(new Intent(getActivity(),MyPatientActivity.class));
			}
		});
		
		//我的会议
		view.findViewById(R.id.rl_my_conference).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				startActivity(new Intent(getActivity(),MyMeetingActivity.class));
			}
		});
		
		//我的邀请
		view.findViewById(R.id.rl_my_invite).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				startActivity(new Intent(getActivity(),MyInviteActivity.class));
				getActivity().overridePendingTransition(R.anim.in_right,0);
			}
		});
		
		//我的消息
		view.findViewById(R.id.rl_my_message).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				startActivityForResult(new Intent(getActivity(),MyMessageActivity.class),1002);
				getActivity().overridePendingTransition(R.anim.in_right,0);
			}
		});
		
		//切换身份
		view.findViewById(R.id.rl_my_switch).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				SpData spData = new SpData(getActivity());
				spData.setStringValue(SpData.KeyClientType, ""+RegisterActivity.clientTypePatient);
				startActivity(new Intent(getActivity(),ChooseWindowActivity.class));
				if(callBack != null){
					callBack.finish();
				}
			}
		});
		
		//关于我们
		view.findViewById(R.id.rl_my_about).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				startActivity(new Intent(getActivity(),WebAboutUsActivity.class));
				getActivity().overridePendingTransition(R.anim.in_right,0);
			}
		});
		
		//退出账号
		view.findViewById(R.id.btn_my_exit).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//信鸽推送反注册
				ApplicationTool.getInstance().stopPushService();
				startActivity(new Intent(getActivity(),ChooseWindowActivity.class)
				.putExtra("comeGuide", true));
				if(callBack != null){
					callBack.finish();
				}
			}
		});
		
		//打电话
		view.findViewById(R.id.iv_my_phone).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(Intent.ACTION_DIAL,Uri.parse("tel:010-67681788"));  
				startActivity(intent); 
			}
		});
		
	}

	private void initViews(View v) {
		tvMsg = (TextView) v.findViewById(R.id.tv_my_message);
		iv_my_headphoto =  (CircularImage) v.findViewById(R.id.iv_my_headphoto);
		tv_my_name = (TextView) v.findViewById(R.id.tv_my_name);
		tvMoney = (TextView) v.findViewById(R.id.tv_my_money);
		tvVersion = (TextView) v.findViewById(R.id.tv_my_about_version);
		tvVersion.setText("当前版本："+Utils.getVersionName(getActivity()));
		
	}
	
	private void initData() {
		queryInitData();
	}
	

	public interface CallBack {   
	    void finish();   
	}
	
	public void setCallBack(CallBack callBack){
		this.callBack = callBack;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (resultCode) {
		case 611:
			boolean isModify = data.getBooleanExtra("isModify", false);
			if(isModify){ //个人信息被修改过重新加载
				reload("正在努力加载……");
				new Thread(new Runnable() {
					@Override
					public void run() {

						//查询医生信息
						queryDoctorPerson();
						closeLoadingDialog();

					}
				}).start();
			}
			break;

		case 100:
			//querymoney();
			break;
			
		}
	}
	
	public void queryNotReadNoctice(){
		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					String s = DataService.queryNotReadNoctice(getActivity()
							,new SpData(getActivity()).getStringValue(SpData.keyPhoneUser, null)
							, "DOCTOR");
					mLog.d("http", "s:"+s);
					if (!HlpUtils.isEmpty(s)){
						final HttpResult hr = JSON.parseObject(s,HttpResult.class);
						if (hr != null){
								final int totalRows = hr.getTotalRows();
								if(totalRows > 0){
									getActivity().runOnUiThread(new Runnable(){  
										@Override  
										public void run() {  
											tvMsg.setText("未读消息"+(totalRows > 99 ? "99+" : totalRows+"")+"条");
										}  
									});
								}else{
									getActivity().runOnUiThread(new Runnable(){  
										@Override  
										public void run() {  
											tvMsg.setText("");
										}  
									});
								}
						}
					}
				}catch (Exception he) {
					he.printStackTrace();
				}
			}
		}).start();
	}
	
	
	/**
	 * 加载医生数据
	 */
	private void queryInitData() {
		reload("正在努力加载……");
		new Thread(new Runnable() {
			@Override
			public void run() {

				//查询医生信息
				queryDoctorPerson();
				//查询收入
				//queryUserMoney();
				//查询未读信息
				
				
				closeLoadingDialog();

			}
		}).start();
	}
	
	private void querymoney() {
		//reload("正在努力加载……");
		new Thread(new Runnable() {
			@Override
			public void run() {

				//查询收入
				queryUserMoney();
				
				//closeLoadingDialog();

			}
		}).start();
	}
	
	private void queryUserMoney() {
		try {
			String s = DataService.queryUserMny(getActivity()
					, new SpData(getActivity()).getStringValue(SpData.keyId, null));
			mLog.d("http", "s:"+s);
			if (!HlpUtils.isEmpty(s)){
				final HttpResult hr = JSON.parseObject(s,HttpResult.class);
				if(hr != null){
					if(hr.isSuccess()){
						if(!isDestroy){
							doctorMoney = JSON.parseObject(hr.getData().toString(), DoctorMoney.class);
							getActivity().runOnUiThread(new Runnable(){  
								@Override  
								public void run() { 
									
									tvMoney.setText("¥"+doctorMoney.getAccountmny());
								}  
							});
						}
					}else{
						/*getActivity().runOnUiThread(new Runnable(){  
							@Override  
							public void run() {  
								Toast.makeText(getActivity(), hr.getData().toString(), Toast.LENGTH_SHORT).show();
							}  
						});*/
					}
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
	}
	
	/**
	 * 查询医生信息
	 */
	private void queryDoctorPerson() {
		try {
			String mobile = new SpData(getActivity().getApplicationContext()).getStringValue(SpData.keyPhoneUser, null);
			//Map<String, String> map = new HashMap<String, String>();
			//map.put("mobile", mobile);
			String s = DataService4Patient.queryDoctorPerson(getActivity(), mobile);
			mLog.e("http", "queryDoctorPerson  s:"+s);
			if (!HlpUtils.isEmpty(s)){
				final HttpResult hr = JSON.parseObject(s,HttpResult.class);
				if(hr != null){
					if(hr.isSuccess()){
						if(!isDestroy){
							doctorPerson = JSON.parseObject(hr.getData().toString(), DoctorPerson.class);
							byte[] data = DataService.getImage(doctorPerson.getHeadimg());
							if(data != null){
								final Bitmap mBitmap = BitmapFactory.decodeByteArray(data, 0, data.length);// bitmap 
								//mLog.d("http", "data:"+JSON.parseArray(hr.getData().toString()).toString());
								getActivity().runOnUiThread(new Runnable(){  
									@Override  
									public void run() { 
										//设置图片
										iv_my_headphoto.setImageBitmap(mBitmap);
									}  
								});
							}
							final String name = doctorPerson.getDoctorname();
							if(name != null){
								getActivity().runOnUiThread(new Runnable(){  
									@Override  
									public void run() { 
										tv_my_name.setText(name);
									}  
								});
							}
						}
					}else{
						/*getActivity().runOnUiThread(new Runnable(){  
							@Override  
							public void run() {  
								Toast.makeText(getActivity(), hr.getData().toString(), Toast.LENGTH_SHORT).show();
							}  
						});*/
					}
				}
			}else{
				/*getActivity().runOnUiThread(new Runnable(){  
					@Override  
					public void run() {  
						Toast.makeText(getActivity(), "请检查网络后重试！", Toast.LENGTH_SHORT).show();
					}  
				});*/
			}
		}catch (Exception he) {
			he.printStackTrace();
		}
	}
	
	public String getSexByIndex(String index){
		if("0".equals(index)){
			return "男";
		}else if("1".equals(index)){
			return "女";
		}
		return "男";
	}
	
	public String getIndexBySex(String sex){
		if("男".equals(sex)){
			return "0";
		}else if("女".equals(sex)){
			return "1";
		}
		return "0";
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
		queryNotReadNoctice();
		querymoney();
	}


	@Override
	public void onDestroy() {
		super.onDestroy();
		isDestroy = true;
	}

}
